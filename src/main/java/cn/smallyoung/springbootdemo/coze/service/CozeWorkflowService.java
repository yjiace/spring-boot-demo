package cn.smallyoung.springbootdemo.coze.service;


import cn.smallyoung.springbootdemo.coze.CozeConstant;
import cn.smallyoung.springbootdemo.coze.model.chat.request.CozeChatRequest;
import cn.smallyoung.springbootdemo.coze.model.chat.response.CozeChatResponse;
import cn.smallyoung.springbootdemo.coze.model.chat.response.CozeChatStreamResponse;
import cn.smallyoung.springbootdemo.coze.model.workflow.request.CozeWorkflowRequest;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 工作流相关接口
 *
 * @author smallyoung
 */

@Slf4j
@Service
public class CozeWorkflowService {

    private final WebClient webClient;

    public CozeWorkflowService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(CozeConstant.COZE_BASE_URL)
                .build();
    }

    /**
     * 执行工作流
     *
     * @param token   鉴权Token
     * @param request 请求参数
     */
    public Mono<CozeChatResponse> workflow(String token, CozeWorkflowRequest request) {
        log.info("发送 Coze Chat 请求: {}", JSONObject.toJSONString(request));
        return webClient.post()
                .uri("/v1/workflow/run")
                .header(CozeConstant.AUTHORIZATION, "Bearer " + token)
                .bodyValue(JSONObject.from(request))
                .retrieve()
                .bodyToMono(CozeChatResponse.class)
                .doOnSuccess(response -> log.info("Coze Workflow 响应成功"))
                .doOnError(error -> log.error("Coze Workflow 请求失败", error));
    }

    /**
     * 发起对话 流式响应。
     *
     * @param token           鉴权Token
     * @param conversationId  会话ID
     * @param cozeChatRequest 请求参数
     */
    public Flux<CozeChatStreamResponse> workflowStream(String token, String conversationId, CozeChatRequest cozeChatRequest) {
        cozeChatRequest.setStream(Boolean.TRUE);
        log.info("发送 Coze Chat Stream 请求: {}", JSONObject.toJSONString(cozeChatRequest));
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v1/workflow/stream_run")
                        .queryParamIfPresent("conversation_id", Optional.ofNullable(conversationId))
                        .build())
                .header(CozeConstant.AUTHORIZATION, "Bearer " + token)
                .bodyValue(JSONObject.from(cozeChatRequest))
                .retrieve()
                .bodyToFlux(String.class)
                .mapNotNull(line -> {
                    String trimmed = line.trim();

                    // 过滤空行和 [DONE] 标记
                    if (trimmed.isEmpty() || "[DONE]".equals(trimmed) || "\"[DONE]\"".equals(trimmed)) {
                        log.debug("跳过结束标记: {}", line);
                        return null;
                    }

                    try {
                        return JSONObject.parseObject(trimmed, CozeChatStreamResponse.class);
                    } catch (Exception e) {
                        log.warn("解析响应失败: {}", trimmed, e);
                        return null;
                    }
                })
                .doOnComplete(() -> log.info("Coze Workflow Stream 完成"))
                .doOnError(error -> log.error("Coze Workflow Stream 请求失败", error));
    }
}
