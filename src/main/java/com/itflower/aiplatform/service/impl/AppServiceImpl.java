package com.itflower.aiplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.itflower.aiplatform.common.DeleteRequest;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.constant.AppConstant;
import com.itflower.aiplatform.constant.UserConstant;
import com.itflower.aiplatform.core.AiGeneratorServiceFacade;
import com.itflower.aiplatform.core.build.VueProjectBuilder;
import com.itflower.aiplatform.core.handler.StreamExecutor;
import com.itflower.aiplatform.model.dto.app.admin.AppUpdateRequestAdmin;
import com.itflower.aiplatform.model.dto.app.user.AppAddRequest;
import com.itflower.aiplatform.model.dto.app.user.AppDeployRequest;
import com.itflower.aiplatform.model.dto.app.user.AppQueryRequest;
import com.itflower.aiplatform.model.dto.app.user.AppUpdateRequest;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import com.itflower.aiplatform.model.enums.MessageTypeEnum;
import com.itflower.aiplatform.model.vo.AppVO;
import com.itflower.aiplatform.model.vo.UserVO;
import com.itflower.aiplatform.service.ChatHistoryService;
import com.itflower.aiplatform.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.itflower.aiplatform.model.entity.App;
import com.itflower.aiplatform.mapper.AppMapper;
import com.itflower.aiplatform.service.AppService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.awt.font.TextHitInfo;
import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author F1ower
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    UserService userService;

    @Resource
    AiGeneratorServiceFacade aiGeneratorServiceFacade;

    @Resource
    ChatHistoryService chatHistoryService;

    @Resource
    StreamExecutor streamExecutor;

    @Resource
    VueProjectBuilder vueProjectBuilder;

    /**
     * 创建 app
     *
     * @param appAddRequest 创建 app 请求
     * @return app id
     */
    @Override
    public Long createApp(AppAddRequest appAddRequest, HttpServletRequest httpServletRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(appAddRequest), ErrorCode.PARAMS_ERROR);
        String initialPrompt = appAddRequest.getInitialPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initialPrompt), ErrorCode.PARAMS_ERROR, "初始化提示不能为空！");

        // 2. 获取当前用户
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR);

        // 3. 填充 app
        App app = new App();
        app.setUserId(loginUser.getId());
        app.setAppName(initialPrompt.substring(0, Math.min(initialPrompt.length(), 12)));
        app.setInitPrompt(initialPrompt);
        // 暂时硬编码
        app.setCodeGenType(GenTypeEnums.VUE_MULTI.getValue());

        // 4. 存入数据库
        boolean res = this.save(app);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "app 创建失败");
        return app.getId();
    }

    /**
     * 根据 id 更新 app
     *
     * @param appUpdateRequest app 更新请求
     */
    @Override
    public void updateAppById(AppUpdateRequest appUpdateRequest, HttpServletRequest httpServletRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(appUpdateRequest), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(appUpdateRequest.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 2. 获取旧 app
        App oldApp = this.getById(appUpdateRequest.getId());
        ThrowUtils.throwIf(ObjUtil.isNull(oldApp), ErrorCode.NOT_FOUND_ERROR);

        // 3. 校验权限
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(!loginUser.getId().equals(oldApp.getUserId()), ErrorCode.NO_AUTH_ERROR);

        // 4. 填充新 app
        App app = new App();
        app.setId(oldApp.getId());
        app.setEditTime(LocalDateTime.now());
        app.setAppName(appUpdateRequest.getAppName());
        app.setCreateTime(LocalDateTime.now());

        // 5. 放入数据库
        boolean res = this.updateById(app);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "app 修改失败");
    }

    /**
     * 根据 id 删除 app
     *
     * @param deleteRequest 删除请求
     */
    @Override
    public void deleteAppById(DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(deleteRequest), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(deleteRequest.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 2. 校验权限
        App app = this.getById(deleteRequest.getId());
        ThrowUtils.throwIf(ObjUtil.isNull(app), ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(!loginUser.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR);

        // 3. 删除 app
        boolean res = this.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "app 删除失败");
    }

    /**
     * 根据 id 获取 APP 信息
     *
     * @param id app id
     * @return app 脱敏信息
     */
    @Override
    public AppVO getAppDetailById(Long id) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(id < 0, ErrorCode.PARAMS_ERROR);

        // 2. 查询 app
        App app = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(app), ErrorCode.NOT_FOUND_ERROR);

        // 3. 查询创建人信息
        UserVO userVOById = userService.getUserVOById(app.getUserId());
        ThrowUtils.throwIf(ObjUtil.isNull(userVOById), ErrorCode.NOT_FOUND_ERROR);

        // 3. 返回 appVO
        AppVO appVO = this.transferAppToVO(app);
        appVO.setUser(userVOById);
        return appVO;
    }

    /**
     * 将 app 转为 appVO
     *
     * @param app app
     * @return appVO
     */
    @Override
    public AppVO transferAppToVO(App app) {
        return BeanUtil.copyProperties(app, AppVO.class);
    }

    /**
     * 根据查询条件查询自己的 appVO
     *
     * @param appQueryRequest 查询条件
     * @param request         请求
     * @return 自己的 appVO列表
     */
    @Override
    public Page<AppVO> getMyAppDetail(AppQueryRequest appQueryRequest, HttpServletRequest request) {
        // 1. 获取登录用户
        User loginUser = userService.getLoginUser(request);

        // 2. 批量获取 UserId 为loginUserId 的 app
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);
        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "单次最多查询 20 个条目");
        Page<App> page = this.page(Page.of(pageNum, pageSize), queryWrapper);

        // 构造 Page<AppVO>，先批量获取 app 的 userId
        List<App> records = page.getRecords();
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<AppVO> appVOList = getAppVOList(records);
        appVOPage.setRecords(appVOList);
        return appVOPage;
    }

    /**
     * 获取 queryWrapper
     *
     * @param appQueryRequest app 查询请求
     * @return queryWrapper
     */
    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appQueryRequest), ErrorCode.PARAMS_ERROR);
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();

        // 2. 构造 queryWrapper
        return QueryWrapper.create()
                .eq("id", id)
                .eq("userId", userId)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    /**
     * 类型转换
     *
     * @param appList app
     * @return appVO
     */
    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 获取 userID
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        // 批量查询
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        // appList 批量设置userVO
        return appList.stream()
                .map(app -> {
                    Long userId = app.getUserId();
                    UserVO userVO = userVOMap.get(userId);
                    AppVO appVO = transferAppToVO(app);
                    appVO.setUser(userVO);
                    return appVO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 分页查询精选 app 列表
     *
     * @return 精选 app 列表
     */
    @Override
    public Page<AppVO> getGoodAppList(AppQueryRequest appQueryRequest) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appQueryRequest), ErrorCode.PARAMS_ERROR);

        // 获取 wrapper
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);

        // 查询数据
        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "单次查询不能超过 20 条");
        Page<App> page = this.page(Page.of(pageNum, pageSize), queryWrapper);
        List<App> records = page.getRecords();
        List<AppVO> appVOList = getAppVOList(records);
        Page<AppVO> AppVOPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        AppVOPage.setRecords(appVOList);

        // 返回数据
        return AppVOPage;
    }

    /**
     * 管理员更改 app 信息
     *
     * @param appUpdateRequestAdmin app 信息修改请求
     */
    @Override
    public void updateAppByIdAdmin(AppUpdateRequestAdmin appUpdateRequestAdmin) {
        // 参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(appUpdateRequestAdmin), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(appUpdateRequestAdmin.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 获取旧 app
        Long id = appUpdateRequestAdmin.getId();
        String appName = appUpdateRequestAdmin.getAppName();
        Integer priority = appUpdateRequestAdmin.getPriority();
        String cover = appUpdateRequestAdmin.getCover();

        App oldApp = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(oldApp), ErrorCode.NOT_FOUND_ERROR);

        // 填充新 app
        App app = new App();
        BeanUtils.copyProperties(oldApp, app);
        app.setAppName(appName);
        app.setPriority(priority);
        app.setCover(cover);

        // 更新入数据库
        boolean res = this.updateById(app);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "管理员更新 app 失败！");
    }

    /**
     * 管理员任意查看 app
     *
     * @param appQueryRequest app 查询请求
     * @return app 列表
     */
    @Override
    public Page<AppVO> getAppDetailAdmin(AppQueryRequest appQueryRequest) {
        // 批量获取 UserId 为loginUserId 的 app
        QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);
        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();
        Page<App> page = this.page(Page.of(pageNum, pageSize), queryWrapper);

        // 构造 Page<AppVO>，先批量获取 app 的 userId
        List<App> records = page.getRecords();
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<AppVO> appVOList = getAppVOList(records);
        appVOPage.setRecords(appVOList);
        return appVOPage;
    }

    /**
     * 管理员任意删除 app
     *
     * @param deleteRequest 删除请求
     */
    @Override
    public void deleteAppByIdAdmin(DeleteRequest deleteRequest) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(deleteRequest), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(deleteRequest.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 删除 app
        boolean res = this.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "管理员删除 app 失败");
    }


    /**
     * 大模型对话生成 app 应用
     *
     * @param appId              appId
     * @param httpServletRequest request
     * @param message            用户发送消息
     * @return 信息流
     */
    @Override
    public Flux<String> chatToGen(Long appId, HttpServletRequest httpServletRequest, String message) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appId) || appId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空！");
        // 校验权限
        App app = this.getById(appId);
        Long userId = app.getUserId();
        User loginUser = userService.getLoginUser(httpServletRequest);
        Long loginUserId = loginUser.getId();
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NO_AUTH_ERROR);
        ThrowUtils.throwIf(!userId.equals(loginUserId), ErrorCode.NO_AUTH_ERROR);
        // 调用门面类
        String codeGenType = app.getCodeGenType();
        GenTypeEnums enumByValue = GenTypeEnums.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(enumByValue == null, ErrorCode.SYSTEM_ERROR, "不支持的生成类型");
        // 插入对话添加逻辑
        chatHistoryService.addChatHistory(appId, userId, message, MessageTypeEnum.USER_MESSAGE.getValue());
        Flux<String> contentFlux = aiGeneratorServiceFacade.generateAndSaveFileStream(message, enumByValue, appId);
        // 使用流处理器统一处理
        try {
            return streamExecutor.doHandle(contentFlux, appId, loginUser, enumByValue);
        } catch (Exception e) {
            String msg = String.format("对话生成流处理失败，发生在 AppServiceImpl, 原因是: %s", e.getMessage());
            log.error(msg);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, msg);
        }
