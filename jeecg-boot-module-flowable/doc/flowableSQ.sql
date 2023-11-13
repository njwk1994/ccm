/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : flow

 Target Server Type    : SQL Server
 Target Server Version : 15000000
 File Encoding         : 65001

 Date: 03/03/2022 18:30:15
*/


-- ----------------------------
-- Table structure for act_evt_log
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_evt_log]') AND type IN ('U'))
	DROP TABLE [dbo].[act_evt_log]
GO

CREATE TABLE [dbo].[act_evt_log] (
  [LOG_NR_] bigint NOT NULL,
  [TYPE_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [TIME_STAMP_] datetime2 NOT NULL,
  [USER_ID_] nvarchar(255) NULL,
  [DATA_] varbinary(max) NULL,
  [LOCK_OWNER_] nvarchar(255) NULL,
  [LOCK_TIME_] datetime2 NULL,
  [IS_PROCESSED_] tinyint NULL
)
GO


-- ----------------------------
-- Records of act_evt_log
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ge_bytearray
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ge_bytearray]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ge_bytearray]
GO

CREATE TABLE [dbo].[act_ge_bytearray] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [NAME_] nvarchar(255) NULL,
  [DEPLOYMENT_ID_] nvarchar(64) NULL,
  [BYTES_] varbinary(max) NULL,
  [GENERATED_] tinyint NULL
)
GO


-- ----------------------------
-- Records of act_ge_bytearray
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ge_property
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ge_property]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ge_property]
GO

CREATE TABLE [dbo].[act_ge_property] (
  [NAME_] nvarchar(64) NOT NULL,
  [VALUE_] nvarchar(300) NULL,
  [REV_] int NULL
)
GO


-- ----------------------------
-- Records of act_ge_property
-- ----------------------------
BEGIN TRANSACTION
GO

INSERT INTO [dbo].[act_ge_property] ([NAME_], [VALUE_], [REV_]) VALUES (N'batch.schema.version', N'6.5.0.6', N'1'), (N'cfg.execution-related-entities-count', N'true', N'1'), (N'cfg.task-related-entities-count', N'true', N'1'), (N'common.schema.version', N'6.5.0.6', N'1'), (N'entitylink.schema.version', N'6.5.0.6', N'1'), (N'eventsubscription.schema.version', N'6.5.0.6', N'1'), (N'identitylink.schema.version', N'6.5.0.6', N'1'), (N'job.schema.version', N'6.5.0.6', N'1'), (N'next.dbid', N'1', N'1'), (N'schema.history', N'create(6.5.0.6)', N'1'), (N'schema.version', N'6.5.0.6', N'1'), (N'task.schema.version', N'6.5.0.6', N'1'), (N'variable.schema.version', N'6.5.0.6', N'1')
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_actinst
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_actinst]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_actinst]
GO

CREATE TABLE [dbo].[act_hi_actinst] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [PROC_DEF_ID_] nvarchar(64) NOT NULL,
  [PROC_INST_ID_] nvarchar(64) NOT NULL,
  [EXECUTION_ID_] nvarchar(64) NOT NULL,
  [ACT_ID_] nvarchar(255) NOT NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [CALL_PROC_INST_ID_] nvarchar(64) NULL,
  [ACT_NAME_] nvarchar(255) NULL,
  [ACT_TYPE_] nvarchar(255) NOT NULL,
  [ASSIGNEE_] nvarchar(255) NULL,
  [START_TIME_] datetime2 NOT NULL,
  [END_TIME_] datetime2 NULL,
  [DURATION_] bigint NULL,
  [DELETE_REASON_] nvarchar(4000) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_hi_actinst
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_attachment
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_attachment]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_attachment]
GO

CREATE TABLE [dbo].[act_hi_attachment] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [USER_ID_] nvarchar(255) NULL,
  [NAME_] nvarchar(255) NULL,
  [DESCRIPTION_] nvarchar(4000) NULL,
  [TYPE_] nvarchar(255) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [URL_] nvarchar(4000) NULL,
  [CONTENT_ID_] nvarchar(64) NULL,
  [TIME_] datetime2 NULL
)
GO


-- ----------------------------
-- Records of act_hi_attachment
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_comment
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_comment]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_comment]
GO

CREATE TABLE [dbo].[act_hi_comment] (
  [ID_] nvarchar(64) NOT NULL,
  [TYPE_] nvarchar(255) NULL,
  [TIME_] datetime2 NOT NULL,
  [USER_ID_] nvarchar(255) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [ACTION_] nvarchar(255) NULL,
  [MESSAGE_] nvarchar(4000) NULL,
  [FULL_MSG_] varbinary(max) NULL
)
GO


-- ----------------------------
-- Records of act_hi_comment
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_detail
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_detail]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_detail]
GO

CREATE TABLE [dbo].[act_hi_detail] (
  [ID_] nvarchar(64) NOT NULL,
  [TYPE_] nvarchar(255) NOT NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [ACT_INST_ID_] nvarchar(64) NULL,
  [NAME_] nvarchar(255) NOT NULL,
  [VAR_TYPE_] nvarchar(255) NULL,
  [REV_] int NULL,
  [TIME_] datetime2 NOT NULL,
  [BYTEARRAY_ID_] nvarchar(64) NULL,
  [DOUBLE_] float NULL,
  [LONG_] bigint NULL,
  [TEXT_] nvarchar(4000) NULL,
  [TEXT2_] nvarchar(4000) NULL
)
GO


-- ----------------------------
-- Records of act_hi_detail
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_entitylink
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_entitylink]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_entitylink]
GO

CREATE TABLE [dbo].[act_hi_entitylink] (
  [ID_] nvarchar(64) NOT NULL,
  [LINK_TYPE_] nvarchar(255) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [REF_SCOPE_ID_] nvarchar(255) NULL,
  [REF_SCOPE_TYPE_] nvarchar(255) NULL,
  [REF_SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [HIERARCHY_TYPE_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_hi_entitylink
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_identitylink
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_identitylink]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_identitylink]
GO

CREATE TABLE [dbo].[act_hi_identitylink] (
  [ID_] nvarchar(64) NOT NULL,
  [GROUP_ID_] nvarchar(255) NULL,
  [TYPE_] nvarchar(255) NULL,
  [USER_ID_] nvarchar(255) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_hi_identitylink
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_procinst
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_procinst]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_procinst]
GO

