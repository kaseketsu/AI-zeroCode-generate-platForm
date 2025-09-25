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