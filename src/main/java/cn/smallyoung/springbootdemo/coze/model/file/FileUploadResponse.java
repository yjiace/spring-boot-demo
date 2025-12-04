package cn.smallyoung.springbootdemo.coze.model.file;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author smallyoung
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8220062206751387519L;

    /**
     * 调用状态码。0 表示调用成功，其他值表示调用失败，你可以通过 msg 字段判断详细的错误原因。
     */
    private Integer code;

    /**
     * 状态信息。API 调用失败时可通过此字段查看详细错误信息。
     * 状态码为 0 时，msg 默认为空。
     */
    private String msg;

    private Data data;

    private Detail detail;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data implements Serializable {

        @Serial
        private static final long serialVersionUID = -5721709895002711148L;

        private Long bytes;

        @JsonProperty("created_at")
        private Long createdAt;

        @JsonProperty("file_name")
        private String fileName;

        private String id;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail implements Serializable {

        @Serial
        private static final long serialVersionUID = 6556341781863272519L;

        @JsonProperty("logid")
        private String logId;
    }


}
