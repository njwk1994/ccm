ALTER TABLE rep_demo_dxtj ALTER COLUMN id NVARCHAR(32)  NOT NULL;
ALTER TABLE rep_demo_dxtj ADD CONSTRAINT PK_id PRIMARY KEY (id);

ALTER TABLE sys_third_account ALTER COLUMN third_type NVARCHAR(50) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'登录来源','SCHEMA', N'dbo','TABLE', N'sys_third_account','COLUMN', N'third_type';

DELETE FROM sys_dict_item WHERE dict_id ='1209733563293962241';
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1209733775114702850', '1209733563293962241', 'MySQL5.5', '1', '', 1, 1, 'admin', '2019-12-25 15:13:02', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1334440962954936321', '1209733563293962241', 'MYSQL5.7+', '4', '', 2, 1, 'admin', '2020-12-03 18:16:02', 'admin', '2021-07-15 13:44:29');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1209733839933476865', '1209733563293962241', 'Oracle', '2', '', 3, 1, 'admin', '2019-12-25 15:13:18', 'admin', '2021-07-15 13:44:08');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1209733903020003330', '1209733563293962241', 'SQLServer', '3', '', 4, 1, 'admin', '2019-12-25 15:13:33', 'admin', '2021-07-15 13:44:11');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1414837074500976641', '1209733563293962241', 'postgresql', '6', '', 5, 1, 'admin', '2021-07-13 14:40:20', 'admin', '2021-07-15 13:44:15');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1415547541091504129', '1209733563293962241', 'marialDB', '5', '', 6, 1, 'admin', '2021-07-15 13:43:28', 'admin', '2021-07-15 13:44:23');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418049969003089922', '1209733563293962241', '达梦', '7', '', 7, 1, 'admin', '2021-07-22 11:27:13', 'admin', '2021-07-22 11:27:30');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050017053036545', '1209733563293962241', '人大金仓', '8', '', 8, 1, 'admin', '2021-07-22 11:27:25', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050075555188737', '1209733563293962241', '神通', '9', '', 9, 1, 'admin', '2021-07-22 11:27:39', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050110669901826', '1209733563293962241', 'SQLite', '10', '', 10, 1, 'admin', '2021-07-22 11:27:47', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050149475602434', '1209733563293962241', 'DB2', '11', '', 11, 1, 'admin', '2021-07-22 11:27:56', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050209823248385', '1209733563293962241', 'Hsqldb', '12', '', 12, 1, 'admin', '2021-07-22 11:28:11', 'admin', '2021-07-22 11:28:27');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050323111399425', '1209733563293962241', 'Derby', '13', '', 13, 1, 'admin', '2021-07-22 11:28:38', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418117316707590146', '1209733563293962241', 'H2', '14', '', 14, 1, 'admin', '2021-07-22 15:54:50', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418491604048449537', '1209733563293962241', '其他数据库', '15', '', 15, 1, 'admin', '2021-07-23 16:42:07', NULL, NULL);

ALTER TABLE sys_permission ADD hide_tab INT NULL DEFAULT NULL;
EXEC sp_addextendedproperty 'MS_Description', N'是否隐藏tab: 0否,1是','SCHEMA', N'dbo','TABLE', N'sys_permission','COLUMN', N'hide_tab';

ALTER TABLE onl_cgform_head ADD low_app_id NVARCHAR(32) NULL DEFAULT NULL;
EXEC sp_addextendedproperty 'MS_Description', N'关联的应用ID','SCHEMA', N'dbo','TABLE', N'onl_cgform_head','COLUMN', N'low_app_id';

UPDATE onl_cgform_field SET db_type = 'string' WHERE db_type = 'String';

ALTER TABLE jimu_report ALTER COLUMN view_count BIGINT NULL;
ALTER TABLE jimu_report ADD DEFAULT (0) FOR view_count WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'浏览次数','SCHEMA', N'dbo','TABLE', N'jimu_report','COLUMN', N'view_count';

ALTER TABLE jimu_report ALTER COLUMN json_str NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'json字符串','SCHEMA', N'dbo','TABLE', N'jimu_report','COLUMN', N'json_str';

ALTER TABLE jimu_report_db_field ADD search_format NVARCHAR(50) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'查询时间格式化表达式','SCHEMA', N'dbo','TABLE', N'jimu_report_db_field','COLUMN', N'search_format';

ALTER TABLE jimu_report_db_param ADD search_format NVARCHAR(50) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'查询时间格式化表达式','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'search_format';

UPDATE jimu_report SET json_str=replace(json_str,'"subtotal":"totalField"','"funcname":"SUM"');

ALTER TABLE jimu_report ADD css_str NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'css增强','SCHEMA', N'dbo','TABLE', N'jimu_report','COLUMN', N'css_str';
ALTER TABLE jimu_report ADD js_str NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'js增强','SCHEMA', N'dbo','TABLE', N'jimu_report','COLUMN', N'js_str';

ALTER TABLE jimu_report_link ALTER COLUMN expression NVARCHAR(MAX) NULL;
ALTER TABLE jimu_report_link ADD DEFAULT NULL FOR expression WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'条件','SCHEMA', N'dbo','TABLE', N'jimu_report_link','COLUMN', N'expression';
ALTER TABLE jimu_report_link ALTER COLUMN requirement NVARCHAR(MAX) NULL;
ALTER TABLE jimu_report_link ADD DEFAULT NULL FOR requirement WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'条件','SCHEMA', N'dbo','TABLE', N'jimu_report_link','COLUMN', N'requirement';

ALTER TABLE jimu_report_db_field ADD ext_json NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'参数配置','SCHEMA', N'dbo','TABLE', N'jimu_report_db_field','COLUMN', N'ext_json';

ALTER TABLE jimu_report_db_param ADD ext_json NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'参数配置','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'ext_json';

ALTER TABLE jimu_report_db ALTER COLUMN is_list NVARCHAR(10) NULL;
ALTER TABLE jimu_report_db ADD DEFAULT ('0') FOR is_list WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'是否是列表0否1是 默认0','SCHEMA', N'dbo','TABLE', N'jimu_report_db','COLUMN', N'is_list';
