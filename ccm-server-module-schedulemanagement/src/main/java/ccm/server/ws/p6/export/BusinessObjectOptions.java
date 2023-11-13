
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>BusinessObjectOptions complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="BusinessObjectOptions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Activity" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Activity" minOccurs="0"/&gt;
 *         &lt;element name="ActivityCode" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityCode" minOccurs="0"/&gt;
 *         &lt;element name="ActivityCodeAssignment" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityCodeAssignment" minOccurs="0"/&gt;
 *         &lt;element name="ActivityCodeType" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityCodeType" minOccurs="0"/&gt;
 *         &lt;element name="ActivityExpense" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityExpense" minOccurs="0"/&gt;
 *         &lt;element name="ActivityNote" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityNote" minOccurs="0"/&gt;
 *         &lt;element name="ActivityPeriodActual" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityPeriodActual" minOccurs="0"/&gt;
 *         &lt;element name="ActivityRisk" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityRisk" minOccurs="0"/&gt;
 *         &lt;element name="ActivityStep" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ActivityStep" minOccurs="0"/&gt;
 *         &lt;element name="Calendar" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Calendar" minOccurs="0"/&gt;
 *         &lt;element name="CostAccount" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}CostAccount" minOccurs="0"/&gt;
 *         &lt;element name="Currency" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Currency" minOccurs="0"/&gt;
 *         &lt;element name="Document" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Document" minOccurs="0"/&gt;
 *         &lt;element name="DocumentCategory" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}DocumentCategory" minOccurs="0"/&gt;
 *         &lt;element name="DocumentStatusCode" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}DocumentStatusCode" minOccurs="0"/&gt;
 *         &lt;element name="EPS" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}EPS" minOccurs="0"/&gt;
 *         &lt;element name="ExpenseCategory" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ExpenseCategory" minOccurs="0"/&gt;
 *         &lt;element name="FinancialPeriod" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}FinancialPeriod" minOccurs="0"/&gt;
 *         &lt;element name="FundingSource" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}FundingSource" minOccurs="0"/&gt;
 *         &lt;element name="NotebookTopic" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}NotebookTopic" minOccurs="0"/&gt;
 *         &lt;element name="OBS" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}OBS" minOccurs="0"/&gt;
 *         &lt;element name="Project" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Project" minOccurs="0"/&gt;
 *         &lt;element name="ProjectBudgetChangeLog" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectBudgetChangeLog" minOccurs="0"/&gt;
 *         &lt;element name="ProjectCode" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectCode" minOccurs="0"/&gt;
 *         &lt;element name="ProjectCodeAssignment" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectCodeAssignment" minOccurs="0"/&gt;
 *         &lt;element name="ProjectCodeType" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectCodeType" minOccurs="0"/&gt;
 *         &lt;element name="ProjectDocument" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectDocument" minOccurs="0"/&gt;
 *         &lt;element name="ProjectFunding" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectFunding" minOccurs="0"/&gt;
 *         &lt;element name="ProjectIssue" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectIssue" minOccurs="0"/&gt;
 *         &lt;element name="ProjectNote" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectNote" minOccurs="0"/&gt;
 *         &lt;element name="ProjectResource" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectResource" minOccurs="0"/&gt;
 *         &lt;element name="ProjectResourceCategory" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectResourceCategory" minOccurs="0"/&gt;
 *         &lt;element name="ProjectResourceQuantity" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectResourceQuantity" minOccurs="0"/&gt;
 *         &lt;element name="ProjectSpendingPlan" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectSpendingPlan" minOccurs="0"/&gt;
 *         &lt;element name="ProjectThreshold" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ProjectThreshold" minOccurs="0"/&gt;
 *         &lt;element name="Relationship" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Relationship" minOccurs="0"/&gt;
 *         &lt;element name="Resource" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Resource" minOccurs="0"/&gt;
 *         &lt;element name="ResourceAssignment" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceAssignment" minOccurs="0"/&gt;
 *         &lt;element name="ResourceAssignmentPeriodActual" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceAssignmentPeriodActual" minOccurs="0"/&gt;
 *         &lt;element name="ResourceCode" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceCode" minOccurs="0"/&gt;
 *         &lt;element name="ResourceCodeAssignment" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceCodeAssignment" minOccurs="0"/&gt;
 *         &lt;element name="ResourceCodeType" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceCodeType" minOccurs="0"/&gt;
 *         &lt;element name="ResourceCurve" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceCurve" minOccurs="0"/&gt;
 *         &lt;element name="ResourceRate" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceRate" minOccurs="0"/&gt;
 *         &lt;element name="ResourceRole" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ResourceRole" minOccurs="0"/&gt;
 *         &lt;element name="Risk" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Risk" minOccurs="0"/&gt;
 *         &lt;element name="RiskCategory" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskCategory" minOccurs="0"/&gt;
 *         &lt;element name="RiskImpact" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskImpact" minOccurs="0"/&gt;
 *         &lt;element name="RiskMatrixScore" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskMatrixScore" minOccurs="0"/&gt;
 *         &lt;element name="RiskMatrixThreshold" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskMatrixThreshold" minOccurs="0"/&gt;
 *         &lt;element name="RiskResponseAction" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskResponseAction" minOccurs="0"/&gt;
 *         &lt;element name="RiskResponseActionImpact" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskResponseActionImpact" minOccurs="0"/&gt;
 *         &lt;element name="RiskResponsePlan" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskResponsePlan" minOccurs="0"/&gt;
 *         &lt;element name="RiskMatrix" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskMatrix" minOccurs="0"/&gt;
 *         &lt;element name="RiskThresholdLevel" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskThresholdLevel" minOccurs="0"/&gt;
 *         &lt;element name="RiskThreshold" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RiskThreshold" minOccurs="0"/&gt;
 *         &lt;element name="Role" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Role" minOccurs="0"/&gt;
 *         &lt;element name="RoleRate" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RoleRate" minOccurs="0"/&gt;
 *         &lt;element name="RoleLimit" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}RoleLimit" minOccurs="0"/&gt;
 *         &lt;element name="Shift" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}Shift" minOccurs="0"/&gt;
 *         &lt;element name="ThresholdParameter" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}ThresholdParameter" minOccurs="0"/&gt;
 *         &lt;element name="UDFCode" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}UDFCode" minOccurs="0"/&gt;
 *         &lt;element name="UDFType" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}UDFType" minOccurs="0"/&gt;
 *         &lt;element name="UDFValue" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}UDFValue" minOccurs="0"/&gt;
 *         &lt;element name="UnitOfMeasure" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}UnitOfMeasure" minOccurs="0"/&gt;
 *         &lt;element name="WBS" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}WBS" minOccurs="0"/&gt;
 *         &lt;element name="WBSCategory" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}WBSCategory" minOccurs="0"/&gt;
 *         &lt;element name="WBSMilestone" type="{http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2}WBSMilestone" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessObjectOptions", propOrder = {
    "activity",
    "activityCode",
    "activityCodeAssignment",
    "activityCodeType",
    "activityExpense",
    "activityNote",
    "activityPeriodActual",
    "activityRisk",
    "activityStep",
    "calendar",
    "costAccount",
    "currency",
    "document",
    "documentCategory",
    "documentStatusCode",
    "eps",
    "expenseCategory",
    "financialPeriod",
    "fundingSource",
    "notebookTopic",
    "obs",
    "project",
    "projectBudgetChangeLog",
    "projectCode",
    "projectCodeAssignment",
    "projectCodeType",
    "projectDocument",
    "projectFunding",
    "projectIssue",
    "projectNote",
    "projectResource",
    "projectResourceCategory",
    "projectResourceQuantity",
    "projectSpendingPlan",
    "projectThreshold",
    "relationship",
    "resource",
    "resourceAssignment",
    "resourceAssignmentPeriodActual",
    "resourceCode",
    "resourceCodeAssignment",
    "resourceCodeType",
    "resourceCurve",
    "resourceRate",
    "resourceRole",
    "risk",
    "riskCategory",
    "riskImpact",
    "riskMatrixScore",
    "riskMatrixThreshold",
    "riskResponseAction",
    "riskResponseActionImpact",
    "riskResponsePlan",
    "riskMatrix",
    "riskThresholdLevel",
    "riskThreshold",
    "role",
    "roleRate",
    "roleLimit",
    "shift",
    "thresholdParameter",
    "udfCode",
    "udfType",
    "udfValue",
    "unitOfMeasure",
    "wbs",
    "wbsCategory",
    "wbsMilestone"
})
public class BusinessObjectOptions {

