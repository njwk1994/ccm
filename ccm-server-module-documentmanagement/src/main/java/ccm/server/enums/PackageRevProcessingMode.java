package ccm.server.enums;

/**
 * 包升版处理模式
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/5/10 17:44
 */
public enum PackageRevProcessingMode {

    EN_DeleteMode("批量删除工作步骤"),
    EN_UpdateMode("批量关联工作步骤"),
    EN_DeleteUpdateMode("批量删除并关联工作步骤");

    PackageRevProcessingMode(String name) {
    }
}
