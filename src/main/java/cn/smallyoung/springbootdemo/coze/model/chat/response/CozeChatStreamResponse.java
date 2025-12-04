package cn.smallyoung.springbootdemo.coze.model.chat.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author smallyoung
 */
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class CozeChatStreamResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 6088839949493540604L;

    /**
     * 编写此消息的智能体 ID。此参数仅在对话产生的消息中返回。
     */
    @JsonProperty("bot_id")
    private String botId;

    /**
     * Chat ID。此参数仅在对话产生的消息中返回。
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * 消息的内容，支持纯文本、多模态（文本、图片、文件混合输入）、卡片等多种类型的内容。
     * 当 role 为 user 时，支持返回多模态内容。
     * 当 role 为 assistant 时，只支持返回纯文本内容。
     */
    private String content;

    /**
     * 消息内容的类型，取值包括：
     * <p>
     * text：文本。
     * object_string：多模态内容，即文本和文件的组合、文本和图片的组合。
     * card：卡片。此枚举值仅在接口响应中出现，不支持作为入参。
     */
    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("content_expands")
    private List<ContentExpand> contentExpands;

    /**
     * 此消息所在的会话 ID。
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 消息的创建时间，格式为 10 位的 Unixtime 时间戳，单位为秒（s）。
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * Message ID，即消息的唯一标识。
     */
    private String id;

    /**
     * 创建消息时的附加消息，查看消息列表时也会返回此附加消息。
     */
    @JsonProperty("meta_data")
    private Object metaData;

    @JsonProperty("parent_message_id")
    private String parentMessageId;

    /**
     * 模型的思维链（CoT），展示模型如何将复杂问题逐步分解为多个简单步骤并推导出最终答案。仅当模型支持深度思考、且智能体开启了深度思考时返回该字段，当前支持深度思考的模型如下：
     * <p>
     * 豆包・1.6・自动深度思考・多模态模型
     * 豆包・1.6・极致速度・多模态模型
     * 豆包・1.5・Pro・视觉深度思考
     * 豆包・GUI・Agent 模型
     * DeepSeek-R1
     */
    @JsonProperty("reasoning_content")
    private String reasoningContent;

    /**
     * 发送这条消息的实体。取值：
     * user：代表该条消息内容是用户发送的。
     * assistant：代表该条消息内容是智能体发送的。
     */
    @JsonProperty("role")
    private String role;

    /**
     * 上下文片段 ID。每次调用清除上下文 API 都会生成一个新的 section_id。
     */
    @JsonProperty("section_id")
    private String sectionId;

    @JsonProperty("time_cost")
    private TimeCost timeCost;

    /**
     * 消息类型。
     * question：用户输入内容。
     * answer：智能体返回给用户的消息内容，支持增量返回。如果工作流绑定了 messge 节点，可能会存在多 answer 场景，此时可以用流式返回的结束标志来判断所有 answer 完成。
     * function_call：智能体对话过程中调用函数（function call）的中间结果。
     * tool_output：调用工具 （function call）后返回的结果。
     * tool_response：调用工具 （function call）后返回的结果。
     * follow_up：如果在智能体上配置打开了用户问题建议开关，则会返回推荐问题相关的回复内容。
     * verbose：多 answer 场景下，服务端会返回一个 verbose 包，对应的 content 为 JSON 格式，content.msg_type =generate_answer_finish 代表全部 answer 回复完成。
     * 仅发起对话（v3）接口支持将此参数作为入参，且：
     * 如果 autoSaveHistory=true，type 支持设置为 question 或 answer。
     * 如果 autoSaveHistory=false，type 支持设置为 question、answer、function_call、tool_output、tool_response。
     * 其中，type=question 只能和 role=user 对应，即仅用户角色可以且只能发起 question 类型的消息
     */
    private String type;

    /**
     * 消息的更新时间，格式为 10 位的 Unixtime 时间戳，单位为秒（s）。
     */
    @JsonProperty("updated_at")
    private Long updatedAt;

    private Usage usage;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentExpand implements Serializable {

        @Serial
        private static final long serialVersionUID = 3766040621851237422L;

        private String info;

        private String type;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeCost implements Serializable {

        @Serial
        private static final long serialVersionUID = 8611282945435886630L;

        @JsonProperty("total_duration_ms")
        private Integer totalDurationMs;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage implements Serializable {

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