    @XmlElement(name = "Activity")
    protected Activity activity;
    @XmlElement(name = "ActivityCode")
    protected ActivityCode activityCode;
    @XmlElement(name = "ActivityCodeAssignment")
    protected ActivityCodeAssignment activityCodeAssignment;
    @XmlElement(name = "ActivityCodeType")
    protected ActivityCodeType activityCodeType;
    @XmlElement(name = "ActivityExpense")
    protected ActivityExpense activityExpense;
    @XmlElement(name = "ActivityNote")
    protected ActivityNote activityNote;
    @XmlElement(name = "ActivityPeriodActual")
    protected ActivityPeriodActual activityPeriodActual;
    @XmlElement(name = "ActivityRisk")
    protected ActivityRisk activityRisk;
    @XmlElement(name = "ActivityStep")
    protected ActivityStep activityStep;
    @XmlElement(name = "Calendar")
    protected Calendar calendar;
    @XmlElement(name = "CostAccount")
    protected CostAccount costAccount;
    @XmlElement(name = "Currency")
    protected Currency currency;
    @XmlElement(name = "Document")
    protected Document document;
    @XmlElement(name = "DocumentCategory")
    protected DocumentCategory documentCategory;
    @XmlElement(name = "DocumentStatusCode")
    protected DocumentStatusCode documentStatusCode;
    @XmlElement(name = "EPS")
    protected EPS eps;
    @XmlElement(name = "ExpenseCategory")
    protected ExpenseCategory expenseCategory;
    @XmlElement(name = "FinancialPeriod")
    protected FinancialPeriod financialPeriod;
    @XmlElement(name = "FundingSource")
    protected FundingSource fundingSource;
    @XmlElement(name = "NotebookTopic")
    protected NotebookTopic notebookTopic;
    @XmlElement(name = "OBS")
    protected OBS obs;
    @XmlElement(name = "Project")
    protected Project project;
    @XmlElement(name = "ProjectBudgetChangeLog")
    protected ProjectBudgetChangeLog projectBudgetChangeLog;
    @XmlElement(name = "ProjectCode")
    protected ProjectCode projectCode;
    @XmlElement(name = "ProjectCodeAssignment")
    protected ProjectCodeAssignment projectCodeAssignment;
    @XmlElement(name = "ProjectCodeType")
    protected ProjectCodeType projectCodeType;
    @XmlElement(name = "ProjectDocument")
    protected ProjectDocument projectDocument;
    @XmlElement(name = "ProjectFunding")
    protected ProjectFunding projectFunding;
    @XmlElement(name = "ProjectIssue")
    protected ProjectIssue projectIssue;
    @XmlElement(name = "ProjectNote")
    protected ProjectNote projectNote;
    @XmlElement(name = "ProjectResource")
    protected ProjectResource projectResource;
    @XmlElement(name = "ProjectResourceCategory")
    protected ProjectResourceCategory projectResourceCategory;
    @XmlElement(name = "ProjectResourceQuantity")
    protected ProjectResourceQuantity projectResourceQuantity;
    @XmlElement(name = "ProjectSpendingPlan")
    protected ProjectSpendingPlan projectSpendingPlan;
    @XmlElement(name = "ProjectThreshold")
    protected ProjectThreshold projectThreshold;
    @XmlElement(name = "Relationship")
    protected Relationship relationship;
    @XmlElement(name = "Resource")
    protected Resource resource;
    @XmlElement(name = "ResourceAssignment")
    protected ResourceAssignment resourceAssignment;
    @XmlElement(name = "ResourceAssignmentPeriodActual")
    protected ResourceAssignmentPeriodActual resourceAssignmentPeriodActual;
    @XmlElement(name = "ResourceCode")
    protected ResourceCode resourceCode;
    @XmlElement(name = "ResourceCodeAssignment")
    protected ResourceCodeAssignment resourceCodeAssignment;
    @XmlElement(name = "ResourceCodeType")
    protected ResourceCodeType resourceCodeType;
    @XmlElement(name = "ResourceCurve")
    protected ResourceCurve resourceCurve;
    @XmlElement(name = "ResourceRate")
    protected ResourceRate resourceRate;
    @XmlElement(name = "ResourceRole")
    protected ResourceRole resourceRole;
    @XmlElement(name = "Risk")
    protected Risk risk;
    @XmlElement(name = "RiskCategory")
    protected RiskCategory riskCategory;
    @XmlElement(name = "RiskImpact")
    protected RiskImpact riskImpact;
    @XmlElement(name = "RiskMatrixScore")
    protected RiskMatrixScore riskMatrixScore;
    @XmlElement(name = "RiskMatrixThreshold")
    protected RiskMatrixThreshold riskMatrixThreshold;
    @XmlElement(name = "RiskResponseAction")
    protected RiskResponseAction riskResponseAction;
    @XmlElement(name = "RiskResponseActionImpact")
    protected RiskResponseActionImpact riskResponseActionImpact;
    @XmlElement(name = "RiskResponsePlan")
    protected RiskResponsePlan riskResponsePlan;
    @XmlElement(name = "RiskMatrix")
    protected RiskMatrix riskMatrix;
    @XmlElement(name = "RiskThresholdLevel")
    protected RiskThresholdLevel riskThresholdLevel;
    @XmlElement(name = "RiskThreshold")
    protected RiskThreshold riskThreshold;
    @XmlElement(name = "Role")
    protected Role role;
    @XmlElement(name = "RoleRate")
    protected RoleRate roleRate;
    @XmlElement(name = "RoleLimit")
    protected RoleLimit roleLimit;
    @XmlElement(name = "Shift")
    protected Shift shift;
    @XmlElement(name = "ThresholdParameter")
    protected ThresholdParameter thresholdParameter;
    @XmlElement(name = "UDFCode")
    protected UDFCode udfCode;
    @XmlElement(name = "UDFType")
    protected UDFType udfType;
    @XmlElement(name = "UDFValue")
    protected UDFValue udfValue;
    @XmlElement(name = "UnitOfMeasure")
    protected UnitOfMeasure unitOfMeasure;
    @XmlElement(name = "WBS")
    protected WBS wbs;
    @XmlElement(name = "WBSCategory")
    protected WBSCategory wbsCategory;
    @XmlElement(name = "WBSMilestone")
    protected WBSMilestone wbsMilestone;

