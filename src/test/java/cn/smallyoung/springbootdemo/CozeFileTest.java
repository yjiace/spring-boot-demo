package cn.smallyoung.springbootdemo;


import cn.smallyoung.springbootdemo.coze.model.file.FileRetrieveResponse;
import cn.smallyoung.springbootdemo.coze.model.file.FileUploadResponse;
import cn.smallyoung.springbootdemo.coze.service.CozeFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.time.Duration;

/**
 *
 * @author smallyoung
 */
@Slf4j
@SpringBootTest
public class CozeFileTest {

    private static final String TOKEN = "pat_VYfrmEW4cemWg5P9lPBPM0GynslzzDZtcV1Ipkpv0tq9RqU2kcuPsHNAOo9ojWAQ";

    @Resource
    private CozeFileService cozeFileService;

    @Test
    public void testUploadFile() {
        Mono<FileUploadResponse> result = cozeFileService.upload(TOKEN, new File("C:\\Users\\yangn\\Desktop\\test.png"));
        StepVerifier.create(result)
                .assertNext(System.out::println)
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }

    @Test
    public void testRetrieve() {
        Mono<FileRetrieveResponse> result = cozeFileService.retrieve(TOKEN, "7579929846948675622");
        StepVerifier.create(result)
                .assertNext(System.out::println)
                .expectComplete()
                .verify(Duration.ofSeconds(30));
    }

}
