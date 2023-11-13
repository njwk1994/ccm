
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RelationshipFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RelationshipFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Aref"/&gt;
 *     &lt;enumeration value="Arls"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="IsPredecessorBaseline"/&gt;
 *     &lt;enumeration value="IsPredecessorTemplate"/&gt;
 *     &lt;enumeration value="IsSuccessorBaseline"/&gt;
 *     &lt;enumeration value="IsSuccessorTemplate"/&gt;
 *     &lt;enumeration value="Lag"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="PredecessorActivityId"/&gt;
 *     &lt;enumeration value="PredecessorActivityName"/&gt;
 *     &lt;enumeration value="PredecessorActivityObjectId"/&gt;
 *     &lt;enumeration value="PredecessorActivityType"/&gt;
 *     &lt;enumeration value="PredecessorFinishDate"/&gt;
 *     &lt;enumeration value="PredecessorProjectId"/&gt;
 *     &lt;enumeration value="PredecessorProjectObjectId"/&gt;
 *     &lt;enumeration value="PredecessorStartDate"/&gt;
 *     &lt;enumeration value="PredecessorWbsName"/&gt;
 *     &lt;enumeration value="SuccessorActivityId"/&gt;
 *     &lt;enumeration value="SuccessorActivityName"/&gt;
 *     &lt;enumeration value="SuccessorActivityObjectId"/&gt;
 *     &lt;enumeration value="SuccessorActivityType"/&gt;
 *     &lt;enumeration value="SuccessorFinishDate"/&gt;
 *     &lt;enumeration value="SuccessorProjectId"/&gt;
 *     &lt;enumeration value="SuccessorProjectObjectId"/&gt;
 *     &lt;enumeration value="SuccessorStartDate"/&gt;
 *     &lt;enumeration value="SuccessorWbsName"/&gt;
 *     &lt;enumeration value="Type"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RelationshipFieldType")
@XmlEnum
public enum RelationshipFieldType {

    @XmlEnumValue("Aref")
    AREF("Aref"),
    @XmlEnumValue("Arls")
    ARLS("Arls"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("IsPredecessorBaseline")
    IS_PREDECESSOR_BASELINE("IsPredecessorBaseline"),
    @XmlEnumValue("IsPredecessorTemplate")
    IS_PREDECESSOR_TEMPLATE("IsPredecessorTemplate"),
    @XmlEnumValue("IsSuccessorBaseline")
    IS_SUCCESSOR_BASELINE("IsSuccessorBaseline"),
    @XmlEnumValue("IsSuccessorTemplate")
    IS_SUCCESSOR_TEMPLATE("IsSuccessorTemplate"),
    @XmlEnumValue("Lag")
    LAG("Lag"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("PredecessorActivityId")
    PREDECESSOR_ACTIVITY_ID("PredecessorActivityId"),
    @XmlEnumValue("PredecessorActivityName")
    PREDECESSOR_ACTIVITY_NAME("PredecessorActivityName"),
    @XmlEnumValue("PredecessorActivityObjectId")
    PREDECESSOR_ACTIVITY_OBJECT_ID("PredecessorActivityObjectId"),
    @XmlEnumValue("PredecessorActivityType")
    PREDECESSOR_ACTIVITY_TYPE("PredecessorActivityType"),
    @XmlEnumValue("PredecessorFinishDate")
    PREDECESSOR_FINISH_DATE("PredecessorFinishDate"),
    @XmlEnumValue("PredecessorProjectId")
    PREDECESSOR_PROJECT_ID("PredecessorProjectId"),
    @XmlEnumValue("PredecessorProjectObjectId")
    PREDECESSOR_PROJECT_OBJECT_ID("PredecessorProjectObjectId"),
    @XmlEnumValue("PredecessorStartDate")
    PREDECESSOR_START_DATE("PredecessorStartDate"),
    @XmlEnumValue("PredecessorWbsName")
    PREDECESSOR_WBS_NAME("PredecessorWbsName"),
    @XmlEnumValue("SuccessorActivityId")
    SUCCESSOR_ACTIVITY_ID("SuccessorActivityId"),
    @XmlEnumValue("SuccessorActivityName")
    SUCCESSOR_ACTIVITY_NAME("SuccessorActivityName"),
    @XmlEnumValue("SuccessorActivityObjectId")
    SUCCESSOR_ACTIVITY_OBJECT_ID("SuccessorActivityObjectId"),
    @XmlEnumValue("SuccessorActivityType")
    SUCCESSOR_ACTIVITY_TYPE("SuccessorActivityType"),
    @XmlEnumValue("SuccessorFinishDate")
    SUCCESSOR_FINISH_DATE("SuccessorFinishDate"),
    @XmlEnumValue("SuccessorProjectId")
    SUCCESSOR_PROJECT_ID("SuccessorProjectId"),
    @XmlEnumValue("SuccessorProjectObjectId")
    SUCCESSOR_PROJECT_OBJECT_ID("SuccessorProjectObjectId"),
    @XmlEnumValue("SuccessorStartDate")
    SUCCESSOR_START_DATE("SuccessorStartDate"),
    @XmlEnumValue("SuccessorWbsName")
    SUCCESSOR_WBS_NAME("SuccessorWbsName"),
    @XmlEnumValue("Type")
    TYPE("Type");
    private final String value;

    RelationshipFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelationshipFieldType fromValue(String v) {
        for (RelationshipFieldType c: RelationshipFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
