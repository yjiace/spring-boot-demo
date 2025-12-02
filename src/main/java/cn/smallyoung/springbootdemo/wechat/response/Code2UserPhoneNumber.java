package cn.smallyoung.springbootdemo.wechat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信小程序，code转手机号响应对象
 *
 * @author smallyoung
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Code2UserPhoneNumber extends CommonResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -800484705415235370L;

    /**
     * 用户手机号信息
     */
    @JsonProperty("phone_info")
    private PhoneInfo phoneInfo;


    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class PhoneInfo implements Serializable {

        @Serial
        private static final long serialVersionUID = -7078615198754301492L;

        /**
         * 用户绑定的手机号（国外手机号会有区号）
         */
        private String phoneNumber;

        /**
         * 没有区号的手机号
         */
        private String purePhoneNumber;

        /**
         * 区号
         */
        private String countryCode;

        /**
         * 数据水印
         */
        private Watermark watermark;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Watermark implements Serializable {

        @Serial
        private static final long serialVersionUID = -6787617981860780129L;

        /**
         * 用户获取手机号操作的时间戳
         */
        private Long timestamp;

        /**
         * 小程序appid
         */
        private String appid;

    }
}
