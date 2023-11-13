package ccm.server.entity.vo;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.yulichang.toolkit.Constant;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullRelationship extends Full {

    public static List<String> generateSelectColumns() {
        List<String> columns = new ArrayList<>();
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "obid");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "domain_uid");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "obj_uid");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "prefix");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "config");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "rel_def_uid");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "is_required");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "order_value");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "creation_Date");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "creation_User");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "termination_Date");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "termination_User");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "uid1");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "uid2");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "obid1");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "obid2");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "domain_uid1");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "domain_uid2");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "class_definition_uid1");
//        columns.add(Constant.TABLE_ALIAS + StringPool.DOT + "class_definition_uid2");

        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_Obid");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Obj_Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_ObjObid");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Interface_Def_Uid" + StringPool.SPACE + "AS" + StringPool.SPACE + "interfaceDefUid");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Creation_Date" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_CreationDate");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Termination_Date" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_TerminationDate");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Creation_User" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_CreationUser");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Termination_User" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_TerminationUser");

        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_Obid");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Property_Def_Uid" + StringPool.SPACE + "AS" + StringPool.SPACE + "propertyDefUid");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Interface_Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_interfaceObid");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Interface_Def_Uid" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_interfaceDefUid");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Str_Value" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_strValue");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Uom" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_uom");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Creation_Date" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_creationDate");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Termination_Date" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_terminationDate");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Creation_User" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_creationUser");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Termination_User" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_terminationUser");
        return columns;
    }

    private String obid;
    private String uid;
    private String config;
    private String relDefUid;
    private String domainUid;
    private String uid1;
    private String uid2;
    private String domainUid1;
    private String domainUid2;
    private String name1;
    private String name2;
    private String obid1;
    private String obid2;
    private Boolean isRequired;
    private Integer orderValue;
    private String classDefinitionUid1;
    private String classDefinitionUid2;
    private String prefix;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;
    private String creationUser;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date terminationDate;
    private String terminationUser;

    private String interfaceObid;
    private String interfaceObjObid;
    private String interfaceDefUid;
    private String interfaceCreationUser;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date interfaceCreationDate;
    private String interfaceTerminationUser;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date interfaceTerminationDate;

    private String propertyObid;
    private String propertyDefUid;
    private String propertyObjObid;
    private String strValue;
    private String uom;
    private String propertyCreationUser;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date propertyCreationDate;
    private String propertyTerminationUser;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date propertyTerminationDate;
    private String propertyInterfaceObid;
    private String propertyInterfaceDefUid;
}