CREATE TABLE [dbo].[act_hi_procinst] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [PROC_INST_ID_] nvarchar(64) NOT NULL,
  [BUSINESS_KEY_] nvarchar(255) NULL,
  [PROC_DEF_ID_] nvarchar(64) NOT NULL,
  [START_TIME_] datetime2 NOT NULL,
  [END_TIME_] datetime2 NULL,
  [DURATION_] bigint NULL,
  [START_USER_ID_] nvarchar(255) NULL,
  [START_ACT_ID_] nvarchar(255) NULL,
  [END_ACT_ID_] nvarchar(255) NULL,
  [SUPER_PROCESS_INSTANCE_ID_] nvarchar(64) NULL,
  [DELETE_REASON_] nvarchar(4000) NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [NAME_] nvarchar(255) NULL,
  [CALLBACK_ID_] nvarchar(255) NULL,
  [CALLBACK_TYPE_] nvarchar(255) NULL,
  [REFERENCE_ID_] nvarchar(255) NULL,
  [REFERENCE_TYPE_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_hi_procinst
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_taskinst
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_taskinst]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_taskinst]
GO

CREATE TABLE [dbo].[act_hi_taskinst] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [TASK_DEF_ID_] nvarchar(64) NULL,
  [TASK_DEF_KEY_] nvarchar(255) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [PROPAGATED_STAGE_INST_ID_] nvarchar(255) NULL,
  [NAME_] nvarchar(255) NULL,
  [PARENT_TASK_ID_] nvarchar(64) NULL,
  [DESCRIPTION_] nvarchar(4000) NULL,
  [OWNER_] nvarchar(255) NULL,
  [ASSIGNEE_] nvarchar(255) NULL,
  [START_TIME_] datetime2 NOT NULL,
  [CLAIM_TIME_] datetime2 NULL,
  [END_TIME_] datetime2 NULL,
  [DURATION_] bigint NULL,
  [DELETE_REASON_] nvarchar(4000) NULL,
  [PRIORITY_] int NULL,
  [DUE_DATE_] datetime2 NULL,
  [FORM_KEY_] nvarchar(255) NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [LAST_UPDATED_TIME_] datetime2 NULL
)
GO


-- ----------------------------
-- Records of act_hi_taskinst
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_tsk_log
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_tsk_log]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_tsk_log]
GO

CREATE TABLE [dbo].[act_hi_tsk_log] (
  [ID_] bigint NOT NULL,
  [TYPE_] nvarchar(64) NULL,
  [TASK_ID_] nvarchar(64) NOT NULL,
  [TIME_STAMP_] datetime2 NOT NULL,
  [USER_ID_] nvarchar(255) NULL,
  [DATA_] nvarchar(4000) NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_hi_tsk_log
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_hi_varinst
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_hi_varinst]') AND type IN ('U'))
	DROP TABLE [dbo].[act_hi_varinst]
GO

CREATE TABLE [dbo].[act_hi_varinst] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [NAME_] nvarchar(255) NOT NULL,
  [VAR_TYPE_] nvarchar(100) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [BYTEARRAY_ID_] nvarchar(64) NULL,
  [DOUBLE_] float NULL,
  [LONG_] bigint NULL,
  [TEXT_] nvarchar(4000) NULL,
  [TEXT2_] nvarchar(4000) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [LAST_UPDATED_TIME_] datetime2 NULL
)
GO


-- ----------------------------
-- Records of act_hi_varinst
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_bytearray
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_bytearray]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_bytearray]
GO

CREATE TABLE [dbo].[act_id_bytearray] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [NAME_] nvarchar(255) NULL,
  [BYTES_] varbinary(max) NULL
)
GO


-- ----------------------------
-- Records of act_id_bytearray
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_group
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_group]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_group]
GO

CREATE TABLE [dbo].[act_id_group] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [NAME_] nvarchar(255) NULL,
  [TYPE_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_id_group
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_info
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_info]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_info]
GO

CREATE TABLE [dbo].[act_id_info] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [USER_ID_] nvarchar(64) NULL,
  [TYPE_] nvarchar(64) NULL,
  [KEY_] nvarchar(255) NULL,
  [VALUE_] nvarchar(255) NULL,
  [PASSWORD_] varbinary(max) NULL,
  [PARENT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_id_info
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_membership
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_membership]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_membership]
GO

CREATE TABLE [dbo].[act_id_membership] (
  [USER_ID_] nvarchar(64) NOT NULL,
  [GROUP_ID_] nvarchar(64) NOT NULL
)
GO


-- ----------------------------
-- Records of act_id_membership
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_priv
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_priv]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_priv]
GO

CREATE TABLE [dbo].[act_id_priv] (
  [ID_] nvarchar(64) NOT NULL,
  [NAME_] nvarchar(255) NOT NULL
)
GO


-- ----------------------------
-- Records of act_id_priv
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_priv_mapping
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_priv_mapping]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_priv_mapping]
GO

CREATE TABLE [dbo].[act_id_priv_mapping] (
  [ID_] nvarchar(64) NOT NULL,
  [PRIV_ID_] nvarchar(64) NOT NULL,
  [USER_ID_] nvarchar(255) NULL,
  [GROUP_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_id_priv_mapping
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_property
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_property]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_property]
GO

CREATE TABLE [dbo].[act_id_property] (
  [NAME_] nvarchar(64) NOT NULL,
  [VALUE_] nvarchar(300) NULL,
  [REV_] int NULL
)
GO


-- ----------------------------
-- Records of act_id_property
-- ----------------------------
BEGIN TRANSACTION
GO

INSERT INTO [dbo].[act_id_property] ([NAME_], [VALUE_], [REV_]) VALUES (N'schema.version', N'6.5.0.6', N'1')
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_token
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_token]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_token]
GO

CREATE TABLE [dbo].[act_id_token] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TOKEN_VALUE_] nvarchar(255) NULL,
  [TOKEN_DATE_] datetime2 NOT NULL,
  [IP_ADDRESS_] nvarchar(255) NULL,
  [USER_AGENT_] nvarchar(255) NULL,
  [USER_ID_] nvarchar(255) NULL,
  [TOKEN_DATA_] nvarchar(2000) NULL
)
GO


-- ----------------------------
-- Records of act_id_token
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_id_user
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_id_user]') AND type IN ('U'))
	DROP TABLE [dbo].[act_id_user]
GO

CREATE TABLE [dbo].[act_id_user] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [FIRST_] nvarchar(255) NULL,
  [LAST_] nvarchar(255) NULL,
  [DISPLAY_NAME_] nvarchar(255) NULL,
  [EMAIL_] nvarchar(255) NULL,
  [PWD_] nvarchar(255) NULL,
  [PICTURE_ID_] nvarchar(64) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_id_user
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_procdef_info
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_procdef_info]') AND type IN ('U'))
	DROP TABLE [dbo].[act_procdef_info]
GO

