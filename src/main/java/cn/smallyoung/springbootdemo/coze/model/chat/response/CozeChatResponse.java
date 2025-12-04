package cn.smallyoung.springbootdemo.coze.model.chat.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author smallyoung
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CozeChatResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -7904354651494494241L;

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
    public static class Data implements Serializable{

        @Serial
        private static final long serialVersionUID = 8497872901431483934L;

        /**
         * 该会话所属的智能体的 ID。
         */
        @JsonProperty("bot_id")
        private String botId;

        /**
         * 对话结束的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
         */
        @JsonProperty("completed_at")
        private Long completedAt;

        /**
         * 会话 ID，即会话的唯一标识。
         */
        @JsonProperty("conversation_id")
        private String conversationId;

        /**
         * 对话创建的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
         */
        @JsonProperty("created_at")
        private Long createdAt;

        /**
         * 对话失败的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
         */
        @JsonProperty("failed_at")
        private Long failedAt;

        /**
         * 对话 ID，即对话的唯一标识。
         */
        @JsonProperty("id")
        private String id;

        @JsonProperty("inserted_additional_messages")
        private List<String> insertedAdditionalMessages;

        @JsonProperty("last_error")
        private LastError lastError;

        /**
         * 发起对话时的附加消息，用于传入使用方的自定义数据，查看对话详情时也会返回此附加消息。
         */
        @JsonProperty("meta_data")
        private Object metaData;

        @JsonProperty("required_action")
        private RequiredAction requiredAction;

        /**
         * 上下文片段 ID。每次调用清除上下文 API 都会生成一个新的 section_id。
         */
        @JsonProperty("section_id")
        private String sectionId;

        /**
         * 对话的运行状态。取值为：
         * created：对话已创建。
         * in_progress：智能体正在处理中。
         * completed：智能体已完成处理，本次对话结束。
         * failed：对话失败。
         * requires_action：对话中断，需要进一步处理。
         * canceled：对话已取消
         */
        private String status;

        @JsonProperty("time_cost")
        private TimeCost timeCost;

        private Usage usage;

        @lombok.Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LastError implements Serializable{

            @Serial
            private static final long serialVersionUID = 8608888211268135261L;

            /**
             * 状态码。
             * 0 代表调用成功
             */
            private Integer code;

            /**
             * 状态信息。API 调用失败时可通过此字段查看详细错误信息。
             */
            private String msg;
        }

        @lombok.Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RequiredAction implements Serializable{

            @Serial
            private static final long serialVersionUID = -6814278296027335987L;

            @JsonProperty("submit_tool_outputs")
            private  SubmitToolOutputs submitToolOutputs;

            /**
             * 额外操作的类型，枚举值：
             * submit_tool_outputs：需要提交工具输出以继续对话。
             */
            private String type;

            @lombok.Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class SubmitToolOutputs implements Serializable{

                @Serial
                private static final long serialVersionUID = 14578298254099368L;

                @JsonProperty("tool_calls")
                private List<ToolCall> toolCalls;

                @lombok.Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class ToolCall implements Serializable{

                    @Serial
                    private static final long serialVersionUID = 4518172565557032522L;

                    /**
                     * 上报运行结果的 ID。
                     */
                    private String id;

                    /**
                     * 工具类型，枚举值包括：
                     * function：待执行的方法，通常是端插件。触发端插件时会返回此枚举值。
                     * reply_message：待回复的选项。触发工作流问答节点时会返回此枚举值。
                     */
                    private String type;

                    private Function function;

                    @lombok.Data
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class Function implements Serializable{

                        @Serial
                        private static final long serialVersionUID = 2447371209076734661L;

                        /**
                         * 当对话状态为 requires_action时，此字段表示需要调用的工具或函数的参数，通常为 JSON 格式的字符串，用于指定工具的具体执行参数。
                         */
                        private String arguments;

                        /**
                         * 当对话状态为 requires_action 时，此字段表示需要调用的工具或函数的名称，用于继续对话。通常与 arguments字段配合使用，指定工具的具体执行方法。
                         */
                        private String name;
                    }
                }
            }
        }

        @lombok.Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TimeCost implements Serializable{

            @Serial
            private static final long serialVersionUID = 8611282945435886630L;

            @JsonProperty("total_duration_ms")
            private Integer totalDurationMs;
        }

        @lombok.Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Usage implements Serializable{

            @Serial
            private static final long serialVersionUID = 3970924204841892689L;

            /**
             * 输入内容所消耗的 Token 数，包含对话上下文、系统提示词、用户当前输入等所有输入类的 Token 消耗。
             */
            @JsonProperty("input_count")
            private Double inputCount;

            /**
             * 大模型输出的内容所消耗的 Token 数。
             */
            @JsonProperty("output_count")
            private Double outputCount;

            /**
             * 本次 API 调用消耗的 Token 总量，包括输入和输出两部分的消耗。
             */
            @JsonProperty("token_count")
            private Double tokenCount;
        }
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail implements Serializable{

        @Serial
        private static final long serialVersionUID = 2318558094174127104L;

        /**
         * 本次请求的日志 ID。
         * 如果遇到异常报错场景，且反复重试仍然报错，可以根据此 logid 及错误码联系扣子团队获取帮助。
         */
        @JsonProperty("logid")
        private String logId;
    }

}
