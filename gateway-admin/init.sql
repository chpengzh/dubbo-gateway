CREATE TABLE api_artifact
(
    `id`               BIGINT(24) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `release_version`  VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '发布版本',
    `api_name`         VARCHAR(255)        NOT NULL DEFAULT 0 COMMENT '接口名称',
    `api_version`      VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '接口版本',
    `dubbo_service`    VARCHAR(512)        NOT NULL DEFAULT '' COMMENT 'Dubbo接口',
    `dubbo_method`     VARCHAR(512)        NOT NULL DEFAULT '' COMMENT 'Dubbo方法名称',
    `dubbo_group`      VARCHAR(255)        NOT NULL DEFAULT '' COMMENT 'Dubbo group信息',
    `dubbo_version`    VARCHAR(255)        NOT NULL DEFAULT '' COMMENT 'Dubbo version信息',
    `params`           TEXT COMMENT '签名快照',
    `approve_id`       VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '审批流id',
    `approve_start`    timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批发起时间',
    `approve_success`  timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批成功时间',
    `approve_user`     VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '审批发起人',
    `approve_result`   TINYINT             NOT NULL DEFAULT 0 COMMENT '审批结果',
    `gmt_create`        timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`       timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_key_release_version (`release_version`),
    KEY idx_api (`api_name`, `api_version`),
    KEY idx_dubbo_service (`dubbo_service`),
    KEY idx_dubbo_method (`dubbo_method`),
    UNIQUE KEY uniq_idx_approve_id (`approve_id`),
    KEY `idx_approve_start` (`approve_start`),
    KEY `idx_approve_success` (`approve_success`),
    KEY `idx_approve_user` (`approve_user`),
    KEY `idx_gmt_create` (`gmt_create`),
    KEY `idx_gmt_modify` (`gmt_modify`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT ='接口版本管理';

CREATE TABLE api_trunk
(
    `id`              BIGINT(24) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `release_version` VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '发布版本',
    `api_name`        VARCHAR(255)        NOT NULL DEFAULT 0 COMMENT '接口名称',
    `api_version`     VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '接口版本',
    `dubbo_service`   VARCHAR(512)        NOT NULL DEFAULT '' COMMENT 'Dubbo接口',
    `dubbo_method`    VARCHAR(512)        NOT NULL DEFAULT '' COMMENT 'Dubbo方法名称',
    `owner`           VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '服务归属',
    `modify_user`     VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '上次审批通过用户',
    `description`     TEXT COMMENT '元数据描述',
    `gmt_create`      timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`      timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_idx_api (`api_name`, `api_version`),
    KEY idx_dubbo_service (`dubbo_service`),
    KEY idx_dubbo_method (`dubbo_method`),
    KEY `idx_modify_user` (`modify_user`),
    KEY `idx_owner` (`owner`),
    KEY `idx_gmt_create` (`gmt_create`),
    KEY `idx_gmt_modify` (`gmt_modify`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT ='接口元数据';

CREATE TABLE approve_process
(
    `id`         BIGINT(24) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `approve_id` VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '审批流id',
    `url`        VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '审批单url',
    `type`       INTEGER             NOT NULL DEFAULT 0 COMMENT '审批单类型:0-接口发布审批',
    `link_id`    VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '关联id',
    `username`   VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '审批发起用户',
    `payload`    TEXT COMMENT '审批提交上下文记录',
    `lock_key`   VARCHAR(255)        NOT NULL DEFAULT '' COMMENT '互斥状态锁',
    `result`     TINYINT             NOT NULL DEFAULT 0 COMMENT '审批结果',
    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_idx_approve_id (`approve_id`),
    KEY idx_url (`url`),
    KEY idx_link_id (`link_id`),
    KEY idx_username (`username`),
    KEY idx_lock_key (`lock_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT ='审批流数据';

CREATE TABLE `api_info`
(
    `id`                  bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `api_name`            varchar(128)        NOT NULL DEFAULT '' COMMENT 'api名称',
    `service_name`        varchar(200)        NOT NULL DEFAULT '' COMMENT 'dubbo服务',
    `service_version`     varchar(30)         NOT NULL DEFAULT '' COMMENT 'dubbo服务版本',
    `service_group`       varchar(256)        NOT NULL DEFAULT '' COMMENT 'Dubbo服务分组',
    `method`              varchar(100)        NOT NULL DEFAULT '' COMMENT 'dubbo方法名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `api` (`api_name`, `api_version`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
    COMMENT ='生产环境接口数据表';

CREATE TABLE `api_param`
(
    `id`                   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `parent_uuid`          varchar(255) NOT NULL DEFAULT '' COMMENT '父级参数uuid',
    `uuid`                 varchar(255) NOT NULL DEFAULT '' COMMENT '参数uuid',
    `api_name`             varchar(128) NOT NULL DEFAULT '' COMMENT 'api名称',
    `source_name`          varchar(64)  NOT NULL DEFAULT '' COMMENT 'api参数名',
    `param_type`           tinyint(4)            DEFAULT '0' COMMENT '参数类型',
    `dest_name`            varchar(50)  NOT NULL DEFAULT '' COMMENT 'dubbo参数名',
    `default_value`        varchar(500)          DEFAULT '' COMMENT '默认值',
    `required`             tinyint(4)            DEFAULT '0' COMMENT '是否必须参数api,0-否,1-是',
    `class_name`           varchar(200)          DEFAULT NULL COMMENT '所属Class',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_idx_uuid` (`uuid`),
    KEY `idx_parent_uuid`(`parent_uuid`),
    KEY `idx_api_name_version` (`api_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
    COMMENT ='生产环境接口参数表';

CREATE TABLE `event_version`
(
    `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `event_key`  varchar(255)        NOT NULL DEFAULT '' COMMENT '事件key',
    `version`    bigint(64)          NOT NULL DEFAULT '0' COMMENT '事件版本号',
    `gmt_create` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_event_version` (`event_key`, `version`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT ='事件版本号锁表';

CREATE TABLE `event_content`
(
    `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
    `event_key`  varchar(255)        NOT NULL DEFAULT '' COMMENT '事件key',
    `version`    bigint(64)          NOT NULL DEFAULT '0' COMMENT '事件版本号',
    `content`    mediumtext COMMENT '事件体文本',
    `gmt_create` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify` timestamp           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_event_version` (`event_key`, `version`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
    COMMENT ='事件版本号表'