CREATE TABLE [dbo].[act_procdef_info] (
  [ID_] nvarchar(64) NOT NULL,
  [PROC_DEF_ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [INFO_JSON_ID_] nvarchar(64) NULL
)
GO


-- ----------------------------
-- Records of act_procdef_info
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_re_deployment
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_re_deployment]') AND type IN ('U'))
	DROP TABLE [dbo].[act_re_deployment]
GO

CREATE TABLE [dbo].[act_re_deployment] (
  [ID_] nvarchar(64) NOT NULL,
  [NAME_] nvarchar(255) NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [KEY_] nvarchar(255) NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [DEPLOY_TIME_] datetime2 NULL,
  [DERIVED_FROM_] nvarchar(64) NULL,
  [DERIVED_FROM_ROOT_] nvarchar(64) NULL,
  [PARENT_DEPLOYMENT_ID_] nvarchar(255) NULL,
  [ENGINE_VERSION_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_re_deployment
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_re_model
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_re_model]') AND type IN ('U'))
	DROP TABLE [dbo].[act_re_model]
GO

CREATE TABLE [dbo].[act_re_model] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [NAME_] nvarchar(255) NULL,
  [KEY_] nvarchar(255) NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [LAST_UPDATE_TIME_] datetime2 NULL,
  [VERSION_] int NULL,
  [META_INFO_] nvarchar(4000) NULL,
  [DEPLOYMENT_ID_] nvarchar(64) NULL,
  [EDITOR_SOURCE_VALUE_ID_] nvarchar(64) NULL,
  [EDITOR_SOURCE_EXTRA_VALUE_ID_] nvarchar(64) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_re_model
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_re_procdef
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_re_procdef]') AND type IN ('U'))
	DROP TABLE [dbo].[act_re_procdef]
GO

CREATE TABLE [dbo].[act_re_procdef] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [NAME_] nvarchar(255) NULL,
  [KEY_] nvarchar(255) NOT NULL,
  [VERSION_] int NOT NULL,
  [DEPLOYMENT_ID_] nvarchar(64) NULL,
  [RESOURCE_NAME_] nvarchar(4000) NULL,
  [DGRM_RESOURCE_NAME_] nvarchar(4000) NULL,
  [DESCRIPTION_] nvarchar(4000) NULL,
  [HAS_START_FORM_KEY_] tinyint NULL,
  [HAS_GRAPHICAL_NOTATION_] tinyint NULL,
  [SUSPENSION_STATE_] int NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [ENGINE_VERSION_] nvarchar(255) NULL,
  [DERIVED_FROM_] nvarchar(64) NULL,
  [DERIVED_FROM_ROOT_] nvarchar(64) NULL,
  [DERIVED_VERSION_] int NOT NULL
)
GO


-- ----------------------------
-- Records of act_re_procdef
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_actinst
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_actinst]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_actinst]
GO

CREATE TABLE [dbo].[act_ru_actinst] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [PROC_DEF_ID_] nvarchar(64) NOT NULL,
  [PROC_INST_ID_] nvarchar(64) NOT NULL,
  [EXECUTION_ID_] nvarchar(64) NOT NULL,
  [ACT_ID_] nvarchar(255) NOT NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [CALL_PROC_INST_ID_] nvarchar(64) NULL,
  [ACT_NAME_] nvarchar(255) NULL,
  [ACT_TYPE_] nvarchar(255) NOT NULL,
  [ASSIGNEE_] nvarchar(255) NULL,
  [START_TIME_] datetime2 NOT NULL,
  [END_TIME_] datetime2 NULL,
  [DURATION_] bigint NULL,
  [DELETE_REASON_] nvarchar(4000) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_actinst
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_deadletter_job
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_deadletter_job]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_deadletter_job]
GO

CREATE TABLE [dbo].[act_ru_deadletter_job] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TYPE_] nvarchar(255) NOT NULL,
  [EXCLUSIVE_] tinyint NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROCESS_INSTANCE_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [ELEMENT_ID_] nvarchar(255) NULL,
  [ELEMENT_NAME_] nvarchar(255) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [EXCEPTION_STACK_ID_] nvarchar(64) NULL,
  [EXCEPTION_MSG_] nvarchar(4000) NULL,
  [DUEDATE_] datetime2 NULL,
  [REPEAT_] nvarchar(255) NULL,
  [HANDLER_TYPE_] nvarchar(255) NULL,
  [HANDLER_CFG_] nvarchar(4000) NULL,
  [CUSTOM_VALUES_ID_] nvarchar(64) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_deadletter_job
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_entitylink
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_entitylink]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_entitylink]
GO

CREATE TABLE [dbo].[act_ru_entitylink] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [CREATE_TIME_] datetime2 NULL,
  [LINK_TYPE_] nvarchar(255) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [REF_SCOPE_ID_] nvarchar(255) NULL,
  [REF_SCOPE_TYPE_] nvarchar(255) NULL,
  [REF_SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [HIERARCHY_TYPE_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_entitylink
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_event_subscr
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_event_subscr]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_event_subscr]
GO

CREATE TABLE [dbo].[act_ru_event_subscr] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [EVENT_TYPE_] nvarchar(255) NOT NULL,
  [EVENT_NAME_] nvarchar(255) NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [ACTIVITY_ID_] nvarchar(64) NULL,
  [CONFIGURATION_] nvarchar(255) NULL,
  [CREATED_] datetime2 NOT NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [SUB_SCOPE_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(64) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(64) NULL,
  [SCOPE_TYPE_] nvarchar(64) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_event_subscr
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_execution
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_execution]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_execution]
GO

CREATE TABLE [dbo].[act_ru_execution] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [BUSINESS_KEY_] nvarchar(255) NULL,
  [PARENT_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [SUPER_EXEC_] nvarchar(64) NULL,
  [ROOT_PROC_INST_ID_] nvarchar(64) NULL,
  [ACT_ID_] nvarchar(255) NULL,
  [IS_ACTIVE_] tinyint NULL,
  [IS_CONCURRENT_] tinyint NULL,
  [IS_SCOPE_] tinyint NULL,
  [IS_EVENT_SCOPE_] tinyint NULL,
  [IS_MI_ROOT_] tinyint NULL,
  [SUSPENSION_STATE_] int NULL,
  [CACHED_ENT_STATE_] int NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [NAME_] nvarchar(255) NULL,
  [START_ACT_ID_] nvarchar(255) NULL,
  [START_TIME_] datetime2 NULL,
  [START_USER_ID_] nvarchar(255) NULL,
  [LOCK_TIME_] datetime2 NULL,
  [IS_COUNT_ENABLED_] tinyint NULL,
  [EVT_SUBSCR_COUNT_] int NULL,
  [TASK_COUNT_] int NULL,
  [JOB_COUNT_] int NULL,
  [TIMER_JOB_COUNT_] int NULL,
  [SUSP_JOB_COUNT_] int NULL,
  [DEADLETTER_JOB_COUNT_] int NULL,
  [VAR_COUNT_] int NULL,
  [ID_LINK_COUNT_] int NULL,
  [CALLBACK_ID_] nvarchar(255) NULL,
  [CALLBACK_TYPE_] nvarchar(255) NULL,
  [REFERENCE_ID_] nvarchar(255) NULL,
  [REFERENCE_TYPE_] nvarchar(255) NULL,
  [PROPAGATED_STAGE_INST_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_execution
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_history_job
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_history_job]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_history_job]
GO