    /**
     * 获取activity属性的值。
     *
     * @return
     *     possible object is
     *     {@link Activity }
     *
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * 设置activity属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Activity }
     *
     */
    public void setActivity(Activity value) {
        this.activity = value;
    }

    /**
     * 获取activityCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityCode }
     *
     */
    public ActivityCode getActivityCode() {
        return activityCode;
    }

    /**
     * 设置activityCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityCode }
     *
     */
    public void setActivityCode(ActivityCode value) {
        this.activityCode = value;
    }

    /**
     * 获取activityCodeAssignment属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityCodeAssignment }
     *
     */
    public ActivityCodeAssignment getActivityCodeAssignment() {
        return activityCodeAssignment;
    }

    /**
     * 设置activityCodeAssignment属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityCodeAssignment }
     *
     */
    public void setActivityCodeAssignment(ActivityCodeAssignment value) {
        this.activityCodeAssignment = value;
    }

    /**
     * 获取activityCodeType属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityCodeType }
     *
     */
    public ActivityCodeType getActivityCodeType() {
        return activityCodeType;
    }

    /**
     * 设置activityCodeType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityCodeType }
     *
     */
    public void setActivityCodeType(ActivityCodeType value) {
        this.activityCodeType = value;
    }

    /**
     * 获取activityExpense属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityExpense }
     *
     */
    public ActivityExpense getActivityExpense() {
        return activityExpense;
    }

