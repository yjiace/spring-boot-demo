package cn.smallyoung.springbootdemo;

import cn.smallyoung.springbootdemo.coze.model.chat.request.CozeChatRequest;
import cn.smallyoung.springbootdemo.coze.model.chat.response.CozeChatResponse;
import cn.smallyoung.springbootdemo.coze.model.chat.response.CozeChatStreamResponse;
import cn.smallyoung.springbootdemo.coze.model.chat.response.RetrieveResponse;
import cn.smallyoung.springbootdemo.coze.service.CozeChatService;
import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.model.ChatEvent;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.config.Consts;
import com.coze.openapi.service.service.CozeAPI;
import io.reactivex.Flowable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author smallyoung
 */
@Slf4j
@SpringBootTest
public class CozeChatTest {

    private static final String TOKEN = "pat_VYfrmEW4cemWg5P9lPBPM0GynslzzDZtcV1Ipkpv0tq9RqU2kcuPsHNAOo9ojWAQ";

    @Resource
    private CozeChatService cozeChatService;

    @Test
    public void test(){
        TokenAuth authCli = new TokenAuth(TOKEN);
        CozeAPI coze = new CozeAPI.Builder()
                .baseURL(Consts.COZE_CN_BASE_URL)
                .auth(authCli)
                .build();
        CreateChatReq req = CreateChatReq.builder()
                .botID("7579803526084411401").userID("abc123")
                .messages(Collections.singletonList(Message.buildUserQuestionText("What can you do?")))
                .build();
        Flowable<ChatEvent> resp = coze.chat().stream(req);
        resp.blockingForEach(event -> {
            if (ChatEventType.CONVERSATION_MESSAGE_DELTA.equals(event.getEvent())) {
                System.out.print(event.getMessage().getContent());
            }
            if (ChatEventType.CONVERSATION_CHAT_COMPLETED.equals(event.getEvent())) {
                System.out.println("Token usage:" + event.getChat().getUsage().getTokenCount());
            }
        });
    }

    @Test
    public void testChat() {
        Mono<CozeChatResponse> result = cozeChatService.chat(TOKEN, null,
                CozeChatRequest.builder()
                        .botId("7579803526084411401").userId("abc123")
                        .additionalMessages(List.of(CozeChatRequest.AdditionalMessage.builder()
                                .role("user").content("你好")
                                .contentType("text").type("question")
                                .build()))
                        .build());

        StepVerifier.create(result)
                .assertNext(info -> log.info("响应信息: {}", info))
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }

    @Test
    public void testChatStream() {
        Flux<CozeChatStreamResponse> result = cozeChatService.chatStream(TOKEN, null,
                CozeChatRequest.builder()
                        .botId("7579803526084411401").userId("abc123")
                        .additionalMessages(List.of(CozeChatRequest.AdditionalMessage.builder()
                                .role("user").content("你好")
                                .contentType("text").type("question")
                                .build()))
                        .build());

        StepVerifier.create(result)
                .thenConsumeWhile(
                        chunk -> {
                            log.info("流式响应信息: {}", chunk);
                            return true;
                        })
                .expectComplete()
                .verify(Duration.ofSeconds(60));
    }

    @Test
    public void testRetrieve() {
        Mono<RetrieveResponse> result = cozeChatService.retrieve(TOKEN, "7579897554675957787", "7579897554676006939");

        StepVerifier.create(result)
                .assertNext(info -> log.info("响应信息: {}", info))
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }

}
