package cn.smallyoung.springbootdemo.wechat.config;


import cn.smallyoung.springbootdemo.wechat.client.WechatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 *
 * @author smallyoung
 */

@Component
public class WechatClientConfig {

    @Bean
    public WechatClient getWechatClient() {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(180));
        WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(WechatClient.class);
    }
}