CREATE TABLE [dbo].[act_ru_history_job] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [LOCK_EXP_TIME_] datetime2 NULL,
  [LOCK_OWNER_] nvarchar(255) NULL,
  [RETRIES_] int NULL,
  [EXCEPTION_STACK_ID_] nvarchar(64) NULL,
  [EXCEPTION_MSG_] nvarchar(4000) NULL,
  [HANDLER_TYPE_] nvarchar(255) NULL,
  [HANDLER_CFG_] nvarchar(4000) NULL,
  [CUSTOM_VALUES_ID_] nvarchar(64) NULL,
  [ADV_HANDLER_CFG_ID_] nvarchar(64) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_history_job
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_identitylink
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_identitylink]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_identitylink]
GO

CREATE TABLE [dbo].[act_ru_identitylink] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [GROUP_ID_] nvarchar(255) NULL,
  [TYPE_] nvarchar(255) NULL,
  [USER_ID_] nvarchar(255) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_identitylink
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_job
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_job]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_job]
GO

CREATE TABLE [dbo].[act_ru_job] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TYPE_] nvarchar(255) NOT NULL,
  [LOCK_EXP_TIME_] datetime2 NULL,
  [LOCK_OWNER_] nvarchar(255) NULL,
  [EXCLUSIVE_] tinyint NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROCESS_INSTANCE_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [ELEMENT_ID_] nvarchar(255) NULL,
  [ELEMENT_NAME_] nvarchar(255) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [RETRIES_] int NULL,
  [EXCEPTION_STACK_ID_] nvarchar(64) NULL,
  [EXCEPTION_MSG_] nvarchar(4000) NULL,
  [DUEDATE_] datetime2 NULL,
  [REPEAT_] nvarchar(255) NULL,
  [HANDLER_TYPE_] nvarchar(255) NULL,
  [HANDLER_CFG_] nvarchar(4000) NULL,
  [CUSTOM_VALUES_ID_] nvarchar(64) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_job
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_suspended_job
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_suspended_job]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_suspended_job]
GO

CREATE TABLE [dbo].[act_ru_suspended_job] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TYPE_] nvarchar(255) NOT NULL,
  [EXCLUSIVE_] tinyint NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROCESS_INSTANCE_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [ELEMENT_ID_] nvarchar(255) NULL,
  [ELEMENT_NAME_] nvarchar(255) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [RETRIES_] int NULL,
  [EXCEPTION_STACK_ID_] nvarchar(64) NULL,
  [EXCEPTION_MSG_] nvarchar(4000) NULL,
  [DUEDATE_] datetime2 NULL,
  [REPEAT_] nvarchar(255) NULL,
  [HANDLER_TYPE_] nvarchar(255) NULL,
  [HANDLER_CFG_] nvarchar(4000) NULL,
  [CUSTOM_VALUES_ID_] nvarchar(64) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_suspended_job
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_task
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_task]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_task]
GO

CREATE TABLE [dbo].[act_ru_task] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [TASK_DEF_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [PROPAGATED_STAGE_INST_ID_] nvarchar(255) NULL,
  [NAME_] nvarchar(255) NULL,
  [PARENT_TASK_ID_] nvarchar(64) NULL,
  [DESCRIPTION_] nvarchar(4000) NULL,
  [TASK_DEF_KEY_] nvarchar(255) NULL,
  [OWNER_] nvarchar(255) NULL,
  [ASSIGNEE_] nvarchar(255) NULL,
  [DELEGATION_] nvarchar(64) NULL,
  [PRIORITY_] int NULL,
  [CREATE_TIME_] datetime2 NULL,
  [DUE_DATE_] datetime2 NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [SUSPENSION_STATE_] int NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [FORM_KEY_] nvarchar(255) NULL,
  [CLAIM_TIME_] datetime2 NULL,
  [IS_COUNT_ENABLED_] tinyint NULL,
  [VAR_COUNT_] int NULL,
  [ID_LINK_COUNT_] int NULL,
  [SUB_TASK_COUNT_] int NULL
)
GO


-- ----------------------------
-- Records of act_ru_task
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_timer_job
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_timer_job]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_timer_job]
GO

CREATE TABLE [dbo].[act_ru_timer_job] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TYPE_] nvarchar(255) NOT NULL,
  [LOCK_EXP_TIME_] datetime2 NULL,
  [LOCK_OWNER_] nvarchar(255) NULL,
  [EXCLUSIVE_] tinyint NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROCESS_INSTANCE_ID_] nvarchar(64) NULL,
  [PROC_DEF_ID_] nvarchar(64) NULL,
  [ELEMENT_ID_] nvarchar(255) NULL,
  [ELEMENT_NAME_] nvarchar(255) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [SCOPE_DEFINITION_ID_] nvarchar(255) NULL,
  [RETRIES_] int NULL,
  [EXCEPTION_STACK_ID_] nvarchar(64) NULL,
  [EXCEPTION_MSG_] nvarchar(4000) NULL,
  [DUEDATE_] datetime2 NULL,
  [REPEAT_] nvarchar(255) NULL,
  [HANDLER_TYPE_] nvarchar(255) NULL,
  [HANDLER_CFG_] nvarchar(4000) NULL,
  [CUSTOM_VALUES_ID_] nvarchar(64) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of act_ru_timer_job
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for act_ru_variable
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[act_ru_variable]') AND type IN ('U'))
	DROP TABLE [dbo].[act_ru_variable]
GO

CREATE TABLE [dbo].[act_ru_variable] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TYPE_] nvarchar(255) NOT NULL,
  [NAME_] nvarchar(255) NOT NULL,
  [EXECUTION_ID_] nvarchar(64) NULL,
  [PROC_INST_ID_] nvarchar(64) NULL,
  [TASK_ID_] nvarchar(64) NULL,
  [SCOPE_ID_] nvarchar(255) NULL,
  [SUB_SCOPE_ID_] nvarchar(255) NULL,
  [SCOPE_TYPE_] nvarchar(255) NULL,
  [BYTEARRAY_ID_] nvarchar(64) NULL,
  [DOUBLE_] float NULL,
  [LONG_] bigint NULL,
  [TEXT_] nvarchar(4000) NULL,
  [TEXT2_] nvarchar(4000) NULL
)
GO


