CREATE TABLE `t_knowledge_base` ( 
`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE, 
`cover_image_path` VARCHAR(255) COMMENT '封面图标', 
`scope_type` SMALLINT COMMENT '权限可见范围:0:私密、1:公开', 
`description_info` TEXT(65535) COMMENT '知识库描述信息', 
`kb_type` SMALLINT COMMENT '知识库类型:0:默认，1：同步外部', 
`indexing_type` SMALLINT COMMENT '索引类型', 
`created_by` BIGINT COMMENT '创建人', 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
`owner` VARCHAR(255) COMMENT '归属，组织、小组、外部关联id比如IM某个群', 
`permission` INT COMMENT '成员内容权限:0:可查看1：可编辑', 
`tenant_id` BIGINT COMMENT '租户id', 
PRIMARY KEY(`id`) 
) COMMENT='知识库表';

CREATE TABLE `t_konwledge_base_outer_mapping` ( 
`id` INT NOT NULL AUTO_INCREMENT UNIQUE, 
`kb_id` BIGINT, 
`out_id` VARCHAR(255) COMMENT '外部关联对象id', 
`out_type` VARCHAR(255) COMMENT '外部系统类型:比如dify、ragflow', 
`extended_info` TEXT(65535) COMMENT '外部额外配置信息', 
`created_by` BIGINT COMMENT '创建人', 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
) COMMENT='知识库外部系统关联';

CREATE TABLE `t_knowledge_base_member` ( 
`id` INT NOT NULL AUTO_INCREMENT UNIQUE, 
`kb_id` BIGINT COMMENT '知识库id', 
`user_id` BIGINT COMMENT '用户id', 
`member_type` SMALLINT COMMENT '成员类型:0：所有者(可管理)、1管理员(可编辑)3、普通成员(仅查看)', 
`created_by` BIGINT COMMENT '创建人', 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
) COMMENT='知识库成员';

CREATE TABLE `t_knowledge_base_category` ( 
`id` BIGINT NOT NULL AUTO_INCREMENT UNIQUE, 
`name` VARCHAR(255), 
`kb_id` BIGINT, 
`created_by` BIGINT, 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
) COMMENT='知识库内分类(分组)信息';

CREATE TABLE `t_knowledge_document` ( 
`id` INT NOT NULL AUTO_INCREMENT UNIQUE, 
`kb_id` BIGINT COMMENT '关联知识库id', 
`category_id` BIGINT COMMENT '文档分类分组信息', 
`title` VARCHAR(255) COMMENT '标题', 
`summary` VARCHAR(255) COMMENT '概述描述内容', 
`content` TEXT(65535) COMMENT '内容，makerdown格式', 
`word_count` INT COMMENT '字数', 
`processing_status` SMALLINT COMMENT '文档处理状态0:未开始,1:完成，2:处理中', 
`doc_status` SMALLINT COMMENT '文档状态可用，0：不可用、1：可用', 
`del_status` SMALLINT COMMENT '删除状态: 0:未删除、1以删除', 
`doc_type` INT COMMENT '文档类型: 0:文本文档类、1、图片 2、音频 3、视频 4、其他未知', 
`doc_suffix` VARCHAR(255) COMMENT '文件后缀', 
`doc_metadata` TEXT(65535) COMMENT '文档其他信息', 
`file_id` BIGINT COMMENT '上传文档关联文件id', 
`preview_info` VARCHAR(255) COMMENT '文档预览信息、预览地址', 
`created_by` BIGINT, 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
) COMMENT='知识文档、知识';


CREATE TABLE `t_knowledge_document_segment` ( 
`id` INT NOT NULL AUTO_INCREMENT UNIQUE, 
`kb_id` BIGINT COMMENT '知识库id', 
`doc_id` BIGINT COMMENT '文档id', 
`content` TEXT(65535) COMMENT '文档切片内容', 
`created_by` BIGINT COMMENT '创建人', 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
) COMMENT='文档片段，文档切片保存';

CREATE TABLE `t_operation_log` ( 
`id` INT NOT NULL AUTO_INCREMENT UNIQUE, 
`title` VARCHAR(255) COMMENT '操作描述', 
`operation_type` SMALLINT COMMENT '操作类型', 
`object_id` SMALLINT COMMENT '操作对象id:如知识库id、知识文档id', 
`object_type` VARCHAR(255) COMMENT '知识库、知识、成员', 
`created_by` BIGINT COMMENT '创建人', 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
);

CREATE TABLE `t_knowledge_file` ( 
`id` INT NOT NULL AUTO_INCREMENT UNIQUE, 
`name` VARCHAR(255) COMMENT '文件名', 
`file_path` VARCHAR(255) COMMENT '存储路径', 
`suffix` VARCHAR(255) COMMENT '后缀名', 
`file_szie` INT COMMENT '文件大小', 
`md5` VARCHAR(255) COMMENT '文件md5值', 
`encryption` SMALLINT COMMENT '文件是否加密', 
`created_by` BIGINT COMMENT '创建人', 
`created_at` DATETIME, 
`updated_by` BIGINT, 
`updated_at` DATETIME, 
PRIMARY KEY(`id`) 
);