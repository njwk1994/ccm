
package ccm.server.ws.p6.export;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the ccm.server.ws.p6.export package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _IntegrationFault_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/IntegrationFaultType/V1", "IntegrationFault");
    private final static QName _ExportProject_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", "ExportProject");
    private final static QName _ExportProjectResponse_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", "ExportProjectResponse");
    private final static QName _ExportProjects_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", "ExportProjects");
    private final static QName _ExportProjectsResponse_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", "ExportProjectsResponse");
    private final static QName _DownloadFiles_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", "DownloadFiles");
    private final static QName _DownloadFilesResponse_QNAME = new QName("http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", "DownloadFilesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ccm.server.ws.p6.export
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IntegrationFaultType }
     *
     */
    public IntegrationFaultType createIntegrationFaultType() {
        return new IntegrationFaultType();
    }

    /**
     * Create an instance of {@link ExportProject }
     *
     */
    public ExportProject createExportProject() {
        return new ExportProject();
    }

    /**
     * Create an instance of {@link ExportProjectResponse }
     *
     */
    public ExportProjectResponse createExportProjectResponse() {
        return new ExportProjectResponse();
    }

    /**
     * Create an instance of {@link ExportProjects }
     *
     */
    public ExportProjects createExportProjects() {
        return new ExportProjects();
    }

    /**
     * Create an instance of {@link ExportProjectsResponse }
     *
     */
    public ExportProjectsResponse createExportProjectsResponse() {
        return new ExportProjectsResponse();
    }

    /**
     * Create an instance of {@link DownloadFiles }
     *
     */
    public DownloadFiles createDownloadFiles() {
        return new DownloadFiles();
    }

    /**
     * Create an instance of {@link DownloadFilesResponse }
     *
     */
    public DownloadFilesResponse createDownloadFilesResponse() {
        return new DownloadFilesResponse();
    }

    /**
     * Create an instance of {@link Activity }
     *
     */
    public Activity createActivity() {
        return new Activity();
    }

    /**
     * Create an instance of {@link ActivityCode }
     *
     */
    public ActivityCode createActivityCode() {
        return new ActivityCode();
    }

    /**
     * Create an instance of {@link ActivityCodeAssignment }
     *
     */
    public ActivityCodeAssignment createActivityCodeAssignment() {
        return new ActivityCodeAssignment();
    }

    /**
     * Create an instance of {@link ActivityCodeType }
     *
     */
    public ActivityCodeType createActivityCodeType() {
        return new ActivityCodeType();
    }

    /**
     * Create an instance of {@link ActivityExpense }
     *
     */
    public ActivityExpense createActivityExpense() {
        return new ActivityExpense();
    }

    /**
     * Create an instance of {@link ActivityNote }
     *
     */
    public ActivityNote createActivityNote() {
        return new ActivityNote();
    }

    /**
     * Create an instance of {@link ActivityPeriodActual }
     *
     */
    public ActivityPeriodActual createActivityPeriodActual() {
        return new ActivityPeriodActual();
    }

    /**
     * Create an instance of {@link ActivityRisk }
     *
     */
    public ActivityRisk createActivityRisk() {
        return new ActivityRisk();
    }

    /**
     * Create an instance of {@link ActivityStep }
     *
     */
    public ActivityStep createActivityStep() {
        return new ActivityStep();
    }

    /**
     * Create an instance of {@link Calendar }
     *
     */
    public Calendar createCalendar() {
        return new Calendar();
    }

    /**
     * Create an instance of {@link CostAccount }
     *
     */
    public CostAccount createCostAccount() {
        return new CostAccount();
    }

    /**
     * Create an instance of {@link Currency }
     *
     */
    public Currency createCurrency() {
        return new Currency();
    }

    /**
     * Create an instance of {@link Document }
     *
     */
    public Document createDocument() {
        return new Document();
    }

    /**
     * Create an instance of {@link DocumentCategory }
     *
     */
    public DocumentCategory createDocumentCategory() {
        return new DocumentCategory();
    }

    /**
     * Create an instance of {@link DocumentStatusCode }
     *
     */
    public DocumentStatusCode createDocumentStatusCode() {
        return new DocumentStatusCode();
    }

    /**
     * Create an instance of {@link EPS }
     *
     */
    public EPS createEPS() {
        return new EPS();
    }

    /**
     * Create an instance of {@link ExpenseCategory }
     *
     */
    public ExpenseCategory createExpenseCategory() {
        return new ExpenseCategory();
    }

    /**
     * Create an instance of {@link FinancialPeriod }
     *
     */
    public FinancialPeriod createFinancialPeriod() {
        return new FinancialPeriod();
    }

    /**
     * Create an instance of {@link FundingSource }
     *
     */
    public FundingSource createFundingSource() {
        return new FundingSource();
    }

    /**
     * Create an instance of {@link NotebookTopic }
     *
     */
    public NotebookTopic createNotebookTopic() {
        return new NotebookTopic();
    }

    /**
     * Create an instance of {@link OBS }
     *
     */
    public OBS createOBS() {
        return new OBS();
    }

    /**
     * Create an instance of {@link Project }
     *
     */
    public Project createProject() {
        return new Project();
    }

    /**
     * Create an instance of {@link ProjectBudgetChangeLog }
     *
     */
    public ProjectBudgetChangeLog createProjectBudgetChangeLog() {
        return new ProjectBudgetChangeLog();
    }

    /**
     * Create an instance of {@link ProjectCode }
     *
     */
    public ProjectCode createProjectCode() {
        return new ProjectCode();
    }

    /**
     * Create an instance of {@link ProjectCodeAssignment }
     *
     */
    public ProjectCodeAssignment createProjectCodeAssignment() {
        return new ProjectCodeAssignment();
    }

    /**
     * Create an instance of {@link ProjectCodeType }
     *
     */
    public ProjectCodeType createProjectCodeType() {
        return new ProjectCodeType();
    }

    /**
     * Create an instance of {@link ProjectDocument }
     *
     */
    public ProjectDocument createProjectDocument() {
        return new ProjectDocument();
    }

    /**
     * Create an instance of {@link ProjectFunding }
     *
     */
    public ProjectFunding createProjectFunding() {
        return new ProjectFunding();
    }

    /**
     * Create an instance of {@link ProjectIssue }
     *
     */
    public ProjectIssue createProjectIssue() {
        return new ProjectIssue();
    }

    /**
     * Create an instance of {@link ProjectNote }
     *
     */
    public ProjectNote createProjectNote() {
        return new ProjectNote();
    }

    /**
     * Create an instance of {@link ProjectResource }
     *
     */
    public ProjectResource createProjectResource() {
        return new ProjectResource();
    }

    /**
     * Create an instance of {@link ProjectResourceCategory }
     *
     */
    public ProjectResourceCategory createProjectResourceCategory() {
        return new ProjectResourceCategory();
    }

    /**
     * Create an instance of {@link ProjectResourceQuantity }
     *
     */
    public ProjectResourceQuantity createProjectResourceQuantity() {
        return new ProjectResourceQuantity();
    }

    /**
     * Create an instance of {@link ProjectSpendingPlan }
     *
     */
    public ProjectSpendingPlan createProjectSpendingPlan() {
        return new ProjectSpendingPlan();
    }

    /**
     * Create an instance of {@link ProjectThreshold }
     *
     */
    public ProjectThreshold createProjectThreshold() {
        return new ProjectThreshold();
    }

    /**
     * Create an instance of {@link Relationship }
     *
     */
    public Relationship createRelationship() {
        return new Relationship();
    }

    /**
     * Create an instance of {@link Resource }
     *
     */
    public Resource createResource() {
        return new Resource();
    }

    /**
     * Create an instance of {@link ResourceAssignment }
     *
     */
    public ResourceAssignment createResourceAssignment() {
        return new ResourceAssignment();
    }

    /**
     * Create an instance of {@link ResourceAssignmentPeriodActual }
     *
     */
    public ResourceAssignmentPeriodActual createResourceAssignmentPeriodActual() {
        return new ResourceAssignmentPeriodActual();
    }

    /**
     * Create an instance of {@link ResourceCode }
     *
     */
    public ResourceCode createResourceCode() {
        return new ResourceCode();
    }

    /**
     * Create an instance of {@link ResourceCodeAssignment }
     *
     */
    public ResourceCodeAssignment createResourceCodeAssignment() {
        return new ResourceCodeAssignment();
    }

    /**
     * Create an instance of {@link ResourceCodeType }
     *
     */
    public ResourceCodeType createResourceCodeType() {
        return new ResourceCodeType();
    }

    /**
     * Create an instance of {@link ResourceCurve }
     *
     */
    public ResourceCurve createResourceCurve() {
        return new ResourceCurve();
    }

    /**
     * Create an instance of {@link ResourceRate }
     *
     */
    public ResourceRate createResourceRate() {
        return new ResourceRate();
    }

    /**
     * Create an instance of {@link ResourceRole }
     *
     */
    public ResourceRole createResourceRole() {
        return new ResourceRole();
    }

    /**
     * Create an instance of {@link Risk }
     *
     */
    public Risk createRisk() {
        return new Risk();
    }

    /**
     * Create an instance of {@link RiskCategory }
     *
     */
    public RiskCategory createRiskCategory() {
        return new RiskCategory();
    }

    /**
     * Create an instance of {@link RiskImpact }
     *
     */
    public RiskImpact createRiskImpact() {
        return new RiskImpact();
    }

    /**
     * Create an instance of {@link RiskMatrixScore }
     *
     */
    public RiskMatrixScore createRiskMatrixScore() {
        return new RiskMatrixScore();
    }

    /**
     * Create an instance of {@link RiskMatrixThreshold }
     *
     */
    public RiskMatrixThreshold createRiskMatrixThreshold() {
        return new RiskMatrixThreshold();
    }

    /**
     * Create an instance of {@link RiskResponseAction }
     *
     */
    public RiskResponseAction createRiskResponseAction() {
        return new RiskResponseAction();
    }

    /**
     * Create an instance of {@link RiskResponseActionImpact }
     *
     */
    public RiskResponseActionImpact createRiskResponseActionImpact() {
        return new RiskResponseActionImpact();
    }

    /**
     * Create an instance of {@link RiskResponsePlan }
     *
     */
    public RiskResponsePlan createRiskResponsePlan() {
        return new RiskResponsePlan();
    }

    /**
     * Create an instance of {@link RiskMatrix }
     *
     */
    public RiskMatrix createRiskMatrix() {
        return new RiskMatrix();
    }

    /**
     * Create an instance of {@link RiskThresholdLevel }
     *
     */
    public RiskThresholdLevel createRiskThresholdLevel() {
        return new RiskThresholdLevel();
    }

    /**
     * Create an instance of {@link RiskThreshold }
     *
     */
    public RiskThreshold createRiskThreshold() {
        return new RiskThreshold();
    }

    /**
     * Create an instance of {@link Role }
     *
     */
    public Role createRole() {
        return new Role();
    }

    /**
     * Create an instance of {@link RoleRate }
     *
     */
    public RoleRate createRoleRate() {
        return new RoleRate();
    }

    /**
     * Create an instance of {@link RoleLimit }
     *
     */
    public RoleLimit createRoleLimit() {
        return new RoleLimit();
    }

    /**
     * Create an instance of {@link Shift }
     *
     */
    public Shift createShift() {
        return new Shift();
    }

    /**
     * Create an instance of {@link ThresholdParameter }
     *
     */
    public ThresholdParameter createThresholdParameter() {
        return new ThresholdParameter();
    }

    /**
     * Create an instance of {@link UDFCode }
     *
     */
    public UDFCode createUDFCode() {
        return new UDFCode();
    }

    /**
     * Create an instance of {@link UDFType }
     *
     */
    public UDFType createUDFType() {
        return new UDFType();
    }

    /**
     * Create an instance of {@link UDFValue }
     *
     */
    public UDFValue createUDFValue() {
        return new UDFValue();
    }

    /**
     * Create an instance of {@link UnitOfMeasure }
     *
     */
    public UnitOfMeasure createUnitOfMeasure() {
        return new UnitOfMeasure();
    }

    /**
     * Create an instance of {@link WBS }
     *
     */
    public WBS createWBS() {
        return new WBS();
    }

    /**
     * Create an instance of {@link WBSCategory }
     *
     */
    public WBSCategory createWBSCategory() {
        return new WBSCategory();
    }

    /**
     * Create an instance of {@link WBSMilestone }
     *
     */
    public WBSMilestone createWBSMilestone() {
        return new WBSMilestone();
    }

    /**
     * Create an instance of {@link BusinessObjectOptions }
     *
     */
    public BusinessObjectOptions createBusinessObjectOptions() {
        return new BusinessObjectOptions();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntegrationFaultType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link IntegrationFaultType }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/IntegrationFaultType/V1", name = "IntegrationFault")
    public JAXBElement<IntegrationFaultType> createIntegrationFault(IntegrationFaultType value) {
        return new JAXBElement<IntegrationFaultType>(_IntegrationFault_QNAME, IntegrationFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportProject }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExportProject }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "ExportProject")
    public JAXBElement<ExportProject> createExportProject(ExportProject value) {
        return new JAXBElement<ExportProject>(_ExportProject_QNAME, ExportProject.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportProjectResponse }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExportProjectResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "ExportProjectResponse")
    public JAXBElement<ExportProjectResponse> createExportProjectResponse(ExportProjectResponse value) {
        return new JAXBElement<ExportProjectResponse>(_ExportProjectResponse_QNAME, ExportProjectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportProjects }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExportProjects }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "ExportProjects")
    public JAXBElement<ExportProjects> createExportProjects(ExportProjects value) {
        return new JAXBElement<ExportProjects>(_ExportProjects_QNAME, ExportProjects.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExportProjectsResponse }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ExportProjectsResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "ExportProjectsResponse")
    public JAXBElement<ExportProjectsResponse> createExportProjectsResponse(ExportProjectsResponse value) {
        return new JAXBElement<ExportProjectsResponse>(_ExportProjectsResponse_QNAME, ExportProjectsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DownloadFiles }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DownloadFiles }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "DownloadFiles")
    public JAXBElement<DownloadFiles> createDownloadFiles(DownloadFiles value) {
        return new JAXBElement<DownloadFiles>(_DownloadFiles_QNAME, DownloadFiles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DownloadFilesResponse }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link DownloadFilesResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "DownloadFilesResponse")
    public JAXBElement<DownloadFilesResponse> createDownloadFilesResponse(DownloadFilesResponse value) {
        return new JAXBElement<DownloadFilesResponse>(_DownloadFilesResponse_QNAME, DownloadFilesResponse.class, null, value);
    }

}
