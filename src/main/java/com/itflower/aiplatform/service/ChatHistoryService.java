package com.itflower.aiplatform.service;

import com.itflower.aiplatform.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.itflower.aiplatform.model.entity.ChatHistory;
import com.itflower.aiplatform.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author F1ower
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加历史聊天记录
     *
     * @param appId       应用 id
     * @param userId      用户 id
     * @param message     消息
     * @param messageType 消息类型
     * @return 是否添加成功
     */
    void addChatHistory(
            Long appId, Long userId, String message, String messageType
    );

    /**
     * 删除应用的全部消息记录
     *
     * @param appId 应用 id
     */
    void removeChatHistory(Long appId);

    /**
     * 构建查询条件
     *
     * @param request 请求封装类
     * @return 封装查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest request);

    /**
     * 根据参数游标查询历史聊天
     *
     * @param appId            应用 id
     * @param pageSize         页面大小
     * @param loginUser        登录用户
     * @param lastGenerateTime 最近一次生成实践
     * @return 分页数据
     */
    Page<ChatHistory> listChatHistoryByPage(
            Long appId, int pageSize, User loginUser,
            LocalDateTime lastGenerateTime
    );

    /**
     * 管理员查询
     *
     * @param queryRequest 请求
     * @return 分页数据
     */
    Page<ChatHistory> listChatHistoryByPageAdmin(ChatHistoryQueryRequest queryRequest);

    /**
     * 拉取 app 最新的 maxCount 条记录到 aiService 中
     *
     * @param appId      应用 id
     * @param chatMemory 聊天记录
     * @param maxCount   最大数量
     * @return 加载条数
     */
    int loadChatMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

}
