CREATE UNIQUE INDEX uniq_sys_third_account_third_type_third_user_id ON sys_third_account(third_type,third_user_id);

INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) VALUES ('1404684556047024130', '08e6b9dc3c04489c8e1ff2ce6f105aa4', '�����û�', '/isystem/online', 'system/SysUserOnlineList', NULL, NULL, 1, NULL, '1', NULL, 0, NULL, 1, 1, 0, 0, NULL, '1', 0, 0, 'admin', '2021-06-15 14:17:51', NULL, NULL, 0);

DELETE FROM sys_depart WHERE id = '743ba9dbdc114af8953a11022ef3096a';

--alter table sys_quartz_job engine = InnoDB;

UPDATE sys_dict_item SET item_value = '6' WHERE item_text = 'MYSQL5.7';

INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1414837074500976641', '1209733563293962241', 'Postgresql', '6', '', '5', '1', 'admin', '2021-07-13 14:40:20', 'admin', '2021-07-15 13:44:15');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1415547541091504129', '1209733563293962241', 'MarialDB', '5', '', '6', '1', 'admin', '2021-07-15 13:43:28', 'admin', '2021-07-15 13:44:23');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050323111399425', '1209733563293962241', 'Derby', '13', '', '13', '1', 'admin', '2021-07-22 11:28:38', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050209823248385', '1209733563293962241', 'Hsqldb', '12', '', '12', '1', 'admin', '2021-07-22 11:28:11', 'admin', '2021-07-22 11:28:27');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050149475602434', '1209733563293962241', 'DB2', '11', '', '11', '1', 'admin', '2021-07-22 11:27:56', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050110669901826', '1209733563293962241', 'SQLite', '10', '', '10', '1', 'admin', '2021-07-22 11:27:47', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050075555188737', '1209733563293962241', '神通', '9', '', '9', '1', 'admin', '2021-07-22 11:27:39', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418050017053036545', '1209733563293962241', '人大金仓', '8', '', '8', '1', 'admin', '2021-07-22 11:27:25', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418049969003089922', '1209733563293962241', '达梦', '7', '', '7', '1', 'admin', '2021-07-22 11:27:13', 'admin', '2021-07-22 11:27:30');
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418117316707590146', '1209733563293962241', 'H2', '14', '', '14', '1', 'admin', '2021-07-22 15:54:50', NULL, NULL);
INSERT INTO sys_dict_item (id, dict_id, item_text, item_value, description, sort_order, status, create_by, create_time, update_by, update_time) VALUES ('1418491604048449537', '1209733563293962241', '其他数据库', '15', '', 15, 1, 'admin', '2021-07-23 16:42:07', NULL, NULL);

ALTER TABLE demo ADD tenant_id INT(10) NULL DEFAULT 0;

ALTER TABLE onl_cgform_head ADD ext_config_json NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'扩展JSON','SCHEMA', N'dbo','TABLE', N'onl_cgform_head','COLUMN', N'ext_config_json';

ALTER TABLE onl_cgreport_head ADD low_app_id NVARCHAR(32) NULL DEFAULT NULL;
EXEC sp_addextendedproperty 'MS_Description', N'关联的应用ID关联的应用ID','SCHEMA', N'dbo','TABLE', N'onl_cgreport_head','COLUMN', N'low_app_id';

