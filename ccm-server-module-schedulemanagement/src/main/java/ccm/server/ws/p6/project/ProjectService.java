package ccm.server.ws.p6.project;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by Apache CXF 3.5.3
 * 2022-09-14T16:10:21.908+08:00
 * Generated source version: 3.5.3
 *
 */
@WebServiceClient(name = "ProjectService",
                  wsdlLocation = "http://192.168.3.18:8206/p6ws/services/ProjectService?wsdl",
                  targetNamespace = "http://xmlns.oracle.com/Primavera/P6/WS/Project/V2")
public class ProjectService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://xmlns.oracle.com/Primavera/P6/WS/Project/V2", "ProjectService");
    public final static QName ProjectPort = new QName("http://xmlns.oracle.com/Primavera/P6/WS/Project/V2", "ProjectPort");
    static {
        URL url = null;
        try {
            url = new URL("http://192.168.3.18:8206/p6ws/services/ProjectService?wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(ProjectService.class.getName())
                .log(java.util.logging.Level.INFO,
                     "Can not initialize the default wsdl from {0}", "http://192.168.3.18:8206/p6ws/services/ProjectService?wsdl");
        }
        WSDL_LOCATION = url;
    }

    public ProjectService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public ProjectService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ProjectService() {
        super(WSDL_LOCATION, SERVICE);
    }

    public ProjectService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public ProjectService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public ProjectService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }




    /**
     *
     * @return
     *     returns ProjectPortType
     */
    @WebEndpoint(name = "ProjectPort")
    public ProjectPortType getProjectPort() {
        return super.getPort(ProjectPort, ProjectPortType.class);
    }

    /**
     *
     * @param features
     *     A list of {@link WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ProjectPortType
     */
    @WebEndpoint(name = "ProjectPort")
    public ProjectPortType getProjectPort(WebServiceFeature... features) {
        return super.getPort(ProjectPort, ProjectPortType.class, features);
    }

}