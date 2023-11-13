/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : jeecg-boot

 Target Server Type    : SQL Server
 Target Server Version : 15000000
 File Encoding         : 65001

 Date: 04/03/2022 10:26:40
*/


-- ----------------------------
-- Table structure for flw_channel_definition
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_channel_definition]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_channel_definition]
GO

CREATE TABLE [dbo].[flw_channel_definition] (
  [ID_] nvarchar(255) NOT NULL,
  [NAME_] nvarchar(255) NULL,
  [VERSION_] int NULL,
  [KEY_] nvarchar(255) NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [DEPLOYMENT_ID_] nvarchar(255) NULL,
  [CREATE_TIME_] datetime2 NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [RESOURCE_NAME_] nvarchar(255) NULL,
  [DESCRIPTION_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of flw_channel_definition
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_ev_databasechangelog
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_ev_databasechangelog]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_ev_databasechangelog]
GO

CREATE TABLE [dbo].[flw_ev_databasechangelog] (
  [ID] nvarchar(255) NOT NULL,
  [AUTHOR] nvarchar(255) NOT NULL,
  [FILENAME] nvarchar(255) NOT NULL,
  [DATEEXECUTED] datetime2 NOT NULL,
  [ORDEREXECUTED] int NOT NULL,
  [EXECTYPE] nvarchar(10) NOT NULL,
  [MD5SUM] nvarchar(35) NULL,
  [DESCRIPTION] nvarchar(255) NULL,
  [COMMENTS] nvarchar(255) NULL,
  [TAG] nvarchar(255) NULL,
  [LIQUIBASE] nvarchar(20) NULL,
  [CONTEXTS] nvarchar(255) NULL,
  [LABELS] nvarchar(255) NULL,
  [DEPLOYMENT_ID] nvarchar(10) NULL
)
GO


-- ----------------------------
-- Records of flw_ev_databasechangelog
-- ----------------------------
BEGIN TRANSACTION
GO

INSERT INTO [dbo].[flw_ev_databasechangelog] ([ID], [AUTHOR], [FILENAME], [DATEEXECUTED], [ORDEREXECUTED], [EXECTYPE], [MD5SUM], [DESCRIPTION], [COMMENTS], [TAG], [LIQUIBASE], [CONTEXTS], [LABELS], [DEPLOYMENT_ID]) VALUES (N'1', N'flowable', N'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', N'2022-03-03 17:00:54', N'1', N'EXECUTED', N'8:1b0c48c9cf7945be799d868a2626d687', N'createTable tableSuffix=FLW_EVENT_DEPLOYMENT; createTable tableSuffix=FLW_EVENT_RESOURCE; createTable tableSuffix=FLW_EVENT_DEFINITION; createIndex indexName=ACT_IDX_EVENT_DEF_UNIQ, tableSuffix=FLW_EVENT_DEFINITION; createTable tableSuffix=FLW_CHANNEL_DEFIN...', N'', NULL, N'3.8.9', NULL, NULL, N'6298054418')
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_ev_databasechangeloglock
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_ev_databasechangeloglock]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_ev_databasechangeloglock]
GO

CREATE TABLE [dbo].[flw_ev_databasechangeloglock] (
  [ID] int NOT NULL,
  [LOCKED] varchar(1) NOT NULL,
  [LOCKGRANTED] datetime2 NULL,
  [LOCKEDBY] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of flw_ev_databasechangeloglock
-- ----------------------------
BEGIN TRANSACTION
GO

INSERT INTO [dbo].[flw_ev_databasechangeloglock] ([ID], [LOCKED], [LOCKGRANTED], [LOCKEDBY]) VALUES (N'1', N'0', NULL, NULL)
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_event_definition
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_event_definition]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_event_definition]
GO

CREATE TABLE [dbo].[flw_event_definition] (
  [ID_] nvarchar(255) NOT NULL,
  [NAME_] nvarchar(255) NULL,
  [VERSION_] int NULL,
  [KEY_] nvarchar(255) NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [DEPLOYMENT_ID_] nvarchar(255) NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [RESOURCE_NAME_] nvarchar(255) NULL,
  [DESCRIPTION_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of flw_event_definition
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_event_deployment
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_event_deployment]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_event_deployment]
GO

