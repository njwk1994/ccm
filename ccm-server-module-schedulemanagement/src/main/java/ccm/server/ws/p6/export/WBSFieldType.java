
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WBSFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="WBSFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AnticipatedFinishDate"/&gt;
 *     &lt;enumeration value="AnticipatedStartDate"/&gt;
 *     &lt;enumeration value="Code"/&gt;
 *     &lt;enumeration value="ContainsSummaryData"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="CurrentBudget"/&gt;
 *     &lt;enumeration value="CurrentVariance"/&gt;
 *     &lt;enumeration value="DistributedCurrentBudget"/&gt;
 *     &lt;enumeration value="EarnedValueComputeType"/&gt;
 *     &lt;enumeration value="EarnedValueETCComputeType"/&gt;
 *     &lt;enumeration value="EarnedValueETCUserValue"/&gt;
 *     &lt;enumeration value="EarnedValueUserPercent"/&gt;
 *     &lt;enumeration value="FinishDate"/&gt;
 *     &lt;enumeration value="ForecastFinishDate"/&gt;
 *     &lt;enumeration value="ForecastStartDate"/&gt;
 *     &lt;enumeration value="GUID"/&gt;
 *     &lt;enumeration value="IndependentETCLaborUnits"/&gt;
 *     &lt;enumeration value="IndependentETCTotalCost"/&gt;
 *     &lt;enumeration value="IntegratedType"/&gt;
 *     &lt;enumeration value="IntegratedWBS"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="IsWorkPackage"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="OBSName"/&gt;
 *     &lt;enumeration value="OBSObjectId"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="OriginalBudget"/&gt;
 *     &lt;enumeration value="ParentObjectId"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ProposedBudget"/&gt;
 *     &lt;enumeration value="RolledUpFinishDate"/&gt;
 *     &lt;enumeration value="RolledUpStartDate"/&gt;
 *     &lt;enumeration value="SequenceNumber"/&gt;
 *     &lt;enumeration value="StartDate"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *     &lt;enumeration value="StatusReviewerName"/&gt;
 *     &lt;enumeration value="StatusReviewerObjectId"/&gt;
 *     &lt;enumeration value="SummaryAccountingVarianceByCost"/&gt;
 *     &lt;enumeration value="SummaryAccountingVarianceByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActivityCount"/&gt;
 *     &lt;enumeration value="SummaryActualDuration"/&gt;
 *     &lt;enumeration value="SummaryActualExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryActualFinishDate"/&gt;
 *     &lt;enumeration value="SummaryActualLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryActualNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualStartDate"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualTotalCost"/&gt;
 *     &lt;enumeration value="SummaryActualValueByCost"/&gt;
 *     &lt;enumeration value="SummaryActualValueByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionDuration"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionLaborCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionTotalCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionTotalCostVariance"/&gt;
 *     &lt;enumeration value="SummaryBaselineCompletedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryBaselineDuration"/&gt;
 *     &lt;enumeration value="SummaryBaselineExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineFinishDate"/&gt;
 *     &lt;enumeration value="SummaryBaselineInProgressActivityCount"/&gt;
 *     &lt;enumeration value="SummaryBaselineLaborCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryBaselineMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryBaselineNotStartedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryBaselineStartDate"/&gt;
 *     &lt;enumeration value="SummaryBaselineTotalCost"/&gt;
 *     &lt;enumeration value="SummaryBudgetAtCompletionByCost"/&gt;
 *     &lt;enumeration value="SummaryBudgetAtCompletionByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryCompletedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryCostPercentOfPlanned"/&gt;
 *     &lt;enumeration value="SummaryCostPerformanceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryCostPerformanceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceByCost"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceIndex"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryDurationPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryDurationPercentOfPlanned"/&gt;
 *     &lt;enumeration value="SummaryDurationVariance"/&gt;
 *     &lt;enumeration value="SummaryEarnedValueByCost"/&gt;
 *     &lt;enumeration value="SummaryEarnedValueByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionByCost"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionHighPercentByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionLowPercentByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateToCompleteByCost"/&gt;
 *     &lt;enumeration value="SummaryEstimateToCompleteByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryExpenseCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryExpenseCostVariance"/&gt;
 *     &lt;enumeration value="SummaryFinishDateVariance"/&gt;
 *     &lt;enumeration value="SummaryInProgressActivityCount"/&gt;
 *     &lt;enumeration value="SummaryLaborCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryLaborCostVariance"/&gt;
 *     &lt;enumeration value="SummaryLaborUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryLaborUnitsVariance"/&gt;
 *     &lt;enumeration value="SummaryMaterialCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryMaterialCostVariance"/&gt;
 *     &lt;enumeration value="SummaryNonLaborCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryNonLaborCostVariance"/&gt;
 *     &lt;enumeration value="SummaryNonLaborUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryNonLaborUnitsVariance"/&gt;
 *     &lt;enumeration value="SummaryNotStartedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryPerformancePercentCompleteByCost"/&gt;
 *     &lt;enumeration value="SummaryPerformancePercentCompleteByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryPlannedCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedDuration"/&gt;
 *     &lt;enumeration value="SummaryPlannedExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedFinishDate"/&gt;
 *     &lt;enumeration value="SummaryPlannedLaborCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryPlannedMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryPlannedStartDate"/&gt;
 *     &lt;enumeration value="SummaryPlannedValueByCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedValueByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryProgressFinishDate"/&gt;
 *     &lt;enumeration value="SummaryRemainingDuration"/&gt;
 *     &lt;enumeration value="SummaryRemainingExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingFinishDate"/&gt;
 *     &lt;enumeration value="SummaryRemainingLaborCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryRemainingMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryRemainingStartDate"/&gt;
 *     &lt;enumeration value="SummaryRemainingTotalCost"/&gt;
 *     &lt;enumeration value="SummarySchedulePercentComplete"/&gt;
 *     &lt;enumeration value="SummarySchedulePercentCompleteByLaborUnits"/&gt;
 *     &lt;enumeration value="SummarySchedulePerformanceIndexByCost"/&gt;
 *     &lt;enumeration value="SummarySchedulePerformanceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceByCost"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceIndex"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryStartDateVariance"/&gt;
 *     &lt;enumeration value="SummaryToCompletePerformanceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryTotalCostVariance"/&gt;
 *     &lt;enumeration value="SummaryTotalFloat"/&gt;
 *     &lt;enumeration value="SummaryUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryVarianceAtCompletionByLaborUnits"/&gt;
 *     &lt;enumeration value="TotalBenefitPlan"/&gt;
 *     &lt;enumeration value="TotalBenefitPlanTally"/&gt;
 *     &lt;enumeration value="TotalSpendingPlan"/&gt;
 *     &lt;enumeration value="TotalSpendingPlanTally"/&gt;
 *     &lt;enumeration value="UnallocatedBudget"/&gt;
 *     &lt;enumeration value="UndistributedCurrentVariance"/&gt;
 *     &lt;enumeration value="WBSCategoryObjectId"/&gt;
 *     &lt;enumeration value="WBSMilestonePercentComplete"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "WBSFieldType")
