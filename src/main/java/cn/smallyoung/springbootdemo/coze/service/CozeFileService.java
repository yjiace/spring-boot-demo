package cn.smallyoung.springbootdemo.coze.service;


import cn.smallyoung.springbootdemo.coze.CozeConstant;
import cn.smallyoung.springbootdemo.coze.model.file.FileRetrieveResponse;
import cn.smallyoung.springbootdemo.coze.model.file.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * 文件相关接口
 *
 * @author smallyoung
 */
@Slf4j
@Service
public class CozeFileService {

    private final WebClient webClient;

    public CozeFileService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(CozeConstant.COZE_BASE_URL)
                .build();
    }


    /**
     * 上传文件
     *
     * @param token 鉴权Token
     * @param file  MultipartFile格式的问题
     * @return 文件信息
     */
    public Mono<FileUploadResponse> upload(String token, MultipartFile file) {
        return this.upload(token, BodyInserters.fromMultipartData("file", file.getResource()));
    }

    /**
     * 上传文件
     *
     * @param token 鉴权Token
     * @param file  File格式的问题
     * @return 文件信息
     */
    public Mono<FileUploadResponse> upload(String token, File file) {
        return this.upload(token, BodyInserters.fromMultipartData("file", new FileSystemResource(file)));
    }

    /**
     * 上传文件
     *
     * @param token       鉴权Token
     * @param fileContent 文件字节
     * @param fileName    文件名
     * @return 文件信息
     */
    public Mono<FileUploadResponse> upload(String token, byte[] fileContent, String fileName) {
        // 1. 将 byte[] 包装为 Resource，并重写 getFilename
        ByteArrayResource resource = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                // 必须返回文件名，服务端通常依赖此字段识别文件格式（如 .png, .txt）
                return fileName;
            }
        };
        return this.upload(token, BodyInserters.fromMultipartData("file", resource));
    }

    /**
     * 上传文件
     *
     * @param token    鉴权Token
     * @param inserter Body文件参数
     * @return 文件信息
     */
    public Mono<FileUploadResponse> upload(String token, BodyInserter<?, ? super ClientHttpRequest> inserter) {
        return webClient.post()
                .uri("/v1/files/upload")
                .header(CozeConstant.AUTHORIZATION, "Bearer " + token)
                .body(inserter).retrieve()
                .bodyToMono(FileUploadResponse.class)
                .doOnSuccess(response -> log.info("Coze upload file 响应成功"))
                .doOnError(error -> log.error("Coze upload file 请求失败", error));
    }


    /**
     * 查看文件详情
     *
     * @param token  鉴权Token
     * @param fileId 文件ID
     * @return 文件信息
     */
    public Mono<FileRetrieveResponse> retrieve(String token, String fileId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/files/retrieve")
                        .queryParam("file_id", fileId)
                        .build())
                .header(CozeConstant.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(FileRetrieveResponse.class)
                .doOnSuccess(response -> log.info("Coze upload file 响应成功"))
                .doOnError(error -> log.error("Coze upload file 请求失败", error));
    }

}
