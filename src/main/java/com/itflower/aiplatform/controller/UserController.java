package com.itflower.aiplatform.controller;

import cn.hutool.core.util.ObjUtil;
import com.itflower.aiplatform.annotation.AuthCheck;
import com.itflower.aiplatform.common.DeleteRequest;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.common.response.BaseResponse;
import com.itflower.aiplatform.common.response.ResultUtils;
import com.itflower.aiplatform.constant.UserConstant;
import com.itflower.aiplatform.model.dto.user.*;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.vo.LoginUserVO;
import com.itflower.aiplatform.model.vo.UserVO;
import com.itflower.aiplatform.service.UserService;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 用户 控制层。
 *
 * @author F1ower
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;


    /**
     * 用户注册
     *
     * @param registerRequest 用户注册请求
     * @return 用户ID
     */
    @PostMapping("/user/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest) {
        // 1.参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(registerRequest), ErrorCode.PARAMS_ERROR);
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();

        // 2.业务处理
        Long userId = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(userId);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request      HTTP请求
     * @return 脱敏用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        // 1.参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(loginRequest), ErrorCode.PARAMS_ERROR);
        String userAccount = loginRequest.getUserAccount();
        String userName = loginRequest.getUserPassword();

        // 2.业务处理
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userName, request);

        // 3.返回结果
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request servletRequest
     * @return boolean
     */
    @PostMapping("/user/logout")
    public BaseResponse<Boolean> userLogOut(HttpServletRequest request) {
        userService.userLogOut(request);
        return ResultUtils.success(true);
    }

    /**
     * 获取当前登录用户
     *
     * @param request HTTP请求
     * @return 当前登录用户
     */
    @GetMapping("/get/login/user")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        LoginUserVO loginUserVO = userService.transferToUserLoginVO(user);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 添加用户
     *
     * @param userAddRequest 添加用户请求
     * @return 添加的用户
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add/user")
    public BaseResponse<UserVO> addUser(@RequestBody UserAddRequest userAddRequest) {
        UserVO userVO = userService.addUser(userAddRequest);
        return ResultUtils.success(userVO);
    }

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete/user")
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest) {
        userService.deleteUserById(deleteRequest);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 用户更新请求
     * @return 更新后的用户信息
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update/user")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(userUpdateRequest);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户列表
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/user")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 根据用户 id 获取脱敏用户信息
     *
     * @param id 用户 id
     * @return 用户脱敏信息
     */
    @GetMapping("/get/userVO")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        UserVO userVO = userService.getUserVOById(id);
        return ResultUtils.success(userVO);
    }

    /**
     * 根据用户 id 获取用户信息
     *
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/get/user")
    public BaseResponse<User> getUserById(Long id) {
        User user = userService.getUserById(id);
        return ResultUtils.success(user);
    }

}