    /**
     * 设置activityExpense属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityExpense }
     *
     */
    public void setActivityExpense(ActivityExpense value) {
        this.activityExpense = value;
    }

    /**
     * 获取activityNote属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityNote }
     *
     */
    public ActivityNote getActivityNote() {
        return activityNote;
    }

    /**
     * 设置activityNote属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityNote }
     *
     */
    public void setActivityNote(ActivityNote value) {
        this.activityNote = value;
    }

    /**
     * 获取activityPeriodActual属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityPeriodActual }
     *
     */
    public ActivityPeriodActual getActivityPeriodActual() {
        return activityPeriodActual;
    }

    /**
     * 设置activityPeriodActual属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityPeriodActual }
     *
     */
    public void setActivityPeriodActual(ActivityPeriodActual value) {
        this.activityPeriodActual = value;
    }

    /**
     * 获取activityRisk属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityRisk }
     *
     */
    public ActivityRisk getActivityRisk() {
        return activityRisk;
    }

    /**
     * 设置activityRisk属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityRisk }
     *
     */
    public void setActivityRisk(ActivityRisk value) {
        this.activityRisk = value;
    }

    /**
     * 获取activityStep属性的值。
     *
     * @return
     *     possible object is
     *     {@link ActivityStep }
     *
     */
    public ActivityStep getActivityStep() {
        return activityStep;
    }

    /**
     * 设置activityStep属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ActivityStep }
     *
     */
    public void setActivityStep(ActivityStep value) {
        this.activityStep = value;
    }

    /**
     * 获取calendar属性的值。
     *
     * @return
     *     possible object is
     *     {@link Calendar }
     *
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * 设置calendar属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *
     */
    public void setCalendar(Calendar value) {
        this.calendar = value;
    }

    /**
     * 获取costAccount属性的值。
     *
     * @return
     *     possible object is
     *     {@link CostAccount }
     *
     */
    public CostAccount getCostAccount() {
        return costAccount;
    }

    /**
     * 设置costAccount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link CostAccount }
     *
     */
    public void setCostAccount(CostAccount value) {
        this.costAccount = value;
    }

    /**
     * 获取currency属性的值。
     *
     * @return
     *     possible object is
     *     {@link Currency }
     *
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * 设置currency属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Currency }
     *
     */
    public void setCurrency(Currency value) {
        this.currency = value;
    }

    /**
     * 获取document属性的值。
     *
     * @return
     *     possible object is
     *     {@link Document }
     *
     */
    public Document getDocument() {
        return document;
    }

    /**
     * 设置document属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Document }
     *
     */
    public void setDocument(Document value) {
        this.document = value;
    }

    /**
     * 获取documentCategory属性的值。
     *
     * @return
     *     possible object is
     *     {@link DocumentCategory }
     *
     */
    public DocumentCategory getDocumentCategory() {
        return documentCategory;
    }