UPDATE jimu_report SET json_str = '{\"loopBlockList\":[],\"area\":{\"sri\":16,\"sci\":5,\"eri\":16,\"eci\":5,\"width\":147,\"height\":25},\"excel_config_id\":\"1347373863746539520\",\"printConfig\":{\"paper\":\"A4\",\"width\":210,\"height\":297,\"definition\":1,\"isBackend\":false,\"marginX\":10,\"marginY\":10,\"layout\":\"portrait\"},\"rows\":{\"0\":{\"cells\":{\"0\":{\"text\":\"\"},\"1\":{\"text\":\"\"}}},\"1\":{\"cells\":{\"0\":{\"text\":\"\"}}},\"3\":{\"cells\":{\"2\":{\"text\":\"\",\"rendered\":\"\"}}},\"5\":{\"cells\":{},\"height\":29},\"6\":{\"cells\":{\"2\":{\"text\":\"\",\"style\":2}},\"height\":34},\"7\":{\"cells\":{\"2\":{\"merge\":[0,4],\"text\":\"实习证明\",\"style\":2}},\"height\":41},\"8\":{\"cells\":{\"1\":{\"text\":\"\",\"style\":3},\"2\":{\"text\":\"\"}}},\"9\":{\"cells\":{\"1\":{\"text\":\"\",\"style\":3},\"2\":{\"text\":\"\",\"style\":3},\"3\":{\"text\":\"\"}},\"isDrag\":true,\"height\":33},\"10\":{\"cells\":{\"2\":{\"text\":\"${tt.name}\",\"style\":11},\"3\":{\"text\":\"同学在我公司与 2020年4月1日 至 2020年5月1日 实习。\",\"style\":19,\"merge\":[0,3],\"height\":34}},\"height\":34},\"11\":{\"cells\":{},\"height\":28},\"12\":{\"cells\":{\"1\":{\"text\":\"\",\"style\":6},\"2\":{\"style\":13,\"text\":\"${tt.pingjia}\",\"merge\":[3,4],\"height\":129}},\"height\":36},\"13\":{\"cells\":{},\"height\":29},\"14\":{\"cells\":{},\"height\":33},\"15\":{\"cells\":{},\"height\":31},\"16\":{\"cells\":{}},\"17\":{\"cells\":{\"1\":{\"text\":\"\"},\"2\":{\"text\":\"特此证明！\",\"style\":12}}},\"20\":{\"cells\":{\"2\":{\"text\":\"\"},\"3\":{\"text\":\"\",\"style\":3},\"4\":{\"text\":\"\"}}},\"21\":{\"cells\":{\"4\":{\"text\":\"\"}}},\"22\":{\"cells\":{\"3\":{\"text\":\"\",\"style\":3},\"4\":{\"text\":\"证明人：\",\"style\":11},\"5\":{\"text\":\"${tt.lingdao}\",\"style\":12}}},\"23\":{\"cells\":{\"4\":{\"text\":\"\"},\"5\":{\"text\":\"${tt.shijian}\",\"style\":15}}},\"len\":100},\"dbexps\":[],\"dicts\":[],\"freeze\":\"A1\",\"dataRectWidth\":576,\"displayConfig\":{},\"background\":{\"path\":\"https://static.jeecg.com/designreport/images/11_1611283832037.png\",\"repeat\":\"no-repeat\",\"width\":\"\",\"height\":\"\"},\"name\":\"sheet1\",\"autofilter\":{},\"styles\":[{\"align\":\"center\"},{\"align\":\"center\",\"font\":{\"size\":14}},{\"align\":\"center\",\"font\":{\"size\":16}},{\"align\":\"right\"},{\"align\":\"left\"},{\"align\":\"left\",\"valign\":\"top\"},{\"align\":\"left\",\"valign\":\"top\",\"textwrap\":true},{\"font\":{\"size\":16}},{\"align\":\"left\",\"valign\":\"top\",\"textwrap\":false},{\"textwrap\":false},{\"textwrap\":true},{\"align\":\"right\",\"font\":{\"size\":12}},{\"font\":{\"size\":12}},{\"align\":\"left\",\"valign\":\"top\",\"textwrap\":true,\"font\":{\"size\":12}},{\"textwrap\":true,\"font\":{\"size\":12}},{\"align\":\"left\",\"font\":{\"size\":12}},{\"font\":{\"size\":12},\"border\":{\"bottom\":[\"thin\",\"#000\"],\"top\":[\"thin\",\"#000\"],\"left\":[\"thin\",\"#000\"],\"right\":[\"thin\",\"#000\"]}},{\"font\":{\"size\":14}},{\"font\":{\"size\":10}},{\"textwrap\":false,\"font\":{\"size\":12}}],\"validations\":[],\"cols\":{\"0\":{\"width\":69},\"1\":{\"width\":41},\"4\":{\"width\":119},\"5\":{\"width\":147},\"6\":{\"width\":31},\"len\":50},\"merges\":[\"C8:G8\",\"D11:G11\",\"C13:G16\"]}'  WHERE id = '1347373863746539520';