-- ----------------------------
-- Records of act_ru_variable
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flow_my_business
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flow_my_business]') AND type IN ('U'))
	DROP TABLE [dbo].[flow_my_business]
GO

CREATE TABLE [dbo].[flow_my_business] (
  [id] nvarchar(50) NOT NULL,
  [create_by] nvarchar(32) NULL,
  [create_time] datetime2 NULL,
  [update_by] nvarchar(32) NULL,
  [update_time] datetime2 NULL,
  [process_definition_key] nvarchar(50) NULL,
  [process_definition_id] nvarchar(50) NULL,
  [process_instance_id] nvarchar(50) NULL,
  [title] nvarchar(500) NULL,
  [data_id] nvarchar(50) NULL,
  [service_impl_name] nvarchar(50) NULL,
  [proposer] nvarchar(50) NULL,
  [act_status] nvarchar(100) NULL,
  [task_id] nvarchar(1000) NULL,
  [task_name] nvarchar(1000) NULL,
  [task_name_id] nvarchar(50) NULL,
  [todo_users] nvarchar(1000) NULL,
  [done_users] nvarchar(1000) NULL,
  [priority] nvarchar(100) NULL
)
GO

EXEC sp_addextendedproperty
'MS_Description', N'主键ID',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建人',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'create_by'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'create_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改人',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'update_by'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'update_time'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程定义key 一个key会有多个版本的id',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'process_definition_key'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程定义id 一个流程定义唯一',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'process_definition_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程业务实例id 一个流程业务唯一，本表中也唯一',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'process_instance_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程业务简要描述',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'title'
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务表id，理论唯一',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'data_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务类名，用来获取spring容器里的服务对象',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'service_impl_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'申请人',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'proposer'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程状态说明，有：启动  撤回  驳回  审批中  审批通过  审批异常',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'act_status'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前的节点定义上的Id,',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'task_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前的节点',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'task_name'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前的节点实例上的Id',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'task_name_id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前的节点可以处理的用户名',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'todo_users'
GO

EXEC sp_addextendedproperty
'MS_Description', N'处理过的人',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'done_users'
GO

EXEC sp_addextendedproperty
'MS_Description', N'当前任务节点的优先级 流程定义的时候所填',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'COLUMN', N'priority'
GO

EXEC sp_addextendedproperty
'MS_Description', N'流程业务扩展表',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business'
GO


