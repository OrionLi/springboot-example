CREATE TABLE `user`
(
    `id`               bigint       NOT NULL COMMENT 'id',
    `username`         varchar(32)  NOT NULL COMMENT '用户名',
    `password`         varchar(255) NOT NULL COMMENT '密码',
    `phone_number`     varchar(32)           DEFAULT NULL COMMENT '电话号码',
    `sex`              char(1)               DEFAULT NULL COMMENT '性别',
    `register_date`    datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '注册时间',
    `last_modify_date` datetime(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_username_uindex` (`username`),
    CHECK (sex = 'M' OR sex = 'F')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;