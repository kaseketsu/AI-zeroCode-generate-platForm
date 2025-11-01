package com.itflower.aiplatform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.itflower.aiplatform.common.PageRequest;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.constant.UserConstant;
import com.itflower.aiplatform.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.MessageTypeEnum;
import com.itflower.aiplatform.model.vo.AppVO;
import com.itflower.aiplatform.model.vo.UserVO;
import com.itflower.aiplatform.service.AppService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.itflower.aiplatform.model.entity.ChatHistory;
import com.itflower.aiplatform.mapper.ChatHistoryMapper;
import com.itflower.aiplatform.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author F1ower
 */
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    @Lazy
    private AppService appService;

    /**
     * 添加历史聊天记录
     *
     * @param appId       应用 id
     * @param userId      用户 id
     * @param message     消息
     * @param messageType 消息类型
     * @return 是否添加成功
     */
    @Override
    public void addChatHistory(Long appId, Long userId, String message, String messageType) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appId) || appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjUtil.isNull(userId) || userId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjUtil.isNull(message), ErrorCode.PARAMS_ERROR);
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.fromValue(messageType);
        ThrowUtils.throwIf(ObjUtil.isNull(messageTypeEnum), ErrorCode.PARAMS_ERROR);

        // 构造 message
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageType)
                .build();

        // 添加到数据库
        this.save(chatHistory);
    }

    /**
     * 删除应用的全部消息记录
     *
     * @param appId 应用 id
     */
    @Override
    public void removeChatHistory(Long appId) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appId) || appId <= 0, ErrorCode.PARAMS_ERROR);

        // 构造 queryWrapper
        QueryWrapper wrapper = QueryWrapper.create()
                .eq("app_id", appId);

        // 删除消息
        this.remove(wrapper);
    }

    /**
     * 构建查询条件
     *
     * @param request 请求封装类
     * @return 封装查询条件
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest request) {
        // 校验参数
        if (ObjUtil.isNull(request)) {
            return new QueryWrapper();
        }

        // 获得参数
        Long appId = request.getAppId();
        String message = request.getMessage();
        String messageType = request.getMessageType();
        Long userId = request.getUserId();
        LocalDateTime lastGenerateTime = request.getLastGenerateTime();
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();

        // 构造参数
        QueryWrapper wrapper = new QueryWrapper()
                .eq("app_id", appId)
                .like("message", message)
                .eq("message_type", messageType)
                .eq("user_id", userId);

        // 特判
        if (ObjUtil.isNotNull(lastGenerateTime)) {
            wrapper.lt("create_time", lastGenerateTime);
        }
        if (StrUtil.isNotBlank(sortField)) {
            wrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            wrapper.orderBy("create_time", false);
        }
        return wrapper;
    }

    /**
     * 根据参数游标查询历史聊天
     *
     * @param appId            应用 id
     * @param pageSize         页面大小
     * @param loginUser        登录用户
     * @param lastGenerateTime 最近一次生成实践
     * @return 分页数据
     */
    @Override
    public Page<ChatHistory> listChatHistoryByPage(Long appId, int pageSize, User loginUser, LocalDateTime lastGenerateTime) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appId) || appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser) || loginUser.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean isAdmin = loginUser.getUserRole() == 1;
        if (!isAdmin) {
            ThrowUtils.throwIf(pageSize < 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "查询页数应该在 0-50 范围内");
        } else {
            ThrowUtils.throwIf(pageSize < 0, ErrorCode.PARAMS_ERROR, "查询数据至少为 0");
        }

        // 校验权限
        AppVO appDetail = appService.getAppDetailById(appId);
        ThrowUtils.throwIf(ObjUtil.isNull(appDetail), ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        UserVO creator = appDetail.getUser();
        ThrowUtils.throwIf(!creator.getId().equals(loginUser.getId()), ErrorCode.PARAMS_ERROR, "无操作权限");

        // 构造查询条件
        ChatHistoryQueryRequest chatHistoryQueryRequest = new ChatHistoryQueryRequest();
        chatHistoryQueryRequest.setAppId(appId);
        chatHistoryQueryRequest.setUserId(loginUser.getId());
        chatHistoryQueryRequest.setLastGenerateTime(lastGenerateTime);
        chatHistoryQueryRequest.setPageSize(pageSize);
        QueryWrapper queryWrapper = getQueryWrapper(chatHistoryQueryRequest);

        // 返回查询
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    /**
     * 管理员查询
     *
     * @param queryRequest 请求
     * @return 分页数据
     */
    @Override
    public Page<ChatHistory> listChatHistoryByPageAdmin(ChatHistoryQueryRequest queryRequest) {
        // 参数校验
        Long appId = queryRequest.getAppId();
        String message = queryRequest.getMessage();
        String messageType = queryRequest.getMessageType();
        Long userId = queryRequest.getUserId();
        LocalDateTime lastGenerateTime = queryRequest.getLastGenerateTime();
        int pageSize = queryRequest.getPageSize();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        AppVO appDetail = appService.getAppDetailById(appId);
        ThrowUtils.throwIf(ObjUtil.isNull(appDetail), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(pageSize < 0, ErrorCode.PARAMS_ERROR, "查询页数不能少于 0");
        if (!ObjUtil.isNull(userId)) {
            ThrowUtils.throwIf(userId <= 0, ErrorCode.PARAMS_ERROR);
        }
        if (!ObjUtil.isNull(messageType)) {
            MessageTypeEnum messageTypeEnum = MessageTypeEnum.fromValue(messageType);
            ThrowUtils.throwIf(ObjUtil.isNull(messageTypeEnum), ErrorCode.PARAMS_ERROR, "不合法的文件类型");
        }

        // 构建查询
        QueryWrapper queryWrapper = getQueryWrapper(queryRequest);

        // 返回结果
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    /**
     * 拉取 app 最新的 maxCount 条记录到 aiService 中
     *
     * @param appId      应用 id
     * @param chatMemory 聊天记录
     * @param maxCount   最大数量
     * @return 加载条数
     */
    @Override
    public int loadChatMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            // 拉取聊天记录
            QueryWrapper wrapper = QueryWrapper.create()
                    .eq("app_id", appId)
                    .orderBy("create_time", false)
                    .limit(1, maxCount);
            List<ChatHistory> histories = this.list(wrapper);
            // 反转记录
            histories.reversed();
            // 加入 chatMemory
            if (CollUtil.isEmpty(histories)) {
                return 0;
            }
            int loadCount = 0;
            for (ChatHistory history : histories) {
                if (MessageTypeEnum.USER_MESSAGE.getValue().equals(history.getMessageType())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                    loadCount++;
                }
                if (MessageTypeEnum.AI_MESSAGE.getValue().equals(history.getMessageType())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                    loadCount++;
                }
            }
            // 返回条数
            log.info("共添加了 {} 条记录", loadCount);
            return loadCount;
        } catch (Exception e) {
            log.error("添加历史记录失败，appId: {}, 原因：{}", appId, e.getMessage());
            return 0;
        }
    }
}


