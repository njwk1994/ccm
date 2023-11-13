package ccm.server.schema.model;

import ccm.server.args.*;
import ccm.server.entity.MetaDataObj;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public class InterfaceDefault extends InterfaceBase {
    @Override
    public MetaDataObj toMetaDataObject() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).toMetaDataObject();
    }

    @Override
    public void OnCreate(createArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCreate(e);
    }

    @Override
    public void OnCreating(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCreating(e);
    }

    @Override
    public void OnCreated(suppressibleArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCreated(e);
    }

    @Override
    public void OnDelete(suppressibleArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnDelete(e);
    }

    @Override
    public void OnDeleting(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnDeleting(e);
    }

    @Override
    public void OnDeleted(suppressibleArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnDeleted(e);
    }

    @Override
    public void OnPreProcess(createArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnPreProcess(e);
    }

    @Override
    public void OnRelationshipAdd(relArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnRelationshipAdd(e);
    }

    @Override
    public void OnRelationshipAdded(relArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnRelationshipAdded(e);
    }

    @Override
    public void OnRelationshipAdding(relArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnRelationshipAdding(e);
    }

    @Override
    public void OnRelationshipRemoved(relArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnRelationshipRemoved(e);
    }

    @Override
    public void OnRelationshipRemoving(relArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnRelationshipRemoving(e);
    }

    @Override
    public void OnTerminate(suppressibleArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnTerminate(e);
    }

    @Override
    public void OnTerminating(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnTerminating(e);
    }

    @Override
    public void OnTerminated(suppressibleArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnTerminated(e);
    }

    @Override
    public void OnUpdate(updateArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnUpdate(e);
    }

    @Override
    public void OnUpdated(suppressibleArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnUpdated(e);
    }

    @Override
    public void OnUpdating(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnUpdating(e);
    }

    @Override
    public void OnValidate(validateArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnValidate(e);
    }

    @Override
    public void commit() {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).commit();
    }

    @Override
    public IObject Copy() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).Copy();
    }


    public InterfaceDefault(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    @Override
    public ICIMConfigurationItem getConfig() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).getConfig();
    }

    @Override
    public boolean IsUniqueKeyUniqueInConfig() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).IsUniqueKeyUniqueInConfig();
    }

    @Override
    public boolean IsUniqueKeyUnique() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).IsUniqueKeyUnique();
    }

    @Override
    public void Delete() throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).Delete();
    }

    @Override
    public void Delete(boolean pblnSuppressEvent) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).Delete(pblnSuppressEvent);
    }

    @Override
    public void doDelete() throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).doDelete();
    }

    @Override
    public void Terminate() throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).Terminate();
    }

    @Override
    public void Terminate(boolean pblnSuppressEvent) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).Terminate(pblnSuppressEvent);
    }

    @Override
    public boolean Validate() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).Validate();
    }

    @Override
    public void BeginUpdate() throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).BeginUpdate();
    }

    @Override
    public void BeginUpdate(boolean pblnValidateForClaim) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).BeginUpdate(pblnValidateForClaim);
    }

    @Override
    public void BeginUpdate(boolean pblnValidateForClaim, boolean pblnSuppressEvents) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).BeginUpdate(pblnValidateForClaim, pblnSuppressEvents);
    }

    @Override
    public void rollback() throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).rollback();
    }

    @Override
    public void OnCopy(copyArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCopy(e);
    }

    @Override
    public void OnCopies(copyArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCopies(e);
    }

    @Override
    public void OnCopying(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCopying(e);
    }

    @Override
    public void OnCreatingValidation(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnCreatingValidation(e);
    }

    @Override
    public void OnRelationshipUpdating(relArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnRelationshipUpdating(e);
    }

    @Override
    public void OnUpdatingValidation(cancelArgs e) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnUpdatingValidation(e);
    }

    @Override
    public void FinishUpdate() throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).FinishUpdate();
    }

    @Override
    public IRelCollection getRels(String relOfEdgeDefUID) throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).getRels(relOfEdgeDefUID);
    }

    @Override
    public String GetIconName() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).GetIconName();
    }

    @Override
    public String OnGetIconNamePrefix() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnGetIconNamePrefix();
    }

    @Override
    public String OnGetIconNameSuffix() throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).OnGetIconNameSuffix();
    }

    @Override
    public void UniqueKeyValidation(cancelArgs cancelArgs) throws Exception {
        this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).UniqueKeyValidation(cancelArgs);
    }

    @Override
    public boolean IsUniqueChecksOnOBIDAndUpdateState(ArrayList<String> parrOBIDs) throws Exception {
        return this.myNext(interfaceDefinitionType.IObject.toString(), new ArrayList<>()).IsUniqueChecksOnOBIDAndUpdateState(parrOBIDs);
    }

    public InterfaceDefault(boolean instantiateRequiredProperties) {
        super(null, instantiateRequiredProperties);
    }

    public void setInterfaceDefinitionUID(String interfaceDefinitionUID) {
        this.interfaceDefinitionUid = interfaceDefinitionUID;
    }
}
