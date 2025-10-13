package com.itflower.aiplatform.service;

import com.itflower.aiplatform.common.DeleteRequest;
import com.itflower.aiplatform.model.dto.app.admin.AppUpdateRequestAdmin;
import com.itflower.aiplatform.model.dto.app.user.AppAddRequest;
import com.itflower.aiplatform.model.dto.app.user.AppDeployRequest;
import com.itflower.aiplatform.model.dto.app.user.AppQueryRequest;
import com.itflower.aiplatform.model.dto.app.user.AppUpdateRequest;
import com.itflower.aiplatform.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.itflower.aiplatform.model.entity.App;
import jakarta.servlet.http.HttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author F1ower
 */
public interface AppService extends IService<App> {

    /**
     * 创建 app
     *
     * @param appAddRequest 创建 app 请求
     * @return app id
     */
    Long createApp(AppAddRequest appAddRequest, HttpServletRequest httpServletRequest);

    /**
     * 根据 id 更新 app
     *
     * @param appUpdateRequest app 更新请求
     * @return 是否成功
     */
    void updateAppById(AppUpdateRequest appUpdateRequest, HttpServletRequest httpServletRequest);

    /**
     * 根据 id 删除 app
     *
     * @param deleteRequest 删除请求
     * @return 是否删除
     */
    void deleteAppById(DeleteRequest deleteRequest, HttpServletRequest httpServletRequest);

    /**
     * 根据 id 获取 APP 信息
     *
     * @param id app id
     * @return app 脱敏信息
     */
    AppVO getAppDetailById(Long id);

    /**
     * 将 app 转为 appVO
     *
     * @param app app
     * @return appVO
     */
    AppVO transferAppToVO(App app);

    /**
     * 根据查询条件查询自己的 appVO
     *
     * @param appQueryRequest 查询条件
     * @param request         请求
     * @return 自己的 appVO列表
     */
    Page<AppVO> getMyAppDetail(AppQueryRequest appQueryRequest, HttpServletRequest request);

    /**
     * 获取 queryWrapper
     *
     * @param appQueryRequest app 查询请求
     * @return queryWrapper
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 类型转换
     *
     * @param appList app
     * @return appVO
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 分页查询精选 app 列表
     *
     * @return 精选 app 列表
     */
    Page<AppVO> getGoodAppList(AppQueryRequest appQueryRequest);

    /**
     * 管理员更改 app 信息
     *
     * @param appUpdateRequestAdmin app 信息修改请求
     */
    void updateAppByIdAdmin(AppUpdateRequestAdmin appUpdateRequestAdmin);

    /**
     * 管理员任意查看 app
     *
     * @param appQueryRequest app 查询请求
     * @return app 列表
     */
    Page<AppVO> getAppDetailAdmin(AppQueryRequest appQueryRequest);

    /**
     * 管理员任意删除 app
     *
     * @param deleteRequest 删除请求
     */
    void deleteAppByIdAdmin(DeleteRequest deleteRequest);

    /**
     * 大模型对话生成 app 应用
     *
     * @param appId              appId
     * @param httpServletRequest request
     * @param message            用户发送消息
     * @return 信息流
     */
    Flux<String> chatToGen(Long appId, HttpServletRequest httpServletRequest, String message);

    /**
     * 部署 app
     *
     * @param appDeployRequest    app 部署请求
     * @param httpServletRequest request
     * @return app 部署地址
     */
    String deployApp(AppDeployRequest appDeployRequest, HttpServletRequest httpServletRequest);
}






