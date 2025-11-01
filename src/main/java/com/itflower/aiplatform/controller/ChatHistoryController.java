package com.itflower.aiplatform.controller;

import com.itflower.aiplatform.annotation.AuthCheck;
import com.itflower.aiplatform.common.response.BaseResponse;
import com.itflower.aiplatform.common.response.ResultUtils;
import com.itflower.aiplatform.constant.UserConstant;
import com.itflower.aiplatform.model.dto.chatHistory.ChatHistoryQueryRequest;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.service.AppService;
import com.itflower.aiplatform.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.itflower.aiplatform.model.entity.ChatHistory;
import com.itflower.aiplatform.service.ChatHistoryService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author F1ower
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private UserService userService;


    /**
     * 对不同应用游标查询数据
     *
     * @param appId            应用 id
     * @param pageSize         查询条数
     * @param lastGenerateTime 最近一次生成实践
     * @param request          servlet
     * @return 分页查询结果
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(
            @PathVariable Long appId, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) LocalDateTime lastGenerateTime,
            HttpServletRequest request
    ) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> chatHistoryPage = chatHistoryService.listChatHistoryByPage(appId, pageSize, loginUser, lastGenerateTime);
        return ResultUtils.success(chatHistoryPage);
    }

    /**
     * 管理员主动查询
     *
     * @param queryRequest 请求封装
     * @return 历史记录
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/admin/list/chatHistory")
    public BaseResponse<Page<ChatHistory>> listAppChatHistoryAdmin(
            @RequestBody ChatHistoryQueryRequest queryRequest
    ) {
        Page<ChatHistory> chatHistoryPage = chatHistoryService.listChatHistoryByPageAdmin(queryRequest);
        return ResultUtils.success(chatHistoryPage);
    }

}
