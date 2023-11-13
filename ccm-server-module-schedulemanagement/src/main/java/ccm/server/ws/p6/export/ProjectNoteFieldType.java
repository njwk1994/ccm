
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ProjectNoteFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ProjectNoteFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AvailableForActivity"/&gt;
 *     &lt;enumeration value="AvailableForEPS"/&gt;
 *     &lt;enumeration value="AvailableForProject"/&gt;
 *     &lt;enumeration value="AvailableForWBS"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Note"/&gt;
 *     &lt;enumeration value="NotebookTopicName"/&gt;
 *     &lt;enumeration value="NotebookTopicObjectId"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="RawTextNote"/&gt;
 *     &lt;enumeration value="WBSCode"/&gt;
 *     &lt;enumeration value="WBSName"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ProjectNoteFieldType")
@XmlEnum
public enum ProjectNoteFieldType {

    @XmlEnumValue("AvailableForActivity")
    AVAILABLE_FOR_ACTIVITY("AvailableForActivity"),
    @XmlEnumValue("AvailableForEPS")
    AVAILABLE_FOR_EPS("AvailableForEPS"),
    @XmlEnumValue("AvailableForProject")
    AVAILABLE_FOR_PROJECT("AvailableForProject"),
    @XmlEnumValue("AvailableForWBS")
    AVAILABLE_FOR_WBS("AvailableForWBS"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Note")
    NOTE("Note"),
    @XmlEnumValue("NotebookTopicName")
    NOTEBOOK_TOPIC_NAME("NotebookTopicName"),
    @XmlEnumValue("NotebookTopicObjectId")
    NOTEBOOK_TOPIC_OBJECT_ID("NotebookTopicObjectId"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("RawTextNote")
    RAW_TEXT_NOTE("RawTextNote"),
    @XmlEnumValue("WBSCode")
    WBS_CODE("WBSCode"),
    @XmlEnumValue("WBSName")
    WBS_NAME("WBSName"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ProjectNoteFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectNoteFieldType fromValue(String v) {
        for (ProjectNoteFieldType c: ProjectNoteFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
