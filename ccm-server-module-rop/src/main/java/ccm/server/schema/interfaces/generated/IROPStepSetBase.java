package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.domainInfo;
import ccm.server.enums.operator;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.utils.ROPUtils;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IROPStepSet;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.SchemaUtility;

import java.util.Iterator;

public abstract class IROPStepSetBase extends InterfaceDefault implements IROPStepSet {

    public IROPStepSetBase(boolean instantiateRequiredProperties) {
        super(ROPUtils.IROPStepSet_InterfaceDef, instantiateRequiredProperties);
    }

    private IObject getConstructionType(String obid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    /**
     * 获取施工分类下的ROP步骤.
     * <p>根据ConstructionType(施工分类)的Id查询Configurations(ROP条件配置).
     *
     * @param constructionTypeObid 施工分类ID
     * @param pageRequest          分页信息
     * @return 施工分类下的ROP步骤
     * @throws Exception
     */
    @Override
    public IObjectCollection getROPStep(PageRequest pageRequest, String constructionTypeObid) throws Exception {
        IObject constructionTypeIObject = getConstructionType(constructionTypeObid);
        IRelCollection iRelCollection = constructionTypeIObject.GetEnd1Relationships().GetRels(ROPUtils.ConstructionType2ROPStep_Rel);
        return iRelCollection.GetEnd2s();
    }

    /**
     * 获取ROP施工步骤.
     *
     * @param uid 施工分类ID
     * @return ROP步骤
     * @throws Exception
     */
    @Override
    public IObject getROPStepByUid(String uid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, ROPUtils.ROPStepSet_ClassDef);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        IObjectCollection ROPStepSetCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
        return ROPStepSetCollection.firstOrDefault();
    }

    /**
     * 创建ROP步骤模板
     *
     * @param ROPStepSet 创建ROP步骤模板
     * @throws Exception
     */
    @Override
    public IObject createROPStepSet(ObjectDTO ROPStepSet) throws Exception {
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject iObject = SchemaUtility.newIObject(ROPUtils.IROPStepSet_InterfaceDef, "", "", domainInfo.SCHEMA.toString(), "");
        IInterface iROPStepSet = iObject.Interfaces().item(ROPUtils.IROPStepSet_InterfaceDef);
        String constructionTypeUid = "";
        for (ObjectItemDTO item : ROPStepSet.getItems()) {
            IProperty property = iROPStepSet.Properties().item(item.getDefUID());
            if (property != null) {
                property.setValue(item.getDisplayValue());
            } else {
                if (item.getDefUID().equals("ConstructionType2ROPStep")) {//前端另外传入的参数
                    constructionTypeUid = item.getDisplayValue().toString();
                }
            }
        }
        // 结束创建
        iObject.ClassDefinition().FinishCreate(iObject);
        //创建关联关系
        IObject constructionTypeObj = getConstructionType(constructionTypeUid);
        IRel constructionType2ROPCriteriaSet = SchemaUtility.newRelationship(ROPUtils.ConstructionType2ROPStep_Rel, constructionTypeObj, iObject, true);
        constructionType2ROPCriteriaSet.ClassDefinition().FinishCreate(constructionType2ROPCriteriaSet);
        CIMContext.Instance.Transaction().commit();
        return iObject;
    }

    /**
     * 更新ROP步骤模板
     *
     * @param ROPStepSet 更新ROP步骤模板
     * @throws Exception
     */
    @Override
    public void updateROPStepSet(ObjectDTO ROPStepSet) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, ROPUtils.ROPStepSet_ClassDef);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, ROPStepSet.getUid());
        IObjectCollection ROPStepSetCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
        IObject iObject = ROPStepSetCollection.firstOrDefault();
        if (iObject != null) {
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            iObject.BeginUpdate();
            for (ObjectItemDTO item : ROPStepSet.getItems()) {
                //待排除不在接口内部的属性，前端传入的其他自定义参数
                iObject.Interfaces().item(ROPUtils.IROPStepSet_InterfaceDef, true).Properties().item(item.getDefUID(), true).setValue(item.toValue());
            }
            CIMContext.Instance.Transaction().commit();
        }
    }

    /**
     * 删除ROP施工步骤
     *
     * @param uid 删除ROP规则组
     * @throws Exception
     */
    @Override
    public void deleteROPStepSet(String uid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, ROPUtils.ROPStepSet_ClassDef);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        IObjectCollection ROPStepSetCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (ROPStepSetCollection != null) {
            Iterator<IObject> ROPSteps = ROPStepSetCollection.GetEnumerator();
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            while (ROPSteps.hasNext()) {
                IObject item = ROPSteps.next();
                item.Delete();
            }
            CIMContext.Instance.Transaction().commit();
        }
    }

    @Override
    public String getROPConstructionPurpose() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPConstructionPurpose").toString();
    }

    @Override
    public void setROPConstructionPurpose(String ROPConstructionPurpose) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPConstructionPurpose", ROPConstructionPurpose);
    }

    @Override
    public String getROPPurposeOrder() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPPurposeOrder").toString();
    }

    @Override
    public void setROPPurposeOrder(String ROPPurposeOrder) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPPurposeOrder", ROPPurposeOrder);
    }

    @Override
    public String getROPStepName() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPStepName").toString();
    }

    @Override
    public void setROPStepName(String ROPStepName) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPStepName", ROPStepName);
    }

    @Override
    public String getROPStepOrder() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPStepOrder").toString();
    }

    @Override
    public void setROPStepOrder(String ROPStepOrder) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPStepOrder", ROPStepOrder);
    }

    @Override
    public boolean getROPConsumesMaterial() {
        return Boolean.parseBoolean(this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPConsumesMaterial").toString());
    }

    @Override
    public void setROPConsumesMaterial(String ROPConsumesMaterial) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPConsumesMaterial", ROPConsumesMaterial);
    }

    @Override
    public String getROPCalculateProperty() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPCalculateProperty").toString();
    }

    @Override
    public void setROPCalculateProperty(String ROPCalculateProperty) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPCalculateProperty", ROPCalculateProperty);
    }

    @Override
    public String getROPProgressWeight() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPProgressWeight").toString();
    }

    @Override
    public void setROPProgressWeight(String ROPProgressWeight) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPProgressWeight", ROPProgressWeight);
    }

    @Override
    public String getROPGroupName() {
        return this.getProperty(ROPUtils.IROPStepSet_InterfaceDef, "ROPGroupName").toString();
    }

    @Override
    public void setROPGroupName(String ROPGroupName) throws Exception {
        this.setPropertyValue(ROPUtils.IROPStepSet_InterfaceDef, "ROPGroupName", ROPGroupName);
    }
}