//        StringBuilder sb = new StringBuilder();
//        return contentFlux.doOnNext(sb::append)
//                .doOnComplete(() -> {
//                    String res = sb.toString();
//                    if (StrUtil.isNotBlank(res)) {
//                        chatHistoryService.addChatHistory(appId, userId, res, MessageTypeEnum.AI_MESSAGE.getValue());
//                    }
//                }).doOnError(error -> {
//                    String errorMessage = "AI 回复失败, " + error.getMessage();
//                    chatHistoryService.addChatHistory(appId, userId, errorMessage, MessageTypeEnum.AI_MESSAGE.getValue());
//                });
    }

    /**
     * 部署 app
     *
     * @param appDeployRequest   app 部署请求
     * @param httpServletRequest request
     * @return app 部署地址
     */
    @Override
    public String deployApp(AppDeployRequest appDeployRequest, HttpServletRequest httpServletRequest) {
        // 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(appDeployRequest), ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(ObjUtil.isNull(appId) || appId <= 0, ErrorCode.PARAMS_ERROR);

        // 校验权限
        App app = this.getById(appId);
        ThrowUtils.throwIf(ObjUtil.isNull(app), ErrorCode.NOT_FOUND_ERROR, "应用不存在！");
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NO_AUTH_ERROR);
        Long loginUserId = loginUser.getId();
        ThrowUtils.throwIf(!loginUserId.equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR);

        // 获取 app 输出地址
        String codeGenType = app.getCodeGenType();
        String outputPath = AppConstant.OUT_PUT_PATH + File.separator + codeGenType + "_" + appId;

        File file = new File(outputPath);
        ThrowUtils.throwIf(!file.exists() || !file.isDirectory(), ErrorCode.NOT_FOUND_ERROR, "应用不存在！");

        // 部署到部署文件夹
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
            app.setDeployKey(deployKey);
        }
        GenTypeEnums typeEnum = GenTypeEnums.getEnumByValue(codeGenType);
        if (typeEnum == GenTypeEnums.VUE_MULTI) {
            if (!VueProjectBuilder.isBuildSuccess(outputPath)) {
                boolean res = vueProjectBuilder.buildProject(new File(outputPath));
                ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "构建 vue 项目失败！");
            }
            file = new File(outputPath, "dist");
            log.info("vue 项目构建成功，地址为: {}", file.getAbsolutePath());
        }
        String deployPath = AppConstant.DEPLOY_PATH + File.separator + deployKey;
        try {
            FileUtil.copyContent(file, new File(deployPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "应用部署失败，" + e.getMessage());
        }

        // 跟新数据库
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        this.updateById(updateApp);

        // 返回访问地址
        return String.format("%s/%s/", AppConstant.HOST_PATH, deployKey);
    }

    /**
     * 重写关联删除应用对话信息
     *
     * @param id id
     * @return bool
     */
    @Override
    public boolean removeById(Serializable id) {
        // 校验参数
        if (ObjUtil.isNull(id)) {
            return false;
        }
        long appId = Long.parseLong(id.toString());
        if (appId <= 0) {
            return false;
        }

        // 删除对话
        try {
            chatHistoryService.removeChatHistory(appId);
        } catch (Exception e) {
            log.error("删除应用关联对话失败, {}", e.getMessage());
        }

        // 删除应用
        return super.removeById(id);
    }
}
