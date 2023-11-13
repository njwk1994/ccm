package ccm.server.enums;

public enum revState {
    EN_Current("当前"),
    EN_Superseded("作废"),
    EN_Revised("升版"),
    EN_New("新增"),
    EN_Migration("迁移"),
    EN_Working("工作中");

    private String state;

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    revState(String state) {
        this.state = state;
    }

}
