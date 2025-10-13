package com.itflower.aiplatform.service;

import com.itflower.aiplatform.common.DeleteRequest;
import com.itflower.aiplatform.model.dto.user.UserAddRequest;
import com.itflower.aiplatform.model.dto.user.UserQueryRequest;
import com.itflower.aiplatform.model.dto.user.UserUpdateRequest;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.vo.LoginUserVO;
import com.itflower.aiplatform.model.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author F1ower
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 脱敏用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @param request servletRequest
     */
    void userLogOut(HttpServletRequest request);

    /**
     * User -> LoginUserVO
     *
     * @param user 用户对象
     * @return 脱敏用户对象
     */
    LoginUserVO transferToUserLoginVO(User user);

    /**
     * 获取当前登录用户
     *
     * @param request HTTP请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获得用户 VO 对象
     *
     * @param user user对象
     * @return 用户 VO
     */
    UserVO getUserVO(User user);

    /**
     * 获得用户 VO 对象 List
     *
     * @param userList 用户对象 List
     * @return 用户 VO 对象 List
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 管理员添加用户
     *
     * @param userAddRequest 天剑用户请求
     * @return 添加后用户脱敏信息
     */
    UserVO addUser(UserAddRequest userAddRequest);

    /**
     * 根据 id 删除用户
     *
     * @param deleteRequest 删除请求
     */
    void deleteUserById(DeleteRequest deleteRequest);

    /**
     * 管理员更新用户信息
     *
     * @param userUpdateRequest 用户更新请求
     */
    void updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 根据用户查询请求获取封装查询对象
     *
     * @param userQueryRequest 用户查询请求
     * @return 封装查询对象
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 根据用户查询请求获取封装查询对象
     *
     * @param userQueryRequest 用户查询请求
     * @return 封装查询对象
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 根据用户 id 获取未脱敏对象
     *
     * @param id 用户 id
     * @return 用户对象
     */
    User getUserById(Long id);

    /**
     * 根据用户 id 获取脱敏对象
     *
     * @param id 用户 id
     * @return 用户对象
     */
    UserVO getUserVOById(Long id);
}
