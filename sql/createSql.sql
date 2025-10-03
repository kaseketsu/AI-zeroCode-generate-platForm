# 创建数据库表
# 2025/9/25 @author: F1ower
# 创建库
create database if not exists my_db;

# 使用库
use my_db;

# 创建 user 表
create table if not exists user
(
    id            bigint auto_increment              not null primary key comment 'id',
    user_account  varchar(512)                       not null comment '账号',
    user_password varchar(512)                       not null comment '密码',
    user_name     varchar(256)                       null comment '用户名',
    user_avatar   varchar(512)                       null comment '头像',
    user_profile  varchar(512)                       null comment '简介',
    user_role     tinyint  default 0                 not null comment '角色',
    edit_time     datetime default current_timestamp not null comment '修改时间',
    create_time   datetime default current_timestamp not null comment '创建时间',
    update_time   datetime default current_timestamp not null comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    unique key uk_user_account (user_account),
    index idx_user_name (user_name)
) comment '用户' collate = utf8mb4_unicode_ci;


# 删除索引
DROP INDEX uk_user_account ON user;

# 修改 user_account 列的字符集
ALTER TABLE user
    MODIFY user_account VARCHAR(512)
        CHARACTER SET utf8mb4
        COLLATE utf8mb4_bin;

# 重建索引
ALTER TABLE user
    ADD UNIQUE KEY uk_user_account (user_account);


-- 应用表
create table app
(
    id           bigint auto_increment comment 'id' primary key,
    appName      varchar(256)                       null comment '应用名称',
    cover        varchar(512)                       null comment '应用封面',
    initPrompt   text                               null comment '应用初始化的 prompt',
    codeGenType  varchar(64)                        null comment '代码生成类型（枚举）',
    deployKey    varchar(64)                        null comment '部署标识',
    deployedTime datetime                           null comment '部署时间',
    priority     int      default 0                 not null comment '优先级',
    userId       bigint                             not null comment '创建用户id',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_deployKey (deployKey), -- 确保部署标识唯一
    INDEX idx_appName (appName),         -- 提升基于应用名称的查询性能
    INDEX idx_userId (userId)            -- 提升基于用户 ID 的查询性能
) comment '应用' collate = utf8mb4_unicode_ci;