-- ----------------------------
-- Records of flow_my_business
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Primary Key structure for table act_evt_log
-- ----------------------------
ALTER TABLE [dbo].[act_evt_log] ADD PRIMARY KEY CLUSTERED ([LOG_NR_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ge_bytearray
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_FK_BYTEARR_DEPL]
ON [dbo].[act_ge_bytearray] (
  [DEPLOYMENT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ge_bytearray
-- ----------------------------
ALTER TABLE [dbo].[act_ge_bytearray] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_ge_property
-- ----------------------------
ALTER TABLE [dbo].[act_ge_property] ADD PRIMARY KEY CLUSTERED ([NAME_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_actinst
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_HI_ACT_INST_START]
ON [dbo].[act_hi_actinst] (
  [START_TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_ACT_INST_END]
ON [dbo].[act_hi_actinst] (
  [END_TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_ACT_INST_PROCINST]
ON [dbo].[act_hi_actinst] (
  [PROC_INST_ID_] ASC,
  [ACT_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_ACT_INST_EXEC]
ON [dbo].[act_hi_actinst] (
  [EXECUTION_ID_] ASC,
  [ACT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_actinst
-- ----------------------------
ALTER TABLE [dbo].[act_hi_actinst] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_attachment
-- ----------------------------
ALTER TABLE [dbo].[act_hi_attachment] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_comment
-- ----------------------------
ALTER TABLE [dbo].[act_hi_comment] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_detail
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_HI_DETAIL_PROC_INST]
ON [dbo].[act_hi_detail] (
  [PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_DETAIL_ACT_INST]
ON [dbo].[act_hi_detail] (
  [ACT_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_DETAIL_TIME]
ON [dbo].[act_hi_detail] (
  [TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_DETAIL_NAME]
ON [dbo].[act_hi_detail] (
  [NAME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_DETAIL_TASK_ID]
ON [dbo].[act_hi_detail] (
  [TASK_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_detail
-- ----------------------------
ALTER TABLE [dbo].[act_hi_detail] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_entitylink
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_HI_ENT_LNK_SCOPE]
ON [dbo].[act_hi_entitylink] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC,
  [LINK_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_ENT_LNK_SCOPE_DEF]
ON [dbo].[act_hi_entitylink] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC,
  [LINK_TYPE_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_entitylink
-- ----------------------------
ALTER TABLE [dbo].[act_hi_entitylink] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_identitylink
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_HI_IDENT_LNK_USER]
ON [dbo].[act_hi_identitylink] (
  [USER_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_IDENT_LNK_SCOPE]
ON [dbo].[act_hi_identitylink] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_IDENT_LNK_SUB_SCOPE]
ON [dbo].[act_hi_identitylink] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_IDENT_LNK_SCOPE_DEF]
ON [dbo].[act_hi_identitylink] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_IDENT_LNK_TASK]
ON [dbo].[act_hi_identitylink] (
  [TASK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_IDENT_LNK_PROCINST]
ON [dbo].[act_hi_identitylink] (
  [PROC_INST_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_identitylink
-- ----------------------------
ALTER TABLE [dbo].[act_hi_identitylink] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_procinst
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [PROC_INST_ID_]
ON [dbo].[act_hi_procinst] (
  [PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_PRO_INST_END]
ON [dbo].[act_hi_procinst] (
  [END_TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_PRO_I_BUSKEY]
ON [dbo].[act_hi_procinst] (
  [BUSINESS_KEY_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_procinst
-- ----------------------------
ALTER TABLE [dbo].[act_hi_procinst] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_taskinst
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_HI_TASK_SCOPE]
ON [dbo].[act_hi_taskinst] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_TASK_SUB_SCOPE]
ON [dbo].[act_hi_taskinst] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_TASK_SCOPE_DEF]
ON [dbo].[act_hi_taskinst] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_TASK_INST_PROCINST]
ON [dbo].[act_hi_taskinst] (
  [PROC_INST_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_taskinst
-- ----------------------------
ALTER TABLE [dbo].[act_hi_taskinst] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_tsk_log
-- ----------------------------
ALTER TABLE [dbo].[act_hi_tsk_log] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_hi_varinst
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_HI_PROCVAR_NAME_TYPE]
ON [dbo].[act_hi_varinst] (
  [NAME_] ASC,
  [VAR_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_VAR_SCOPE_ID_TYPE]
ON [dbo].[act_hi_varinst] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_VAR_SUB_ID_TYPE]
ON [dbo].[act_hi_varinst] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_PROCVAR_PROC_INST]
ON [dbo].[act_hi_varinst] (
  [PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_PROCVAR_TASK_ID]
ON [dbo].[act_hi_varinst] (
  [TASK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_HI_PROCVAR_EXE]
ON [dbo].[act_hi_varinst] (
  [EXECUTION_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_hi_varinst
-- ----------------------------
ALTER TABLE [dbo].[act_hi_varinst] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_id_bytearray
-- ----------------------------
ALTER TABLE [dbo].[act_id_bytearray] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_id_group
-- ----------------------------
ALTER TABLE [dbo].[act_id_group] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_id_info
-- ----------------------------
ALTER TABLE [dbo].[act_id_info] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_id_membership
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_FK_MEMB_GROUP]
ON [dbo].[act_id_membership] (
  [GROUP_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_id_membership
-- ----------------------------
ALTER TABLE [dbo].[act_id_membership] ADD PRIMARY KEY CLUSTERED ([USER_ID_], [GROUP_ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_id_priv
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [ACT_UNIQ_PRIV_NAME]
ON [dbo].[act_id_priv] (
  [NAME_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_id_priv
-- ----------------------------
ALTER TABLE [dbo].[act_id_priv] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_id_priv_mapping
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_FK_PRIV_MAPPING]
ON [dbo].[act_id_priv_mapping] (
  [PRIV_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_PRIV_USER]
ON [dbo].[act_id_priv_mapping] (
  [USER_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_PRIV_GROUP]
ON [dbo].[act_id_priv_mapping] (
  [GROUP_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_id_priv_mapping
-- ----------------------------
ALTER TABLE [dbo].[act_id_priv_mapping] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_id_property
-- ----------------------------
ALTER TABLE [dbo].[act_id_property] ADD PRIMARY KEY CLUSTERED ([NAME_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_id_token
-- ----------------------------
ALTER TABLE [dbo].[act_id_token] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_id_user
-- ----------------------------
ALTER TABLE [dbo].[act_id_user] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_procdef_info
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [ACT_UNIQ_INFO_PROCDEF]
ON [dbo].[act_procdef_info] (
  [PROC_DEF_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_INFO_PROCDEF]
ON [dbo].[act_procdef_info] (
  [PROC_DEF_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_INFO_JSON_BA]
ON [dbo].[act_procdef_info] (
  [INFO_JSON_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_procdef_info
-- ----------------------------
ALTER TABLE [dbo].[act_procdef_info] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_re_deployment
-- ----------------------------
ALTER TABLE [dbo].[act_re_deployment] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_re_model
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_FK_MODEL_SOURCE]
ON [dbo].[act_re_model] (
  [EDITOR_SOURCE_VALUE_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_MODEL_SOURCE_EXTRA]
ON [dbo].[act_re_model] (
  [EDITOR_SOURCE_EXTRA_VALUE_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_MODEL_DEPLOYMENT]
ON [dbo].[act_re_model] (
  [DEPLOYMENT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_re_model
-- ----------------------------
ALTER TABLE [dbo].[act_re_model] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_re_procdef
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [ACT_UNIQ_PROCDEF]
ON [dbo].[act_re_procdef] (
  [KEY_] ASC,
  [VERSION_] ASC,
  [DERIVED_VERSION_] ASC,
  [TENANT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_re_procdef
-- ----------------------------
ALTER TABLE [dbo].[act_re_procdef] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_actinst
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_RU_ACTI_START]
ON [dbo].[act_ru_actinst] (
  [START_TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_RU_ACTI_END]
ON [dbo].[act_ru_actinst] (
  [END_TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_RU_ACTI_PROC]
ON [dbo].[act_ru_actinst] (
  [PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_RU_ACTI_PROC_ACT]
ON [dbo].[act_ru_actinst] (
  [PROC_INST_ID_] ASC,
  [ACT_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_RU_ACTI_EXEC]
ON [dbo].[act_ru_actinst] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_RU_ACTI_EXEC_ACT]
ON [dbo].[act_ru_actinst] (
  [EXECUTION_ID_] ASC,
  [ACT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_actinst
-- ----------------------------
ALTER TABLE [dbo].[act_ru_actinst] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_deadletter_job
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID]
ON [dbo].[act_ru_deadletter_job] (
  [EXCEPTION_STACK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID]
ON [dbo].[act_ru_deadletter_job] (
  [CUSTOM_VALUES_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_DJOB_SCOPE]
ON [dbo].[act_ru_deadletter_job] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_DJOB_SUB_SCOPE]
ON [dbo].[act_ru_deadletter_job] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_DJOB_SCOPE_DEF]
ON [dbo].[act_ru_deadletter_job] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_DEADLETTER_JOB_EXECUTION]
ON [dbo].[act_ru_deadletter_job] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE]
ON [dbo].[act_ru_deadletter_job] (
  [PROCESS_INSTANCE_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_DEADLETTER_JOB_PROC_DEF]
ON [dbo].[act_ru_deadletter_job] (
  [PROC_DEF_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_deadletter_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_deadletter_job] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_entitylink
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_ENT_LNK_SCOPE]
ON [dbo].[act_ru_entitylink] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC,
  [LINK_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_ENT_LNK_SCOPE_DEF]
ON [dbo].[act_ru_entitylink] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC,
  [LINK_TYPE_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_entitylink
-- ----------------------------
ALTER TABLE [dbo].[act_ru_entitylink] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_event_subscr
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_EVENT_SUBSCR_CONFIG_]
ON [dbo].[act_ru_event_subscr] (
  [CONFIGURATION_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_EVENT_EXEC]
ON [dbo].[act_ru_event_subscr] (
  [EXECUTION_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_event_subscr
-- ----------------------------
ALTER TABLE [dbo].[act_ru_event_subscr] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_execution
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_EXEC_BUSKEY]
ON [dbo].[act_ru_execution] (
  [BUSINESS_KEY_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDC_EXEC_ROOT]
ON [dbo].[act_ru_execution] (
  [ROOT_PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_EXE_PROCINST]
ON [dbo].[act_ru_execution] (
  [PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_EXE_PARENT]
ON [dbo].[act_ru_execution] (
  [PARENT_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_EXE_SUPER]
ON [dbo].[act_ru_execution] (
  [SUPER_EXEC_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_EXE_PROCDEF]
ON [dbo].[act_ru_execution] (
  [PROC_DEF_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_execution
-- ----------------------------
ALTER TABLE [dbo].[act_ru_execution] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_history_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_history_job] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_identitylink
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_IDENT_LNK_USER]
ON [dbo].[act_ru_identitylink] (
  [USER_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_IDENT_LNK_GROUP]
ON [dbo].[act_ru_identitylink] (
  [GROUP_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_IDENT_LNK_SCOPE]
ON [dbo].[act_ru_identitylink] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_IDENT_LNK_SUB_SCOPE]
ON [dbo].[act_ru_identitylink] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_IDENT_LNK_SCOPE_DEF]
ON [dbo].[act_ru_identitylink] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_ATHRZ_PROCEDEF]
ON [dbo].[act_ru_identitylink] (
  [PROC_DEF_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TSKASS_TASK]
ON [dbo].[act_ru_identitylink] (
  [TASK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_IDL_PROCINST]
ON [dbo].[act_ru_identitylink] (
  [PROC_INST_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_identitylink
-- ----------------------------
ALTER TABLE [dbo].[act_ru_identitylink] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_job
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_JOB_EXCEPTION_STACK_ID]
ON [dbo].[act_ru_job] (
  [EXCEPTION_STACK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_JOB_CUSTOM_VALUES_ID]
ON [dbo].[act_ru_job] (
  [CUSTOM_VALUES_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_JOB_SCOPE]
ON [dbo].[act_ru_job] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_JOB_SUB_SCOPE]
ON [dbo].[act_ru_job] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_JOB_SCOPE_DEF]
ON [dbo].[act_ru_job] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_JOB_EXECUTION]
ON [dbo].[act_ru_job] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_JOB_PROCESS_INSTANCE]
ON [dbo].[act_ru_job] (
  [PROCESS_INSTANCE_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_JOB_PROC_DEF]
ON [dbo].[act_ru_job] (
  [PROC_DEF_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_job] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_suspended_job
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID]
ON [dbo].[act_ru_suspended_job] (
  [EXCEPTION_STACK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID]
ON [dbo].[act_ru_suspended_job] (
  [CUSTOM_VALUES_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_SJOB_SCOPE]
ON [dbo].[act_ru_suspended_job] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_SJOB_SUB_SCOPE]
ON [dbo].[act_ru_suspended_job] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_SJOB_SCOPE_DEF]
ON [dbo].[act_ru_suspended_job] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_SUSPENDED_JOB_EXECUTION]
ON [dbo].[act_ru_suspended_job] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE]
ON [dbo].[act_ru_suspended_job] (
  [PROCESS_INSTANCE_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_SUSPENDED_JOB_PROC_DEF]
ON [dbo].[act_ru_suspended_job] (
  [PROC_DEF_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_suspended_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_suspended_job] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_task
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_TASK_CREATE]
ON [dbo].[act_ru_task] (
  [CREATE_TIME_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TASK_SCOPE]
ON [dbo].[act_ru_task] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TASK_SUB_SCOPE]
ON [dbo].[act_ru_task] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TASK_SCOPE_DEF]
ON [dbo].[act_ru_task] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TASK_EXE]
ON [dbo].[act_ru_task] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TASK_PROCINST]
ON [dbo].[act_ru_task] (
  [PROC_INST_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TASK_PROCDEF]
ON [dbo].[act_ru_task] (
  [PROC_DEF_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_task
-- ----------------------------
ALTER TABLE [dbo].[act_ru_task] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_timer_job
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID]
ON [dbo].[act_ru_timer_job] (
  [EXCEPTION_STACK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID]
ON [dbo].[act_ru_timer_job] (
  [CUSTOM_VALUES_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TJOB_SCOPE]
ON [dbo].[act_ru_timer_job] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TJOB_SUB_SCOPE]
ON [dbo].[act_ru_timer_job] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_TJOB_SCOPE_DEF]
ON [dbo].[act_ru_timer_job] (
  [SCOPE_DEFINITION_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TIMER_JOB_EXECUTION]
ON [dbo].[act_ru_timer_job] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TIMER_JOB_PROCESS_INSTANCE]
ON [dbo].[act_ru_timer_job] (
  [PROCESS_INSTANCE_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_TIMER_JOB_PROC_DEF]
ON [dbo].[act_ru_timer_job] (
  [PROC_DEF_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_timer_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_timer_job] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table act_ru_variable
-- ----------------------------
CREATE NONCLUSTERED INDEX [ACT_IDX_RU_VAR_SCOPE_ID_TYPE]
ON [dbo].[act_ru_variable] (
  [SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_RU_VAR_SUB_ID_TYPE]
ON [dbo].[act_ru_variable] (
  [SUB_SCOPE_ID_] ASC,
  [SCOPE_TYPE_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_VAR_BYTEARRAY]
ON [dbo].[act_ru_variable] (
  [BYTEARRAY_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_IDX_VARIABLE_TASK_ID]
ON [dbo].[act_ru_variable] (
  [TASK_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_VAR_EXE]
ON [dbo].[act_ru_variable] (
  [EXECUTION_ID_] ASC
)
GO

CREATE NONCLUSTERED INDEX [ACT_FK_VAR_PROCINST]
ON [dbo].[act_ru_variable] (
  [PROC_INST_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table act_ru_variable
-- ----------------------------
ALTER TABLE [dbo].[act_ru_variable] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table flow_my_business
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [dataid]
ON [dbo].[flow_my_business] (
  [data_id] ASC
)
GO

EXEC sp_addextendedproperty
'MS_Description', N'业务数据Id索引',
'SCHEMA', N'dbo',
'TABLE', N'flow_my_business',
'INDEX', N'dataid'
GO


-- ----------------------------
-- Primary Key structure for table flow_my_business
-- ----------------------------
ALTER TABLE [dbo].[flow_my_business] ADD PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Foreign Keys structure for table act_ge_bytearray
-- ----------------------------
ALTER TABLE [dbo].[act_ge_bytearray] ADD CONSTRAINT [ACT_FK_BYTEARR_DEPL] FOREIGN KEY ([DEPLOYMENT_ID_]) REFERENCES [dbo].[act_re_deployment] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_id_membership
-- ----------------------------
ALTER TABLE [dbo].[act_id_membership] ADD CONSTRAINT [ACT_FK_MEMB_GROUP] FOREIGN KEY ([GROUP_ID_]) REFERENCES [dbo].[act_id_group] ([ID_])
GO

ALTER TABLE [dbo].[act_id_membership] ADD CONSTRAINT [ACT_FK_MEMB_USER] FOREIGN KEY ([USER_ID_]) REFERENCES [dbo].[act_id_user] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_id_priv_mapping
-- ----------------------------
ALTER TABLE [dbo].[act_id_priv_mapping] ADD CONSTRAINT [ACT_FK_PRIV_MAPPING] FOREIGN KEY ([PRIV_ID_]) REFERENCES [dbo].[act_id_priv] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_procdef_info
-- ----------------------------
ALTER TABLE [dbo].[act_procdef_info] ADD CONSTRAINT [ACT_FK_INFO_JSON_BA] FOREIGN KEY ([INFO_JSON_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_procdef_info] ADD CONSTRAINT [ACT_FK_INFO_PROCDEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_re_model
-- ----------------------------
ALTER TABLE [dbo].[act_re_model] ADD CONSTRAINT [ACT_FK_MODEL_DEPLOYMENT] FOREIGN KEY ([DEPLOYMENT_ID_]) REFERENCES [dbo].[act_re_deployment] ([ID_])
GO

ALTER TABLE [dbo].[act_re_model] ADD CONSTRAINT [ACT_FK_MODEL_SOURCE] FOREIGN KEY ([EDITOR_SOURCE_VALUE_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_re_model] ADD CONSTRAINT [ACT_FK_MODEL_SOURCE_EXTRA] FOREIGN KEY ([EDITOR_SOURCE_EXTRA_VALUE_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_deadletter_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_deadletter_job] ADD CONSTRAINT [ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES] FOREIGN KEY ([CUSTOM_VALUES_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_deadletter_job] ADD CONSTRAINT [ACT_FK_DEADLETTER_JOB_EXCEPTION] FOREIGN KEY ([EXCEPTION_STACK_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_deadletter_job] ADD CONSTRAINT [ACT_FK_DEADLETTER_JOB_EXECUTION] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_deadletter_job] ADD CONSTRAINT [ACT_FK_DEADLETTER_JOB_PROC_DEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_deadletter_job] ADD CONSTRAINT [ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE] FOREIGN KEY ([PROCESS_INSTANCE_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_event_subscr
-- ----------------------------
ALTER TABLE [dbo].[act_ru_event_subscr] ADD CONSTRAINT [ACT_FK_EVENT_EXEC] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_execution
-- ----------------------------
ALTER TABLE [dbo].[act_ru_execution] ADD CONSTRAINT [ACT_FK_EXE_PARENT] FOREIGN KEY ([PARENT_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_execution] ADD CONSTRAINT [ACT_FK_EXE_PROCDEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_execution] ADD CONSTRAINT [ACT_FK_EXE_PROCINST] FOREIGN KEY ([PROC_INST_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_execution] ADD CONSTRAINT [ACT_FK_EXE_SUPER] FOREIGN KEY ([SUPER_EXEC_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_identitylink
-- ----------------------------
ALTER TABLE [dbo].[act_ru_identitylink] ADD CONSTRAINT [ACT_FK_ATHRZ_PROCEDEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_identitylink] ADD CONSTRAINT [ACT_FK_IDL_PROCINST] FOREIGN KEY ([PROC_INST_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_identitylink] ADD CONSTRAINT [ACT_FK_TSKASS_TASK] FOREIGN KEY ([TASK_ID_]) REFERENCES [dbo].[act_ru_task] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_job] ADD CONSTRAINT [ACT_FK_JOB_CUSTOM_VALUES] FOREIGN KEY ([CUSTOM_VALUES_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_job] ADD CONSTRAINT [ACT_FK_JOB_EXCEPTION] FOREIGN KEY ([EXCEPTION_STACK_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_job] ADD CONSTRAINT [ACT_FK_JOB_EXECUTION] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_job] ADD CONSTRAINT [ACT_FK_JOB_PROC_DEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_job] ADD CONSTRAINT [ACT_FK_JOB_PROCESS_INSTANCE] FOREIGN KEY ([PROCESS_INSTANCE_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_suspended_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_suspended_job] ADD CONSTRAINT [ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES] FOREIGN KEY ([CUSTOM_VALUES_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_suspended_job] ADD CONSTRAINT [ACT_FK_SUSPENDED_JOB_EXCEPTION] FOREIGN KEY ([EXCEPTION_STACK_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_suspended_job] ADD CONSTRAINT [ACT_FK_SUSPENDED_JOB_EXECUTION] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_suspended_job] ADD CONSTRAINT [ACT_FK_SUSPENDED_JOB_PROC_DEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_suspended_job] ADD CONSTRAINT [ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE] FOREIGN KEY ([PROCESS_INSTANCE_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_task
-- ----------------------------
ALTER TABLE [dbo].[act_ru_task] ADD CONSTRAINT [ACT_FK_TASK_EXE] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_task] ADD CONSTRAINT [ACT_FK_TASK_PROCDEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_task] ADD CONSTRAINT [ACT_FK_TASK_PROCINST] FOREIGN KEY ([PROC_INST_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_timer_job
-- ----------------------------
ALTER TABLE [dbo].[act_ru_timer_job] ADD CONSTRAINT [ACT_FK_TIMER_JOB_CUSTOM_VALUES] FOREIGN KEY ([CUSTOM_VALUES_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_timer_job] ADD CONSTRAINT [ACT_FK_TIMER_JOB_EXCEPTION] FOREIGN KEY ([EXCEPTION_STACK_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_timer_job] ADD CONSTRAINT [ACT_FK_TIMER_JOB_EXECUTION] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_timer_job] ADD CONSTRAINT [ACT_FK_TIMER_JOB_PROC_DEF] FOREIGN KEY ([PROC_DEF_ID_]) REFERENCES [dbo].[act_re_procdef] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_timer_job] ADD CONSTRAINT [ACT_FK_TIMER_JOB_PROCESS_INSTANCE] FOREIGN KEY ([PROCESS_INSTANCE_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO


-- ----------------------------
-- Foreign Keys structure for table act_ru_variable
-- ----------------------------
ALTER TABLE [dbo].[act_ru_variable] ADD CONSTRAINT [ACT_FK_VAR_BYTEARRAY] FOREIGN KEY ([BYTEARRAY_ID_]) REFERENCES [dbo].[act_ge_bytearray] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_variable] ADD CONSTRAINT [ACT_FK_VAR_EXE] FOREIGN KEY ([EXECUTION_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

ALTER TABLE [dbo].[act_ru_variable] ADD CONSTRAINT [ACT_FK_VAR_PROCINST] FOREIGN KEY ([PROC_INST_ID_]) REFERENCES [dbo].[act_ru_execution] ([ID_])
GO