    /**
     * 设置documentCategory属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link DocumentCategory }
     *
     */
    public void setDocumentCategory(DocumentCategory value) {
        this.documentCategory = value;
    }

    /**
     * 获取documentStatusCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link DocumentStatusCode }
     *
     */
    public DocumentStatusCode getDocumentStatusCode() {
        return documentStatusCode;
    }

    /**
     * 设置documentStatusCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link DocumentStatusCode }
     *
     */
    public void setDocumentStatusCode(DocumentStatusCode value) {
        this.documentStatusCode = value;
    }

    /**
     * 获取eps属性的值。
     *
     * @return
     *     possible object is
     *     {@link EPS }
     *
     */
    public EPS getEPS() {
        return eps;
    }

    /**
     * 设置eps属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link EPS }
     *
     */
    public void setEPS(EPS value) {
        this.eps = value;
    }

    /**
     * 获取expenseCategory属性的值。
     *
     * @return
     *     possible object is
     *     {@link ExpenseCategory }
     *
     */
    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    /**
     * 设置expenseCategory属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ExpenseCategory }
     *
     */
    public void setExpenseCategory(ExpenseCategory value) {
        this.expenseCategory = value;
    }

    /**
     * 获取financialPeriod属性的值。
     *
     * @return
     *     possible object is
     *     {@link FinancialPeriod }
     *
     */
    public FinancialPeriod getFinancialPeriod() {
        return financialPeriod;
    }

    /**
     * 设置financialPeriod属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link FinancialPeriod }
     *
     */
    public void setFinancialPeriod(FinancialPeriod value) {
        this.financialPeriod = value;
    }

    /**
     * 获取fundingSource属性的值。
     *
     * @return
     *     possible object is
     *     {@link FundingSource }
     *
     */
    public FundingSource getFundingSource() {
        return fundingSource;
    }

    /**
     * 设置fundingSource属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link FundingSource }
     *
     */
    public void setFundingSource(FundingSource value) {
        this.fundingSource = value;
    }

    /**
     * 获取notebookTopic属性的值。
     *
     * @return
     *     possible object is
     *     {@link NotebookTopic }
     *
     */
    public NotebookTopic getNotebookTopic() {
        return notebookTopic;
    }

    /**
     * 设置notebookTopic属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link NotebookTopic }
     *
     */
    public void setNotebookTopic(NotebookTopic value) {
        this.notebookTopic = value;
    }

    /**
     * 获取obs属性的值。
     *
     * @return
     *     possible object is
     *     {@link OBS }
     *
     */
    public OBS getOBS() {
        return obs;
    }

    /**
     * 设置obs属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link OBS }
     *
     */
    public void setOBS(OBS value) {
        this.obs = value;
    }

    /**
     * 获取project属性的值。
     *
     * @return
     *     possible object is
     *     {@link Project }
     *
     */
    public Project getProject() {
        return project;
    }

    /**
     * 设置project属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Project }
     *
     */
    public void setProject(Project value) {
        this.project = value;
    }

    /**
     * 获取projectBudgetChangeLog属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectBudgetChangeLog }
     *
     */
    public ProjectBudgetChangeLog getProjectBudgetChangeLog() {
        return projectBudgetChangeLog;
    }

    /**
     * 设置projectBudgetChangeLog属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectBudgetChangeLog }
     *
     */
    public void setProjectBudgetChangeLog(ProjectBudgetChangeLog value) {
        this.projectBudgetChangeLog = value;
    }

    /**
     * 获取projectCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectCode }
     *
     */
    public ProjectCode getProjectCode() {
        return projectCode;
    }

    /**
     * 设置projectCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectCode }
     *
     */
    public void setProjectCode(ProjectCode value) {
        this.projectCode = value;
    }

    /**
     * 获取projectCodeAssignment属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectCodeAssignment }
     *
     */
    public ProjectCodeAssignment getProjectCodeAssignment() {
        return projectCodeAssignment;
    }

    /**
     * 设置projectCodeAssignment属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectCodeAssignment }
     *
     */
    public void setProjectCodeAssignment(ProjectCodeAssignment value) {
        this.projectCodeAssignment = value;
    }

    /**
     * 获取projectCodeType属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectCodeType }
     *
     */
    public ProjectCodeType getProjectCodeType() {
        return projectCodeType;
    }

    /**
     * 设置projectCodeType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectCodeType }
     *
     */
    public void setProjectCodeType(ProjectCodeType value) {
        this.projectCodeType = value;
    }

    /**
     * 获取projectDocument属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectDocument }
     *
     */
    public ProjectDocument getProjectDocument() {
        return projectDocument;
    }

    /**
     * 设置projectDocument属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectDocument }
     *
     */
    public void setProjectDocument(ProjectDocument value) {
        this.projectDocument = value;
    }

    /**
     * 获取projectFunding属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectFunding }
     *
     */
    public ProjectFunding getProjectFunding() {
        return projectFunding;
    }