@XmlEnum
public enum WBSFieldType {

    @XmlEnumValue("AnticipatedFinishDate")
    ANTICIPATED_FINISH_DATE("AnticipatedFinishDate"),
    @XmlEnumValue("AnticipatedStartDate")
    ANTICIPATED_START_DATE("AnticipatedStartDate"),
    @XmlEnumValue("Code")
    CODE("Code"),
    @XmlEnumValue("ContainsSummaryData")
    CONTAINS_SUMMARY_DATA("ContainsSummaryData"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("CurrentBudget")
    CURRENT_BUDGET("CurrentBudget"),
    @XmlEnumValue("CurrentVariance")
    CURRENT_VARIANCE("CurrentVariance"),
    @XmlEnumValue("DistributedCurrentBudget")
    DISTRIBUTED_CURRENT_BUDGET("DistributedCurrentBudget"),
    @XmlEnumValue("EarnedValueComputeType")
    EARNED_VALUE_COMPUTE_TYPE("EarnedValueComputeType"),
    @XmlEnumValue("EarnedValueETCComputeType")
    EARNED_VALUE_ETC_COMPUTE_TYPE("EarnedValueETCComputeType"),
    @XmlEnumValue("EarnedValueETCUserValue")
    EARNED_VALUE_ETC_USER_VALUE("EarnedValueETCUserValue"),
    @XmlEnumValue("EarnedValueUserPercent")
    EARNED_VALUE_USER_PERCENT("EarnedValueUserPercent"),
    @XmlEnumValue("FinishDate")
    FINISH_DATE("FinishDate"),
    @XmlEnumValue("ForecastFinishDate")
    FORECAST_FINISH_DATE("ForecastFinishDate"),
    @XmlEnumValue("ForecastStartDate")
    FORECAST_START_DATE("ForecastStartDate"),
    GUID("GUID"),
    @XmlEnumValue("IndependentETCLaborUnits")
    INDEPENDENT_ETC_LABOR_UNITS("IndependentETCLaborUnits"),
    @XmlEnumValue("IndependentETCTotalCost")
    INDEPENDENT_ETC_TOTAL_COST("IndependentETCTotalCost"),
    @XmlEnumValue("IntegratedType")
    INTEGRATED_TYPE("IntegratedType"),
    @XmlEnumValue("IntegratedWBS")
    INTEGRATED_WBS("IntegratedWBS"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("IsWorkPackage")
    IS_WORK_PACKAGE("IsWorkPackage"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("OBSName")
    OBS_NAME("OBSName"),
    @XmlEnumValue("OBSObjectId")
    OBS_OBJECT_ID("OBSObjectId"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("OriginalBudget")
    ORIGINAL_BUDGET("OriginalBudget"),
    @XmlEnumValue("ParentObjectId")
    PARENT_OBJECT_ID("ParentObjectId"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ProposedBudget")
    PROPOSED_BUDGET("ProposedBudget"),
    @XmlEnumValue("RolledUpFinishDate")
    ROLLED_UP_FINISH_DATE("RolledUpFinishDate"),
    @XmlEnumValue("RolledUpStartDate")
    ROLLED_UP_START_DATE("RolledUpStartDate"),
    @XmlEnumValue("SequenceNumber")
    SEQUENCE_NUMBER("SequenceNumber"),
    @XmlEnumValue("StartDate")
    START_DATE("StartDate"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("StatusReviewerName")
    STATUS_REVIEWER_NAME("StatusReviewerName"),
    @XmlEnumValue("StatusReviewerObjectId")
    STATUS_REVIEWER_OBJECT_ID("StatusReviewerObjectId"),
    @XmlEnumValue("SummaryAccountingVarianceByCost")
    SUMMARY_ACCOUNTING_VARIANCE_BY_COST("SummaryAccountingVarianceByCost"),
    @XmlEnumValue("SummaryAccountingVarianceByLaborUnits")
    SUMMARY_ACCOUNTING_VARIANCE_BY_LABOR_UNITS("SummaryAccountingVarianceByLaborUnits"),
    @XmlEnumValue("SummaryActivityCount")
    SUMMARY_ACTIVITY_COUNT("SummaryActivityCount"),
    @XmlEnumValue("SummaryActualDuration")
    SUMMARY_ACTUAL_DURATION("SummaryActualDuration"),
    @XmlEnumValue("SummaryActualExpenseCost")
    SUMMARY_ACTUAL_EXPENSE_COST("SummaryActualExpenseCost"),
    @XmlEnumValue("SummaryActualFinishDate")
    SUMMARY_ACTUAL_FINISH_DATE("SummaryActualFinishDate"),
    @XmlEnumValue("SummaryActualLaborCost")
    SUMMARY_ACTUAL_LABOR_COST("SummaryActualLaborCost"),
    @XmlEnumValue("SummaryActualLaborUnits")
    SUMMARY_ACTUAL_LABOR_UNITS("SummaryActualLaborUnits"),
    @XmlEnumValue("SummaryActualMaterialCost")
    SUMMARY_ACTUAL_MATERIAL_COST("SummaryActualMaterialCost"),
    @XmlEnumValue("SummaryActualNonLaborCost")
    SUMMARY_ACTUAL_NON_LABOR_COST("SummaryActualNonLaborCost"),
    @XmlEnumValue("SummaryActualNonLaborUnits")
    SUMMARY_ACTUAL_NON_LABOR_UNITS("SummaryActualNonLaborUnits"),
    @XmlEnumValue("SummaryActualStartDate")
    SUMMARY_ACTUAL_START_DATE("SummaryActualStartDate"),
    @XmlEnumValue("SummaryActualThisPeriodCost")
    SUMMARY_ACTUAL_THIS_PERIOD_COST("SummaryActualThisPeriodCost"),
    @XmlEnumValue("SummaryActualThisPeriodLaborCost")
    SUMMARY_ACTUAL_THIS_PERIOD_LABOR_COST("SummaryActualThisPeriodLaborCost"),
    @XmlEnumValue("SummaryActualThisPeriodLaborUnits")
    SUMMARY_ACTUAL_THIS_PERIOD_LABOR_UNITS("SummaryActualThisPeriodLaborUnits"),
    @XmlEnumValue("SummaryActualThisPeriodMaterialCost")
    SUMMARY_ACTUAL_THIS_PERIOD_MATERIAL_COST("SummaryActualThisPeriodMaterialCost"),
    @XmlEnumValue("SummaryActualThisPeriodNonLaborCost")
    SUMMARY_ACTUAL_THIS_PERIOD_NON_LABOR_COST("SummaryActualThisPeriodNonLaborCost"),
    @XmlEnumValue("SummaryActualThisPeriodNonLaborUnits")
    SUMMARY_ACTUAL_THIS_PERIOD_NON_LABOR_UNITS("SummaryActualThisPeriodNonLaborUnits"),
    @XmlEnumValue("SummaryActualTotalCost")
    SUMMARY_ACTUAL_TOTAL_COST("SummaryActualTotalCost"),
    @XmlEnumValue("SummaryActualValueByCost")
    SUMMARY_ACTUAL_VALUE_BY_COST("SummaryActualValueByCost"),
    @XmlEnumValue("SummaryActualValueByLaborUnits")
    SUMMARY_ACTUAL_VALUE_BY_LABOR_UNITS("SummaryActualValueByLaborUnits"),
    @XmlEnumValue("SummaryAtCompletionDuration")
    SUMMARY_AT_COMPLETION_DURATION("SummaryAtCompletionDuration"),
    @XmlEnumValue("SummaryAtCompletionExpenseCost")
    SUMMARY_AT_COMPLETION_EXPENSE_COST("SummaryAtCompletionExpenseCost"),
    @XmlEnumValue("SummaryAtCompletionLaborCost")
    SUMMARY_AT_COMPLETION_LABOR_COST("SummaryAtCompletionLaborCost"),
    @XmlEnumValue("SummaryAtCompletionLaborUnits")
    SUMMARY_AT_COMPLETION_LABOR_UNITS("SummaryAtCompletionLaborUnits"),
    @XmlEnumValue("SummaryAtCompletionMaterialCost")
    SUMMARY_AT_COMPLETION_MATERIAL_COST("SummaryAtCompletionMaterialCost"),
    @XmlEnumValue("SummaryAtCompletionNonLaborCost")
    SUMMARY_AT_COMPLETION_NON_LABOR_COST("SummaryAtCompletionNonLaborCost"),
    @XmlEnumValue("SummaryAtCompletionNonLaborUnits")
    SUMMARY_AT_COMPLETION_NON_LABOR_UNITS("SummaryAtCompletionNonLaborUnits"),
    @XmlEnumValue("SummaryAtCompletionTotalCost")
    SUMMARY_AT_COMPLETION_TOTAL_COST("SummaryAtCompletionTotalCost"),
    @XmlEnumValue("SummaryAtCompletionTotalCostVariance")
    SUMMARY_AT_COMPLETION_TOTAL_COST_VARIANCE("SummaryAtCompletionTotalCostVariance"),
    @XmlEnumValue("SummaryBaselineCompletedActivityCount")
    SUMMARY_BASELINE_COMPLETED_ACTIVITY_COUNT("SummaryBaselineCompletedActivityCount"),
    @XmlEnumValue("SummaryBaselineDuration")
    SUMMARY_BASELINE_DURATION("SummaryBaselineDuration"),
    @XmlEnumValue("SummaryBaselineExpenseCost")
    SUMMARY_BASELINE_EXPENSE_COST("SummaryBaselineExpenseCost"),
    @XmlEnumValue("SummaryBaselineFinishDate")
    SUMMARY_BASELINE_FINISH_DATE("SummaryBaselineFinishDate"),
    @XmlEnumValue("SummaryBaselineInProgressActivityCount")
    SUMMARY_BASELINE_IN_PROGRESS_ACTIVITY_COUNT("SummaryBaselineInProgressActivityCount"),
    @XmlEnumValue("SummaryBaselineLaborCost")
    SUMMARY_BASELINE_LABOR_COST("SummaryBaselineLaborCost"),
    @XmlEnumValue("SummaryBaselineLaborUnits")
    SUMMARY_BASELINE_LABOR_UNITS("SummaryBaselineLaborUnits"),
    @XmlEnumValue("SummaryBaselineMaterialCost")
    SUMMARY_BASELINE_MATERIAL_COST("SummaryBaselineMaterialCost"),
    @XmlEnumValue("SummaryBaselineNonLaborCost")
    SUMMARY_BASELINE_NON_LABOR_COST("SummaryBaselineNonLaborCost"),
    @XmlEnumValue("SummaryBaselineNonLaborUnits")
    SUMMARY_BASELINE_NON_LABOR_UNITS("SummaryBaselineNonLaborUnits"),
    @XmlEnumValue("SummaryBaselineNotStartedActivityCount")
    SUMMARY_BASELINE_NOT_STARTED_ACTIVITY_COUNT("SummaryBaselineNotStartedActivityCount"),
    @XmlEnumValue("SummaryBaselineStartDate")
    SUMMARY_BASELINE_START_DATE("SummaryBaselineStartDate"),
    @XmlEnumValue("SummaryBaselineTotalCost")
    SUMMARY_BASELINE_TOTAL_COST("SummaryBaselineTotalCost"),
    @XmlEnumValue("SummaryBudgetAtCompletionByCost")
    SUMMARY_BUDGET_AT_COMPLETION_BY_COST("SummaryBudgetAtCompletionByCost"),
    @XmlEnumValue("SummaryBudgetAtCompletionByLaborUnits")
    SUMMARY_BUDGET_AT_COMPLETION_BY_LABOR_UNITS("SummaryBudgetAtCompletionByLaborUnits"),
    @XmlEnumValue("SummaryCompletedActivityCount")
    SUMMARY_COMPLETED_ACTIVITY_COUNT("SummaryCompletedActivityCount"),
    @XmlEnumValue("SummaryCostPercentComplete")
    SUMMARY_COST_PERCENT_COMPLETE("SummaryCostPercentComplete"),
    @XmlEnumValue("SummaryCostPercentOfPlanned")
    SUMMARY_COST_PERCENT_OF_PLANNED("SummaryCostPercentOfPlanned"),
    @XmlEnumValue("SummaryCostPerformanceIndexByCost")
    SUMMARY_COST_PERFORMANCE_INDEX_BY_COST("SummaryCostPerformanceIndexByCost"),
    @XmlEnumValue("SummaryCostPerformanceIndexByLaborUnits")
    SUMMARY_COST_PERFORMANCE_INDEX_BY_LABOR_UNITS("SummaryCostPerformanceIndexByLaborUnits"),
    @XmlEnumValue("SummaryCostVarianceByCost")
    SUMMARY_COST_VARIANCE_BY_COST("SummaryCostVarianceByCost"),
    @XmlEnumValue("SummaryCostVarianceByLaborUnits")
    SUMMARY_COST_VARIANCE_BY_LABOR_UNITS("SummaryCostVarianceByLaborUnits"),
    @XmlEnumValue("SummaryCostVarianceIndex")
    SUMMARY_COST_VARIANCE_INDEX("SummaryCostVarianceIndex"),
    @XmlEnumValue("SummaryCostVarianceIndexByCost")
    SUMMARY_COST_VARIANCE_INDEX_BY_COST("SummaryCostVarianceIndexByCost"),
    @XmlEnumValue("SummaryCostVarianceIndexByLaborUnits")
    SUMMARY_COST_VARIANCE_INDEX_BY_LABOR_UNITS("SummaryCostVarianceIndexByLaborUnits"),
    @XmlEnumValue("SummaryDurationPercentComplete")
    SUMMARY_DURATION_PERCENT_COMPLETE("SummaryDurationPercentComplete"),
    @XmlEnumValue("SummaryDurationPercentOfPlanned")
    SUMMARY_DURATION_PERCENT_OF_PLANNED("SummaryDurationPercentOfPlanned"),
    @XmlEnumValue("SummaryDurationVariance")
    SUMMARY_DURATION_VARIANCE("SummaryDurationVariance"),
    @XmlEnumValue("SummaryEarnedValueByCost")
    SUMMARY_EARNED_VALUE_BY_COST("SummaryEarnedValueByCost"),
    @XmlEnumValue("SummaryEarnedValueByLaborUnits")
    SUMMARY_EARNED_VALUE_BY_LABOR_UNITS("SummaryEarnedValueByLaborUnits"),
    @XmlEnumValue("SummaryEstimateAtCompletionByCost")
    SUMMARY_ESTIMATE_AT_COMPLETION_BY_COST("SummaryEstimateAtCompletionByCost"),
    @XmlEnumValue("SummaryEstimateAtCompletionByLaborUnits")
    SUMMARY_ESTIMATE_AT_COMPLETION_BY_LABOR_UNITS("SummaryEstimateAtCompletionByLaborUnits"),
    @XmlEnumValue("SummaryEstimateAtCompletionHighPercentByLaborUnits")
    SUMMARY_ESTIMATE_AT_COMPLETION_HIGH_PERCENT_BY_LABOR_UNITS("SummaryEstimateAtCompletionHighPercentByLaborUnits"),
    @XmlEnumValue("SummaryEstimateAtCompletionLowPercentByLaborUnits")
    SUMMARY_ESTIMATE_AT_COMPLETION_LOW_PERCENT_BY_LABOR_UNITS("SummaryEstimateAtCompletionLowPercentByLaborUnits"),
    @XmlEnumValue("SummaryEstimateToCompleteByCost")
    SUMMARY_ESTIMATE_TO_COMPLETE_BY_COST("SummaryEstimateToCompleteByCost"),
    @XmlEnumValue("SummaryEstimateToCompleteByLaborUnits")
    SUMMARY_ESTIMATE_TO_COMPLETE_BY_LABOR_UNITS("SummaryEstimateToCompleteByLaborUnits"),
    @XmlEnumValue("SummaryExpenseCostPercentComplete")
    SUMMARY_EXPENSE_COST_PERCENT_COMPLETE("SummaryExpenseCostPercentComplete"),
    @XmlEnumValue("SummaryExpenseCostVariance")
    SUMMARY_EXPENSE_COST_VARIANCE("SummaryExpenseCostVariance"),
    @XmlEnumValue("SummaryFinishDateVariance")
    SUMMARY_FINISH_DATE_VARIANCE("SummaryFinishDateVariance"),
    @XmlEnumValue("SummaryInProgressActivityCount")
    SUMMARY_IN_PROGRESS_ACTIVITY_COUNT("SummaryInProgressActivityCount"),
    @XmlEnumValue("SummaryLaborCostPercentComplete")
    SUMMARY_LABOR_COST_PERCENT_COMPLETE("SummaryLaborCostPercentComplete"),
    @XmlEnumValue("SummaryLaborCostVariance")
    SUMMARY_LABOR_COST_VARIANCE("SummaryLaborCostVariance"),
    @XmlEnumValue("SummaryLaborUnitsPercentComplete")
    SUMMARY_LABOR_UNITS_PERCENT_COMPLETE("SummaryLaborUnitsPercentComplete"),
    @XmlEnumValue("SummaryLaborUnitsVariance")
    SUMMARY_LABOR_UNITS_VARIANCE("SummaryLaborUnitsVariance"),
    @XmlEnumValue("SummaryMaterialCostPercentComplete")
    SUMMARY_MATERIAL_COST_PERCENT_COMPLETE("SummaryMaterialCostPercentComplete"),
    @XmlEnumValue("SummaryMaterialCostVariance")
    SUMMARY_MATERIAL_COST_VARIANCE("SummaryMaterialCostVariance"),
    @XmlEnumValue("SummaryNonLaborCostPercentComplete")
    SUMMARY_NON_LABOR_COST_PERCENT_COMPLETE("SummaryNonLaborCostPercentComplete"),
    @XmlEnumValue("SummaryNonLaborCostVariance")
    SUMMARY_NON_LABOR_COST_VARIANCE("SummaryNonLaborCostVariance"),
    @XmlEnumValue("SummaryNonLaborUnitsPercentComplete")
    SUMMARY_NON_LABOR_UNITS_PERCENT_COMPLETE("SummaryNonLaborUnitsPercentComplete"),
    @XmlEnumValue("SummaryNonLaborUnitsVariance")
    SUMMARY_NON_LABOR_UNITS_VARIANCE("SummaryNonLaborUnitsVariance"),
    @XmlEnumValue("SummaryNotStartedActivityCount")
    SUMMARY_NOT_STARTED_ACTIVITY_COUNT("SummaryNotStartedActivityCount"),
    @XmlEnumValue("SummaryPerformancePercentCompleteByCost")
    SUMMARY_PERFORMANCE_PERCENT_COMPLETE_BY_COST("SummaryPerformancePercentCompleteByCost"),
    @XmlEnumValue("SummaryPerformancePercentCompleteByLaborUnits")
    SUMMARY_PERFORMANCE_PERCENT_COMPLETE_BY_LABOR_UNITS("SummaryPerformancePercentCompleteByLaborUnits"),
    @XmlEnumValue("SummaryPlannedCost")
    SUMMARY_PLANNED_COST("SummaryPlannedCost"),
    @XmlEnumValue("SummaryPlannedDuration")
    SUMMARY_PLANNED_DURATION("SummaryPlannedDuration"),
    @XmlEnumValue("SummaryPlannedExpenseCost")
    SUMMARY_PLANNED_EXPENSE_COST("SummaryPlannedExpenseCost"),
    @XmlEnumValue("SummaryPlannedFinishDate")
    SUMMARY_PLANNED_FINISH_DATE("SummaryPlannedFinishDate"),
    @XmlEnumValue("SummaryPlannedLaborCost")
    SUMMARY_PLANNED_LABOR_COST("SummaryPlannedLaborCost"),
    @XmlEnumValue("SummaryPlannedLaborUnits")
    SUMMARY_PLANNED_LABOR_UNITS("SummaryPlannedLaborUnits"),
    @XmlEnumValue("SummaryPlannedMaterialCost")
    SUMMARY_PLANNED_MATERIAL_COST("SummaryPlannedMaterialCost"),
    @XmlEnumValue("SummaryPlannedNonLaborCost")
    SUMMARY_PLANNED_NON_LABOR_COST("SummaryPlannedNonLaborCost"),
    @XmlEnumValue("SummaryPlannedNonLaborUnits")
    SUMMARY_PLANNED_NON_LABOR_UNITS("SummaryPlannedNonLaborUnits"),
    @XmlEnumValue("SummaryPlannedStartDate")
    SUMMARY_PLANNED_START_DATE("SummaryPlannedStartDate"),
    @XmlEnumValue("SummaryPlannedValueByCost")
    SUMMARY_PLANNED_VALUE_BY_COST("SummaryPlannedValueByCost"),
    @XmlEnumValue("SummaryPlannedValueByLaborUnits")
    SUMMARY_PLANNED_VALUE_BY_LABOR_UNITS("SummaryPlannedValueByLaborUnits"),
    @XmlEnumValue("SummaryProgressFinishDate")
    SUMMARY_PROGRESS_FINISH_DATE("SummaryProgressFinishDate"),
    @XmlEnumValue("SummaryRemainingDuration")
    SUMMARY_REMAINING_DURATION("SummaryRemainingDuration"),
    @XmlEnumValue("SummaryRemainingExpenseCost")
    SUMMARY_REMAINING_EXPENSE_COST("SummaryRemainingExpenseCost"),
    @XmlEnumValue("SummaryRemainingFinishDate")
    SUMMARY_REMAINING_FINISH_DATE("SummaryRemainingFinishDate"),
    @XmlEnumValue("SummaryRemainingLaborCost")
    SUMMARY_REMAINING_LABOR_COST("SummaryRemainingLaborCost"),
    @XmlEnumValue("SummaryRemainingLaborUnits")
    SUMMARY_REMAINING_LABOR_UNITS("SummaryRemainingLaborUnits"),
    @XmlEnumValue("SummaryRemainingMaterialCost")
    SUMMARY_REMAINING_MATERIAL_COST("SummaryRemainingMaterialCost"),
    @XmlEnumValue("SummaryRemainingNonLaborCost")
    SUMMARY_REMAINING_NON_LABOR_COST("SummaryRemainingNonLaborCost"),
    @XmlEnumValue("SummaryRemainingNonLaborUnits")
    SUMMARY_REMAINING_NON_LABOR_UNITS("SummaryRemainingNonLaborUnits"),
    @XmlEnumValue("SummaryRemainingStartDate")
    SUMMARY_REMAINING_START_DATE("SummaryRemainingStartDate"),
    @XmlEnumValue("SummaryRemainingTotalCost")
    SUMMARY_REMAINING_TOTAL_COST("SummaryRemainingTotalCost"),
    @XmlEnumValue("SummarySchedulePercentComplete")
    SUMMARY_SCHEDULE_PERCENT_COMPLETE("SummarySchedulePercentComplete"),
    @XmlEnumValue("SummarySchedulePercentCompleteByLaborUnits")
    SUMMARY_SCHEDULE_PERCENT_COMPLETE_BY_LABOR_UNITS("SummarySchedulePercentCompleteByLaborUnits"),
    @XmlEnumValue("SummarySchedulePerformanceIndexByCost")
    SUMMARY_SCHEDULE_PERFORMANCE_INDEX_BY_COST("SummarySchedulePerformanceIndexByCost"),
    @XmlEnumValue("SummarySchedulePerformanceIndexByLaborUnits")
    SUMMARY_SCHEDULE_PERFORMANCE_INDEX_BY_LABOR_UNITS("SummarySchedulePerformanceIndexByLaborUnits"),
    @XmlEnumValue("SummaryScheduleVarianceByCost")
    SUMMARY_SCHEDULE_VARIANCE_BY_COST("SummaryScheduleVarianceByCost"),
    @XmlEnumValue("SummaryScheduleVarianceByLaborUnits")
    SUMMARY_SCHEDULE_VARIANCE_BY_LABOR_UNITS("SummaryScheduleVarianceByLaborUnits"),
    @XmlEnumValue("SummaryScheduleVarianceIndex")
    SUMMARY_SCHEDULE_VARIANCE_INDEX("SummaryScheduleVarianceIndex"),
    @XmlEnumValue("SummaryScheduleVarianceIndexByCost")
    SUMMARY_SCHEDULE_VARIANCE_INDEX_BY_COST("SummaryScheduleVarianceIndexByCost"),
    @XmlEnumValue("SummaryScheduleVarianceIndexByLaborUnits")
    SUMMARY_SCHEDULE_VARIANCE_INDEX_BY_LABOR_UNITS("SummaryScheduleVarianceIndexByLaborUnits"),
    @XmlEnumValue("SummaryStartDateVariance")
    SUMMARY_START_DATE_VARIANCE("SummaryStartDateVariance"),
    @XmlEnumValue("SummaryToCompletePerformanceIndexByCost")
    SUMMARY_TO_COMPLETE_PERFORMANCE_INDEX_BY_COST("SummaryToCompletePerformanceIndexByCost"),
    @XmlEnumValue("SummaryTotalCostVariance")
    SUMMARY_TOTAL_COST_VARIANCE("SummaryTotalCostVariance"),
    @XmlEnumValue("SummaryTotalFloat")
    SUMMARY_TOTAL_FLOAT("SummaryTotalFloat"),
    @XmlEnumValue("SummaryUnitsPercentComplete")
    SUMMARY_UNITS_PERCENT_COMPLETE("SummaryUnitsPercentComplete"),
    @XmlEnumValue("SummaryVarianceAtCompletionByLaborUnits")
    SUMMARY_VARIANCE_AT_COMPLETION_BY_LABOR_UNITS("SummaryVarianceAtCompletionByLaborUnits"),
    @XmlEnumValue("TotalBenefitPlan")
    TOTAL_BENEFIT_PLAN("TotalBenefitPlan"),
    @XmlEnumValue("TotalBenefitPlanTally")
    TOTAL_BENEFIT_PLAN_TALLY("TotalBenefitPlanTally"),
    @XmlEnumValue("TotalSpendingPlan")
    TOTAL_SPENDING_PLAN("TotalSpendingPlan"),
    @XmlEnumValue("TotalSpendingPlanTally")
    TOTAL_SPENDING_PLAN_TALLY("TotalSpendingPlanTally"),
    @XmlEnumValue("UnallocatedBudget")
    UNALLOCATED_BUDGET("UnallocatedBudget"),
    @XmlEnumValue("UndistributedCurrentVariance")
    UNDISTRIBUTED_CURRENT_VARIANCE("UndistributedCurrentVariance"),
    @XmlEnumValue("WBSCategoryObjectId")
    WBS_CATEGORY_OBJECT_ID("WBSCategoryObjectId"),
    @XmlEnumValue("WBSMilestonePercentComplete")
    WBS_MILESTONE_PERCENT_COMPLETE("WBSMilestonePercentComplete");
    private final String value;

    WBSFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WBSFieldType fromValue(String v) {
        for (WBSFieldType c: WBSFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
