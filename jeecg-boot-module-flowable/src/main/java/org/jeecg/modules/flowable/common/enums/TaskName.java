package org.jeecg.modules.flowable.common.enums;

/**
 * 流程意见类型
 */
public enum TaskName {
    /**
     * 说明
     */
    STARTAPPLY("发起申请", "发起申请"), ENDFLOW("流程结束", "流程结束");

    /**
     * 类型
     */
    private final String type;

    /**
     * 说明
     */
    private final String remark;

    TaskName(String type, String remark) {
        this.type = type;
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }
}
