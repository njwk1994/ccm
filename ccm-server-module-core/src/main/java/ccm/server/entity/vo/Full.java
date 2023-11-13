package ccm.server.entity.vo;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.github.yulichang.toolkit.Constant;

import java.util.ArrayList;
import java.util.List;

public class Full {
    public static List<String> generateSelectColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_obid");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Obj_Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_objObid");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Interface_Def_Uid" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_interfaceDefUid");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Creation_Date" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_creationDate");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Termination_Date" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_terminationDate");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Creation_User" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_creationUser");
        columns.add(Constant.TABLE_ALIAS + 1 + StringPool.DOT + "Termination_User" + StringPool.SPACE + "AS" + StringPool.SPACE + "interface_terminationUser");

        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_obid");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Obj_Obid" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_objObid");
        columns.add(Constant.TABLE_ALIAS + 2 + StringPool.DOT + "Property_Def_Uid" + StringPool.SPACE + "AS" + StringPool.SPACE + "property_propertyDefUid");
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
}
