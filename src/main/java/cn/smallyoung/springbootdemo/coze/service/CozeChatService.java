package cn.smallyoung.springbootdemo.coze.service;


import cn.smallyoung.springbootdemo.coze.CozeConstant;
import cn.smallyoung.springbootdemo.coze.model.chat.request.CozeChatRequest;
import cn.smallyoung.springbootdemo.coze.model.chat.response.CozeChatResponse;
import cn.smallyoung.springbootdemo.coze.model.chat.response.CozeChatStreamResponse;
import cn.smallyoung.springbootdemo.coze.model.chat.response.RetrieveResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 *
 * @author smallyoung
 */
@Slf4j
@Service
public class CozeChatService {

    private final WebClient webClient;

    public CozeChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(CozeConstant.COZE_BASE_URL)
                .build();
    }

    /**
     * 发起对话
     *
     * @param token           鉴权Token
     * @param conversationId  会话ID
     * @param cozeChatRequest 请求参数
     */
    public Mono<CozeChatResponse> chat(String token, String conversationId, CozeChatRequest cozeChatRequest) {
        cozeChatRequest.setStream(Boolean.FALSE);
        log.info("发送 Coze Chat 请求: {}", JSONObject.toJSONString(cozeChatRequest));
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v3/chat")
                        .queryParamIfPresent("conversation_id", Optional.ofNullable(conversationId))
                        .build())
                .header(CozeConstant.AUTHORIZATION, "Bearer " + token)
                .bodyValue(JSONObject.from(cozeChatRequest))
                .retrieve()
                .bodyToMono(CozeChatResponse.class)
                .doOnSuccess(response -> log.info("Coze Chat 响应成功"))
                .doOnError(error -> log.error("Coze Chat 请求失败", error));
    }

    /**
     * 发起对话 流式响应。
     *
     * @param token           鉴权Token
     * @param conversationId  会话ID
     * @param cozeChatRequest 请求参数
     */
    public Flux<CozeChatStreamResponse> chatStream(String token, String conversationId, CozeChatRequest cozeChatRequest) {
        cozeChatRequest.setStream(Boolean.TRUE);
        log.info("发送 Coze Chat Stream 请求: {}", JSONObject.toJSONString(cozeChatRequest));
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v3/chat")
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
                .doOnComplete(() -> log.info("Coze Chat Stream 完成"))
                .doOnError(error -> log.error("Coze Chat Stream 请求失败", error));
    }

    /**
     * 查看对话详情
     *
     * @param token          鉴权Token
     * @param conversationId 会话ID
     * @param chatId         Chat ID，即对话的唯一标识
     */
    public Mono<RetrieveResponse> retrieve(String token, String conversationId, String chatId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v3/chat/retrieve")
                        .queryParam("conversation_id", conversationId)
                        .queryParam("chat_id", chatId)
                        .build())
                .header(CozeConstant.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RetrieveResponse.class)
                .doOnSuccess(response -> log.info("Coze Retrieve 响应成功"))
                .doOnError(error -> log.error("Coze Retrieve 请求失败", error));

    }
}
