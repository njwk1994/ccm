
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ActivityFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ActivityFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AccountingVariance"/&gt;
 *     &lt;enumeration value="AccountingVarianceLaborUnits"/&gt;
 *     &lt;enumeration value="ActivityOwnerUserId"/&gt;
 *     &lt;enumeration value="ActualDuration"/&gt;
 *     &lt;enumeration value="ActualExpenseCost"/&gt;
 *     &lt;enumeration value="ActualFinishDate"/&gt;
 *     &lt;enumeration value="ActualLaborCost"/&gt;
 *     &lt;enumeration value="ActualLaborUnits"/&gt;
 *     &lt;enumeration value="ActualMaterialCost"/&gt;
 *     &lt;enumeration value="ActualNonLaborCost"/&gt;
 *     &lt;enumeration value="ActualNonLaborUnits"/&gt;
 *     &lt;enumeration value="ActualStartDate"/&gt;
 *     &lt;enumeration value="ActualThisPeriodLaborCost"/&gt;
 *     &lt;enumeration value="ActualThisPeriodLaborUnits"/&gt;
 *     &lt;enumeration value="ActualThisPeriodMaterialCost"/&gt;
 *     &lt;enumeration value="ActualThisPeriodNonLaborCost"/&gt;
 *     &lt;enumeration value="ActualThisPeriodNonLaborUnits"/&gt;
 *     &lt;enumeration value="ActualTotalCost"/&gt;
 *     &lt;enumeration value="ActualTotalUnits"/&gt;
 *     &lt;enumeration value="AtCompletionDuration"/&gt;
 *     &lt;enumeration value="AtCompletionExpenseCost"/&gt;
 *     &lt;enumeration value="AtCompletionLaborCost"/&gt;
 *     &lt;enumeration value="AtCompletionLaborUnits"/&gt;
 *     &lt;enumeration value="AtCompletionLaborUnitsVariance"/&gt;
 *     &lt;enumeration value="AtCompletionMaterialCost"/&gt;
 *     &lt;enumeration value="AtCompletionNonLaborCost"/&gt;
 *     &lt;enumeration value="AtCompletionNonLaborUnits"/&gt;
 *     &lt;enumeration value="AtCompletionTotalCost"/&gt;
 *     &lt;enumeration value="AtCompletionTotalUnits"/&gt;
 *     &lt;enumeration value="AtCompletionVariance"/&gt;
 *     &lt;enumeration value="AutoComputeActuals"/&gt;
 *     &lt;enumeration value="Baseline1Duration"/&gt;
 *     &lt;enumeration value="Baseline1FinishDate"/&gt;
 *     &lt;enumeration value="Baseline1PlannedDuration"/&gt;
 *     &lt;enumeration value="Baseline1PlannedExpenseCost"/&gt;
 *     &lt;enumeration value="Baseline1PlannedLaborCost"/&gt;
 *     &lt;enumeration value="Baseline1PlannedLaborUnits"/&gt;
 *     &lt;enumeration value="Baseline1PlannedMaterialCost"/&gt;
 *     &lt;enumeration value="Baseline1PlannedNonLaborCost"/&gt;
 *     &lt;enumeration value="Baseline1PlannedNonLaborUnits"/&gt;
 *     &lt;enumeration value="Baseline1PlannedTotalCost"/&gt;
 *     &lt;enumeration value="Baseline1StartDate"/&gt;
 *     &lt;enumeration value="BaselineDuration"/&gt;
 *     &lt;enumeration value="BaselineFinishDate"/&gt;
 *     &lt;enumeration value="BaselinePlannedDuration"/&gt;
 *     &lt;enumeration value="BaselinePlannedExpenseCost"/&gt;
 *     &lt;enumeration value="BaselinePlannedLaborCost"/&gt;
 *     &lt;enumeration value="BaselinePlannedLaborUnits"/&gt;
 *     &lt;enumeration value="BaselinePlannedMaterialCost"/&gt;
 *     &lt;enumeration value="BaselinePlannedNonLaborCost"/&gt;
 *     &lt;enumeration value="BaselinePlannedNonLaborUnits"/&gt;
 *     &lt;enumeration value="BaselinePlannedTotalCost"/&gt;
 *     &lt;enumeration value="BaselineStartDate"/&gt;
 *     &lt;enumeration value="BudgetAtCompletion"/&gt;
 *     &lt;enumeration value="CBSCode"/&gt;
 *     &lt;enumeration value="CBSId"/&gt;
 *     &lt;enumeration value="CBSObjectId"/&gt;
 *     &lt;enumeration value="CalendarName"/&gt;
 *     &lt;enumeration value="CalendarObjectId"/&gt;
 *     &lt;enumeration value="CostPercentComplete"/&gt;
 *     &lt;enumeration value="CostPercentOfPlanned"/&gt;
 *     &lt;enumeration value="CostPerformanceIndex"/&gt;
 *     &lt;enumeration value="CostPerformanceIndexLaborUnits"/&gt;
 *     &lt;enumeration value="CostVariance"/&gt;
 *     &lt;enumeration value="CostVarianceIndex"/&gt;
 *     &lt;enumeration value="CostVarianceIndexLaborUnits"/&gt;
 *     &lt;enumeration value="CostVarianceLaborUnits"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DataDate"/&gt;
 *     &lt;enumeration value="Duration1Variance"/&gt;
 *     &lt;enumeration value="DurationPercentComplete"/&gt;
 *     &lt;enumeration value="DurationPercentOfPlanned"/&gt;
 *     &lt;enumeration value="DurationType"/&gt;
 *     &lt;enumeration value="DurationVariance"/&gt;
 *     &lt;enumeration value="EarlyFinishDate"/&gt;
 *     &lt;enumeration value="EarlyStartDate"/&gt;
 *     &lt;enumeration value="EarnedValueCost"/&gt;
 *     &lt;enumeration value="EarnedValueLaborUnits"/&gt;
 *     &lt;enumeration value="EstimateAtCompletionCost"/&gt;
 *     &lt;enumeration value="EstimateAtCompletionLaborUnits"/&gt;
 *     &lt;enumeration value="EstimateToComplete"/&gt;
 *     &lt;enumeration value="EstimateToCompleteLaborUnits"/&gt;
 *     &lt;enumeration value="EstimatedWeight"/&gt;
 *     &lt;enumeration value="ExpectedFinishDate"/&gt;
 *     &lt;enumeration value="ExpenseCost1Variance"/&gt;
 *     &lt;enumeration value="ExpenseCostPercentComplete"/&gt;
 *     &lt;enumeration value="ExpenseCostVariance"/&gt;
 *     &lt;enumeration value="ExternalEarlyStartDate"/&gt;
 *     &lt;enumeration value="ExternalLateFinishDate"/&gt;
 *     &lt;enumeration value="Feedback"/&gt;
 *     &lt;enumeration value="FinishDate"/&gt;
 *     &lt;enumeration value="FinishDate1Variance"/&gt;
 *     &lt;enumeration value="FinishDateVariance"/&gt;
 *     &lt;enumeration value="FloatPath"/&gt;
 *     &lt;enumeration value="FloatPathOrder"/&gt;
 *     &lt;enumeration value="FreeFloat"/&gt;
 *     &lt;enumeration value="GUID"/&gt;
 *     &lt;enumeration value="HasFutureBucketData"/&gt;
 *     &lt;enumeration value="Id"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsCritical"/&gt;
 *     &lt;enumeration value="IsLongestPath"/&gt;
 *     &lt;enumeration value="IsNewFeedback"/&gt;
 *     &lt;enumeration value="IsStarred"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="IsWorkPackage"/&gt;
 *     &lt;enumeration value="LaborCost1Variance"/&gt;
 *     &lt;enumeration value="LaborCostPercentComplete"/&gt;
 *     &lt;enumeration value="LaborCostVariance"/&gt;
 *     &lt;enumeration value="LaborUnits1Variance"/&gt;
 *     &lt;enumeration value="LaborUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="LaborUnitsVariance"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="LateFinishDate"/&gt;
 *     &lt;enumeration value="LateStartDate"/&gt;
 *     &lt;enumeration value="LevelingPriority"/&gt;
 *     &lt;enumeration value="LocationName"/&gt;
 *     &lt;enumeration value="LocationObjectId"/&gt;
 *     &lt;enumeration value="MaterialCost1Variance"/&gt;
 *     &lt;enumeration value="MaterialCostPercentComplete"/&gt;
 *     &lt;enumeration value="MaterialCostVariance"/&gt;
 *     &lt;enumeration value="MaximumDuration"/&gt;
 *     &lt;enumeration value="MinimumDuration"/&gt;
 *     &lt;enumeration value="MostLikelyDuration"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="NonLaborCost1Variance"/&gt;
 *     &lt;enumeration value="NonLaborCostPercentComplete"/&gt;
 *     &lt;enumeration value="NonLaborCostVariance"/&gt;
 *     &lt;enumeration value="NonLaborUnits1Variance"/&gt;
 *     &lt;enumeration value="NonLaborUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="NonLaborUnitsVariance"/&gt;
 *     &lt;enumeration value="NotesToResources"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="OwnerIDArray"/&gt;
 *     &lt;enumeration value="OwnerNamesArray"/&gt;
 *     &lt;enumeration value="PercentComplete"/&gt;
 *     &lt;enumeration value="PercentCompleteType"/&gt;
 *     &lt;enumeration value="PerformancePercentComplete"/&gt;
 *     &lt;enumeration value="PhysicalPercentComplete"/&gt;
 *     &lt;enumeration value="PlannedDuration"/&gt;
 *     &lt;enumeration value="PlannedExpenseCost"/&gt;
 *     &lt;enumeration value="PlannedFinishDate"/&gt;
 *     &lt;enumeration value="PlannedLaborCost"/&gt;
 *     &lt;enumeration value="PlannedLaborUnits"/&gt;
 *     &lt;enumeration value="PlannedMaterialCost"/&gt;
 *     &lt;enumeration value="PlannedNonLaborCost"/&gt;
 *     &lt;enumeration value="PlannedNonLaborUnits"/&gt;
 *     &lt;enumeration value="PlannedStartDate"/&gt;
 *     &lt;enumeration value="PlannedTotalCost"/&gt;
 *     &lt;enumeration value="PlannedTotalUnits"/&gt;
 *     &lt;enumeration value="PlannedValueCost"/&gt;
 *     &lt;enumeration value="PlannedValueLaborUnits"/&gt;
 *     &lt;enumeration value="PostResponsePessimisticFinish"/&gt;
 *     &lt;enumeration value="PostResponsePessimisticStart"/&gt;
 *     &lt;enumeration value="PreResponsePessimisticFinish"/&gt;
 *     &lt;enumeration value="PreResponsePessimisticStart"/&gt;
 *     &lt;enumeration value="PrimaryConstraintDate"/&gt;
 *     &lt;enumeration value="PrimaryConstraintType"/&gt;
 *     &lt;enumeration value="PrimaryResourceId"/&gt;
 *     &lt;enumeration value="PrimaryResourceName"/&gt;
 *     &lt;enumeration value="PrimaryResourceObjectId"/&gt;
 *     &lt;enumeration value="ProjectFlag"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectName"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ProjectProjectFlag"/&gt;
 *     &lt;enumeration value="RemainingDuration"/&gt;
 *     &lt;enumeration value="RemainingEarlyFinishDate"/&gt;
 *     &lt;enumeration value="RemainingEarlyStartDate"/&gt;
 *     &lt;enumeration value="RemainingExpenseCost"/&gt;
 *     &lt;enumeration value="RemainingFloat"/&gt;
 *     &lt;enumeration value="RemainingLaborCost"/&gt;
 *     &lt;enumeration value="RemainingLaborUnits"/&gt;
 *     &lt;enumeration value="RemainingLateFinishDate"/&gt;
 *     &lt;enumeration value="RemainingLateStartDate"/&gt;
 *     &lt;enumeration value="RemainingMaterialCost"/&gt;
 *     &lt;enumeration value="RemainingNonLaborCost"/&gt;
 *     &lt;enumeration value="RemainingNonLaborUnits"/&gt;
 *     &lt;enumeration value="RemainingTotalCost"/&gt;
 *     &lt;enumeration value="RemainingTotalUnits"/&gt;
 *     &lt;enumeration value="ResumeDate"/&gt;
 *     &lt;enumeration value="ReviewFinishDate"/&gt;
 *     &lt;enumeration value="ReviewRequired"/&gt;
 *     &lt;enumeration value="ReviewStatus"/&gt;
 *     &lt;enumeration value="SchedulePercentComplete"/&gt;
 *     &lt;enumeration value="SchedulePerformanceIndex"/&gt;
 *     &lt;enumeration value="SchedulePerformanceIndexLaborUnits"/&gt;
 *     &lt;enumeration value="ScheduleVariance"/&gt;
 *     &lt;enumeration value="ScheduleVarianceIndex"/&gt;
 *     &lt;enumeration value="ScheduleVarianceIndexLaborUnits"/&gt;
 *     &lt;enumeration value="ScheduleVarianceLaborUnits"/&gt;
 *     &lt;enumeration value="SecondaryConstraintDate"/&gt;
 *     &lt;enumeration value="SecondaryConstraintType"/&gt;
 *     &lt;enumeration value="StartDate"/&gt;
 *     &lt;enumeration value="StartDate1Variance"/&gt;
 *     &lt;enumeration value="StartDateVariance"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *     &lt;enumeration value="StatusCode"/&gt;
 *     &lt;enumeration value="SuspendDate"/&gt;
 *     &lt;enumeration value="ToCompletePerformanceIndex"/&gt;
 *     &lt;enumeration value="TotalCost1Variance"/&gt;
 *     &lt;enumeration value="TotalCostVariance"/&gt;
 *     &lt;enumeration value="TotalFloat"/&gt;
 *     &lt;enumeration value="Type"/&gt;
 *     &lt;enumeration value="UnitsPercentComplete"/&gt;
 *     &lt;enumeration value="UnreadCommentCount"/&gt;
 *     &lt;enumeration value="WBSCode"/&gt;
 *     &lt;enumeration value="WBSName"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *     &lt;enumeration value="WBSPath"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ActivityFieldType")
