package ccm.server.ws.p6.export;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.5.3
 * 2022-09-14T16:10:38.884+08:00
 * Generated source version: 3.5.3
 *
 */
@WebService(targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", name = "ExportPortType")
@XmlSeeAlso({ObjectFactory.class})
public interface ExportPortType {

    @WebMethod(operationName = "DownloadFiles", action = "DownloadFiles")
    @RequestWrapper(localName = "DownloadFiles", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", className = "ccm.server.ws.p6.export.DownloadFiles")
    @ResponseWrapper(localName = "DownloadFilesResponse", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", className = "ccm.server.ws.p6.export.DownloadFilesResponse")
    @WebResult(name = "NumberOfFiles", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
    public int downloadFiles(

        @WebParam(name = "JobType", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        String jobType,
        @WebParam(name = "JobName", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        java.util.List<String> jobName,
        @WebParam(name = "StartDate", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        javax.xml.datatype.XMLGregorianCalendar startDate,
        @WebParam(name = "EndDate", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        javax.xml.datatype.XMLGregorianCalendar endDate
    ) throws IntegrationFault;

    @WebMethod(operationName = "ExportProject", action = "ExportProject")
    @RequestWrapper(localName = "ExportProject", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", className = "ccm.server.ws.p6.export.ExportProject")
    @ResponseWrapper(localName = "ExportProjectResponse", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", className = "ccm.server.ws.p6.export.ExportProjectResponse")
    @WebResult(name = "ProjectData", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
    public javax.activation.DataHandler exportProject(

        @WebParam(name = "Encoding", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        String encoding,
        @WebParam(name = "FileType", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.FileTypeType fileType,
        @WebParam(name = "LineSeparator", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.LineSeparator lineSeparator,
        @WebParam(name = "ProjectObjectId", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        int projectObjectId,
        @WebParam(name = "SpreadPeriodType", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.SpreadPeriodType spreadPeriodType,
        @WebParam(name = "Spacing", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        String spacing,
        @WebParam(name = "BusinessObjectOptions", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.BusinessObjectOptions businessObjectOptions
    ) throws IntegrationFault;

    @WebMethod(operationName = "ExportProjects", action = "ExportProjects")
    @RequestWrapper(localName = "ExportProjects", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", className = "ccm.server.ws.p6.export.ExportProjects")
    @ResponseWrapper(localName = "ExportProjectsResponse", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2", className = "ccm.server.ws.p6.export.ExportProjectsResponse")
    @WebResult(name = "ProjectData", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
    public javax.activation.DataHandler exportProjects(

        @WebParam(name = "Encoding", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        String encoding,
        @WebParam(name = "FileType", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.FileTypeType fileType,
        @WebParam(name = "LineSeparator", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.LineSeparator lineSeparator,
        @WebParam(name = "ProjectObjectId", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        java.util.List<Integer> projectObjectId,
        @WebParam(name = "SpreadPeriodType", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.SpreadPeriodType spreadPeriodType,
        @WebParam(name = "Spacing", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        String spacing,
        @WebParam(name = "BusinessObjectOptions", targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2")
        ccm.server.ws.p6.export.BusinessObjectOptions businessObjectOptions
    ) throws IntegrationFault;
}