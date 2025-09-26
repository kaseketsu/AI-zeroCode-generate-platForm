package com.itflower.aiplatform.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnums {

    USER("用户", 0),
    ADMIN("管理员", 1);

    private final String name;

    private final int value;

    UserRoleEnums(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据传入参数返回对应枚举类
     *
     * @param value 传入参数
     * @return 枚举对象
     */
    public static UserRoleEnums fromValue(int value) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        for (UserRoleEnums e : UserRoleEnums.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}
