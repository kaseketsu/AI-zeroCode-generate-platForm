package com.itflower.aiplatform.model.dto.user;

import com.itflower.aiplatform.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求
 */
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -8722477551196773655L;

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 角色
     */
    private Integer userRole;

}