@XmlEnum
public enum ActivityFieldType {

    @XmlEnumValue("AccountingVariance")
    ACCOUNTING_VARIANCE("AccountingVariance"),
    @XmlEnumValue("AccountingVarianceLaborUnits")
    ACCOUNTING_VARIANCE_LABOR_UNITS("AccountingVarianceLaborUnits"),
    @XmlEnumValue("ActivityOwnerUserId")
    ACTIVITY_OWNER_USER_ID("ActivityOwnerUserId"),
    @XmlEnumValue("ActualDuration")
    ACTUAL_DURATION("ActualDuration"),
    @XmlEnumValue("ActualExpenseCost")
    ACTUAL_EXPENSE_COST("ActualExpenseCost"),
    @XmlEnumValue("ActualFinishDate")
    ACTUAL_FINISH_DATE("ActualFinishDate"),
    @XmlEnumValue("ActualLaborCost")
    ACTUAL_LABOR_COST("ActualLaborCost"),
    @XmlEnumValue("ActualLaborUnits")
    ACTUAL_LABOR_UNITS("ActualLaborUnits"),
    @XmlEnumValue("ActualMaterialCost")
    ACTUAL_MATERIAL_COST("ActualMaterialCost"),
    @XmlEnumValue("ActualNonLaborCost")
    ACTUAL_NON_LABOR_COST("ActualNonLaborCost"),
    @XmlEnumValue("ActualNonLaborUnits")
    ACTUAL_NON_LABOR_UNITS("ActualNonLaborUnits"),
    @XmlEnumValue("ActualStartDate")
    ACTUAL_START_DATE("ActualStartDate"),
    @XmlEnumValue("ActualThisPeriodLaborCost")
    ACTUAL_THIS_PERIOD_LABOR_COST("ActualThisPeriodLaborCost"),
    @XmlEnumValue("ActualThisPeriodLaborUnits")
    ACTUAL_THIS_PERIOD_LABOR_UNITS("ActualThisPeriodLaborUnits"),
    @XmlEnumValue("ActualThisPeriodMaterialCost")
    ACTUAL_THIS_PERIOD_MATERIAL_COST("ActualThisPeriodMaterialCost"),
    @XmlEnumValue("ActualThisPeriodNonLaborCost")
    ACTUAL_THIS_PERIOD_NON_LABOR_COST("ActualThisPeriodNonLaborCost"),
    @XmlEnumValue("ActualThisPeriodNonLaborUnits")
    ACTUAL_THIS_PERIOD_NON_LABOR_UNITS("ActualThisPeriodNonLaborUnits"),
    @XmlEnumValue("ActualTotalCost")
    ACTUAL_TOTAL_COST("ActualTotalCost"),
    @XmlEnumValue("ActualTotalUnits")
    ACTUAL_TOTAL_UNITS("ActualTotalUnits"),
    @XmlEnumValue("AtCompletionDuration")
    AT_COMPLETION_DURATION("AtCompletionDuration"),
    @XmlEnumValue("AtCompletionExpenseCost")
    AT_COMPLETION_EXPENSE_COST("AtCompletionExpenseCost"),
    @XmlEnumValue("AtCompletionLaborCost")
    AT_COMPLETION_LABOR_COST("AtCompletionLaborCost"),
    @XmlEnumValue("AtCompletionLaborUnits")
    AT_COMPLETION_LABOR_UNITS("AtCompletionLaborUnits"),
    @XmlEnumValue("AtCompletionLaborUnitsVariance")
    AT_COMPLETION_LABOR_UNITS_VARIANCE("AtCompletionLaborUnitsVariance"),
    @XmlEnumValue("AtCompletionMaterialCost")
    AT_COMPLETION_MATERIAL_COST("AtCompletionMaterialCost"),
    @XmlEnumValue("AtCompletionNonLaborCost")
    AT_COMPLETION_NON_LABOR_COST("AtCompletionNonLaborCost"),
    @XmlEnumValue("AtCompletionNonLaborUnits")
    AT_COMPLETION_NON_LABOR_UNITS("AtCompletionNonLaborUnits"),
    @XmlEnumValue("AtCompletionTotalCost")
    AT_COMPLETION_TOTAL_COST("AtCompletionTotalCost"),
    @XmlEnumValue("AtCompletionTotalUnits")
    AT_COMPLETION_TOTAL_UNITS("AtCompletionTotalUnits"),
    @XmlEnumValue("AtCompletionVariance")
    AT_COMPLETION_VARIANCE("AtCompletionVariance"),
    @XmlEnumValue("AutoComputeActuals")
    AUTO_COMPUTE_ACTUALS("AutoComputeActuals"),
    @XmlEnumValue("Baseline1Duration")
    BASELINE_1_DURATION("Baseline1Duration"),
    @XmlEnumValue("Baseline1FinishDate")
    BASELINE_1_FINISH_DATE("Baseline1FinishDate"),
    @XmlEnumValue("Baseline1PlannedDuration")
    BASELINE_1_PLANNED_DURATION("Baseline1PlannedDuration"),
    @XmlEnumValue("Baseline1PlannedExpenseCost")
    BASELINE_1_PLANNED_EXPENSE_COST("Baseline1PlannedExpenseCost"),
    @XmlEnumValue("Baseline1PlannedLaborCost")
    BASELINE_1_PLANNED_LABOR_COST("Baseline1PlannedLaborCost"),
    @XmlEnumValue("Baseline1PlannedLaborUnits")
    BASELINE_1_PLANNED_LABOR_UNITS("Baseline1PlannedLaborUnits"),
    @XmlEnumValue("Baseline1PlannedMaterialCost")
    BASELINE_1_PLANNED_MATERIAL_COST("Baseline1PlannedMaterialCost"),
    @XmlEnumValue("Baseline1PlannedNonLaborCost")
    BASELINE_1_PLANNED_NON_LABOR_COST("Baseline1PlannedNonLaborCost"),
    @XmlEnumValue("Baseline1PlannedNonLaborUnits")
    BASELINE_1_PLANNED_NON_LABOR_UNITS("Baseline1PlannedNonLaborUnits"),
    @XmlEnumValue("Baseline1PlannedTotalCost")
    BASELINE_1_PLANNED_TOTAL_COST("Baseline1PlannedTotalCost"),
    @XmlEnumValue("Baseline1StartDate")
    BASELINE_1_START_DATE("Baseline1StartDate"),
    @XmlEnumValue("BaselineDuration")
    BASELINE_DURATION("BaselineDuration"),
    @XmlEnumValue("BaselineFinishDate")
    BASELINE_FINISH_DATE("BaselineFinishDate"),
    @XmlEnumValue("BaselinePlannedDuration")
    BASELINE_PLANNED_DURATION("BaselinePlannedDuration"),
    @XmlEnumValue("BaselinePlannedExpenseCost")
    BASELINE_PLANNED_EXPENSE_COST("BaselinePlannedExpenseCost"),
    @XmlEnumValue("BaselinePlannedLaborCost")
    BASELINE_PLANNED_LABOR_COST("BaselinePlannedLaborCost"),
    @XmlEnumValue("BaselinePlannedLaborUnits")
    BASELINE_PLANNED_LABOR_UNITS("BaselinePlannedLaborUnits"),
    @XmlEnumValue("BaselinePlannedMaterialCost")
    BASELINE_PLANNED_MATERIAL_COST("BaselinePlannedMaterialCost"),
    @XmlEnumValue("BaselinePlannedNonLaborCost")
    BASELINE_PLANNED_NON_LABOR_COST("BaselinePlannedNonLaborCost"),
    @XmlEnumValue("BaselinePlannedNonLaborUnits")
    BASELINE_PLANNED_NON_LABOR_UNITS("BaselinePlannedNonLaborUnits"),
    @XmlEnumValue("BaselinePlannedTotalCost")
    BASELINE_PLANNED_TOTAL_COST("BaselinePlannedTotalCost"),
    @XmlEnumValue("BaselineStartDate")
    BASELINE_START_DATE("BaselineStartDate"),
    @XmlEnumValue("BudgetAtCompletion")
    BUDGET_AT_COMPLETION("BudgetAtCompletion"),
    @XmlEnumValue("CBSCode")
    CBS_CODE("CBSCode"),
    @XmlEnumValue("CBSId")
    CBS_ID("CBSId"),
    @XmlEnumValue("CBSObjectId")
    CBS_OBJECT_ID("CBSObjectId"),
    @XmlEnumValue("CalendarName")
    CALENDAR_NAME("CalendarName"),
    @XmlEnumValue("CalendarObjectId")
    CALENDAR_OBJECT_ID("CalendarObjectId"),
    @XmlEnumValue("CostPercentComplete")
    COST_PERCENT_COMPLETE("CostPercentComplete"),
    @XmlEnumValue("CostPercentOfPlanned")
    COST_PERCENT_OF_PLANNED("CostPercentOfPlanned"),
    @XmlEnumValue("CostPerformanceIndex")
    COST_PERFORMANCE_INDEX("CostPerformanceIndex"),
    @XmlEnumValue("CostPerformanceIndexLaborUnits")
    COST_PERFORMANCE_INDEX_LABOR_UNITS("CostPerformanceIndexLaborUnits"),
    @XmlEnumValue("CostVariance")
    COST_VARIANCE("CostVariance"),
    @XmlEnumValue("CostVarianceIndex")
    COST_VARIANCE_INDEX("CostVarianceIndex"),
    @XmlEnumValue("CostVarianceIndexLaborUnits")
    COST_VARIANCE_INDEX_LABOR_UNITS("CostVarianceIndexLaborUnits"),
    @XmlEnumValue("CostVarianceLaborUnits")
    COST_VARIANCE_LABOR_UNITS("CostVarianceLaborUnits"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("DataDate")
    DATA_DATE("DataDate"),
    @XmlEnumValue("Duration1Variance")
    DURATION_1_VARIANCE("Duration1Variance"),
    @XmlEnumValue("DurationPercentComplete")
    DURATION_PERCENT_COMPLETE("DurationPercentComplete"),
    @XmlEnumValue("DurationPercentOfPlanned")
    DURATION_PERCENT_OF_PLANNED("DurationPercentOfPlanned"),
    @XmlEnumValue("DurationType")
    DURATION_TYPE("DurationType"),
    @XmlEnumValue("DurationVariance")
    DURATION_VARIANCE("DurationVariance"),
    @XmlEnumValue("EarlyFinishDate")
    EARLY_FINISH_DATE("EarlyFinishDate"),
    @XmlEnumValue("EarlyStartDate")
    EARLY_START_DATE("EarlyStartDate"),
    @XmlEnumValue("EarnedValueCost")
    EARNED_VALUE_COST("EarnedValueCost"),
    @XmlEnumValue("EarnedValueLaborUnits")
    EARNED_VALUE_LABOR_UNITS("EarnedValueLaborUnits"),
    @XmlEnumValue("EstimateAtCompletionCost")
    ESTIMATE_AT_COMPLETION_COST("EstimateAtCompletionCost"),
    @XmlEnumValue("EstimateAtCompletionLaborUnits")
    ESTIMATE_AT_COMPLETION_LABOR_UNITS("EstimateAtCompletionLaborUnits"),
    @XmlEnumValue("EstimateToComplete")
    ESTIMATE_TO_COMPLETE("EstimateToComplete"),
    @XmlEnumValue("EstimateToCompleteLaborUnits")
    ESTIMATE_TO_COMPLETE_LABOR_UNITS("EstimateToCompleteLaborUnits"),
    @XmlEnumValue("EstimatedWeight")
    ESTIMATED_WEIGHT("EstimatedWeight"),
    @XmlEnumValue("ExpectedFinishDate")
    EXPECTED_FINISH_DATE("ExpectedFinishDate"),
    @XmlEnumValue("ExpenseCost1Variance")
    EXPENSE_COST_1_VARIANCE("ExpenseCost1Variance"),
    @XmlEnumValue("ExpenseCostPercentComplete")
    EXPENSE_COST_PERCENT_COMPLETE("ExpenseCostPercentComplete"),
    @XmlEnumValue("ExpenseCostVariance")
    EXPENSE_COST_VARIANCE("ExpenseCostVariance"),
    @XmlEnumValue("ExternalEarlyStartDate")
    EXTERNAL_EARLY_START_DATE("ExternalEarlyStartDate"),
    @XmlEnumValue("ExternalLateFinishDate")
    EXTERNAL_LATE_FINISH_DATE("ExternalLateFinishDate"),
    @XmlEnumValue("Feedback")
    FEEDBACK("Feedback"),
    @XmlEnumValue("FinishDate")
    FINISH_DATE("FinishDate"),
    @XmlEnumValue("FinishDate1Variance")
    FINISH_DATE_1_VARIANCE("FinishDate1Variance"),
    @XmlEnumValue("FinishDateVariance")
    FINISH_DATE_VARIANCE("FinishDateVariance"),
    @XmlEnumValue("FloatPath")
    FLOAT_PATH("FloatPath"),
    @XmlEnumValue("FloatPathOrder")
    FLOAT_PATH_ORDER("FloatPathOrder"),
    @XmlEnumValue("FreeFloat")
    FREE_FLOAT("FreeFloat"),
    GUID("GUID"),
    @XmlEnumValue("HasFutureBucketData")
    HAS_FUTURE_BUCKET_DATA("HasFutureBucketData"),
    @XmlEnumValue("Id")
    ID("Id"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsCritical")
    IS_CRITICAL("IsCritical"),
    @XmlEnumValue("IsLongestPath")
    IS_LONGEST_PATH("IsLongestPath"),
    @XmlEnumValue("IsNewFeedback")
    IS_NEW_FEEDBACK("IsNewFeedback"),
    @XmlEnumValue("IsStarred")
    IS_STARRED("IsStarred"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("IsWorkPackage")
    IS_WORK_PACKAGE("IsWorkPackage"),
    @XmlEnumValue("LaborCost1Variance")
    LABOR_COST_1_VARIANCE("LaborCost1Variance"),
    @XmlEnumValue("LaborCostPercentComplete")
    LABOR_COST_PERCENT_COMPLETE("LaborCostPercentComplete"),
    @XmlEnumValue("LaborCostVariance")
    LABOR_COST_VARIANCE("LaborCostVariance"),
    @XmlEnumValue("LaborUnits1Variance")
    LABOR_UNITS_1_VARIANCE("LaborUnits1Variance"),
    @XmlEnumValue("LaborUnitsPercentComplete")
    LABOR_UNITS_PERCENT_COMPLETE("LaborUnitsPercentComplete"),
    @XmlEnumValue("LaborUnitsVariance")
    LABOR_UNITS_VARIANCE("LaborUnitsVariance"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("LateFinishDate")
    LATE_FINISH_DATE("LateFinishDate"),
    @XmlEnumValue("LateStartDate")
    LATE_START_DATE("LateStartDate"),
    @XmlEnumValue("LevelingPriority")
    LEVELING_PRIORITY("LevelingPriority"),
    @XmlEnumValue("LocationName")
    LOCATION_NAME("LocationName"),
    @XmlEnumValue("LocationObjectId")
    LOCATION_OBJECT_ID("LocationObjectId"),
    @XmlEnumValue("MaterialCost1Variance")
    MATERIAL_COST_1_VARIANCE("MaterialCost1Variance"),
    @XmlEnumValue("MaterialCostPercentComplete")
    MATERIAL_COST_PERCENT_COMPLETE("MaterialCostPercentComplete"),
    @XmlEnumValue("MaterialCostVariance")
    MATERIAL_COST_VARIANCE("MaterialCostVariance"),
    @XmlEnumValue("MaximumDuration")
    MAXIMUM_DURATION("MaximumDuration"),
    @XmlEnumValue("MinimumDuration")
    MINIMUM_DURATION("MinimumDuration"),
    @XmlEnumValue("MostLikelyDuration")
    MOST_LIKELY_DURATION("MostLikelyDuration"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("NonLaborCost1Variance")
    NON_LABOR_COST_1_VARIANCE("NonLaborCost1Variance"),
    @XmlEnumValue("NonLaborCostPercentComplete")
    NON_LABOR_COST_PERCENT_COMPLETE("NonLaborCostPercentComplete"),
    @XmlEnumValue("NonLaborCostVariance")
    NON_LABOR_COST_VARIANCE("NonLaborCostVariance"),
    @XmlEnumValue("NonLaborUnits1Variance")
    NON_LABOR_UNITS_1_VARIANCE("NonLaborUnits1Variance"),
    @XmlEnumValue("NonLaborUnitsPercentComplete")
    NON_LABOR_UNITS_PERCENT_COMPLETE("NonLaborUnitsPercentComplete"),
    @XmlEnumValue("NonLaborUnitsVariance")
    NON_LABOR_UNITS_VARIANCE("NonLaborUnitsVariance"),
    @XmlEnumValue("NotesToResources")
    NOTES_TO_RESOURCES("NotesToResources"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("OwnerIDArray")
    OWNER_ID_ARRAY("OwnerIDArray"),
    @XmlEnumValue("OwnerNamesArray")
    OWNER_NAMES_ARRAY("OwnerNamesArray"),
    @XmlEnumValue("PercentComplete")
    PERCENT_COMPLETE("PercentComplete"),
    @XmlEnumValue("PercentCompleteType")
    PERCENT_COMPLETE_TYPE("PercentCompleteType"),
    @XmlEnumValue("PerformancePercentComplete")
    PERFORMANCE_PERCENT_COMPLETE("PerformancePercentComplete"),
    @XmlEnumValue("PhysicalPercentComplete")
    PHYSICAL_PERCENT_COMPLETE("PhysicalPercentComplete"),
    @XmlEnumValue("PlannedDuration")
    PLANNED_DURATION("PlannedDuration"),
    @XmlEnumValue("PlannedExpenseCost")
    PLANNED_EXPENSE_COST("PlannedExpenseCost"),
    @XmlEnumValue("PlannedFinishDate")
    PLANNED_FINISH_DATE("PlannedFinishDate"),
    @XmlEnumValue("PlannedLaborCost")
    PLANNED_LABOR_COST("PlannedLaborCost"),
    @XmlEnumValue("PlannedLaborUnits")
    PLANNED_LABOR_UNITS("PlannedLaborUnits"),
    @XmlEnumValue("PlannedMaterialCost")
    PLANNED_MATERIAL_COST("PlannedMaterialCost"),
    @XmlEnumValue("PlannedNonLaborCost")
    PLANNED_NON_LABOR_COST("PlannedNonLaborCost"),
    @XmlEnumValue("PlannedNonLaborUnits")
    PLANNED_NON_LABOR_UNITS("PlannedNonLaborUnits"),
    @XmlEnumValue("PlannedStartDate")
    PLANNED_START_DATE("PlannedStartDate"),
    @XmlEnumValue("PlannedTotalCost")
    PLANNED_TOTAL_COST("PlannedTotalCost"),
    @XmlEnumValue("PlannedTotalUnits")
    PLANNED_TOTAL_UNITS("PlannedTotalUnits"),
    @XmlEnumValue("PlannedValueCost")
    PLANNED_VALUE_COST("PlannedValueCost"),
    @XmlEnumValue("PlannedValueLaborUnits")
    PLANNED_VALUE_LABOR_UNITS("PlannedValueLaborUnits"),
    @XmlEnumValue("PostResponsePessimisticFinish")
    POST_RESPONSE_PESSIMISTIC_FINISH("PostResponsePessimisticFinish"),
    @XmlEnumValue("PostResponsePessimisticStart")
    POST_RESPONSE_PESSIMISTIC_START("PostResponsePessimisticStart"),
    @XmlEnumValue("PreResponsePessimisticFinish")
    PRE_RESPONSE_PESSIMISTIC_FINISH("PreResponsePessimisticFinish"),
    @XmlEnumValue("PreResponsePessimisticStart")
    PRE_RESPONSE_PESSIMISTIC_START("PreResponsePessimisticStart"),
    @XmlEnumValue("PrimaryConstraintDate")
    PRIMARY_CONSTRAINT_DATE("PrimaryConstraintDate"),
    @XmlEnumValue("PrimaryConstraintType")
    PRIMARY_CONSTRAINT_TYPE("PrimaryConstraintType"),
    @XmlEnumValue("PrimaryResourceId")
    PRIMARY_RESOURCE_ID("PrimaryResourceId"),
    @XmlEnumValue("PrimaryResourceName")
    PRIMARY_RESOURCE_NAME("PrimaryResourceName"),
    @XmlEnumValue("PrimaryResourceObjectId")
    PRIMARY_RESOURCE_OBJECT_ID("PrimaryResourceObjectId"),
    @XmlEnumValue("ProjectFlag")
    PROJECT_FLAG("ProjectFlag"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectName")
    PROJECT_NAME("ProjectName"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ProjectProjectFlag")
    PROJECT_PROJECT_FLAG("ProjectProjectFlag"),
    @XmlEnumValue("RemainingDuration")
    REMAINING_DURATION("RemainingDuration"),
    @XmlEnumValue("RemainingEarlyFinishDate")
    REMAINING_EARLY_FINISH_DATE("RemainingEarlyFinishDate"),
    @XmlEnumValue("RemainingEarlyStartDate")
    REMAINING_EARLY_START_DATE("RemainingEarlyStartDate"),
    @XmlEnumValue("RemainingExpenseCost")
    REMAINING_EXPENSE_COST("RemainingExpenseCost"),
    @XmlEnumValue("RemainingFloat")
    REMAINING_FLOAT("RemainingFloat"),
    @XmlEnumValue("RemainingLaborCost")
    REMAINING_LABOR_COST("RemainingLaborCost"),
    @XmlEnumValue("RemainingLaborUnits")
    REMAINING_LABOR_UNITS("RemainingLaborUnits"),
    @XmlEnumValue("RemainingLateFinishDate")
    REMAINING_LATE_FINISH_DATE("RemainingLateFinishDate"),
    @XmlEnumValue("RemainingLateStartDate")
    REMAINING_LATE_START_DATE("RemainingLateStartDate"),
    @XmlEnumValue("RemainingMaterialCost")
    REMAINING_MATERIAL_COST("RemainingMaterialCost"),
    @XmlEnumValue("RemainingNonLaborCost")
    REMAINING_NON_LABOR_COST("RemainingNonLaborCost"),
    @XmlEnumValue("RemainingNonLaborUnits")
    REMAINING_NON_LABOR_UNITS("RemainingNonLaborUnits"),
    @XmlEnumValue("RemainingTotalCost")
    REMAINING_TOTAL_COST("RemainingTotalCost"),
    @XmlEnumValue("RemainingTotalUnits")
    REMAINING_TOTAL_UNITS("RemainingTotalUnits"),
    @XmlEnumValue("ResumeDate")
    RESUME_DATE("ResumeDate"),
    @XmlEnumValue("ReviewFinishDate")
    REVIEW_FINISH_DATE("ReviewFinishDate"),
    @XmlEnumValue("ReviewRequired")
    REVIEW_REQUIRED("ReviewRequired"),
    @XmlEnumValue("ReviewStatus")
    REVIEW_STATUS("ReviewStatus"),
    @XmlEnumValue("SchedulePercentComplete")
    SCHEDULE_PERCENT_COMPLETE("SchedulePercentComplete"),
    @XmlEnumValue("SchedulePerformanceIndex")
    SCHEDULE_PERFORMANCE_INDEX("SchedulePerformanceIndex"),
    @XmlEnumValue("SchedulePerformanceIndexLaborUnits")
    SCHEDULE_PERFORMANCE_INDEX_LABOR_UNITS("SchedulePerformanceIndexLaborUnits"),
    @XmlEnumValue("ScheduleVariance")
    SCHEDULE_VARIANCE("ScheduleVariance"),
    @XmlEnumValue("ScheduleVarianceIndex")
    SCHEDULE_VARIANCE_INDEX("ScheduleVarianceIndex"),
    @XmlEnumValue("ScheduleVarianceIndexLaborUnits")
    SCHEDULE_VARIANCE_INDEX_LABOR_UNITS("ScheduleVarianceIndexLaborUnits"),
    @XmlEnumValue("ScheduleVarianceLaborUnits")
    SCHEDULE_VARIANCE_LABOR_UNITS("ScheduleVarianceLaborUnits"),
    @XmlEnumValue("SecondaryConstraintDate")
    SECONDARY_CONSTRAINT_DATE("SecondaryConstraintDate"),
    @XmlEnumValue("SecondaryConstraintType")
    SECONDARY_CONSTRAINT_TYPE("SecondaryConstraintType"),
    @XmlEnumValue("StartDate")
    START_DATE("StartDate"),
    @XmlEnumValue("StartDate1Variance")
    START_DATE_1_VARIANCE("StartDate1Variance"),
    @XmlEnumValue("StartDateVariance")
    START_DATE_VARIANCE("StartDateVariance"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("StatusCode")
    STATUS_CODE("StatusCode"),
    @XmlEnumValue("SuspendDate")
    SUSPEND_DATE("SuspendDate"),
    @XmlEnumValue("ToCompletePerformanceIndex")
    TO_COMPLETE_PERFORMANCE_INDEX("ToCompletePerformanceIndex"),
    @XmlEnumValue("TotalCost1Variance")
    TOTAL_COST_1_VARIANCE("TotalCost1Variance"),
    @XmlEnumValue("TotalCostVariance")
    TOTAL_COST_VARIANCE("TotalCostVariance"),
    @XmlEnumValue("TotalFloat")
    TOTAL_FLOAT("TotalFloat"),
    @XmlEnumValue("Type")
    TYPE("Type"),
    @XmlEnumValue("UnitsPercentComplete")
    UNITS_PERCENT_COMPLETE("UnitsPercentComplete"),
    @XmlEnumValue("UnreadCommentCount")
    UNREAD_COMMENT_COUNT("UnreadCommentCount"),
    @XmlEnumValue("WBSCode")
    WBS_CODE("WBSCode"),
    @XmlEnumValue("WBSName")
    WBS_NAME("WBSName"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId"),
    @XmlEnumValue("WBSPath")
    WBS_PATH("WBSPath");
    private final String value;

    ActivityFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActivityFieldType fromValue(String v) {
        for (ActivityFieldType c: ActivityFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