UPDATE jimu_report_data_source SET connect_times = 0;

ALTER TABLE jimu_report_data_source ALTER COLUMN connect_times TINYINT NULL;
ALTER TABLE jimu_report_data_source ADD DEFAULT (1) FOR connect_times WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'连接失败次数','SCHEMA', N'dbo','TABLE', N'jimu_report_data_source','COLUMN', N'connect_times';

ALTER TABLE jimu_report_db_param ALTER COLUMN param_value NVARCHAR(MAX) NULL;
ALTER TABLE jimu_report_db_param ADD DEFAULT NULL FOR param_value WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'参数默认值','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'param_value';

DELETE FROM jimu_report_map WHERE id IN (SELECT id FROM (SELECT id FROM jimu_report_map WHERE NAME IN ( SELECT NAME FROM jimu_report_map GROUP BY NAME HAVING count(NAME) > 1)) T) AND id NOT IN (SELECT id FROM ( SELECT min(id) id FROM jimu_report_map GROUP BY NAME HAVING count(NAME) > 1) M);

CREATE UNIQUE INDEX uniq_jmreport_map_name ON jimu_report_map(name);

UPDATE jimu_report SET VIEW_COUNT = 0 WHERE VIEW_COUNT IS NULL OR VIEW_COUNT = '';
ALTER TABLE jimu_report ALTER COLUMN view_count BIGINT NULL;
ALTER TABLE jimu_report ADD DEFAULT (0) FOR view_count WITH VALUES;
EXEC sp_addextendedproperty 'MS_Description', N'浏览次数','SCHEMA', N'dbo','TABLE', N'jimu_report','COLUMN', N'view_count';

CREATE INDEX idx_jimu_report_id ON jimu_report_db(jimu_report_id);
CREATE INDEX idx_db_source_id ON jimu_report_db(db_source);
CREATE INDEX idx_dbfield_order_num ON jimu_report_db_field(order_num);
CREATE INDEX uniq_jmreport_createby ON jimu_report(create_by);
CREATE INDEX uniq_jmreport_delflag ON jimu_report(del_flag);
CREATE INDEX uniq_link_reportid ON jimu_report_link(report_id);

ALTER TABLE jimu_report ALTER COLUMN json_str NVARCHAR(MAX) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'json字符串','SCHEMA', N'dbo','TABLE', N'jimu_report','COLUMN', N'json_str';

ALTER TABLE jimu_report_link ADD expression NVARCHAR(255) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'表达式','SCHEMA', N'dbo','TABLE', N'jimu_report_link','COLUMN', N'expression';

ALTER TABLE jimu_report_db_param ADD search_flag INT(1) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'查询标识0否1是 默认0','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'search_flag';
UPDATE jimu_report_db_param SET search_flag = 0;

IF EXISTS(Select 1 From Sysobjects Where Name='jimu_dict')
DROP table jimu_dict
    GO
SELECT * INTO jimu_dict FROM sys_dict;

IF EXISTS(Select 1 From Sysobjects Where Name='jimu_dict_item')
DROP table jimu_dict_item
    GO
SELECT * INTO jimu_dict_item FROM sys_dict_item;

ALTER TABLE jimu_report_db_param ADD widget_type NVARCHAR(50) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'查询控件类型','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'widget_type';

ALTER TABLE jimu_report_db_param ADD search_mode INT(1) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'查询模式1简单2范围','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'search_mode';

ALTER TABLE jimu_report_db_param ADD dict_code NVARCHAR(255) NULL;
EXEC sp_addextendedproperty 'MS_Description', N'字典','SCHEMA', N'dbo','TABLE', N'jimu_report_db_param','COLUMN', N'dict_code';
