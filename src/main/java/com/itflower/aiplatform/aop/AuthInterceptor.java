package com.itflower.aiplatform.aop;

import cn.hutool.core.util.ObjUtil;
import com.itflower.aiplatform.annotation.AuthCheck;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.UserRoleEnums;
import com.itflower.aiplatform.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 权限拦截
     * @param pjp 拦截点
     * @param authCheck 权限注解
     * @return 继续执行
     * @throws Throwable 异常
     */
    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint pjp, AuthCheck authCheck) throws Throwable {
        // 1.获取所需角色权限
        String currentRole = authCheck.mustRole();
        UserRoleEnums mustRoleEnum = UserRoleEnums.fromKey(currentRole);

        // 2.获取当前登录用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        // 3.校验用户角色
        if (ObjUtil.isNull(mustRoleEnum)) {
            return pjp.proceed();
        }
        UserRoleEnums loginUserEnum = UserRoleEnums.fromValue(loginUser.getUserRole());
        ThrowUtils.throwIf(
                ObjUtil.isNull(loginUserEnum), ErrorCode.NO_AUTH_ERROR
        );
        ThrowUtils.throwIf(
                UserRoleEnums.ADMIN.equals(mustRoleEnum) && !UserRoleEnums.ADMIN.equals(loginUserEnum),
                ErrorCode.NO_AUTH_ERROR
        );
        return pjp.proceed();
    }
}
