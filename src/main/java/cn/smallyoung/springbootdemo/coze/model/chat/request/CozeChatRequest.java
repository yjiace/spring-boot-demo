package cn.smallyoung.springbootdemo.coze.model.chat.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author smallyoung
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CozeChatRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7891754455344582615L;

    /**
     * 要进行会话聊天的智能体 ID。
     * 进入智能体的 开发页面，开发页面 URL 中 bot 参数后的数字就是智能体 ID
     * 确保当前使用的访问密钥已被授予智能体所属空间的 chat 权限。
     */
    @JsonProperty("bot_id")
    private String botId;

    /**
     * 标识当前与智能体对话的用户，由使用方自行定义、生成与维护。
     * user_id 用于标识对话中的不同用户，不同的 user_id，其对话的上下文消息、数据库等对话记忆数据互相隔离。
     * 如果不需要用户数据隔离，可将此参数固定为一个任意字符串，例如 123，abc 等。
     * 出于数据隐私及信息安全等方面的考虑，不建议使用业务系统中定义的用户 ID。
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 是否启用流式返回。
     */
    private Boolean stream;

    /**
     * 对话的附加信息。
     * 你可以通过此字段传入历史消息和本次对话中用户的问题。数组长度限制为 100，即最多传入 100 条消息。
     */
    @JsonProperty("additional_messages")
    private List<AdditionalMessage> additionalMessages;

    /**
     * 智能体中定义的变量。
     * 在智能体 prompt 中设置变量 {{key}} 后，可以通过该参数传入变量值，同时支持 Jinja2 语法
     */
    @JsonProperty("custom_variables")
    private Map<String, Object> customVariables;

    /**
     * 是否保存本次对话记录。
     * true：（默认）会话中保存本次对话记录，包括 additional_messages 中指定的所有消息、本次对话的模型回复结果、模型执行中间结果。
     * false：会话中不保存本次对话记录，后续也无法通过任何方式查看本次对话信息、消息详情。在同一个会话中再次发起对话时，本次会话也不会作为上下文传递给模型。
     * 非流式响应下（stream=false），此参数必须设置为 true，即保存本次对话记录，否则无法查看对话状态和模型回复。
     * 调用端插件时，此参数必须设置为 true，即保存本次对话记录，否则提交工具执行结果时会提示 5000 错误，
     */
    @JsonProperty("auto_save_history")
    private Boolean autoSaveHistory = true;

    /**
     * 附加信息，通常用于封装一些业务相关的字段。查看对话详情时，扣子会透传此附加信息，查看消息列表时不会返回该附加信息。
     * 自定义键值对，应指定为 Map 对象格式。长度为 16 对键值对，其中键（key）的长度范围为 1～64 个字符，值（value）的长度范围为 1～512 个字符。
     */
    @JsonProperty("meta_data")
    private Map<String, Object> metaData;

    /**
     * 附加参数，通常用于特殊场景下指定一些必要参数供模型判断，例如指定经纬度，并询问智能体此位置的天气。
     * 自定义键值对格式，其中键（key）仅支持设置为：
     * latitude：纬度，此时值（Value）为纬度值，例如 39.9800718。
     * longitude：经度，此时值（Value）为经度值，例如 116.309314。
     * 示例：{"latitude":"39.9800718","longitude":"116.309314"}
     */
    @JsonProperty("extra_params")
    private Map<String, Object> extraParams;

    @JsonProperty("shortcut_command")
    private ShortcutCommand shortcutCommand;

    /**
     * 给自定义参数赋值并传给对话流。
     * 你可以根据实际业务需求，在对话流开始节点的输入参数中设置自定义参数，调用本接口发起对话时，可以通过parameters 参数传入自定义参数的值并传给对话流。示例代码请参见为自定义参数赋值。
     */
    private Map<String, Object> parameters = Map.of();

    /**
     * 设置问答节点返回的内容是否为卡片形式。默认为 false。
     * true：问答节点返回卡片形式的内容。
     * API 渠道暂时不支持直接渲染卡片交互形式。仅在 Chat SDK 中支持呈现智能体的卡片交互。
     * 如果需要实现卡片内容展示，你可以在该 API 响应中获取卡片数据，在 Card SDK 的 runtimeOptions.dsl 中引用 API 返回的卡片 data 字段，通过 Card SDK 进行解析并将卡片数据转换为可视化界面，具体请参见安装并使用 Card SDK。
     * false：问答节点返回普通文本形式的内容。
     */
    @JsonProperty("enable_card")
    private Boolean enableCard;

    /**
     * 智能体的发布状态，用于指定与已发布版本的智能体对话还是和最新草稿版本的智能体对话。默认值为 published_online。枚举值：
     * published_online：已发布的线上版本。
     * unpublished_draft：草稿版本。
     */
    @JsonProperty("publish_status")
    private String publishStatus;

    /**
     * 指定智能体的版本号，用于与历史版本的智能体进行对话。默认与最新版本的智能体对话。
     * 当 publish_status 设置为 unpublished_draft时，填写此参数会提示 4000 错误。
     * 你可以通过查看智能体版本列表 API 查看智能体的版本号。
     */
    @JsonProperty("bot_version")
    private String botVersion;

    private Regenerate regenerate;

    public static CozeChatRequestBuilder builder() {
        CozeChatRequestBuilder requestBuilder =  new CozeChatRequestBuilder();
        requestBuilder.parameters = Map.of();
        return  requestBuilder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalMessage implements Serializable{

        @Serial
        private static final long serialVersionUID = 580170561809894001L;

        /**
         * 发送这条消息的实体。取值：
         * user：代表该条消息内容是用户发送的。
         * assistant：代表该条消息内容是智能体发送的。
         */
        private String role;

        /**
         * 消息类型。默认为 question。
         * question：用户输入内容。
         * answer：智能体返回给用户的消息内容，支持增量返回。如果工作流绑定了输出节点，可能会存在多 answer 场景，此时可以用流式返回的结束标志来判断所有 answer 完成。
         * function_call：智能体对话过程中调用函数（function call）的中间结果。
         * tool_response：调用工具 （function call）后返回的结果。
         * 如果 autoSaveHistory=true，type 支持设置为 question 或 answer。
         * 如果 autoSaveHistory=false，type 支持设置为 question、answer、function_call、tool_output/tool_response。
         * 其中，type=question 只能和 role=user 对应，即仅用户角色可以且只能发起 question 类型的消息。
         */
        private String type;

        /**
         * 消息内容的类型，content 不为空时，此参数为必选。支持设置为：
         * text：文本。
         * object_string：多模态内容，即文本和文件的组合、文本和图片的组合。
         * card：卡片。此枚举值仅在接口响应中出现，不支持作为入参。
         * content 不为空时，此参数为必选。
         */
        @JsonProperty("content_type")
        private String contentType;

        /**
         * 消息的内容，支持纯文本、多模态（文本、图片、文件混合输入）、卡片等多种类型的内容。
         * content_type 为 object_string 时，content 为 object_string object 数组序列化之后的 JSON String，详细说明可参考 object_string object。
         * 当 content_type = text 时，content 为普通文本，例如 "content" :"Hello!"。
         */
        private String content;

        /**
         * 创建消息时的附加消息，查看消息列表时会返回此附加消息。
         * 自定义键值对，应指定为 Map 对象格式。长度为 16 对键值对，其中键（key）的长度范围为 1～64 个字符，值（value）的长度范围为 1～512 个字符。
         */
        @JsonProperty("meta_data")
        private Map<String, Object> metaData;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortcutCommand implements Serializable{

        @Serial
        private static final long serialVersionUID = -2093386196705996567L;
        /**
         * 对话要执行的快捷指令 ID，必须是智能体已绑定的快捷指令。
         * 你可以通过获取智能体配置接口中的ShortcutCommandInfo查看快捷指令 ID
         */
        @JsonProperty("command_id")
        private String commandId;

        /**
         * 用户输入的快捷指令组件参数信息。
         * 自定义键值对，其中键（key）为快捷指令组件的名称，值（value）为组件对应的用户输入，为 object_string object 数组序列化之后的 JSON String，详细说明可参考 object_string object。
         */
        private Map<String, Object> parameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Regenerate implements Serializable{

        @Serial
        private static final long serialVersionUID = -3247704106254738372L;
        
        /**
         * 对于 openapi 来说，这个字段是必传的。但是这里为了兼容老版本，所以设置为可选
         */
        @JsonProperty("chat_id")
        private String chatId;

        @JsonProperty("message_id")
        private String messageId;

        /**
         * 不对外暴露。从扣子站内来的请求，用这个字段区分入口
         */
        @JsonProperty("coze_web_scene")
        private String cozeWebScene;

        /**
         * default (不传也是这个)，extended 扩展模式（包含自动恢复、真实取消、退出重连等机制）
         */
        @JsonProperty("stream_mode")
        private String streamMode;
    }
}