CREATE TABLE [dbo].[flw_event_deployment] (
  [ID_] nvarchar(255) NOT NULL,
  [NAME_] nvarchar(255) NULL,
  [CATEGORY_] nvarchar(255) NULL,
  [DEPLOY_TIME_] datetime2 NULL,
  [TENANT_ID_] nvarchar(255) NULL,
  [PARENT_DEPLOYMENT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of flw_event_deployment
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_event_resource
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_event_resource]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_event_resource]
GO

CREATE TABLE [dbo].[flw_event_resource] (
  [ID_] nvarchar(255) NOT NULL,
  [NAME_] nvarchar(255) NULL,
  [DEPLOYMENT_ID_] nvarchar(255) NULL,
  [RESOURCE_BYTES_] varbinary(max) NULL
)
GO


-- ----------------------------
-- Records of flw_event_resource
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_ru_batch
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_ru_batch]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_ru_batch]
GO

CREATE TABLE [dbo].[flw_ru_batch] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [TYPE_] nvarchar(64) NOT NULL,
  [SEARCH_KEY_] nvarchar(255) NULL,
  [SEARCH_KEY2_] nvarchar(255) NULL,
  [CREATE_TIME_] datetime2 NOT NULL,
  [COMPLETE_TIME_] datetime2 NULL,
  [STATUS_] nvarchar(255) NULL,
  [BATCH_DOC_ID_] nvarchar(64) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of flw_ru_batch
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for flw_ru_batch_part
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[flw_ru_batch_part]') AND type IN ('U'))
	DROP TABLE [dbo].[flw_ru_batch_part]
GO

CREATE TABLE [dbo].[flw_ru_batch_part] (
  [ID_] nvarchar(64) NOT NULL,
  [REV_] int NULL,
  [BATCH_ID_] nvarchar(64) NULL,
  [TYPE_] nvarchar(64) NOT NULL,
  [SCOPE_ID_] nvarchar(64) NULL,
  [SUB_SCOPE_ID_] nvarchar(64) NULL,
  [SCOPE_TYPE_] nvarchar(64) NULL,
  [SEARCH_KEY_] nvarchar(255) NULL,
  [SEARCH_KEY2_] nvarchar(255) NULL,
  [CREATE_TIME_] datetime2 NOT NULL,
  [COMPLETE_TIME_] datetime2 NULL,
  [STATUS_] nvarchar(255) NULL,
  [RESULT_DOC_ID_] nvarchar(64) NULL,
  [TENANT_ID_] nvarchar(255) NULL
)
GO


-- ----------------------------
-- Records of flw_ru_batch_part
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Indexes structure for table flw_channel_definition
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [ACT_IDX_CHANNEL_DEF_UNIQ]
ON [dbo].[flw_channel_definition] (
  [KEY_] ASC,
  [VERSION_] ASC,
  [TENANT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table flw_channel_definition
-- ----------------------------
ALTER TABLE [dbo].[flw_channel_definition] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table flw_ev_databasechangeloglock
-- ----------------------------
ALTER TABLE [dbo].[flw_ev_databasechangeloglock] ADD PRIMARY KEY CLUSTERED ([ID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table flw_event_definition
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [ACT_IDX_EVENT_DEF_UNIQ]
ON [dbo].[flw_event_definition] (
  [KEY_] ASC,
  [VERSION_] ASC,
  [TENANT_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table flw_event_definition
-- ----------------------------
ALTER TABLE [dbo].[flw_event_definition] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table flw_event_deployment
-- ----------------------------
ALTER TABLE [dbo].[flw_event_deployment] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table flw_event_resource
-- ----------------------------
ALTER TABLE [dbo].[flw_event_resource] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table flw_ru_batch
-- ----------------------------
ALTER TABLE [dbo].[flw_ru_batch] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table flw_ru_batch_part
-- ----------------------------
CREATE NONCLUSTERED INDEX [FLW_IDX_BATCH_PART]
ON [dbo].[flw_ru_batch_part] (
  [BATCH_ID_] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table flw_ru_batch_part
-- ----------------------------
ALTER TABLE [dbo].[flw_ru_batch_part] ADD PRIMARY KEY CLUSTERED ([ID_])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Foreign Keys structure for table flw_ru_batch_part
-- ----------------------------
ALTER TABLE [dbo].[flw_ru_batch_part] ADD CONSTRAINT [FLW_FK_BATCH_PART_PARENT] FOREIGN KEY ([BATCH_ID_]) REFERENCES [dbo].[flw_ru_batch] ([ID_])
GO

