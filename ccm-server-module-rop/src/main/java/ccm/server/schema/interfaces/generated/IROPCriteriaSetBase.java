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
import ccm.server.schema.interfaces.IROPCriteriaSet;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.InterfaceDefUtility;
import ccm.server.utils.SchemaUtility;

import java.util.Iterator;

public abstract class IROPCriteriaSetBase extends InterfaceDefault implements IROPCriteriaSet {

    public IROPCriteriaSetBase(boolean instantiateRequiredProperties) {
        super(ROPUtils.IROPCriteriaSet_InterfaceDef, instantiateRequiredProperties);
    }

    private IObject getConstructionType(String obid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    /**
     * 创建配置条件
     */
    @Override
    public IObject createROPCriteriaSet(ObjectDTO ROPCriteriaSet) throws Exception {
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject iObject = SchemaUtility.newIObject(
            ROPUtils.ROPCriteriaSet_ClassDef,
            ROPCriteriaSet.getName(),
            ROPCriteriaSet.getDescription(),
            domainInfo.SCHEMA.toString(), "");
        IInterface iInterface = InterfaceDefUtility.verifyInterface(iObject, ROPUtils.IROPCriteriaSet_InterfaceDef);
        String constructionTypeUid = "";
        for (ObjectItemDTO item : ROPCriteriaSet.getItems()) {
            IProperty property = iInterface.Properties().item(item.getDefUID());
            if (property != null) {
                if (item.getDefUID().equals("ConstructionType2ROPCriteria")) {//前端另外传入的参数
                    constructionTypeUid = item.getDisplayValue().toString();
                } else {
                    property.setValue(item.getDisplayValue());
                }
            }
        }
        iObject.ClassDefinition().FinishCreate(iObject);
        //创建关联关系
        IObject constructionTypeObj = getConstructionType(constructionTypeUid);
        IRel constructionType2ROPCriteriaSet = SchemaUtility.newRelationship(ROPUtils.ConstructionType2ROPCriteria_Rel, constructionTypeObj, iObject, true);
        constructionType2ROPCriteriaSet.ClassDefinition().FinishCreate(constructionType2ROPCriteriaSet);
        CIMContext.Instance.Transaction().commit();
        return iObject;
    }

    /**
     * 获取施工分类下的ROP条件配置.
     * <p>根据ConstructionType(施工分类)的Id查询Configurations(ROP条件配置).
     *
     * @param objId       施工分类ID
     * @param pageRequest 分页信息
     * @return 施工分类下的ROP条件配置信息
     * @throws Exception
     */
    @Override
    public IObjectCollection getROPCriteria(PageRequest pageRequest, String objId) throws Exception {
        IObject constructionTypeIObject = getConstructionType(objId);
        IRelCollection iRelCollection = constructionTypeIObject.GetEnd1Relationships().GetRels(ROPUtils.ConstructionType2ROPCriteria_Rel);
        IObjectCollection configurationCriteria = iRelCollection.GetEnd2s();
        return configurationCriteria;
    }

    /**
     * 获根据对象ID获取ROP配置条件
     *
     * @param objId ROP条件配置ID
     * @return 施工分类下的ROP条件配置信息
     * @throws Exception
     */
    @Override
    public IObject getROPCriteriaByUid(String objId) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, ROPUtils.ROPCriteriaSet_ClassDef);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, objId);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    /**
     * 更新ROP规则条件
     *
     * @param ROPCriteriaSet 更新ROP规则组
     * @throws Exception
     */
    @Override
    public void updateROPCriteriaSet(ObjectDTO ROPCriteriaSet) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, ROPUtils.ROPCriteriaSet_ClassDef);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, ROPCriteriaSet.getUid());
        IObjectCollection ROPCriteriaSetCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
        IObject iObject = ROPCriteriaSetCollection.firstOrDefault();
        if (iObject != null) {
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            iObject.BeginUpdate();
            for (ObjectItemDTO item : ROPCriteriaSet.getItems()) {
                //待排除不在接口内的属性
                iObject.Interfaces().item(ROPUtils.IROPCriteriaSet_InterfaceDef, true).Properties().item(item.getDefUID(), true).setValue(item.toValue());
            }
            CIMContext.Instance.Transaction().commit();
        }
    }

    /**
     * 删除ROP规则条件
     *
     * @param uid 删除ROP规则组
     * @throws Exception
     */
    @Override
    public void deleteROPCriteriaSet(String uid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, ROPUtils.ROPCriteriaSet_ClassDef);
        CIMContext.Instance.QueryEngine().addUIDForQuery(queryRequest, operator.equal, uid);
        IObjectCollection ROPCriteriaSetCollection = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (ROPCriteriaSetCollection != null) {
            Iterator<IObject> ROPCriterias = ROPCriteriaSetCollection.GetEnumerator();
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            while (ROPCriterias.hasNext()) {
                IObject item = ROPCriterias.next();
                item.Delete();
            }
            CIMContext.Instance.Transaction().commit();
        }
    }

    @Override
    public String getROPComponentClasses() {
        return this.getProperty(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPComponentClasses").toString();
    }

    @Override
    public void setROPComponentClasses(String ROPComponentClasses) throws Exception {
        this.setPropertyValue(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPComponentClasses", ROPComponentClasses);
    }

    @Override
    public String getROPCalculateProperty() {
        return this.getProperty(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPCalculateProperty").toString();
    }

    @Override
    public void setROPCalculateProperty(String ROPCalculateProperty) throws Exception {
        this.setPropertyValue(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPCalculateProperty", ROPCalculateProperty);
    }

    @Override
    public String getROPUOM() {
        return this.getProperty(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPUOM").toString();
    }

    @Override
    public void setROPUOM(String ROPUOM) throws Exception {
        this.setPropertyValue(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPUOM", ROPUOM);
    }

    @Override
    public String getROPCondition() {
        return this.getProperty(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPCondition").toString();
    }

    @Override
    public void setROPCondition(String ROPCondition) throws Exception {
        this.setPropertyValue(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPCondition", ROPCondition);
    }

    @Override
    public String getROPGroupName() {
        return this.getProperty(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPGroupName").toString();
    }

    @Override
    public void setROPGroupName(String ROPGroupName) throws Exception {
        this.setPropertyValue(ROPUtils.IROPCriteriaSet_InterfaceDef, "ROPGroupName", ROPGroupName);
    }
}
