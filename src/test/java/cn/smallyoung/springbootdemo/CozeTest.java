package cn.smallyoung.springbootdemo;


import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.model.ChatEvent;
import com.coze.openapi.client.chat.model.ChatEventType;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.service.auth.TokenAuth;
import com.coze.openapi.service.config.Consts;
import com.coze.openapi.service.service.CozeAPI;
import io.reactivex.Flowable;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 *
 * @author smallyoung
 */
public class CozeTest {

    private static final String TOKEN = "pat_VYfrmEW4cemWg5P9lPBPM0GynslzzDZtcV1Ipkpv0tq9RqU2kcuPsHNAOo9ojWAQ";

    @Test
    public void test() {
        TokenAuth authCli = new TokenAuth(TOKEN);
        CozeAPI coze = new CozeAPI.Builder()
                .baseURL(Consts.COZE_CN_BASE_URL)
                .auth(authCli)
                .build();
        CreateChatReq req = CreateChatReq.builder()
                .botID("7579803526084411401").userID("abc123")
                .messages(Collections.singletonList(Message.buildUserQuestionText("你好")))
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
}
