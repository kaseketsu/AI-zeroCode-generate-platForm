package com.itflower.aiplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.itflower.aiplatform.common.DeleteRequest;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.model.dto.user.UserAddRequest;
import com.itflower.aiplatform.model.dto.user.UserQueryRequest;
import com.itflower.aiplatform.model.dto.user.UserUpdateRequest;
import com.itflower.aiplatform.model.enums.UserRoleEnums;
import com.itflower.aiplatform.model.vo.LoginUserVO;
import com.itflower.aiplatform.model.vo.UserVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.mapper.UserMapper;
import com.itflower.aiplatform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.itflower.aiplatform.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author F1ower
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户注册
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "账号或密码为空");
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度过短");
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度过短");
        ThrowUtils.throwIf(!checkPassword.equals(userPassword), ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");

        // 2. 检查是否重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        long res = this.mapper.selectCountByQuery(queryWrapper);
        ThrowUtils.throwIf(res > 0, ErrorCode.PARAMS_ERROR, "账号重复");

        // 3. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 4. 插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserRole(UserRoleEnums.USER.getValue());
        user.setUserName("千早爱音");
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "注册失败, 数据库操作失败");
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1。 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");

        // 2. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 3. 查询用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = this.getOne(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");

        // 4. 记录登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 4. 返回脱敏用户信息
        return transferToUserLoginVO(user);
    }

    /**
     * 用户注销
     * @param request servletRequest
     */
    @Override
    public void userLogOut(HttpServletRequest request) {
        // 移除登录态
       request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 用户id
     */
    @Override
    public LoginUserVO transferToUserLoginVO(User user) {
        return BeanUtil.copyProperties(user, LoginUserVO.class);
    }

    /**
     * 获取登录用户
     *
     * @param request HTTP请求
     * @return 登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1.从 session 获取用户信息
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        // 2. 参数检验
        ThrowUtils.throwIf(ObjUtil.isNull(user) || user.getId() < 0, ErrorCode.NOT_LOGIN_ERROR);

        // 3. 查询数据库确保获取最新信息
        User byId = this.getById(user.getId());
        ThrowUtils.throwIf(byId == null || byId.getId() < 0, ErrorCode.NOT_LOGIN_ERROR);

        // 4. 返回用户信息
        return byId;
    }

    /**
     * 获得用户 VO 对象
     *
     * @param user user对象
     * @return 用户 VO
     */
    @Override
    public UserVO getUserVO(User user) {
        return BeanUtil.copyProperties(user, UserVO.class);
    }

    /**
     * 获得用户 VO 对象 List
     *
     * @param userList 用户对象 List
     * @return 用户 VO 对象 List
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 管理员添加用户
     *
     * @param userAddRequest 天剑用户请求
     * @return 添加后用户脱敏信息
     */
    @Override
    public UserVO addUser(UserAddRequest userAddRequest) {
        // 1.参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(userAddRequest), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjUtil.isNull(userAddRequest.getUserAccount()), ErrorCode.PARAMS_ERROR);

        // 2.密码加密（设置默认参数）
        if (ObjUtil.isNull(userAddRequest.getUserName())) {
            userAddRequest.setUserName("千早爱音");
        }

        String encryptPassword;
        if (ObjUtil.isNull(userAddRequest.getUserPassword())) {
            encryptPassword = getEncryptPassword("12345678");
            userAddRequest.setUserPassword(encryptPassword);
        } else {
            encryptPassword = getEncryptPassword(userAddRequest.getUserPassword());
            userAddRequest.setUserPassword(encryptPassword);
        }

        // 3.查询数据库是否重复
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_account", userAddRequest.getUserAccount());
        User user = this.getOne(wrapper);
        ThrowUtils.throwIf(!ObjUtil.isNull(user), ErrorCode.PARAMS_ERROR, "用户重复添加！");

        // 4.田佳茹数据库
        User addUser = new User();
        BeanUtil.copyProperties(userAddRequest, addUser);
        this.save(addUser);
        return getUserVO(addUser);
    }

    /**
     * 根据 id 删除用户
     *
     * @param deleteRequest 删除请求
     */
    @Override
    public void deleteUserById(DeleteRequest deleteRequest) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(deleteRequest), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(deleteRequest.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 2. 删除用户
        int res = this.mapper.deleteById(deleteRequest.getId());
        ThrowUtils.throwIf(res <= 0, ErrorCode.OPERATION_ERROR, "记录删除失败！");
    }

    /**
     * 管理员更新用户信息
     *
     * @param userUpdateRequest 用户更新请求
     */
    @Override
    public void updateUser(UserUpdateRequest userUpdateRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(userUpdateRequest) || userUpdateRequest.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 2. 查询用户
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("id", userUpdateRequest.getId());
        User user = this.getOne(wrapper);
        ThrowUtils.throwIf(ObjUtil.isNull(user), ErrorCode.NOT_FOUND_ERROR);

        // 3. 更新用户
        User updateUser = new User();
        BeanUtil.copyProperties(userUpdateRequest, updateUser);
        this.updateById(updateUser);
    }

    /**
     * 根据用户查询请求获取封装查询对象
     *
     * @param userQueryRequest 用户查询请求
     * @return 封装查询对象
     */
    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(ObjUtil.isNull(userQueryRequest), ErrorCode.PARAMS_ERROR);

        // 2. 获取参数
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        Integer userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        // 3. 返回查询对象
        return QueryWrapper.create()
                .eq("id", id)
                .eq("user_role", userRole)
                .like("user_account", userAccount)
                .like("user_name", userName)
                .like("user_profile", userProfile)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    /**
     * 根据用户查询请求获取封装查询对象
     *
     * @param userQueryRequest 用户查询请求
     * @return 封装查询对象
     */
    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(userQueryRequest), ErrorCode.PARAMS_ERROR);

        // 2. 获取构造条件
        long current = userQueryRequest.getPageNum();
        long size = userQueryRequest.getPageSize();
        QueryWrapper queryWrapper = getQueryWrapper(userQueryRequest);

        // 3. 分页查询
        Page<User> userPage = this.page(new Page<>(current, size), queryWrapper);

        // 4. 返回脱敏后分页结果
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotalRow());
        List<UserVO> userVOList = getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    /**
     * 根据用户 id 获取未脱敏对象
     *
     * @param id 用户 id
     * @return 用户对象
     */
    @Override
    public User getUserById(Long id) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(id) || id < 0, ErrorCode.PARAMS_ERROR);

        // 2. 查询用户信息
        User user = this.getById(id);

        // 3. 返回用户信息
        return user;
    }

    /**
     * 根据用户 id 获取脱敏对象
     *
     * @param id 用户 id
     * @return 用户对象
     */
    @Override
    public UserVO getUserVOById(Long id) {
        // 1. 校验参数
        ThrowUtils.throwIf(ObjUtil.isNull(id) || id < 0, ErrorCode.PARAMS_ERROR);

        // 2. 查询用户信息
        User user = this.getById(id);

        // 3. 返回脱敏后用户信息
        return BeanUtil.copyProperties(user, UserVO.class);
    }


    /**
     * 返回加密后密码
     *
     * @param password 密码
     * @return 加密后密码
     */
    public String getEncryptPassword(String password) {
        final String salt = "F1ower";
        return DigestUtil.sha256Hex(password + salt);
    }
}
