package ccm.server.enums;

public enum workStepStatus {
    EN_Typical("典型"),
    EN_RevisedDelete("图纸升版删除"),
    EN_RevisedReserve("图纸升版遗留"),
    EN_RevisedTempProcess("图纸升版临时处理"),
    EN_ROPDelete("ROP升版删除");

    workStepStatus(String name) {
    }

}
