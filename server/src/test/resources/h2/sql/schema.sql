drop table if exists db_region_code;
create table `db_region_code` (
  `id` bigint(20) unsigned NOT NULL COMMENT '主键id',
  `region_name` varchar(128) DEFAULT NULL COMMENT '名称',
  `code` varchar(255) DEFAULT NULL COMMENT '编号',
  `region_id` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '区域id',
  `comment` varchar(255) DEFAULT NULL COMMENT '备注',
  `level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '层级',
  `max_num` int(4) NOT NULL DEFAULT '0' COMMENT '最大编码',
  `parent_id` bigint(20) unsigned NOT NULL COMMENT '父级id',
  `gmt_modifed` timestamp NULL  COMMENT '更新时间',
  `db_create_time` timestamp NOT NULL DEFAULT '2000-01-01 00:00:00',
  `db_update_time` timestamp NULL COMMENT '更新时间',
  `code_nums` text COMMENT '最大编码',
  PRIMARY KEY (`id`),
  KEY `idx_region_id` (`region_id`),
  KEY `idx_code` (`code`)
) ;