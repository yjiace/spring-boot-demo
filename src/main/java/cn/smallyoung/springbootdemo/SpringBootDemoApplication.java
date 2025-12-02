package cn.smallyoung.springbootdemo;

import cn.smallyoung.springbootdemo.wechat.client.WechatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@SpringBootApplication
public class SpringBootDemoApplication {

    @Bean
    public WechatClient getWechatClient() {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(180));
        WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(WechatClient.class);
    }

    public static void main(String[] args) {
        System.setProperty("Log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }

}
