
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>DocumentFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="DocumentFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Author"/&gt;
 *     &lt;enumeration value="ContentRepositoryDocumentInternalId"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="Deliverable"/&gt;
 *     &lt;enumeration value="Description"/&gt;
 *     &lt;enumeration value="DocumentCategoryName"/&gt;
 *     &lt;enumeration value="DocumentCategoryObjectId"/&gt;
 *     &lt;enumeration value="DocumentStatusCodeName"/&gt;
 *     &lt;enumeration value="DocumentStatusCodeObjectId"/&gt;
 *     &lt;enumeration value="DocumentType"/&gt;
 *     &lt;enumeration value="GUID"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ParentObjectId"/&gt;
 *     &lt;enumeration value="PrivateLocation"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="PublicLocation"/&gt;
 *     &lt;enumeration value="ReferenceNumber"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="RevisionDate"/&gt;
 *     &lt;enumeration value="SequenceNumber"/&gt;
 *     &lt;enumeration value="Title"/&gt;
 *     &lt;enumeration value="Version"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "DocumentFieldType")
@XmlEnum
public enum DocumentFieldType {

    @XmlEnumValue("Author")
    AUTHOR("Author"),
    @XmlEnumValue("ContentRepositoryDocumentInternalId")
    CONTENT_REPOSITORY_DOCUMENT_INTERNAL_ID("ContentRepositoryDocumentInternalId"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("Deliverable")
    DELIVERABLE("Deliverable"),
    @XmlEnumValue("Description")
    DESCRIPTION("Description"),
    @XmlEnumValue("DocumentCategoryName")
    DOCUMENT_CATEGORY_NAME("DocumentCategoryName"),
    @XmlEnumValue("DocumentCategoryObjectId")
    DOCUMENT_CATEGORY_OBJECT_ID("DocumentCategoryObjectId"),
    @XmlEnumValue("DocumentStatusCodeName")
    DOCUMENT_STATUS_CODE_NAME("DocumentStatusCodeName"),
    @XmlEnumValue("DocumentStatusCodeObjectId")
    DOCUMENT_STATUS_CODE_OBJECT_ID("DocumentStatusCodeObjectId"),
    @XmlEnumValue("DocumentType")
    DOCUMENT_TYPE("DocumentType"),
    GUID("GUID"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ParentObjectId")
    PARENT_OBJECT_ID("ParentObjectId"),
    @XmlEnumValue("PrivateLocation")
    PRIVATE_LOCATION("PrivateLocation"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("PublicLocation")
    PUBLIC_LOCATION("PublicLocation"),
    @XmlEnumValue("ReferenceNumber")
    REFERENCE_NUMBER("ReferenceNumber"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("RevisionDate")
    REVISION_DATE("RevisionDate"),
    @XmlEnumValue("SequenceNumber")
    SEQUENCE_NUMBER("SequenceNumber"),
    @XmlEnumValue("Title")
    TITLE("Title"),
    @XmlEnumValue("Version")
    VERSION("Version");
    private final String value;

    DocumentFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DocumentFieldType fromValue(String v) {
        for (DocumentFieldType c: DocumentFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
