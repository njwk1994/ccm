package ccm.server.schema.interfaces.generated;


import ccm.server.args.createArgs;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.operationState;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.revState;
import ccm.server.schema.interfaces.ICIMRevisionItem;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;

public abstract class ICIMRevisionItemBase extends InterfaceDefault implements ICIMRevisionItem {


    @Override
    public void setCIMRevisionItemMajorRevision(String majorRevision) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemMajorRevision.toString(), majorRevision);
    }

    @Override
    public String getCIMRevisionItemMajorRevision() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemMajorRevision.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCIMRevisionItemMinorRevision(String minorRevision) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemMinorRevision.toString(), minorRevision);
    }

    @Override
    public String getCIMRevisionItemMinorRevision() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemMinorRevision.toString());
        return ValueConversionUtility.toString(property);
    }

    public ICIMRevisionItemBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMRevisionItem.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMRevisionItemRevState(String revState) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemRevState.toString(), revState);
    }

    @Override
    public String getCIMRevisionItemRevState() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemRevState.toString());
        return ValueConversionUtility.toString(property);
    }

    /**
     * 设置关联关系升版状态
     *
     * @param relRevState
     * @throws Exception
     */
    @Override
    public void setCIMRevisionItemDetailRevState(String relRevState) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemDetailRevState.toString(), relRevState);
    }

    /**
     * 获取 关联关系升版状态
     *
     * @return
     * @throws Exception
     */
    @Override
    public String getCIMRevisionItemDetailRevState() throws Exception {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemDetailRevState.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCIMRevisionItemOperationState(String operationState) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemOperationState.toString(), operationState);
    }

    @Override
    public String getCIMRevisionItemOperationState() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMRevisionItem.toString(), propertyDefinitionType.CIMRevisionItemOperationState.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void OnCreate(createArgs e) throws Exception {
        super.OnCreate(e);
    }

    @Override
    public void setObjectDelete(boolean pblnNeedTransaction) throws Exception {
        if (pblnNeedTransaction) SchemaUtility.beginTransaction();
        this.BeginUpdate();
        this.setCIMRevisionItemOperationState(operationState.EN_Deleted.toString());
        this.setCIMRevisionItemRevState(revState.EN_Superseded.toString());
        this.FinishUpdate();
        if (pblnNeedTransaction) SchemaUtility.commitTransaction();
    }
}