    /**
     * 设置projectFunding属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectFunding }
     *
     */
    public void setProjectFunding(ProjectFunding value) {
        this.projectFunding = value;
    }

    /**
     * 获取projectIssue属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectIssue }
     *
     */
    public ProjectIssue getProjectIssue() {
        return projectIssue;
    }

    /**
     * 设置projectIssue属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectIssue }
     *
     */
    public void setProjectIssue(ProjectIssue value) {
        this.projectIssue = value;
    }

    /**
     * 获取projectNote属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectNote }
     *
     */
    public ProjectNote getProjectNote() {
        return projectNote;
    }

    /**
     * 设置projectNote属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectNote }
     *
     */
    public void setProjectNote(ProjectNote value) {
        this.projectNote = value;
    }

    /**
     * 获取projectResource属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectResource }
     *
     */
    public ProjectResource getProjectResource() {
        return projectResource;
    }

    /**
     * 设置projectResource属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectResource }
     *
     */
    public void setProjectResource(ProjectResource value) {
        this.projectResource = value;
    }

    /**
     * 获取projectResourceCategory属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectResourceCategory }
     *
     */
    public ProjectResourceCategory getProjectResourceCategory() {
        return projectResourceCategory;
    }

    /**
     * 设置projectResourceCategory属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectResourceCategory }
     *
     */
    public void setProjectResourceCategory(ProjectResourceCategory value) {
        this.projectResourceCategory = value;
    }

    /**
     * 获取projectResourceQuantity属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectResourceQuantity }
     *
     */
    public ProjectResourceQuantity getProjectResourceQuantity() {
        return projectResourceQuantity;
    }

    /**
     * 设置projectResourceQuantity属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectResourceQuantity }
     *
     */
    public void setProjectResourceQuantity(ProjectResourceQuantity value) {
        this.projectResourceQuantity = value;
    }

    /**
     * 获取projectSpendingPlan属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectSpendingPlan }
     *
     */
    public ProjectSpendingPlan getProjectSpendingPlan() {
        return projectSpendingPlan;
    }

    /**
     * 设置projectSpendingPlan属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectSpendingPlan }
     *
     */
    public void setProjectSpendingPlan(ProjectSpendingPlan value) {
        this.projectSpendingPlan = value;
    }

    /**
     * 获取projectThreshold属性的值。
     *
     * @return
     *     possible object is
     *     {@link ProjectThreshold }
     *
     */
    public ProjectThreshold getProjectThreshold() {
        return projectThreshold;
    }

    /**
     * 设置projectThreshold属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ProjectThreshold }
     *
     */
    public void setProjectThreshold(ProjectThreshold value) {
        this.projectThreshold = value;
    }

    /**
     * 获取relationship属性的值。
     *
     * @return
     *     possible object is
     *     {@link Relationship }
     *
     */
    public Relationship getRelationship() {
        return relationship;
    }

    /**
     * 设置relationship属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Relationship }
     *
     */
    public void setRelationship(Relationship value) {
        this.relationship = value;
    }

    /**
     * 获取resource属性的值。
     *
     * @return
     *     possible object is
     *     {@link Resource }
     *
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * 设置resource属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Resource }
     *
     */
    public void setResource(Resource value) {
        this.resource = value;
    }

    /**
     * 获取resourceAssignment属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceAssignment }
     *
     */
    public ResourceAssignment getResourceAssignment() {
        return resourceAssignment;
    }

    /**
     * 设置resourceAssignment属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceAssignment }
     *
     */
    public void setResourceAssignment(ResourceAssignment value) {
        this.resourceAssignment = value;
    }

    /**
     * 获取resourceAssignmentPeriodActual属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceAssignmentPeriodActual }
     *
     */
    public ResourceAssignmentPeriodActual getResourceAssignmentPeriodActual() {
        return resourceAssignmentPeriodActual;
    }

    /**
     * 设置resourceAssignmentPeriodActual属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceAssignmentPeriodActual }
     *
     */
    public void setResourceAssignmentPeriodActual(ResourceAssignmentPeriodActual value) {
        this.resourceAssignmentPeriodActual = value;
    }

    /**
     * 获取resourceCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceCode }
     *
     */
    public ResourceCode getResourceCode() {
        return resourceCode;
    }

    /**
     * 设置resourceCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceCode }
     *
     */
    public void setResourceCode(ResourceCode value) {
        this.resourceCode = value;
    }

    /**
     * 获取resourceCodeAssignment属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceCodeAssignment }
     *
     */
    public ResourceCodeAssignment getResourceCodeAssignment() {
        return resourceCodeAssignment;
    }

    /**
     * 设置resourceCodeAssignment属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceCodeAssignment }
     *
     */
    public void setResourceCodeAssignment(ResourceCodeAssignment value) {
        this.resourceCodeAssignment = value;
    }

