package com.itflower.aiplatform.controller;

import cn.hutool.json.JSONUtil;
import com.itflower.aiplatform.annotation.AuthCheck;
import com.itflower.aiplatform.common.DeleteRequest;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.common.response.BaseResponse;
import com.itflower.aiplatform.common.response.ResultUtils;
import com.itflower.aiplatform.constant.AppConstant;
import com.itflower.aiplatform.constant.UserConstant;
import com.itflower.aiplatform.model.dto.app.admin.AppUpdateRequestAdmin;
import com.itflower.aiplatform.model.dto.app.user.AppAddRequest;
import com.itflower.aiplatform.model.dto.app.user.AppDeployRequest;
import com.itflower.aiplatform.model.dto.app.user.AppQueryRequest;
import com.itflower.aiplatform.model.dto.app.user.AppUpdateRequest;
import com.itflower.aiplatform.model.entity.App;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.vo.AppVO;
import com.itflower.aiplatform.service.AppService;
import com.itflower.aiplatform.service.CodeDownloadService;
import com.itflower.aiplatform.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author F1ower
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;
    @Autowired
    private UserService userService;
    @Resource
    private CodeDownloadService codeDownloadService;


    /**
     * 部署 app
     *
     * @param appDeployRequest 部署请求
     * @param request          request
     * @return 部署地址
     */
    @PostMapping("/user/deploy")
    public BaseResponse<String> deploy(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {
        String deployPath = appService.deployApp(appDeployRequest, request);
        return ResultUtils.success(deployPath);
    }

    /**
     * 大模型对话生成文件
     *
     * @param appId              appId
     * @param message            请求
     * @param httpServletRequest request
     * @return 信息流
     */
    @GetMapping(value = "/user/chat/gen", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGen(
            @RequestParam Long appId,
            @RequestParam String message,
            HttpServletRequest httpServletRequest
    ) {
        // 生成信息流
        Flux<String> stringFlux = appService.chatToGen(appId, httpServletRequest, message);
        return stringFlux
                .map(chunck -> {
                    Map<String, String> res = Map.of("d", chunck);
                    String jsonStr = JSONUtil.toJsonStr(res);
                    return ServerSentEvent
                            .<String>builder()
                            .data(jsonStr)
                            .build();
                })
                .concatWith(
                        Mono.just(
                                ServerSentEvent
                                        .<String>builder()
                                        .data("")
                                        .event("done")
                                        .build()
                        )
                );
    }

    /**
     * 创建 app
     *
     * @param appAddRequest 添加 app 请求
     * @param request       request
     * @return id
     */
    @PostMapping("/user/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        Long id = appService.createApp(appAddRequest, request);
        return ResultUtils.success(id);
    }

    /**
     * 根据 id 更新 app
     *
     * @param appUpdateRequest app 更新请求
     * @param request          request
     * @return boolean
     */
    @PostMapping("/user/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        appService.updateAppById(appUpdateRequest, request);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 删除 app
     *
     * @param deleteRequest 删除请求
     * @param request       request
     * @return boolean
     */
    @PostMapping("/user/delete")
    public BaseResponse<Boolean> deleteAppById(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        appService.deleteAppById(deleteRequest, request);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取 app 信息
     *
     * @param id app id
     * @return appVO
     */
    @GetMapping("user/get/id")
    public BaseResponse<AppVO> getAppById(Long id) {
        AppVO appDetailById = appService.getAppDetailById(id);
        return ResultUtils.success(appDetailById);
    }

    /**
     * 获取自己的 app 集合
     *
     * @param appQueryRequest app 请求
     * @param request         request
     * @return 自己的 app 集合
     */
    @PostMapping("user/get/my/app")
    public BaseResponse<Page<AppVO>> getMyAppDetail(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {
        Page<AppVO> myAppDetail = appService.getMyAppDetail(appQueryRequest, request);
        return ResultUtils.success(myAppDetail);
    }

    /**
     * 获取精选 app
     *
     * @param appQueryRequest app 查询请求
     * @return 精选 app
     */
    @PostMapping("user/get/good/app")
    public BaseResponse<Page<AppVO>> getGoodAppDetail(@RequestBody AppQueryRequest appQueryRequest) {
        Page<AppVO> goodAppList = appService.getGoodAppList(appQueryRequest);
        return ResultUtils.success(goodAppList);
    }

    /**
     * 管理员删除 app
     *
     * @param deleteRequest 删除请求
     * @return boolean
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("admin/delete/id")
    public BaseResponse<Boolean> deleteAppByIdAdmin(@RequestBody DeleteRequest deleteRequest) {
        appService.deleteAppByIdAdmin(deleteRequest);
        return ResultUtils.success(true);
    }

    /**
     * 管理员更新 app
     *
     * @param appUpdateRequest app 更新请求
     * @return boolean
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("admin/update")
    public BaseResponse<Boolean> updateAppAdmin(@RequestBody AppUpdateRequestAdmin appUpdateRequest) {
        appService.updateAppByIdAdmin(appUpdateRequest);
        return ResultUtils.success(true);
    }

    /**
     * 获取任意 app 细节
     *
     * @param appQueryRequest app 查询请求
     * @return app 结果
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("admin/get/detail/page")
    public BaseResponse<Page<AppVO>> getAppDetailByPageAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        Page<AppVO> appDetailAdmin = appService.getAppDetailAdmin(appQueryRequest);
        return ResultUtils.success(appDetailAdmin);
    }

    /**
     * 获取任意 app 细节
     *
     * @param id id
     * @return app 结果
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("admin/get/detail/id")
    public BaseResponse<AppVO> getAppDetailByIdAdmin(Long id) {
        AppVO appDetailById = appService.getAppDetailById(id);
        return ResultUtils.success(appDetailById);
    }

    @GetMapping("/download/{appId}")
    public void downloadApp(@PathVariable Long appId, HttpServletResponse response, HttpServletRequest request) {
        // 校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        // 获取 app
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null || app.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // 校验用户权限
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() <= 0, ErrorCode.NO_AUTH_ERROR);
        ThrowUtils.throwIf(!loginUser.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR);
        // 创建路径
        String filePath = AppConstant.OUT_PUT_PATH + File.separator + app.getCodeGenType() + "_" + appId;
        // 检查是否存在
        File file = new File(filePath);
        ThrowUtils.throwIf(!file.exists(), ErrorCode.PARAMS_ERROR, "应用代码不存在，请先创建应用");
        // 执行下载
        codeDownloadService.downloadCodeAsZip(filePath, String.valueOf(appId), response);
    }

}
