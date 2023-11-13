
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WBSMilestoneFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="WBSMilestoneFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsCompleted"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="SequenceNumber"/&gt;
 *     &lt;enumeration value="WBSCode"/&gt;
 *     &lt;enumeration value="WBSName"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *     &lt;enumeration value="Weight"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "WBSMilestoneFieldType")
@XmlEnum
public enum WBSMilestoneFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsCompleted")
    IS_COMPLETED("IsCompleted"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("SequenceNumber")
    SEQUENCE_NUMBER("SequenceNumber"),
    @XmlEnumValue("WBSCode")
    WBS_CODE("WBSCode"),
    @XmlEnumValue("WBSName")
    WBS_NAME("WBSName"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId"),
    @XmlEnumValue("Weight")
    WEIGHT("Weight");
    private final String value;

    WBSMilestoneFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WBSMilestoneFieldType fromValue(String v) {
        for (WBSMilestoneFieldType c: WBSMilestoneFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
