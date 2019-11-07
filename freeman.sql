/*
 Navicat Premium Data Transfer

 Source Server         : docker-mysql5.7
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : freeman

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 04/11/2019 22:20:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CRON_EXPRESSION` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------
INSERT INTO `qrtz_job_details` VALUES ('MyScheduler', 'TASK_1', 'DEFAULT', NULL, 'com.freeman.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B45597372001A636F6D2E667265656D616E2E6A6F622E646F6D61696E2E4A6F62058D52AC1093A3040200084C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C000A6D6574686F644E616D6571007E00094C0009706172616D6574657271007E00094C000672656D61726B71007E00094C000673746174757371007E000978720029636F6D2E667265656D616E2E737072696E672E646174612E646F6D61696E2E42617365456E74697479E8AB980EE471BC510200024C000764656C466C616771007E00094C0006706172616D7371007E000378720024636F6D2E667265656D616E2E737072696E672E646174612E646F6D61696E2E4D6F64656C5F824591B9294E480200014C000C7265706F7369746F7269657371007E000378707371007E00053F4000000000000077080000001000000000787400013070740008746573745461736B737200126A6176612E73716C2E54696D657374616D702618D5C80153BF650200014900056E616E6F737872000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000016D0A0CEBD0780000000074000E302F31202A202A202A202A203F317372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000017400047465737474000F68656C6C6F20776F726C642061616174001BE69C89E58F82E4BBBBE58AA1E8B083E5BAA6E6B58BE8AF95616161740001317800);

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------
INSERT INTO `qrtz_locks` VALUES ('MyScheduler', 'STATE_ACCESS');
INSERT INTO `qrtz_locks` VALUES ('MyScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------
INSERT INTO `qrtz_scheduler_state` VALUES ('MyScheduler', 'lzx-PC1572877019537', 1572877172717, 15000);

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `INT_PROP_1` int(11) NULL DEFAULT NULL,
  `INT_PROP_2` int(11) NULL DEFAULT NULL,
  `LONG_PROP_1` bigint(20) NULL DEFAULT NULL,
  `LONG_PROP_2` bigint(20) NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
  `PRIORITY` int(11) NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_area
-- ----------------------------
DROP TABLE IF EXISTS `sys_area`;
CREATE TABLE `sys_area`  (
  `id` mediumint(7) UNSIGNED NOT NULL AUTO_INCREMENT,
  `level` tinyint(1) UNSIGNED NOT NULL COMMENT '层级',
  `parent_code` bigint(14) UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级行政代码',
  `area_code` bigint(14) UNSIGNED NOT NULL DEFAULT 0 COMMENT '行政代码',
  `zip_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL DEFAULT 000000 COMMENT '邮政编码',
  `city_code` char(6) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '区号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `short_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '简称',
  `merger_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '组合名',
  `pinyin` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '拼音',
  `lng` decimal(10, 6) NOT NULL DEFAULT 0.000000 COMMENT '经度',
  `lat` decimal(10, 6) NOT NULL DEFAULT 0.000000 COMMENT '纬度',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`area_code`) USING BTREE,
  INDEX `idx_parent_code`(`parent_code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '中国行政地区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort_no` int(6) NULL DEFAULT 0 COMMENT '字典排序',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '状态[0:锁定 1:有效]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, '性别', 'sys_sex', 1, 'sfsf', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (2, '显示隐藏', 'sys_show_hide', 1, 'fsdfs', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (3, '启用停用', 'sys_status', 1, 'sdfs', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (4, '是否', 'sys_yes_no', 1, 'sdfsdf', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (8, '任务状态', 'sys_job_status', 1, 'fsdf', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (12, '通知类型', 'sys_notice_type', 1, 'fsdf', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (14, '通知状态', 'sys_notice_status', 1, 'sdfsdffsd', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (16, '机构类型', 'sys_org_type', 1, 'ffsdf', 1, '2018-03-16 11:33:00', 1, '2018-03-16 11:33:00', '0');
INSERT INTO `sys_dict` VALUES (20, '数据范围', 'sys_data_scope', 0, 'sdf', 1, '2019-08-15 16:47:18', 1, '2019-08-15 16:47:23', '0');
INSERT INTO `sys_dict` VALUES (1162242535511429120, '测试1', 'sys_test1', 51, '托尔斯泰1', 1, '2019-08-16 00:59:49', 1, '2019-08-16 01:21:50', '1');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '字典类型',
  `label` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '字典显示文字',
  `value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '字典键值',
  `sort_no` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` bigint(20) NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `status` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '状态[0:停用 1:启用]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '字典选项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES (1, 'sys_sex', '男', '1', 100, 'SSS', 1, '2019-08-04 00:54:48', 1, '2019-08-04 00:54:52', '1');
INSERT INTO `sys_dict_item` VALUES (2, 'sys_sex', '女', '2', 200, 'SSS', 1, '2019-08-04 00:54:48', 1, '2019-08-04 00:54:52', '1');
INSERT INTO `sys_dict_item` VALUES (3, 'sys_sex', '保密', '3', 300, 'F', 1, '2019-08-04 00:54:48', 1, '2019-08-16 09:18:58', '1');
INSERT INTO `sys_dict_item` VALUES (4, 'sys_status', '正常', '1', 100, NULL, 1, '2019-08-04 00:54:48', 1, '2019-08-04 00:54:52', '1');
INSERT INTO `sys_dict_item` VALUES (5, 'sys_status', '禁用', '0', 200, NULL, 1, '2019-08-04 00:54:48', 1, '2019-08-04 00:54:52', '1');
INSERT INTO `sys_dict_item` VALUES (6, 'sys_org_type', '公司', '1', 100, NULL, 1, NULL, 1, NULL, '1');
INSERT INTO `sys_dict_item` VALUES (7, 'sys_org_type', '部门', '2', 200, NULL, 1, NULL, 1, NULL, '1');
INSERT INTO `sys_dict_item` VALUES (51, 'sys_data_scope', '全部', '0', 100, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (52, 'sys_data_scope', '自定义', '1', 200, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (53, 'sys_data_scope', '本公司及以下', '2', 300, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (54, 'sys_data_scope', '本公司', '3', 400, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (55, 'sys_data_scope', '本部门及以下', '4', 500, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (56, 'sys_data_scope', '本部门', '5', 600, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (57, 'sys_data_scope', '仅本人', '6', 700, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');
INSERT INTO `sys_dict_item` VALUES (58, 'sys_data_scope', '无', '7', 800, '数据范围', 1, '2019-08-15 16:49:08', 1, '2019-08-15 16:49:12', '1');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint(20) NOT NULL COMMENT '日志ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作用户',
  `os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `browser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `browser_version` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '浏览器版本',
  `ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作者IP',
  `location` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作地点',
  `operation` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作内容',
  `uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `method` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作方法',
  `parameter` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '方法参数',
  `time` decimal(11, 0) NULL DEFAULT NULL COMMENT '耗时',
  `error_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------
INSERT INTO `sys_log` VALUES (1169920560520630272, 2, 'admin', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567765772122\"]}', 38, NULL, '2019-09-06 05:29:32');
INSERT INTO `sys_log` VALUES (1169922665981546496, 1, 'system', 'Linux', 'Firefox', '65.0', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567766274112\"]}', 35, NULL, '2019-09-06 05:37:54');
INSERT INTO `sys_log` VALUES (1169949433975148544, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567772656156\"]}', 40, NULL, '2019-09-06 07:24:16');
INSERT INTO `sys_log` VALUES (1169951386612404224, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.addJob()', '{\"cronExpression\":[\"0/1 * * * * ?\"],\"beanName\":[\"com.freeman.job.util.ScheduleJob\"],\"methodName\":[\"executeInternal\"],\"remark\":[\"hahah\"]}', 41, NULL, '2019-09-06 07:32:02');
INSERT INTO `sys_log` VALUES (1169957554395156480, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/11', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567774592331\"]}', 9, NULL, '2019-09-06 07:56:32');
INSERT INTO `sys_log` VALUES (1169957805675909120, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567774651789\"]}', 81, NULL, '2019-09-06 07:57:32');
INSERT INTO `sys_log` VALUES (1169958757451567104, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.updateJob()', '{\"beanName\":[\"testTask\"],\"cronExpression\":[\"0/1 * * * * ?1\"],\"methodName\":[\"test\"],\"remark\":[\"有参任务调度测试\"],\"jobId\":[\"1\"],\"status\":[\"1\"]}', 136, NULL, '2019-09-06 08:01:19');
INSERT INTO `sys_log` VALUES (1169959095294365696, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567774959335\"]}', 64, NULL, '2019-09-06 08:02:40');
INSERT INTO `sys_log` VALUES (1169959142371233792, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567774970911\"]}', 21, NULL, '2019-09-06 08:02:51');
INSERT INTO `sys_log` VALUES (1169959200516870144, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567774984792\"]}', 8, NULL, '2019-09-06 08:03:05');
INSERT INTO `sys_log` VALUES (1169963494523670528, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567776008489\"]}', 43, NULL, '2019-09-06 08:20:09');
INSERT INTO `sys_log` VALUES (1169963506020257792, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567776011289\"]}', 20, NULL, '2019-09-06 08:20:11');
INSERT INTO `sys_log` VALUES (1169963533262262272, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567776017801\"]}', 10, NULL, '2019-09-06 08:20:18');
INSERT INTO `sys_log` VALUES (1169964817562341376, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567776323971\"]}', 18, NULL, '2019-09-06 08:25:24');
INSERT INTO `sys_log` VALUES (1169964824776544256, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567776325723\"]}', 12, NULL, '2019-09-06 08:25:26');
INSERT INTO `sys_log` VALUES (1169964831923638272, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567776327435\"]}', 10, NULL, '2019-09-06 08:25:27');
INSERT INTO `sys_log` VALUES (1169964991118446592, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567776365388\"]}', 10, NULL, '2019-09-06 08:26:05');
INSERT INTO `sys_log` VALUES (1169964999779684352, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567776367453\"]}', 10, NULL, '2019-09-06 08:26:08');
INSERT INTO `sys_log` VALUES (1169965006213746688, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567776368981\"]}', 15, NULL, '2019-09-06 08:26:09');
INSERT INTO `sys_log` VALUES (1169965497115086848, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567776486025\"]}', 10, NULL, '2019-09-06 08:28:06');
INSERT INTO `sys_log` VALUES (1169965504769691648, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567776487849\"]}', 14, NULL, '2019-09-06 08:28:08');
INSERT INTO `sys_log` VALUES (1169965513414152192, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567776489913\"]}', 10, NULL, '2019-09-06 08:28:10');
INSERT INTO `sys_log` VALUES (1169965542929469440, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567776496953\"]}', 9, NULL, '2019-09-06 08:28:17');
INSERT INTO `sys_log` VALUES (1169965553650110464, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567776499497\"]}', 10, NULL, '2019-09-06 08:28:20');
INSERT INTO `sys_log` VALUES (1169965562617532416, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567776501649\"]}', 7, NULL, '2019-09-06 08:28:22');
INSERT INTO `sys_log` VALUES (1169965778880040960, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567776553203\"]}', 11, NULL, '2019-09-06 08:29:13');
INSERT INTO `sys_log` VALUES (1169965881892147200, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567776577771\"]}', 6, NULL, '2019-09-06 08:29:38');
INSERT INTO `sys_log` VALUES (1169989966667845632, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567782320019\"]}', 11, NULL, '2019-09-06 10:05:20');
INSERT INTO `sys_log` VALUES (1169990401080299520, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567782423598\"]}', 6, NULL, '2019-09-06 10:07:04');
INSERT INTO `sys_log` VALUES (1169990643787894784, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/run/1', 'com.freeman.job.controller.JobController.runJob()', '{\"_t\":[\"1567782481464\"]}', 8, NULL, '2019-09-06 10:08:02');
INSERT INTO `sys_log` VALUES (1169991167404806144, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567782606300\"]}', 11, NULL, '2019-09-06 10:10:06');
INSERT INTO `sys_log` VALUES (1169991180038049792, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567782609316\"]}', 7, NULL, '2019-09-06 10:10:09');
INSERT INTO `sys_log` VALUES (1169991220248842240, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/resume/1', 'com.freeman.job.controller.JobController.resumeJob()', '{\"_t\":[\"1567782618900\"]}', 12, NULL, '2019-09-06 10:10:19');
INSERT INTO `sys_log` VALUES (1169991255875260416, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job/pause/1', 'com.freeman.job.controller.JobController.pauseJob()', '{\"_t\":[\"1567782627396\"]}', 9, NULL, '2019-09-06 10:10:27');
INSERT INTO `sys_log` VALUES (1169998701855379456, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.updateJob()', '{\"beanName\":[\"testTask\"],\"cronExpression\":[\"0/1 * * * * ?1\"],\"methodName\":[\"test\"],\"parameter\":[\"hello world a\"],\"remark\":[\"有参任务调度测试\"],\"jobId\":[\"1\"],\"status\":[\"1\"]}', 86259, NULL, '2019-09-06 10:39:59');
INSERT INTO `sys_log` VALUES (1169999365637541888, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.updateJob()', '{\"beanName\":[\"testTask\"],\"cronExpression\":[\"0/1 * * * * ?1\"],\"methodName\":[\"test\"],\"parameter\":[\"hello world abc\"],\"remark\":[\"有参任务调度测试abc\"],\"jobId\":[\"1\"],\"status\":[\"1\"]}', 70387, NULL, '2019-09-06 10:42:40');
INSERT INTO `sys_log` VALUES (1169999755565207552, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.updateJob()', '{\"beanName\":[\"testTask\"],\"cronExpression\":[\"0/1 * * * * ?1\"],\"methodName\":[\"test\"],\"parameter\":[\"hello world abccc\"],\"remark\":[\"有参任务调度测试abccc\"],\"jobId\":[\"1\"],\"status\":[\"1\"]}', 68407, NULL, '2019-09-06 10:44:14');
INSERT INTO `sys_log` VALUES (1170000684750344192, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.updateJob()', '{\"beanName\":[\"testTask\"],\"cronExpression\":[\"0/1 * * * * ?1\"],\"methodName\":[\"test\"],\"parameter\":[\"hello world ccc\"],\"remark\":[\"有参任务调度测试ccc\"],\"jobId\":[\"1\"],\"status\":[\"1\"]}', 25152, NULL, '2019-09-06 10:47:53');
INSERT INTO `sys_log` VALUES (1170000933954916352, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '', '/job', 'com.freeman.job.controller.JobController.updateJob()', '{\"beanName\":[\"testTask\"],\"cronExpression\":[\"0/1 * * * * ?1\"],\"methodName\":[\"test\"],\"parameter\":[\"hello world aaa\"],\"remark\":[\"有参任务调度测试aaa\"],\"jobId\":[\"1\"],\"status\":[\"1\"]}', 31769, NULL, '2019-09-06 10:48:50');
INSERT INTO `sys_log` VALUES (1170157002844934144, 2, 'admin', 'Linux', 'Firefox', '65.0', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567822144382\"]}', 38, NULL, '2019-09-06 21:09:05');
INSERT INTO `sys_log` VALUES (1170628448000020480, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567934545612\"]}', 50, NULL, '2019-09-08 04:22:26');
INSERT INTO `sys_log` VALUES (1170633745917677568, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567935808541\"]}', 178, NULL, '2019-09-08 04:43:29');
INSERT INTO `sys_log` VALUES (1170636017527558144, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567936350167\"]}', 117, NULL, '2019-09-08 04:52:31');
INSERT INTO `sys_log` VALUES (1170636509217427456, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567936467556\"]}', 94, NULL, '2019-09-08 04:54:28');
INSERT INTO `sys_log` VALUES (1170636845931958272, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '127.0.0.1', '', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567936547959\"]}', 41, NULL, '2019-09-08 04:55:48');
INSERT INTO `sys_log` VALUES (1170654558872014848, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '172.17.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567940770662\"]}', 123, NULL, '2019-09-08 06:06:11');
INSERT INTO `sys_log` VALUES (1170654618678595584, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '172.17.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567940785153\"]}', 81, NULL, '2019-09-08 06:06:25');
INSERT INTO `sys_log` VALUES (1170669374479142912, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '172.17.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567944303007\"]}', 176, NULL, '2019-09-08 07:05:04');
INSERT INTO `sys_log` VALUES (1170670865440968704, 1, 'system', 'Android', 'Chrome', '73.0.3683.86', '172.17.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1567944658777\"]}', 47, NULL, '2019-09-08 07:10:59');
INSERT INTO `sys_log` VALUES (1171041718271873024, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1568032991534\"]}', 12898, NULL, '2019-09-09 07:44:37');
INSERT INTO `sys_log` VALUES (1191350508376428544, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1572875046856\"]}', 14723, NULL, '2019-11-04 07:44:30');
INSERT INTO `sys_log` VALUES (1191351603119132672, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1572875330666\"]}', 51, NULL, '2019-11-04 07:48:51');
INSERT INTO `sys_log` VALUES (1191351737143922688, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1572875362947\"]}', 17, NULL, '2019-11-04 07:49:23');
INSERT INTO `sys_log` VALUES (1191353604120907776, 1, 'system', 'Linux', 'Chrome', '73.0.3683.86', '127.0.0.1', '0|0|0|内网IP|内网IP', '查看用户', '/sys/user', 'com.freeman.sys.controller.SysUserController.userList()', '{\"pageSize\":[\"10\"],\"pageNum\":[\"1\"],\"_t\":[\"1572875807757\"]}', 51, NULL, '2019-11-04 07:56:48');

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `login_time` datetime(0) NOT NULL COMMENT '登录时间',
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录地点',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '登录日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO `sys_login_log` VALUES (1169904594671767552, 1, 'system', '2019-09-06 04:25:42', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169907609294213120, 2, 'admin', '2019-09-06 04:38:05', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169909446881710080, 2, 'admin', '2019-09-06 04:45:23', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169910121493565440, 1, 'system', '2019-09-06 04:48:04', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169910283590832128, 2, 'admin', '2019-09-06 04:48:42', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169910894742867968, 2, 'admin', '2019-09-06 04:51:08', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169911245374099456, 1, 'system', '2019-09-06 04:52:31', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169911744093622272, 2, 'admin', '2019-09-06 04:54:30', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169913371206750208, 2, 'admin', '2019-09-06 05:00:58', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169916024791896064, 2, 'admin', '2019-09-06 05:11:31', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169917607042748416, 2, 'admin', '2019-09-06 05:17:48', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169919251788730368, 2, 'admin', '2019-09-06 05:24:20', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169919778312294400, 2, 'admin', '2019-09-06 05:26:26', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169920354290896896, 1, 'system', '2019-09-06 05:28:43', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169920483118944256, 2, 'admin', '2019-09-06 05:29:14', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169921940127879168, 2, 'admin', '2019-09-06 05:35:01', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169922370471858176, 1, 'system', '2019-09-06 05:36:44', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169924229513220096, 1, 'system', '2019-09-06 05:44:07', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169931694497730560, 1, 'system', '2019-09-06 06:13:47', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169932653189795840, 1, 'system', '2019-09-06 06:17:35', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169933381144809472, 1, 'system', '2019-09-06 06:20:29', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169934455696134144, 1, 'system', '2019-09-06 06:24:45', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169935024515059712, 2, 'admin', '2019-09-06 06:27:01', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169940068085927936, 1, 'system', '2019-09-06 06:47:03', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169944266206744576, 2, 'admin', '2019-09-06 07:03:44', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169944397245190144, 2, 'admin', '2019-09-06 07:04:15', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169947741003059200, 2, 'admin', '2019-09-06 07:17:33', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169947913317650432, 2, 'admin', '2019-09-06 07:18:14', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169948014446514176, 2, 'admin', '2019-09-06 07:18:38', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169948053394821120, 2, 'admin', '2019-09-06 07:18:47', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169948558326108160, 2, 'admin', '2019-09-06 07:20:48', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169948791558770688, 2, 'admin', '2019-09-06 07:21:43', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169949213044379648, 1, 'system', '2019-09-06 07:23:24', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169949877636042752, 1, 'system', '2019-09-06 07:26:02', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169949944174481408, 1, 'system', '2019-09-06 07:26:18', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169957172898041856, 1, 'system', '2019-09-06 07:55:01', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169989891094876160, 1, 'system', '2019-09-06 10:05:02', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1169998220315725824, 1, 'system', '2019-09-06 10:37:37', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170156737651675136, 1, 'system', '2019-09-06 21:08:01', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170156950080589824, 2, 'admin', '2019-09-06 21:08:52', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170268188345241600, 1, 'system', '2019-09-07 04:30:53', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170269956802220032, 1, 'system', '2019-09-07 04:37:55', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170275293554937856, 1, 'system', '2019-09-07 04:59:07', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170529010015408128, 1, 'system', '2019-09-07 21:47:18', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170530957296537600, 1, 'system', '2019-09-07 21:55:02', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170554058356953088, 1, 'system', '2019-09-07 23:26:50', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170628405348143104, 1, 'system', '2019-09-08 04:22:16', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1170654528203264000, 1, 'system', '2019-09-08 06:06:04', '0|0|0|内网IP|内网IP', '172.17.0.1');
INSERT INTO `sys_login_log` VALUES (1170705158213079040, 1, 'system', '2019-09-08 09:27:15', '0|0|0|内网IP|内网IP', '172.17.0.1');
INSERT INTO `sys_login_log` VALUES (1171041094792777728, 1, 'system', '2019-09-09 07:42:09', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1171750477055004672, 1, 'system', '2019-09-11 06:40:58', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1190123193654972416, 1, 'system', '2019-10-31 23:27:35', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1190124369024782336, 1, 'system', '2019-10-31 23:32:16', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1190125286717526016, 1, 'system', '2019-10-31 23:35:54', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1190126345938669568, 1, 'system', '2019-10-31 23:40:07', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1190127635615846400, 1, 'system', '2019-10-31 23:45:14', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191275199014768640, 1, 'system', '2019-11-04 02:45:15', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191281127885770752, 1, 'system', '2019-11-04 03:08:48', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191329918374514688, 1, 'system', '2019-11-04 06:22:41', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191331361177341952, 1, 'system', '2019-11-04 06:28:25', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191332019880202240, 1, 'system', '2019-11-04 06:31:02', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191336238242729984, 1, 'system', '2019-11-04 06:47:48', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191347968834408448, 1, 'system', '2019-11-04 07:34:25', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191348728666132480, 1, 'system', '2019-11-04 07:37:26', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191350391267266560, 1, 'system', '2019-11-04 07:44:02', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191354664755531776, 1, 'system', '2019-11-04 08:01:01', '0|0|0|内网IP|内网IP', '127.0.0.1');
INSERT INTO `sys_login_log` VALUES (1191357414260215808, 1, 'system', '2019-11-04 08:11:57', '0|0|0|内网IP|内网IP', '127.0.0.1');

-- ----------------------------
-- Table structure for sys_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT 'ID',
  `parent_id` bigint(16) UNSIGNED NOT NULL COMMENT '上级机构ID',
  `type` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '机构类型[1:公司,2:部门,3:子部门]',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '公司/部门名称',
  `code` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司/部门编码',
  `sort_no` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '排序',
  `phone` char(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `address` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` bigint(16) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` bigint(20) UNSIGNED NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `status` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态[0:不启用,1:启用]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '组织机构' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_org
-- ----------------------------
INSERT INTO `sys_org` VALUES (1, 0, '1', '能力有限公司', '', 1, NULL, NULL, NULL, 1, '2017-01-04 12:42:04', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (2, 1, '2', '研发一部', '', 1, NULL, NULL, NULL, 1, '2017-01-04 12:42:04', 1, '2019-01-18 00:59:37', '1');
INSERT INTO `sys_org` VALUES (3, 1, '2', '技术二部', '', 2, NULL, NULL, NULL, 1, '2017-01-04 12:42:04', 1, '2019-01-05 14:09:39', '1');
INSERT INTO `sys_org` VALUES (4, 1, '2', '市场部', '', 3, NULL, NULL, NULL, 1, '2017-01-04 12:42:04', 1, '2019-01-23 06:27:56', '1');
INSERT INTO `sys_org` VALUES (5, 1, '2', '人事部', '', 4, NULL, NULL, NULL, 1, '2017-01-04 12:42:04', 1, '2019-01-23 06:27:59', '1');
INSERT INTO `sys_org` VALUES (6, 1, '2', '测试部', '', 5, NULL, NULL, NULL, 1, '2017-01-04 12:42:04', 1, '2019-01-17 08:15:47', '1');
INSERT INTO `sys_org` VALUES (7, 1, '1', '才华出众公司', '', 2, NULL, NULL, NULL, 1, '2018-07-04 22:12:26', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (8, 0, '1', '名额有限公司', '', 3, NULL, NULL, NULL, 1, '2019-10-04 23:32:36', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (9, 7, '2', '商务部', '', 1, NULL, NULL, NULL, 1, '2018-07-04 22:12:26', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (10, 7, '2', '销售部', '', 2, NULL, NULL, NULL, 1, '2018-07-04 22:12:26', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (11, 10, '2', '售前咨询部', '', 1, NULL, NULL, NULL, 1, '2018-07-04 22:12:26', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (12, 10, '2', '售后客服部', '', 2, NULL, NULL, NULL, 1, '2018-07-04 22:12:26', 1, '2019-01-05 21:08:27', '1');
INSERT INTO `sys_org` VALUES (1161552448226201600, 8, '2', 'aaaa', NULL, 10, NULL, NULL, NULL, 1, '2019-08-14 03:17:39', 1, '2019-08-14 03:46:43', '1');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '菜单/按钮ID',
  `parent_id` bigint(20) UNSIGNED NOT NULL COMMENT '上级菜单ID',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单/按钮名称',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对应路由path',
  `component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对应路由组件component',
  `perms` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识(英文逗号拼接)',
  `icon` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `type` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型[1:菜单,2:按钮]',
  `sort_no` int(11) UNSIGNED NULL DEFAULT NULL COMMENT '排序',
  `hide` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否显示在菜单中(比如个人中心,修改头像,修改密码,...这类的通用菜单,不需要显示在菜单中)',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_by` bigint(20) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) UNSIGNED NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单/按钮' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 0, '系统管理', '/system', 'PageView', NULL, 'appstore-o', '1', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (2, 0, '系统监控', '/monitor', 'PageView', NULL, 'dashboard', '1', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (3, 1, '用户管理', '/system/user', 'system/user/UserList', 'user:view', '', '1', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (4, 1, '角色管理', '/system/role', 'system/role/RoleList', 'role:view', '', '1', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (5, 1, '菜单管理', '/system/permission', 'system/permission/PermissionList', 'menu:view', '', '1', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (6, 1, '部门管理', '/system/org', 'system/org/OrgList', 'org:view', '', '1', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (8, 2, '在线用户', '/monitor/online', 'monitor/Online', 'user:online', '', '1', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (10, 2, '系统日志', '/monitor/systemlog', 'monitor/SystemLog', 'log:view', '', '1', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (11, 3, '新增用户', '', '', 'user:add', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (12, 3, '修改用户', '', '', 'user:update', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (13, 3, '删除用户', '', '', 'user:delete', NULL, '2', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (14, 4, '新增角色', '', '', 'role:add', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (15, 4, '修改角色', '', '', 'role:update', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (16, 4, '删除角色', '', '', 'role:delete', NULL, '2', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (17, 5, '新增菜单', '', '', 'menu:add', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (18, 5, '修改菜单', '', '', 'menu:update', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (19, 5, '删除菜单', '', '', 'menu:delete', NULL, '2', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (20, 6, '新增部门', '', '', 'org:add', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (21, 6, '修改部门', '', '', 'org:update', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (22, 6, '删除部门', '', '', 'org:delete', NULL, '2', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (23, 8, '踢出用户', '', '', 'user:kickout', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (24, 10, '删除日志', '', '', 'log:delete', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (64, 1, '字典管理', '/system/dict', 'system/dict/DictList', 'dict:view', '', '1', 5, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (65, 64, '新增字典', '', '', 'dict:add', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (66, 64, '修改字典', '', '', 'dict:update', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (67, 64, '删除字典', '', '', 'dict:delete', NULL, '2', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (101, 0, '任务调度', '/job', 'PageView', NULL, 'clock-circle-o', '1', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (102, 101, '定时任务', '/job/job', 'quartz/job/Job', 'job:view', '', '1', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (103, 102, '新增任务', '', '', 'job:add', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (104, 102, '修改任务', '', '', 'job:update', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (105, 102, '删除任务', '', '', 'job:delete', NULL, '2', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (106, 102, '暂停任务', '', '', 'job:pause', NULL, '2', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (107, 102, '恢复任务', '', '', 'job:resume', NULL, '2', 5, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (108, 102, '立即执行任务', '', '', 'job:run', NULL, '2', 6, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (109, 101, '调度日志', '/job/log', 'quartz/log/JobLog', 'jobLog:view', '', '1', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (110, 109, '删除日志', '', '', 'jobLog:delete', NULL, '2', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (113, 2, 'Redis监控', '/monitor/redis/info', 'monitor/RedisInfo', 'redis:view', '', '1', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (121, 2, '请求追踪', '/monitor/httptrace', 'monitor/Httptrace', NULL, NULL, '1', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (122, 2, '系统信息', '/monitor/system', 'EmptyPageView', NULL, NULL, '1', 5, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (123, 122, 'Tomcat信息', '/monitor/system/tomcatinfo', 'monitor/TomcatInfo', NULL, NULL, '1', 1, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (124, 122, 'JVM信息', '/monitor/system/jvminfo', 'monitor/JvmInfo', NULL, NULL, '1', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (127, 122, '服务器信息', '/monitor/system/info', 'monitor/SystemInfo', NULL, NULL, '1', 3, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (128, 0, '其他模块', '/others', 'PageView', NULL, 'coffee', '1', 5, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (129, 128, '导入导出', '/others/excel', 'others/Excel', NULL, NULL, '1', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (130, 3, '导出Excel', NULL, NULL, 'user:export', NULL, '2', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (131, 4, '导出Excel', NULL, NULL, 'role:export', NULL, '2', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (132, 5, '导出Excel', NULL, NULL, 'menu:export', NULL, '2', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (133, 6, '导出Excel', NULL, NULL, 'org:export', NULL, '2', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (134, 64, '导出Excel', NULL, NULL, 'dict:export', NULL, '2', 4, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (135, 3, '密码重置', NULL, NULL, 'user:reset', NULL, '2', 5, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (136, 10, '导出Excel', NULL, NULL, 'log:export', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (137, 102, '导出Excel', NULL, NULL, 'job:export', NULL, '2', 7, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');
INSERT INTO `sys_permission` VALUES (138, 109, '导出Excel', NULL, NULL, 'jobLog:export', NULL, '2', 2, NULL, NULL, 0, '2019-08-12 10:35:36', 0, '2019-08-12 10:35:36');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL COMMENT '角色ID',
  `parent_id` bigint(20) NULL DEFAULT NULL COMMENT '父角色ID',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `code` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编码',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `sort_no` int(11) NULL DEFAULT 1000 COMMENT '排序',
  `status` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '数据状态(0:禁用,１: 启用)',
  `create_by` bigint(16) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` bigint(20) UNSIGNED NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `data_scope` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据范围',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, NULL, '管理员', '', '管理员', 1000, '1', 1, '2019-06-10 16:48:08', 1, '2019-06-15 16:48:08', NULL);
INSERT INTO `sys_role` VALUES (2, NULL, '注册用户', '', '可查看，新增，导出', 1000, '1', 1, '2019-06-15 16:48:08', 1, '2019-06-15 16:48:08', '2');
INSERT INTO `sys_role` VALUES (72, NULL, '普通用户', '', '只可查看，好可怜哦', 1000, '1', 1, '2019-06-20 16:48:08', 1, '2019-06-15 16:48:08', NULL);

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色可查看的部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (2, 100);
INSERT INTO `sys_role_dept` VALUES (2, 101);
INSERT INTO `sys_role_dept` VALUES (2, 105);

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '菜单/按钮ID',
  `data_rule_ids` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对应的规则IDS'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色对应的菜单/按钮' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 3, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 11, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 12, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 13, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 4, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 14, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 15, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 16, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 5, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 17, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 18, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 19, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 6, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 20, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 21, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 22, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 64, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 65, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 66, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 67, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 2, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 8, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 23, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 10, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 24, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 113, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 121, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 122, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 124, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 123, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 125, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 101, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 102, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 103, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 104, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 105, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 106, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 107, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 108, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 109, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 110, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 58, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 59, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 61, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 81, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 82, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 83, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 127, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 128, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 129, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 130, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 135, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 131, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 132, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 133, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 134, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 136, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 137, NULL);
INSERT INTO `sys_role_permission` VALUES (1, 138, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 1, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 3, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 4, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 5, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 6, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 64, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 2, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 8, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 10, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 113, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 121, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 122, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 124, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 123, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 127, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 101, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 102, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 109, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 58, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 59, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 61, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 81, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 82, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 83, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 128, NULL);
INSERT INTO `sys_role_permission` VALUES (72, 129, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 3, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 1, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 4, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 5, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 6, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 64, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 2, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 8, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 10, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 113, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 121, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 122, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 124, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 123, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 125, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 101, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 102, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 109, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 58, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 59, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 61, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 81, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 82, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 83, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 127, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 128, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 129, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 130, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 14, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 17, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 132, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 20, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 133, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 65, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 134, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 136, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 103, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 137, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 138, NULL);
INSERT INTO `sys_role_permission` VALUES (2, 131, NULL);
INSERT INTO `sys_role_permission` VALUES (1161918364717813760, 128, NULL);
INSERT INTO `sys_role_permission` VALUES (1161929536980520960, 109, NULL);
INSERT INTO `sys_role_permission` VALUES (1161929536980520960, 138, NULL);
INSERT INTO `sys_role_permission` VALUES (1161929536980520960, 110, NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL COMMENT '用户ID',
  `dept_id` bigint(20) UNSIGNED NULL DEFAULT NULL COMMENT '所在部门ID',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `realname` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `nickname` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `code` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户编号',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `salt` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'md5密码盐值',
  `avatar` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `sex` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别[0:男,1:女,2:保密]',
  `age` int(11) NULL DEFAULT NULL,
  `birthday` datetime(0) NULL DEFAULT NULL COMMENT '生日',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` char(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `description` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最近登录时间',
  `create_by` bigint(20) UNSIGNED NOT NULL COMMENT '创建人',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_by` bigint(20) UNSIGNED NOT NULL COMMENT '修改人',
  `update_time` datetime(0) NOT NULL COMMENT '修改时间',
  `status` char(5) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态[0:锁定,1:正常]',
  `qqid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'QQopenid',
  `wxid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信openid',
  `wbid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微博openid',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 0, 'system', NULL, NULL, '', 'e295f9d10483a522cbc07879408ec14c', '2da00a9b5d49ad9f', 'avatar.jpg', '2', 31, NULL, 'hao123@gmail.com', '13455533233', '喵喵～', '2019-11-04 08:11:57', 1, '2017-07-09 15:47:19', 1, '2019-08-08 10:46:42', '1', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 2, 'admin', NULL, NULL, '', 'af6de4fabc184c0c37944d0f3d185ea1', '31fc4012f2370528', 'jZUIxmJycoymBprLOUbT.png', '1', 22, NULL, 'admin@qq.com', '15134627380', 'Who am i,tell me', '2019-09-06 21:08:52', 1, '2017-12-29 16:16:39', 1, '2019-01-18 00:59:09', '1', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (12, 2, 'manager', NULL, NULL, '', 'df9ab6549dcf1b43708a4f8f187f0cf8', '31fc4012f2370528', 'default.jpg', '2', 113, NULL, 'jim@foxmail.com', '13721212121', 'how instesting', '2019-07-24 08:52:03', 1, '2018-12-10 07:34:05', 1, '2019-08-11 08:33:40', '1', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (14, 12, 'guest', NULL, NULL, '', '552649f10640385d0728a80a4242893e', '1', 'default.jpg', '1', 145, NULL, 'jackma@hotmail.com', NULL, 'amazing', '2019-07-24 08:52:03', 1, '2018-12-18 07:36:08', 1, '2019-01-24 03:08:01', '1', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES (1158308878627246080, 6, 'test', NULL, NULL, NULL, '27a1b05d31108b6045df13395e299481', '31fc4012f2370528', 'default.jpg', '2', NULL, NULL, 'test@163.com', '17051215487', NULL, NULL, 1, '2019-08-05 04:28:52', 1, '2019-08-05 04:28:52', '1', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_config`;
CREATE TABLE `sys_user_config`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `theme` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统主题 dark暗色风格，light明亮风格',
  `layout` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统布局 side侧边栏，head顶部栏',
  `multi_page` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面风格 1多标签页 0单页',
  `fix_siderbar` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面滚动是否固定侧边栏 1固定 0不固定',
  `fix_header` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面滚动是否固定顶栏 1固定 0不固定',
  `color` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主题颜色 RGB值',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户个性化主题配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_config
-- ----------------------------
INSERT INTO `sys_user_config` VALUES (1, 'light', 'side', '1', '1', '1', 'rgb(24, 144, 255)');
INSERT INTO `sys_user_config` VALUES (2, 'light', 'side', '0', '0', '0', 'rgb(24, 144, 255)');
INSERT INTO `sys_user_config` VALUES (12, 'dark', 'side', '1', '1', '1', 'rgb(66, 185, 131)');
INSERT INTO `sys_user_config` VALUES (1158308884423774208, 'dark', 'side', '0', '1', '1', 'rgb(66, 185, 131)');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (1158308878627246080, 1);
INSERT INTO `sys_user_role` VALUES (1158308878627246080, 2);
INSERT INTO `sys_user_role` VALUES (1158308878627246080, 72);
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (1, 2);
INSERT INTO `sys_user_role` VALUES (12, 1);
INSERT INTO `sys_user_role` VALUES (12, 2);
INSERT INTO `sys_user_role` VALUES (12, 72);

-- ----------------------------
-- Table structure for t_job
-- ----------------------------
DROP TABLE IF EXISTS `t_job`;
CREATE TABLE `t_job`  (
  `job_id` bigint(20) NOT NULL COMMENT '任务id',
  `bean_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'spring bean名称',
  `method_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',
  `parameter` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'cron表达式',
  `status` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_job_log
-- ----------------------------
DROP TABLE IF EXISTS `t_job_log`;
CREATE TABLE `t_job_log`  (
  `log_id` bigint(20) NOT NULL COMMENT '任务日志id',
  `job_id` bigint(20) NOT NULL COMMENT '任务id',
  `bean_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'spring bean名称',
  `method_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',
  `parameter` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
  `status` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '失败信息',
  `times` decimal(11, 0) NULL DEFAULT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for test_address
-- ----------------------------
DROP TABLE IF EXISTS `test_address`;
CREATE TABLE `test_address`  (
  `id` bigint(32) NOT NULL,
  `user_id` bigint(32) NULL DEFAULT NULL,
  `label` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址标签（家、公司）',
  `country` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区县',
  `street` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '街道',
  `detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_address
-- ----------------------------
INSERT INTO `test_address` VALUES (1000, 101, 'label', 'china', 'hedong', 'jimo', 'moji', 'jiojio', 'dd');
INSERT INTO `test_address` VALUES (1001, 101, 'label', 'china1', 'hedong1', 'jimo1', 'moji1', 'jiojio1', 'dd1');

-- ----------------------------
-- Table structure for test_id_card
-- ----------------------------
DROP TABLE IF EXISTS `test_id_card`;
CREATE TABLE `test_id_card`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `number` varchar(52) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_id_card
-- ----------------------------
INSERT INTO `test_id_card` VALUES (1, 101, '123456');
INSERT INTO `test_id_card` VALUES (2, 102, '456123');

-- ----------------------------
-- Table structure for test_phone
-- ----------------------------
DROP TABLE IF EXISTS `test_phone`;
CREATE TABLE `test_phone`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `number` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `brand` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_phone
-- ----------------------------
INSERT INTO `test_phone` VALUES (1, 101, '139000000000', 'iPhone');
INSERT INTO `test_phone` VALUES (2, 101, '180000000000', 'HuaWei');

-- ----------------------------
-- Table structure for test_user
-- ----------------------------
DROP TABLE IF EXISTS `test_user`;
CREATE TABLE `test_user`  (
  `id` bigint(32) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_card_id` bigint(20) NULL DEFAULT NULL,
  `username` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `nickname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `age` int(3) NULL DEFAULT NULL COMMENT '年龄',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `sign` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `email` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `wx_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '微信ID',
  `assets` decimal(64, 4) NULL DEFAULT NULL COMMENT '资产',
  `lock` tinyint(1) NULL DEFAULT 0 COMMENT '锁定账户',
  `version` int(20) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `country` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '国籍/地区',
  `nation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '民族',
  `blood_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '血型',
  `education` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学历水平',
  `native_place` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '籍贯',
  `marital_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '婚姻状况',
  `religious_belief` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宗教信仰',
  `intro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
  `create_by` bigint(32) UNSIGNED NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(32) UNSIGNED NULL DEFAULT NULL COMMENT '最近修改的人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改的时间',
  `istatus` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态(）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test_user
-- ----------------------------
INSERT INTO `test_user` VALUES (101, NULL, 'Jack', 1, '霸王', '大家一起来', '2738wsdfs9fas9', 'http://phone.baidu.com/ninainaigetuier.jpg', 123, NULL, NULL, '2019-05-01', 'hao123@163.com', '18051015487', '23456ugfd', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 10086, '2019-05-02 10:12:01', 10086, '2019-05-02 10:12:01', NULL);
INSERT INTO `test_user` VALUES (102, NULL, 'Rose', 2, '急急急吧', '一起来嗨吧~', '2738wsdfs9fas9', 'http://phone.baidu.com/ninainaigetuier.jpg', 123, NULL, NULL, '2019-04-30', 'hao123@163.com', '18051015487', '23456ugfd', NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 10086, '2019-05-02 10:13:46', 10086, '2019-05-02 11:01:37', NULL);

-- ----------------------------
-- Function structure for fnCommpanyIdByOrgId
-- ----------------------------
DROP FUNCTION IF EXISTS `fnCommpanyIdByOrgId`;
delimiter ;;
CREATE FUNCTION `fnCommpanyIdByOrgId`(oid BIGINT)
 RETURNS bigint(20)
BEGIN
   -- 根据机构ID,然后循环查询机构、判断机构类型是"1" 说明是公司，返回机构ID (如果 传入用户ID为空 or 获取不到部门 or 部门查不到 是"1"的上级 ==> f返回 0)
		DECLARE orgId BIGINT DEFAULT NULL;
		DECLARE orgType CHAR;
		DECLARE resultid BIGINT DEFAULT 0;

		IF oid is not null and oid <> 0 THEN
		 		REPEAT
						SELECT so.id,so.type,so.parent_id INTO orgId,orgType,oid FROM sys_org so WHERE so.id = oid;
						IF orgType = '1' THEN
							SET resultid = orgId;
						END IF;						
				UNTIL (orgType = '1' OR orgId = 0) END REPEAT;
		 END IF;

		RETURN resultid;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for fnCommpanyIdByUid
-- ----------------------------
DROP FUNCTION IF EXISTS `fnCommpanyIdByUid`;
delimiter ;;
CREATE FUNCTION `fnCommpanyIdByUid`(uid BIGINT)
 RETURNS bigint(20)
BEGIN
   -- 根据用户ID获取部门ID,然后循环查询机构、判断机构类型是"1" 说明是公司，返回机构ID (如果 传入用户ID为空 or 获取不到部门 or 部门查不到 是"1"的上级 ==> f返回 0)
		DECLARE oid BIGINT DEFAULT (SELECT su.dept_id FROM sys_user su WHERE su.id = uid);
		DECLARE orgId BIGINT DEFAULT NULL;
		DECLARE orgType CHAR;
		DECLARE resultid BIGINT DEFAULT 0;

		IF oid is not null and oid <>0 THEN
		 		REPEAT
						SELECT so.id,so.type,so.parent_id INTO orgId,orgType,oid FROM sys_org so WHERE so.id = oid;
						IF orgType = '1' THEN
							SET resultid = orgId;
						END IF;						
				UNTIL (orgType = '1' OR orgId = 0) END REPEAT;
		 END IF;

		RETURN resultid;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for fnDeptIdByUid
-- ----------------------------
DROP FUNCTION IF EXISTS `fnDeptIdByUid`;
delimiter ;;
CREATE FUNCTION `fnDeptIdByUid`(uid BIGINT)
 RETURNS bigint(20)
BEGIN
	#根据数据表的createBy(创建数据的用户ID), 获取对应的 用户部门ID
	DECLARE deptid BIGINT DEFAULT NULL;
	IF(uid is not null and uid <> 0) THEN
	    SELECT su.dept_id INTO deptid FROM sys_user su WHERE su.id = uid;
			RETURN deptid;
	END IF;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for fnOrgAncestor
-- ----------------------------
DROP FUNCTION IF EXISTS `fnOrgAncestor`;
delimiter ;;
CREATE FUNCTION `fnOrgAncestor`(oid BIGINT)
 RETURNS varchar(4000) CHARSET utf8
BEGIN
		#根据机构ID获取对应的 组织机构祖先IDS集合+传入的机构ID (0,xxx,xxx,xxx,用户部门ID)
    DECLARE str VARCHAR(4000) DEFAULT oid;
    DECLARE fid BIGINT(20) DEFAULT NULL;
    WHILE oid is not null and oid != 0 DO
        -- 给定义的变量赋值 可以用SET或INTO
        #与下一句等效SET fid=(SELECT  so.parent_id FROM sys_org so WHERE so.id=id);
        SELECT so.parent_id INTO fid FROM sys_org so WHERE so.id =  oid;
				IF(fid is not null and fid>=0) THEN
            SET str = CONCAT( fid, ',' ,str);
            SET oid = fid;
				END IF;
		END WHILE;
		RETURN str;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for fnOrgAncestorByUid
-- ----------------------------
DROP FUNCTION IF EXISTS `fnOrgAncestorByUid`;
delimiter ;;
CREATE FUNCTION `fnOrgAncestorByUid`(uid BIGINT)
 RETURNS varchar(4000) CHARSET utf8
BEGIN
	#根据数据表的createBy(创建数据的用户ID), 获取对应的 组织机构祖先IDS集合+传入用户的部门ID (0,xxx,xxx,xxx,用户部门ID)
	DECLARE str VARCHAR(4000) DEFAULT '';
	DECLARE deptid BIGINT DEFAULT NULL;
	IF(uid is not null and uid <> 0) THEN
	    SELECT dept_id INTO deptid FROM sys_user su WHERE su.id = uid;
			SELECT fnOrgAncestor(deptid) INTO str FROM DUAL;
	END IF;
  RETURN str;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for fnOrgChildren
-- ----------------------------
DROP FUNCTION IF EXISTS `fnOrgChildren`;
delimiter ;;
CREATE FUNCTION `fnOrgChildren`(oid BIGINT)
 RETURNS text CHARSET utf8
BEGIN
	#根据父节点ID获取所有的子孙节点IDS集合,不包括oid自己
	DECLARE str text DEFAULT NULL;
	DECLARE cids text DEFAULT oid;
	WHILE cids is not null DO
	    SELECT GROUP_CONCAT(so.id) INTO cids from sys_org so WHERE FIND_IN_SET(so.parent_id,cids)>0;
			IF(str is not null and cids is not null) THEN
		      SET str = CONCAT(str,',',cids);
			ELSEIF(cids is not null) THEN
			    SET str = cids;
	    END IF;
	END WHILE;
  RETURN str;
END
;;
delimiter ;

-- ----------------------------
-- Function structure for fnPermsChildren
-- ----------------------------
DROP FUNCTION IF EXISTS `fnPermsChildren`;
delimiter ;;
CREATE FUNCTION `fnPermsChildren`(id BIGINT)
 RETURNS text CHARSET utf8
BEGIN
	#根据父节点ID获取所有的子孙节点IDS集合,不包括id自己
	DECLARE str text DEFAULT NULL;
	DECLARE cids text DEFAULT id;
	WHILE cids is not null DO
	    SELECT GROUP_CONCAT(sp.`id`) INTO cids from sys_permission sp WHERE FIND_IN_SET(sp.parent_id,cids)>0;
			IF(str is not null and cids is not null) THEN
		      SET str = CONCAT(str,',',cids);
			ELSEIF(cids is not null) THEN
			    SET str = cids;
	    END IF;
	END WHILE;
  RETURN str;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for prOrgAncestor
-- ----------------------------
DROP PROCEDURE IF EXISTS `prOrgAncestor`;
delimiter ;;
CREATE PROCEDURE `prOrgAncestor`(INOUT id BIGINT,OUT parentId BIGINT,OUT name VARCHAR(64),OUT type VARCHAR(64))
BEGIN
  -- sql语句 查找所有父级和自己
  SELECT @fid AS id,(SELECT @fid := sot.parent_id FROM sys_org  sot WHERE sot.`id` = @fid) AS parentId,so.`name` AS name,so.`type` AS type FROM sys_org so,(SELECT @fid:=id) initval WHERE @fid <> 0;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for prOrgChildren
-- ----------------------------
DROP PROCEDURE IF EXISTS `prOrgChildren`;
delimiter ;;
CREATE PROCEDURE `prOrgChildren`(INOUT id BIGINT,OUT parentId BIGINT,OUT name VARCHAR(64),INOUT type VARCHAR(64))
BEGIN
    -- sql语句 查找所有子级（不包含本身） type 机构类型 0:不限, 1:公司, 2:部门
		
			IF(type =0) THEN
		          SELECT so.`id` as id,so.parent_id as parentId,so.`name` as name,so.`type` as type FROM sys_org so, ( SELECT @pv := id ) initialization
		          WHERE FIND_IN_SET( so.parent_id, @pv )> 0  AND LENGTH(@pv := concat( @pv, ',', so.`id` )) ORDER BY so.sort_no;
			ELSE
			    		SELECT so.`id` as id,so.parent_id as parentId,so.`name` as name,so.`type` as type FROM (SELECT * FROM sys_org sot WHERE sot.`type` = type ) so, ( SELECT @pv := id ) initialization
							WHERE FIND_IN_SET( so.parent_id, @pv )> 0  AND LENGTH(@pv := concat( @pv, ',', so.`id` )) ORDER BY so.sort_no;
	    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
