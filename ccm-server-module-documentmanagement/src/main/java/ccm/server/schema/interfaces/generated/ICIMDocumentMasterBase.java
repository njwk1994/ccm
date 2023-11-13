package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.enums.revState;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map;

public abstract class ICIMDocumentMasterBase extends InterfaceDefault implements ICIMDocumentMaster {

    public ICIMDocumentMasterBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMDocumentMaster.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMDocCategory(String docCategory) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocCategory.toString(), docCategory);
    }

    @Override
    public String getCIMDocCategory() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocCategory.toString()).Value().toString();
    }

    @Override
    public void setCIMDocState(String docState) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocState.toString(), docState);
    }

    @Override
    public String getCIMDocState() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocState.toString()).Value().toString();
    }

    @Override
    public void setCIMDocType(String docType) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocType.toString(), docType);
    }

    @Override
    public String getCIMDocType() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocType.toString()).Value().toString();
    }

    @Override
    public void setCIMDocSubType(String docSubType) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocSubType.toString(), docSubType);
    }

    @Override
    public String getCIMDocSubType() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocSubType.toString()).Value().toString();
    }

    @Override
    public void setCIMDocTitle(String docTitle) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocTitle.toString(), docTitle);
    }

    @Override
    public String getCIMDocTitle() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentMaster.toString(), propertyDefinitionType.CIMDocTitle.toString()).Value().toString();
    }

    @Override
    public IObjectCollection getDocumentRevisions() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.CIMDocumentRevisions.toString());
        return SchemaUtility.hasValue(relCollection) ? relCollection.GetEnd2s() : null;
    }


    @Override
    public ICIMDocumentRevision getNewestRevision() throws Exception {
        IObject newestRevision = SchemaUtility.getNewestRevision(this);
        return newestRevision != null ? newestRevision.toInterface(ICIMDocumentRevision.class) : null;
    }

    @Override
    public IObjectCollection getLatestRevisions() throws Exception {
        IObjectCollection documentRevisions = this.getDocumentRevisions();
        if (documentRevisions.hasValue()) {
            IObjectCollection lcolContainer = new ObjectCollection();
            Iterator<IObject> iterator = documentRevisions.GetEnumerator();
            while (iterator.hasNext()) {
                ICIMDocumentRevision revision = iterator.next().toInterface(ICIMDocumentRevision.class);
                if (!revision.getCIMRevState().equals(revState.EN_Superseded.toString())) {
                    lcolContainer.append(revision);
                }
            }
            return lcolContainer;
        }
        return null;
    }

    @Override
    public void setMasterPropertiesByJsonInfo(JSONObject docMasterInfo) throws Exception {
        if (docMasterInfo != null) {
            for (Map.Entry<String, Object> item : docMasterInfo.entrySet()) {
                SchemaUtility.setObjPropertyValue(this, interfaceDefinitionType.ICIMDocumentMaster.toString(), item.getKey(), item.getValue(), false, false);
            }
        }
    }

    @Override
    public ICIMObjClass getPrimaryClassification() throws Exception {
        IObject end1 = this.GetEnd2Relationships().GetRel(relDefinitionType.CIMPrimaryClassification.toString(), false).GetEnd1();
        return end1 != null ? end1.toInterface(ICIMObjClass.class) : null;
    }

    @Override
    public boolean isRevised() throws Exception {
        ICIMRevisionItem icimRevisionItem = this.toInterface(ICIMRevisionItem.class);
        if (icimRevisionItem == null) {
            throw new Exception("转换为ICIMRevisionItem对象失败!");
        }
        return revState.EN_Revised.toString().equalsIgnoreCase(icimRevisionItem.getCIMRevisionItemRevState());
    }

    @Override
    public void clearRevisedStatus() throws Exception {
        if (this.isRevised()) {
            ICIMRevisionItem icimRevisionItem = this.toInterface(ICIMRevisionItem.class);
            icimRevisionItem.BeginUpdate();
            icimRevisionItem.setCIMRevisionItemRevState(revState.EN_Current.toString());
            icimRevisionItem.FinishUpdate();
        }
    }

    @Override
    public IObjectCollection getDesignObjects() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.CCMDocument2DesignObj.toString(), false);
        return SchemaUtility.hasValue(relCollection) ? relCollection.GetEnd2s() : null;
    }

    @Override
    public void fillPropForObjectDTO(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null && CommonUtility.hasValue(objectDTO.getItems())) {
            ICIMDocumentRevision newestRevision = this.getNewestRevision();
            if (newestRevision == null) {
                throw new Exception("未找到Master对象关联的Revision对象");
            }
            ICIMDocumentVersion documentVersion = newestRevision.getNewestDocumentVersion();
            if (documentVersion == null) {
                throw new Exception("未找到有效的Version对象!");
            }
            for (ObjectItemDTO propItem : objectDTO.getItems()) {
                String lstrPropDefUID = propItem.getDefUID();
                String lstrInterfaceDef = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(lstrPropDefUID);
                if (!this.Interfaces().hasInterface(lstrInterfaceDef)) {
                    if (newestRevision.Interfaces().hasInterface(lstrInterfaceDef)) {
                        propItem.setDisplayValue(newestRevision.Interfaces().getPropertyValue(lstrInterfaceDef, lstrPropDefUID));
                    } else {
                        if (documentVersion.Interfaces().hasInterface(lstrInterfaceDef)) {
                            propItem.setDisplayValue(documentVersion.Interfaces().getPropertyValue(lstrInterfaceDef, lstrPropDefUID));
                        }
                    }
                }
            }
        }
    }
}
