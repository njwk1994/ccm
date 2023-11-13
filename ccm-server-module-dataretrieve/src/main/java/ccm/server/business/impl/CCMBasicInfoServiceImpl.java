package ccm.server.business.impl;

import ccm.server.business.ICCMBasicInfoService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.ExpansionMode;
import ccm.server.enums.domainInfo;
import ccm.server.enums.operator;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICCMComponentCategory;
import ccm.server.schema.interfaces.ICCMConstructionType;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IInterface;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.InterfaceDefUtility;
import ccm.server.utils.PageUtility;
import ccm.server.utils.SchemaUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Iterator;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2021/12/29 13:01
 */
@Service
public class CCMBasicInfoServiceImpl implements ICCMBasicInfoService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /* =========================================== 施工分类 start =========================================== */

    /**
     * 获取施工分类表单
     *
     * @param formPurpose 表单类型
     * @return
     * @throws Exception
     */
    @Override
    public ObjectDTO getConstructionTypeForm(String formPurpose) throws Exception {
        IObject form = schemaBusinessService.getForm(formPurpose, DataRetrieveUtils.CCM_CONSTRUCTION_TYPE);
        if (form != null)
            return form.toInterface(ICIMForm.class).generatePopup(formPurpose);
        else
            return schemaBusinessService.generateDefaultPopup(DataRetrieveUtils.CCM_CONSTRUCTION_TYPE);
    }

    /**
     * 获取所有施工分类
     * <p>{@param pageIndex}和{@param pageSize}都大于0时才分页,否则获取所有施工分类</p>
     *
     * @param pageRequest pageIndex 当前页 pageSize  每页条数
     * @return 施工分类的分页数据
     * @throws Exception
     */
    @Override
    public IObjectCollection getConstructionTypes(PageRequest pageRequest) throws Exception {
        // 开启事务
//        if (!CIMContext.Instance.Transaction().inTransaction()) {
//            CIMContext.Instance.Transaction().start();
//        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    /**
     * 根据OBID获取施工分类
     *
     * @param obid pageIndex 当前页 pageSize  每页条数
     * @return 施工分类的分页数据
     * @throws Exception
     */
    @Override
    public IObject getConstructionTypeByOBID(String obid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    /**
     * 新建施工分类
     *
     * @param toCreateConstructionTypeDTO 施工分类数据对象
     * @return 施工分类ID
     * @throws Exception
     */
    @Override
    public IObject createConstructionType(ObjectDTO toCreateConstructionTypeDTO) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newConstructionType = SchemaUtility.newIObject(DataRetrieveUtils.CCM_CONSTRUCTION_TYPE,
            toCreateConstructionTypeDTO.getName(),
            toCreateConstructionTypeDTO.getDescription(),
            "", "");
        /*InterfaceDefUtility.addInterface(newConstructionType,
                DataRetrieveUtils.I_CONSTRUCTION_TYPE);*/
        for (ObjectItemDTO item : toCreateConstructionTypeDTO.getItems()) {
            newConstructionType.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newConstructionType.ClassDefinition().FinishCreate(newConstructionType);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newConstructionType;
    }

    /**
     * 删除施工分类
     * <p>根据ConstructionType(施工分类)的Id删除ConstructionType(施工分类).</p>
     *
     * @param constructionTypeOBID 施工分类OBID
     * @throws Exception
     */
    @Override
    public void deleteConstructionType(String constructionTypeOBID) throws Exception {
        // 获取已存在的施工分类
        if (!StringUtils.isEmpty(constructionTypeOBID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, constructionTypeOBID);
            IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            if (object != null) {
                // 开启事务
                if (!CIMContext.Instance.Transaction().inTransaction()) {
                    CIMContext.Instance.Transaction().start();
                }
                object.Delete();
                // 提交事务
                CIMContext.Instance.Transaction().commit();
            }
        }
    }

    /**
     * 更新施工分类
     *
     * @param toUpdateConstructionType 待更新施工分类
     * @throws Exception
     */
    @Override
    public void updateConstructionType(ObjectDTO toUpdateConstructionType) throws Exception {
        // 获取已存在的施工分类
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdateConstructionType.getObid());
        Iterator<IObject> existConstructionTypes = CIMContext.Instance.QueryEngine().query(queryRequest).GetEnumerator();
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        while (existConstructionTypes.hasNext()) {
            // 更新施工分类属性
            IObject existConstructionType = existConstructionTypes.next();
            existConstructionType.BeginUpdate();
            for (ObjectItemDTO item : toUpdateConstructionType.getItems()) {
                existConstructionType.Interfaces().item(DataRetrieveUtils.I_CONSTRUCTION_TYPE, true)
                    .Properties().item(item.getDefUID(), true).setValue(item.toValue());
            }
            existConstructionType.FinishUpdate();
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    /**
     * 获取施工分类下的设计类型
     *
     * @param constructionTypeId 施工分类ID
     * @return 当前施工分类下的设计类型的分页数据
     * @throws Exception
     */
    @Override
    public IObjectCollection getDesignTypesUnderConstructionType(String constructionTypeId, PageRequest pageRequest) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }

        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, constructionTypeId);
        // 添加关联
        CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest,
            DataRetrieveUtils.REL_CONSTRUCTION_TYPE_2_COMPONENT_CATEGORY,
            DataRetrieveUtils.I_CONSTRUCTION_TYPE, propertyDefinitionType.UID.toString(), operator.equal, constructionTypeId, ExpansionMode.relatedObject);

        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        // 返回数据
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    /**
     * 添加设计数据类型到施工分类
     *
     * @param constructionTypeUID 施工分类UID
     * @param designType          设计数据类型
     * @return 设计数据类型ID
     * @throws Exception
     */
    @Override
    public void addDesignTypeIntoConstructionType(String constructionTypeUID, ObjectDTO designType) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        // 新建设计类型
        IObject newDesignType = createDesignType(designType);
        // 获取施工分类
        IObject constructionTypeObj = SchemaUtility.newIObject(DataRetrieveUtils.CCM_CONSTRUCTION_TYPE, "", "",
            domainInfo.SCHEMA.toString(), constructionTypeUID);
        IInterface iInterface = InterfaceDefUtility.verifyInterface(constructionTypeObj, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        ICCMConstructionType iConstructionType = iInterface.toInterface(ICCMConstructionType.class);
        // 建立关联关系
        iConstructionType.createConstructionType2ComponentCategory(constructionTypeObj, newDesignType);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }
    /* =========================================== 施工分类 end =========================================== */
    /* =========================================== 设计数据类型 start =========================================== */

    /**
     * 获取设计类型表单
     *
     * @param formPurpose
     * @return
     * @throws Exception
     */
    @Override
    public ObjectDTO getDesignTypeForm(String formPurpose) throws Exception {
        IObject form = schemaBusinessService.generateForm(DataRetrieveUtils.CCM_COMPONENT_CATEGORY);
        return form.toObjectDTO();
    }

    /**
     * 新建设计类型
     *
     * @param toCreateDesignType 待创建设计类型数据
     * @return 新设计类型
     * @throws Exception
     */
    @Override
    public IObject createDesignType(ObjectDTO toCreateDesignType) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newDesignType = SchemaUtility.newIObject(DataRetrieveUtils.CCM_COMPONENT_CATEGORY, "", "",
            domainInfo.SCHEMA.toString(), "");
        // 检查接口
        IInterface iInterface = InterfaceDefUtility.verifyInterface(newDesignType, DataRetrieveUtils.I_COMPONENT_CATEGORY);
        ICCMComponentCategory iComponentCategory = iInterface.toInterface(ICCMComponentCategory.class);
        iComponentCategory.createDesignType(newDesignType, toCreateDesignType);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newDesignType;
    }

    /**
     * 删除设计类型(同时删除关联关系)
     *
     * @param designTypeId 设计类型ID
     * @throws Exception
     */
    @Override
    public void deleteDesignType(String designTypeId) throws Exception {
        // 获取已存在的设计类型
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, designTypeId);
        Iterator<IObject> existDesignTypes = CIMContext.Instance.QueryEngine().query(queryRequest).GetEnumerator();
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject componentCategory = SchemaUtility.newIObject(DataRetrieveUtils.CCM_COMPONENT_CATEGORY, "", "",
            domainInfo.SCHEMA.toString(), "");
        ICCMComponentCategory iComponentCategory = componentCategory.toInterface(ICCMComponentCategory.class);
        // 删除设计类型
        iComponentCategory.deleteDesignType(existDesignTypes);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    /**
     * 更新设计类型
     *
     * @param toUpdateDesignType 待更新设计类型
     * @throws Exception
     */
    @Override
    public void updateDesignType(ObjectDTO toUpdateDesignType) throws Exception {
        // 获取已存在的设计类型
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_CONSTRUCTION_TYPE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdateDesignType.getObid());
        Iterator<IObject> existDesignTypes = CIMContext.Instance.QueryEngine().query(queryRequest).GetEnumerator();
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject iObject = SchemaUtility.newIObject(DataRetrieveUtils.CCM_COMPONENT_CATEGORY, "", "",
            domainInfo.SCHEMA.toString(), "");
        ICCMComponentCategory iComponentCategory = iObject.toInterface(ICCMComponentCategory.class);
        iComponentCategory.updateDesignType(existDesignTypes, toUpdateDesignType);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }
    /* =========================================== 设计数据类型 end =========================================== */
}
