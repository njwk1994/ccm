package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.utils.DocumentUtils;
import ccm.server.utils.SchemaUtility;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class ICIMRevisionCollectionsBase extends InterfaceDefault implements ICIMRevisionCollections {

    public ICIMRevisionCollectionsBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMRevisionCollections.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMPipeUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMPipeUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCIMPipeUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMPipeUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCIMPipeComponentUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMPipeComponentUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCIMPipeComponentUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMPipeComponentUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCIMSupportUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMSupportUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCIMSupportUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMSupportUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCIMWeldUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMWeldUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCIMWeldUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMWeldUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCIMSpoolUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMSpoolUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCIMSpoolUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CIMSpoolUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public String[] getCCMPipeLineUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMPipeLineUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMPipeLineUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMPipeLineUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMBoltUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMBoltUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMBoltUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMBoltUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMGasketUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMGasketUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMGasketUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMGasketUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMEquipUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMEquipUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMEquipUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMEquipUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMSubEquipUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSubEquipUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMSubEquipUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSubEquipUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMCableTrayUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMCableTrayUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMCableTrayUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMCableTrayUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMCableTrayComponentUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMCableTrayComponentUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMCableTrayComponentUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMCableTrayComponentUIDs.toString(), lstrUIDs);
    }

    @NotNull
    private String getUiDs(String[] uids) {
        String lstrUIDs = "";
        if (uids != null && uids.length > 0) {
            lstrUIDs = String.join(",", uids);
        }
        return lstrUIDs;
    }

    @Override
    public String[] getCCMCableUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMCableUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMCableUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMCableUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMInstrumentUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMInstrumentUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMInstrumentUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMInstrumentUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMDuctLineUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMDuctLineUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMDuctLineUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMDuctLineUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMDuctComponentUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMDuctComponentUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMDuctComponentUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMDuctComponentUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMJunctionBoxUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMJunctionBoxUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMJunctionBoxUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMJunctionBoxUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMSTPartUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSTPartUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMSTPartUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSTPartUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMSTComponentUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSTComponentUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMSTComponentUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSTComponentUIDs.toString(), lstrUIDs);
    }

    @Override
    public String[] getCCMSTBlockUIDs() {
        Object displayValue = SchemaUtility.getSpecialPropertyValue(this, interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSTBlockUIDs.toString());
        if (!StringUtils.isEmpty(displayValue)) {
            return displayValue.toString().split(",");
        }
        return new String[0];
    }

    @Override
    public void setCCMSTBlockUIDs(String... uids) throws Exception {
        String lstrUIDs = getUiDs(uids);
        this.setPropertyValue(interfaceDefinitionType.ICIMRevisionCollections.toString(), propertyDefinitionType.CCMSTBlockUIDs.toString(), lstrUIDs);
    }

    /*
     * @Descriptions : 比对被删除的设计对象
     * @Author: Chen Jing
     * @Date: 2022/4/25 18:19
     * @param designObjects 当前发布的设计对象集合
     * @Return:
     */
    @Override
    public IObjectCollection updateHasDeletedDesignObjStatusByNewDesignObjUIDs(List<JSONObject> designObjs, IRelCollection relatedDesignObjs, boolean systemUpgradeDeleteData) throws Exception {
        IObjectCollection lcolHasDeletedDesignObjects = new ObjectCollection();
        if (CommonUtility.hasValue(designObjs)) {
            //按照类型分组 这里面可能已经存在有的类型已经被删了,没有了,所以要额外判断类型
            Map<String, List<JSONObject>> groupByClassDef = designObjs.stream().collect(Collectors.groupingBy(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID)));
            //创建集合保存被删除的对象
            Map<String, List<String>> ldicDeletedDesignObjs = new HashMap<>();
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMPipe, this.getCIMPipeUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMPipeComponent, this.getCIMPipeComponentUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMSpool, this.getCIMSpoolUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMWeld, this.getCIMWeldUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMSupport, this.getCIMSupportUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMBolt, this.getCCMBoltUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMGasket, this.getCCMGasketUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMEquip, this.getCCMEquipUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMSubEquip, this.getCCMSubEquipUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMCableTray, this.getCCMCableTrayUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMCableTrayComponent, this.getCCMCableTrayComponentUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMCable, this.getCCMCableUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMInstrument, this.getCCMInstrumentUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMJunctionBox, this.getCCMJunctionBoxUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMDuctLine, this.getCCMDuctLineUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMDuctComponent, this.getCCMDuctComponentUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMSTPart, this.getCCMSTPartUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMSTComponent, this.getCCMSTComponentUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMSTBlock, this.getCCMSTBlockUIDs());
            getHasDeletedDesignObjects(groupByClassDef, ldicDeletedDesignObjs, classDefinitionType.CCMPipeLine, this.getCCMPipeLineUIDs());
            if (ldicDeletedDesignObjs.size() > 0) {
                for (Map.Entry<String, List<String>> entry : ldicDeletedDesignObjs.entrySet()) {
                    IObjectCollection lcolHasDeleteDesignObjsFromDoc = CIMContext.Instance.ProcessCache().queryObjectsByUIDAndClassDefinition(entry.getValue(), entry.getKey());
                    if (SchemaUtility.hasValue(lcolHasDeleteDesignObjsFromDoc)) {
                        Iterator<IObject> e = lcolHasDeleteDesignObjsFromDoc.GetEnumerator();
                        while (e.hasNext()) {
                            IObject lobjDesignObject = e.next();
                            ICIMRevisionItem revisionItem = lobjDesignObject.toInterface(ICIMRevisionItem.class);
                            if (systemUpgradeDeleteData) {
                                //开启升版删除 ,变更状态
                                revisionItem.setObjectDelete(false);
                                lcolHasDeletedDesignObjects.append(lobjDesignObject);
                            } else {
                                //未开启升版删除 ,只端开关联关系
                                if (SchemaUtility.hasValue(relatedDesignObjs)) {
                                    List<IRel> rels = relatedDesignObjs.toList(IRel.class);
                                    IRel iRel = rels.stream().filter(r -> {
                                        try {
                                            return r.UID2().equalsIgnoreCase(lobjDesignObject.UID());
                                        } catch (Exception ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }).findFirst().orElse(null);
                                    if (null != iRel) {
                                        iRel.Delete();
                                    }
                                }
                            }
                            // 2022.12.26 HT 添加施工系统ROP 工作步骤 和 包标记处理开关
                            if (DocumentUtils.constructionStatus) {
                                log.info("配置文件开关状态:{}", DocumentUtils.constructionStatus);
                                IROPExecutableItem iropExecutableItem = lobjDesignObject.toInterface(IROPExecutableItem.class);
                                iropExecutableItem.updateWorkStepForDeletedDesignObj(false);
                            }
                            // 2022.12.26 HT 添加施工系统ROP 工作步骤 和 包标记处理开关

                        }
                    }
                }
            }
        }
        return lcolHasDeletedDesignObjects;
    }

    private void getHasDeletedDesignObjects(Map<String, List<JSONObject>> newDesignObjs, Map<String, List<String>> deletedDesignObjsContainer, classDefinitionType classDef, String[] oriDesignObjUIDs) {
        //如果新的对象中有该类型对象,判断哪些是删除的
        List<String> lcolHasDeletedUIDs = new ArrayList<>();
        if (newDesignObjs.containsKey(classDef.toString())) {
            List<String> lcolNewDesignObjUIDs = CommonUtility.getUIDsFromJSONObjects(newDesignObjs.get(classDef.toString()));
            if (oriDesignObjUIDs != null && oriDesignObjUIDs.length > 0) {
                for (String lstrOriDesignObjUID : oriDesignObjUIDs) {
                    if (lcolNewDesignObjUIDs == null || lcolNewDesignObjUIDs.size() <= 0 || !lcolNewDesignObjUIDs.contains(lstrOriDesignObjUID)) {
                        lcolHasDeletedUIDs.add(lstrOriDesignObjUID);
                    }
                }
            }
        } else {
            //如果新的对象中没有该类型对象,说明该类型对象被全部删除了
            if (oriDesignObjUIDs != null && oriDesignObjUIDs.length > 0) {
                lcolHasDeletedUIDs.addAll(Arrays.asList(oriDesignObjUIDs));
            }
        }
        if (CommonUtility.hasValue(lcolHasDeletedUIDs)) {
            deletedDesignObjsContainer.put(classDef.toString(), lcolHasDeletedUIDs);
        }
    }

    @Override
    public void setDesignObjUIDs(List<JSONObject> designObjs) throws Exception {
        if (CommonUtility.hasValue(designObjs)) {
            Map<String, List<JSONObject>> groupByClassDef = designObjs.stream().collect(Collectors.groupingBy(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID)));
            for (Map.Entry<String, List<JSONObject>> entry : groupByClassDef.entrySet()) {
                String lstrClassDef = entry.getKey();
                String[] larrUIDs = CommonUtility.getUIDsFromJSONObjectsWithArr(entry.getValue());
                if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMPipe.toString())) {
                    this.setCIMPipeUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMPipeComponent.toString())) {
                    this.setCIMPipeComponentUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMSpool.toString())) {
                    this.setCIMSpoolUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMSupport.toString())) {
                    this.setCIMSupportUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMWeld.toString())) {
                    this.setCIMWeldUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMPipeLine.toString())) {
                    this.setCCMPipeLineUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMPipe.toString())) {
                    this.setCIMPipeUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMBolt.toString())) {
                    this.setCCMBoltUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMGasket.toString())) {
                    this.setCCMGasketUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMEquip.toString())) {
                    this.setCCMEquipUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMSubEquip.toString())) {
                    this.setCCMSubEquipUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMCable.toString())) {
                    this.setCCMCableUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMCableTray.toString())) {
                    this.setCCMCableTrayUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMCableTrayComponent.toString())) {
                    this.setCCMCableTrayComponentUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMInstrument.toString())) {
                    this.setCCMInstrumentUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMJunctionBox.toString())) {
                    this.setCCMJunctionBoxUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMDuctLine.toString())) {
                    this.setCCMDuctLineUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMDuctComponent.toString())) {
                    this.setCCMDuctComponentUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMSTPart.toString())) {
                    this.setCCMSTPartUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMSTComponent.toString())) {
                    this.setCCMSTComponentUIDs(larrUIDs);
                } else if (lstrClassDef.equalsIgnoreCase(classDefinitionType.CCMSTBlock.toString())) {
                    this.setCCMSTBlockUIDs(larrUIDs);
                }
            }
        }
    }
}
