package ccm.server.schema.interfaces;

public interface ICIMRevisionItem extends IObject {

    void setCIMRevisionItemMajorRevision(String majorRevision) throws Exception;

    String getCIMRevisionItemMajorRevision();

    void setCIMRevisionItemMinorRevision(String minorRevision) throws Exception;

    String getCIMRevisionItemMinorRevision();

    void setCIMRevisionItemRevState(String revState) throws Exception;

    String getCIMRevisionItemRevState() throws Exception;

    /**
     * 设置关联关系升版状态
     *
     * @param relRevState
     * @throws Exception
     */
    void setCIMRevisionItemDetailRevState(String relRevState) throws Exception;

    /**
     * 获取 关联关系升版状态
     *
     * @return
     * @throws Exception
     */
    String getCIMRevisionItemDetailRevState() throws Exception;

    void setCIMRevisionItemOperationState(String operationState) throws Exception;

    String getCIMRevisionItemOperationState() throws Exception;

    void setObjectDelete(boolean pblnNeedTransaction) throws Exception;
}
