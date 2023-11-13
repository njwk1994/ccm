package ccm.server.enums;

/**
 * 详细升版状态
 *
 * @author HuangTao
 * @version 1.0
 */
public enum DetailRevState {

    EN_Normal("无升版信息"),
    EN_DocumentRevisedDelete("图纸升版删除"),
    EN_ROPRevisedDelete("ROP升版删除"),
    EN_DocumentRevised("图纸升版"),
    EN_ROPRevised("ROP升版");

    private final String name;

    DetailRevState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