    /**
     * 获取resourceCodeType属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceCodeType }
     *
     */
    public ResourceCodeType getResourceCodeType() {
        return resourceCodeType;
    }

    /**
     * 设置resourceCodeType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceCodeType }
     *
     */
    public void setResourceCodeType(ResourceCodeType value) {
        this.resourceCodeType = value;
    }

    /**
     * 获取resourceCurve属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceCurve }
     *
     */
    public ResourceCurve getResourceCurve() {
        return resourceCurve;
    }

    /**
     * 设置resourceCurve属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceCurve }
     *
     */
    public void setResourceCurve(ResourceCurve value) {
        this.resourceCurve = value;
    }

    /**
     * 获取resourceRate属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceRate }
     *
     */
    public ResourceRate getResourceRate() {
        return resourceRate;
    }

    /**
     * 设置resourceRate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceRate }
     *
     */
    public void setResourceRate(ResourceRate value) {
        this.resourceRate = value;
    }

    /**
     * 获取resourceRole属性的值。
     *
     * @return
     *     possible object is
     *     {@link ResourceRole }
     *
     */
    public ResourceRole getResourceRole() {
        return resourceRole;
    }

    /**
     * 设置resourceRole属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ResourceRole }
     *
     */
    public void setResourceRole(ResourceRole value) {
        this.resourceRole = value;
    }

    /**
     * 获取risk属性的值。
     *
     * @return
     *     possible object is
     *     {@link Risk }
     *
     */
    public Risk getRisk() {
        return risk;
    }

    /**
     * 设置risk属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Risk }
     *
     */
    public void setRisk(Risk value) {
        this.risk = value;
    }

    /**
     * 获取riskCategory属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskCategory }
     *
     */
    public RiskCategory getRiskCategory() {
        return riskCategory;
    }

    /**
     * 设置riskCategory属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskCategory }
     *
     */
    public void setRiskCategory(RiskCategory value) {
        this.riskCategory = value;
    }

    /**
     * 获取riskImpact属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskImpact }
     *
     */
    public RiskImpact getRiskImpact() {
        return riskImpact;
    }

    /**
     * 设置riskImpact属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskImpact }
     *
     */
    public void setRiskImpact(RiskImpact value) {
        this.riskImpact = value;
    }

    /**
     * 获取riskMatrixScore属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskMatrixScore }
     *
     */
    public RiskMatrixScore getRiskMatrixScore() {
        return riskMatrixScore;
    }

    /**
     * 设置riskMatrixScore属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskMatrixScore }
     *
     */
    public void setRiskMatrixScore(RiskMatrixScore value) {
        this.riskMatrixScore = value;
    }

    /**
     * 获取riskMatrixThreshold属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskMatrixThreshold }
     *
     */
    public RiskMatrixThreshold getRiskMatrixThreshold() {
        return riskMatrixThreshold;
    }

    /**
     * 设置riskMatrixThreshold属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskMatrixThreshold }
     *
     */
    public void setRiskMatrixThreshold(RiskMatrixThreshold value) {
        this.riskMatrixThreshold = value;
    }

    /**
     * 获取riskResponseAction属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskResponseAction }
     *
     */
    public RiskResponseAction getRiskResponseAction() {
        return riskResponseAction;
    }

    /**
     * 设置riskResponseAction属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskResponseAction }
     *
     */
    public void setRiskResponseAction(RiskResponseAction value) {
        this.riskResponseAction = value;
    }

    /**
     * 获取riskResponseActionImpact属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskResponseActionImpact }
     *
     */
    public RiskResponseActionImpact getRiskResponseActionImpact() {
        return riskResponseActionImpact;
    }

    /**
     * 设置riskResponseActionImpact属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskResponseActionImpact }
     *
     */
    public void setRiskResponseActionImpact(RiskResponseActionImpact value) {
        this.riskResponseActionImpact = value;
    }

    /**
     * 获取riskResponsePlan属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskResponsePlan }
     *
     */
    public RiskResponsePlan getRiskResponsePlan() {
        return riskResponsePlan;
    }

    /**
     * 设置riskResponsePlan属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskResponsePlan }
     *
     */
    public void setRiskResponsePlan(RiskResponsePlan value) {
        this.riskResponsePlan = value;
    }

    /**
     * 获取riskMatrix属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskMatrix }
     *
     */
    public RiskMatrix getRiskMatrix() {
        return riskMatrix;
    }

    /**
     * 设置riskMatrix属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskMatrix }
     *
     */
    public void setRiskMatrix(RiskMatrix value) {
        this.riskMatrix = value;
    }

    /**
     * 获取riskThresholdLevel属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskThresholdLevel }
     *
     */
    public RiskThresholdLevel getRiskThresholdLevel() {
        return riskThresholdLevel;
    }

