package ccm.server.enums;

public enum operationPurpose {
    create,
    update,
    query,
    info,
    all,
    none;

    private String uid;

    private operationPurpose() {
        this.setUid("operationPurpose_" + this.toString());
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }
}
