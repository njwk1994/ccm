package ccm.server.schema.interfaces.generated;


import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public abstract class ICIMDocumentRevisionBase extends InterfaceDefault implements ICIMDocumentRevision {
    public ICIMDocumentRevisionBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMDocumentRevision.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMExternalRevision(String externalRevision) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMExternalRevision.toString(), externalRevision);
    }

    @Override
    public String getCIMExternalRevision() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMExternalRevision.toString()).Value().toString();
    }

    @Override
    public void setCIMMajorRevision(String majorRevision) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMMajorRevision.toString(), majorRevision);
    }

    @Override
    public String getCIMMajorRevision() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMMajorRevision.toString()).Value().toString();
    }

    @Override
    public void setCIMRevisionScheme(String revisionScheme) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevisionSchema.toString(), revisionScheme);
    }

    @Override
    public String getCIMRevisionScheme() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevisionSchema.toString()).Value().toString();
    }

    @Override
    public void setCIMMinorRevision(String minorRevision) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMMinorRevision.toString(), minorRevision);
    }

    @Override
    public String getCIMMinorRevision() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMMinorRevision.toString()).Value().toString();
    }

    @Override
    public void setCIMRevState(String revState) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevState.toString(), revState);
    }

    @Override
    public String getCIMRevState() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevState.toString()).Value().toString();
    }

    @Override
    public void setCIMSignOffComments(String signOffComments) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMSignOffComments.toString(), signOffComments);
    }

    @Override
    public String getCIMSignOffComments() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMSignOffComments.toString()).Value().toString();
    }

    @Override
    public void setCIMRevIssueDate(Date date) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevIssueDate.toString(), CommonUtility.formatDateWithDateFormat(date));
    }

    @Override
    public Date getCIMRevIssueDate() throws ParseException {
        return CommonUtility.parseStrToDate(this.getProperty(interfaceDefinitionType.ICIMDocumentRevision.toString(), propertyDefinitionType.CIMRevIssueDate.toString()).Value().toString());
    }

    @Override
    public IObjectCollection getDocumentVersions() throws Exception {
        return this.GetEnd1Relationships().GetRels(relDefinitionType.CIMDocumentRevisionVersions.toString()).GetEnd2s();
    }

    @Override
    public ICIMDocumentVersion getNewestDocumentVersion() throws Exception {
        IObject newestDocumentVersion = SchemaUtility.getNewestDocumentVersion(this);
        return newestDocumentVersion != null ? newestDocumentVersion.toInterface(ICIMDocumentVersion.class) : null;
    }

    @Override
    public ICIMDocumentMaster getDocumentMaster() throws Exception {
        IObject master = this.GetEnd2Relationships().GetRel(relDefinitionType.CIMDocumentRevisions.toString()).GetEnd1();
        if (master == null) {
            return null;
        }
        return master.toInterface(ICIMDocumentMaster.class);
    }

    @Override
    public void signOff(String comments) throws Exception {
        ICIMDocumentMaster documentMaster = this.getDocumentMaster();
        if (documentMaster == null) throw new Exception("cannot found document master,name:" + this.Name());
        CIMContext.Instance.Transaction().start();
        if (!documentMaster.getCIMDocState().equals(docState.EN_IFC.toString())) {
            documentMaster.BeginUpdate();
            documentMaster.setCIMDocState(docState.EN_IFC.toString());
            documentMaster.FinishUpdate();
        }
        this.BeginUpdate();
        this.setCIMRevIssueDate(new Date());
        this.setCIMRevState(revState.EN_Current.toString());
        this.setCIMSignOffComments(comments);
        this.FinishUpdate();
        ICIMDocumentRevision previousRevision = this.getPreviousRevision();
        if (previousRevision != null) {
            previousRevision.BeginUpdate();
            previousRevision.setCIMRevState(revState.EN_Superseded.toString());
            previousRevision.FinishUpdate();
        }
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public ICIMDocumentRevision getPreviousRevision() throws Exception {
        //根据创建日期来获取前一个版本
        ICIMDocumentMaster documentMaster = this.getDocumentMaster();
        if (documentMaster != null) {
            IObjectCollection lcolBeforeContainer = new ObjectCollection();
            IObjectCollection documentRevisions = documentMaster.getDocumentRevisions();
            if (documentRevisions.hasValue()) {
                Iterator<IObject> iterator = documentRevisions.GetEnumerator();
                while (iterator.hasNext()) {
                    IObject revision = iterator.next();
                    if (revision.CreationDate().before(this.CreationDate())) {
                        lcolBeforeContainer.append(revision);
                    }
                }
            }
            if (lcolBeforeContainer.hasValue()) {
                lcolBeforeContainer.sorting(new Comparator<IObject>() {
                    @Override
                    public int compare(IObject o1, IObject o2) {
                        return o1.CreationDate().compareTo(o2.CreationDate());
                    }
                });
                return lcolBeforeContainer.get(lcolBeforeContainer.Size() - 1).toInterface(ICIMDocumentRevision.class);
            }
        }
        return null;
    }

    @Override
    public ICIMRevisionScheme getRevisionScheme() throws Exception {
        IObject revScheme = this.GetEnd1Relationships().GetRel(relDefinitionType.CIMDocumentRevisionScheme.toString()).GetEnd2();
        if (revScheme != null) {
            return revScheme.toInterface(ICIMRevisionScheme.class);
        }
        return null;
    }

    @Override
    public void superseded(boolean needTransaction) throws Exception {
        this.BeginUpdate();
        this.setCIMRevState(revState.EN_Superseded.toString());
        this.FinishUpdate();
    }

    @Override
    public IObjectCollection getLatestVersions() throws Exception {
        IObjectCollection documentVersions = this.getDocumentVersions();
        if (documentVersions.hasValue()) {
            IObjectCollection lcolContainer = new ObjectCollection();
            Iterator<IObject> iterator = documentVersions.GetEnumerator();
            while (iterator.hasNext()) {
                ICIMDocumentVersion documentVersion = iterator.next().toInterface(ICIMDocumentVersion.class);
                if (!documentVersion.getCIMIsDocVersionSuperseded()) {
                    lcolContainer.append(documentVersion);
                }
            }
            return lcolContainer;
        }
        return null;
    }

    @Override
    public void signOff() throws Exception {
        this.signOff("");
    }

    @Override
    public void revise(boolean pblnMajorRevise) throws Exception {
        if (this.getCIMRevState().equals(revState.EN_Current.toString())) {
            ICIMDocumentMaster documentMaster = this.getDocumentMaster();
            if (documentMaster == null) throw new Exception("未找到关联的Master对象");
            IObjectCollection documentRevisions = documentMaster.getDocumentRevisions();
            if (documentRevisions.hasValue()) {
                Iterator<IObject> iterator = documentRevisions.GetEnumerator();
                while (iterator.hasNext()) {
                    ICIMDocumentRevision revision = iterator.next().toInterface(ICIMDocumentRevision.class);
                    if (revision.getCIMRevState().equals(revState.EN_Revised.toString())) {
                        throw new Exception("已经存在Working状态的版本,无法重复升版!");
                    }
                }
            }
            //获取升版的版本号
            String lstrMajor = "";
            String lstrMinor = "";
            ICIMRevisionScheme revisionScheme = this.getRevisionScheme();
            if (revisionScheme == null) throw new Exception("revision schema未找到!");
            if (pblnMajorRevise) {
                try {
                    lstrMajor = revisionScheme.getNextMajorRevisionValue(getCIMMajorRevision());
                    lstrMinor = getCIMMinorRevision();
                } catch (Exception ex) {
                    lstrMajor = revisionScheme.getFirstRevisionValue(true);
                    lstrMinor = revisionScheme.getNextMinorRevisionValue(getCIMMinorRevision());
                }
            } else {
                try {
                    lstrMinor = revisionScheme.getNextMinorRevisionValue(getCIMMinorRevision());
                    lstrMajor = getCIMMajorRevision();
                } catch (Exception ex) {
                    lstrMinor = revisionScheme.getFirstRevisionValue(false);
                    lstrMajor = revisionScheme.getNextMajorRevisionValue(getCIMMajorRevision());
                }
            }
            log.info("下个版本major:" + lstrMajor + ",minor:" + lstrMinor);
            //1.创建Revision,和Master建立关联关系,与RevisionScheme建立关联关系
            SchemaUtility.beginTransaction();
            IObject newRevision = SchemaUtility.newIObject(classDefinitionType.CIMDocumentRevision.toString(), documentMaster.Name(), documentMaster.Description(), null, CommonUtility.generateUUID());
            if (newRevision == null) throw new Exception("创建Revision失败!");
            ICIMDocumentRevision revision = newRevision.toInterface(ICIMDocumentRevision.class);
            revision.setCIMRevState(revState.EN_Working.toString());
            revision.setCIMMajorRevision(lstrMajor);
            revision.setCIMMinorRevision(lstrMinor);
            revision.setCIMRevIssueDate(null);
            revision.setCIMExternalRevision(lstrMajor + lstrMinor);
            newRevision.ClassDefinition().FinishCreate(newRevision);
            IRel relOfDocRevision = SchemaUtility.newRelationship(relDefinitionType.CIMDocumentRevisions.toString(), documentMaster, newRevision, true);
            relOfDocRevision.ClassDefinition().FinishCreate(relOfDocRevision);
            IRel relOfRevisionScheme = SchemaUtility.newRelationship(relDefinitionType.CIMDocumentRevisionScheme.toString(), newRevision, revisionScheme, true);
            relOfRevisionScheme.ClassDefinition().FinishCreate(relOfRevisionScheme);
            //2.创建Version,与Revision建立关联关系,
            IObjectCollection latestVersions = this.getLatestVersions();
            ICIMDocumentVersion documentVersion = latestVersions.hasValue() ? latestVersions.get(0).toInterface(ICIMDocumentVersion.class) : this.getNewestDocumentVersion();
            IObject newVersion = SchemaUtility.newIObject(classDefinitionType.CIMDocumentVersion.toString(), documentVersion.Name(), documentVersion.Description(), null, CommonUtility.generateUUID());
            if (newVersion == null) throw new Exception("创建version对象失败!");
            ICIMDocumentVersion documentVersion1 = newVersion.toInterface(ICIMDocumentVersion.class);
            if (documentVersion1 == null) throw new Exception("接口转换失败!请检查Schema");
            documentVersion1.setCIMDocVersion(documentVersion.getCIMDocVersion());
            documentVersion1.setCIMIsDocVersionCheckedOut(false);
            documentVersion1.setCIMIsDocVersionSuperseded(false);
            newVersion.ClassDefinition().FinishCreate(newVersion);
            IRel relOfRevisionToVersion = SchemaUtility.newRelationship(relDefinitionType.CIMDocumentRevisionVersions.toString(), newRevision, newVersion, true);
            relOfRevisionToVersion.ClassDefinition().FinishCreate(relOfRevisionToVersion);
            IObjectCollection allFiles = documentVersion.toInterface(ICIMFileComposition.class).getAllFiles();
            if (allFiles.hasValue()) {
                Iterator<IObject> iterator = allFiles.GetEnumerator();
                while (iterator.hasNext()) {
                    ICIMFile file = iterator.next().toInterface(ICIMFile.class);
                    IObject newFile = SchemaUtility.newIObject(classDefinitionType.CIMFile.toString(), file.Name(), file.Description(), domainInfo.DATA.toString(), CommonUtility.generateUUID());
                    if (newFile == null) throw new Exception("创建CIMFile失败!");
                    ICIMFile icimFile = newFile.toInterface(ICIMFile.class);
                    icimFile.setBucketName(file.getBucketName());
                    icimFile.setBucketName(file.getBucketObjName());
                    icimFile.setFileExt(file.getFileExt());
                    icimFile.setFileName(file.getFileName());
                    newFile.ClassDefinition().FinishCreate(newFile);
                    ICIMFileType fileType = file.getFileType();
                    if (fileType != null) {
                        IRel relOfFileType = SchemaUtility.newRelationship(relDefinitionType.CIMFileFileType.toString(), newFile, fileType, true);
                        relOfFileType.ClassDefinition().FinishCreate(relOfFileType);
                    }
                    IRel relOfFileComposition = SchemaUtility.newRelationship(relDefinitionType.CIMFileComposition.toString(), newFile, newVersion, true);
                    relOfFileComposition.ClassDefinition().FinishCreate(relOfFileComposition);
                }
            }
            SchemaUtility.commitTransaction();
        } else {
            throw new Exception("状态异常,无法进行升版!");
        }
    }

    @Override
    public void revise() throws Exception {
        this.revise(true);
    }

    @Override
    public void undoSignOff() throws Exception {
        if (this.getCIMRevState().equals(revState.EN_Current.toString())) {
            SchemaUtility.beginTransaction();
            this.BeginUpdate();
            this.setCIMSignOffComments("");
            this.setCIMRevState(revState.EN_Working.toString());
            this.setCIMRevIssueDate(null);
            this.FinishUpdate();
            SchemaUtility.commitTransaction();
        } else {
            throw new Exception("只有Current状态的文档才能执行该操作!");
        }
    }

    @Override
    public void setRevisionPropertyInfo(JSONObject revisionPropertyInfo) throws Exception {
        if (revisionPropertyInfo != null) {
            for (Map.Entry<String, Object> item : revisionPropertyInfo.entrySet()) {
                SchemaUtility.setObjPropertyValue(this, interfaceDefinitionType.ICIMDocumentRevision.toString(), item.getKey(), item.getValue(), false, false);
            }
        }
    }
}