    /**
     * 设置riskThresholdLevel属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskThresholdLevel }
     *
     */
    public void setRiskThresholdLevel(RiskThresholdLevel value) {
        this.riskThresholdLevel = value;
    }

    /**
     * 获取riskThreshold属性的值。
     *
     * @return
     *     possible object is
     *     {@link RiskThreshold }
     *
     */
    public RiskThreshold getRiskThreshold() {
        return riskThreshold;
    }

    /**
     * 设置riskThreshold属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RiskThreshold }
     *
     */
    public void setRiskThreshold(RiskThreshold value) {
        this.riskThreshold = value;
    }

    /**
     * 获取role属性的值。
     *
     * @return
     *     possible object is
     *     {@link Role }
     *
     */
    public Role getRole() {
        return role;
    }

    /**
     * 设置role属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Role }
     *
     */
    public void setRole(Role value) {
        this.role = value;
    }

    /**
     * 获取roleRate属性的值。
     *
     * @return
     *     possible object is
     *     {@link RoleRate }
     *
     */
    public RoleRate getRoleRate() {
        return roleRate;
    }

    /**
     * 设置roleRate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RoleRate }
     *
     */
    public void setRoleRate(RoleRate value) {
        this.roleRate = value;
    }

    /**
     * 获取roleLimit属性的值。
     *
     * @return
     *     possible object is
     *     {@link RoleLimit }
     *
     */
    public RoleLimit getRoleLimit() {
        return roleLimit;
    }

    /**
     * 设置roleLimit属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link RoleLimit }
     *
     */
    public void setRoleLimit(RoleLimit value) {
        this.roleLimit = value;
    }

    /**
     * 获取shift属性的值。
     *
     * @return
     *     possible object is
     *     {@link Shift }
     *
     */
    public Shift getShift() {
        return shift;
    }

    /**
     * 设置shift属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Shift }
     *
     */
    public void setShift(Shift value) {
        this.shift = value;
    }

    /**
     * 获取thresholdParameter属性的值。
     *
     * @return
     *     possible object is
     *     {@link ThresholdParameter }
     *
     */
    public ThresholdParameter getThresholdParameter() {
        return thresholdParameter;
    }

    /**
     * 设置thresholdParameter属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ThresholdParameter }
     *
     */
    public void setThresholdParameter(ThresholdParameter value) {
        this.thresholdParameter = value;
    }

    /**
     * 获取udfCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link UDFCode }
     *
     */
    public UDFCode getUDFCode() {
        return udfCode;
    }

    /**
     * 设置udfCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link UDFCode }
     *
     */
    public void setUDFCode(UDFCode value) {
        this.udfCode = value;
    }

    /**
     * 获取udfType属性的值。
     *
     * @return
     *     possible object is
     *     {@link UDFType }
     *
     */
    public UDFType getUDFType() {
        return udfType;
    }

    /**
     * 设置udfType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link UDFType }
     *
     */
    public void setUDFType(UDFType value) {
        this.udfType = value;
    }

    /**
     * 获取udfValue属性的值。
     *
     * @return
     *     possible object is
     *     {@link UDFValue }
     *
     */
    public UDFValue getUDFValue() {
        return udfValue;
    }

    /**
     * 设置udfValue属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link UDFValue }
     *
     */
    public void setUDFValue(UDFValue value) {
        this.udfValue = value;
    }

    /**
     * 获取unitOfMeasure属性的值。
     *
     * @return
     *     possible object is
     *     {@link UnitOfMeasure }
     *
     */
    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    /**
     * 设置unitOfMeasure属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link UnitOfMeasure }
     *
     */
    public void setUnitOfMeasure(UnitOfMeasure value) {
        this.unitOfMeasure = value;
    }

    /**
     * 获取wbs属性的值。
     *
     * @return
     *     possible object is
     *     {@link WBS }
     *
     */
    public WBS getWBS() {
        return wbs;
    }

    /**
     * 设置wbs属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link WBS }
     *
     */
    public void setWBS(WBS value) {
        this.wbs = value;
    }

    /**
     * 获取wbsCategory属性的值。
     *
     * @return
     *     possible object is
     *     {@link WBSCategory }
     *
     */
    public WBSCategory getWBSCategory() {
        return wbsCategory;
    }

    /**
     * 设置wbsCategory属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link WBSCategory }
     *
     */
    public void setWBSCategory(WBSCategory value) {
        this.wbsCategory = value;
    }

    /**
     * 获取wbsMilestone属性的值。
     *
     * @return
     *     possible object is
     *     {@link WBSMilestone }
     *
     */
    public WBSMilestone getWBSMilestone() {
        return wbsMilestone;
    }

    /**
     * 设置wbsMilestone属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link WBSMilestone }
     *
     */
    public void setWBSMilestone(WBSMilestone value) {
        this.wbsMilestone = value;
    }

}
