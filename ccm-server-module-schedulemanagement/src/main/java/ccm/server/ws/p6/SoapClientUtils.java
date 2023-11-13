package ccm.server.ws.p6;

import ccm.server.context.CIMContext;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.webservice.SoapClient;
import lombok.extern.slf4j.Slf4j;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/14 19:00
 */
@Slf4j
public class SoapClientUtils {

    private static final Map<String, Set<SoapClient>> soapClientMap = new ConcurrentHashMap<>();

    private static final String WSSE_NAMESPACE_URL = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WSU_NAMESPACE_URL = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    private static final String PASSWORD_TYPE_URL = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";

    private SoapClientUtils() {
    }

    /**
     * 检查URL路径并使结尾为 '/'
     *
     * @param path
     * @return
     */
    public static String checkURLPath(String path) {
        return path.endsWith("/") ? path : path + "/";
    }

    /**
     * 创建不缓存的带请求头的客户端
     *
     * @param wsdlUrl
     * @param username
     * @param password
     * @return
     */
    public static SoapClient createClientWithWsseNoCache(String wsdlUrl, String username, String password) {
        SoapClient soapClient = SoapClient.create(wsdlUrl);
        try {
            SOAPHeader soapHeader = soapClient.getMessage().getSOAPHeader();
            // 创建 Nonce 参数
            String uuid = UUID.fastUUID().toString(true);
            String nonce64Str = Base64Encoder.encode(uuid);
            // 创建 Created 参数
            DateTime now = DateTime.now();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dataString = now.toString(df);
            // 创建 Security 节点
            SOAPElement security = soapHeader.addChildElement(new QName(WSSE_NAMESPACE_URL, "Security", "wsse"));
            // 创建 UsernameToken 节点
            SOAPElement usernameTokenElement = security.addChildElement(new QName(WSSE_NAMESPACE_URL, "UsernameToken", "wsse"));
            usernameTokenElement.addAttribute(new QName("xmlns:wsu"), WSU_NAMESPACE_URL);
            // 创建 Username 节点
            usernameTokenElement.addChildElement(new QName(WSSE_NAMESPACE_URL, "Username", "wsse")).addTextNode(username);
            // 创建 Password 节点
            SOAPElement passwordElement = usernameTokenElement.addChildElement(new QName(WSSE_NAMESPACE_URL, "Password", "wsse"));
            passwordElement.setAttribute("Type", PASSWORD_TYPE_URL);
            passwordElement.addTextNode(password);
            // 创建 Nonce 节点
            usernameTokenElement.addChildElement(new QName(WSSE_NAMESPACE_URL, "Nonce", "wsse")).addTextNode(nonce64Str);
            // 创建 Created 节点
            usernameTokenElement.addChildElement(new QName(WSU_NAMESPACE_URL, "Created", "wsu")).addTextNode(dataString);
        } catch (SOAPException e) {
            log.error("获取并生成WebService请求头失败!{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new RuntimeException("获取并生成WebService请求头失败!" + ExceptionUtil.getMessage(e));
        }
        return soapClient;
    }

    /**
     * 生成缓存用key
     *
     * @param wsdlUrl
     * @param projectName
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String generateKey(String wsdlUrl, String projectName, String username, String password) throws Exception {
        String configName;
        try {
            ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
            configName = configurationItem.Name();
        } catch (Exception e) {
            throw new Exception("生成Key时获取Config项目信息失败.");
        }
        return "[" + configName + "]" + username + "@" + password + "#" + projectName + ":" + wsdlUrl;
    }

    /**
     * 创建SOAP客户端
     *
     * @param wsdlUrl
     * @param projectName
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static SoapClient createSoapClient(String wsdlUrl, String projectName, String username, String password) throws Exception {
        String key = generateKey(wsdlUrl, projectName, username, password);
        Set<SoapClient> soapClients = getSoapClients(key);
        SoapClient soapClient = SoapClient.create(wsdlUrl);
        soapClients.add(soapClient);
        return soapClient;
    }

    /**
     * 根据key获取SOAP客户端集合,不存在则创建集合
     *
     * @param key
     * @return
     */
    public static Set<SoapClient> getSoapClients(String key) {
        Set<SoapClient> soapClients = soapClientMap.get(key);
        if (null == soapClients) {
            // 使用线程安全Set
            Set<SoapClient> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
            soapClientMap.put(key, set);
        }
        return soapClients;
    }

    /**
     * 根据key获取SOAP客户端
     *
     * @param key
     * @return
     */
    public static SoapClient getSoapClient(String key) {
        Set<SoapClient> soapClients = soapClientMap.get(key);
        if (null == soapClients || soapClients.isEmpty()) {
            return null;
        }
        return getSoapClient(soapClients);
    }

    /**
     * 根据参数获取SOAP客户端,如果不存在则创建客户端
     *
     * @param wsdlUrl
     * @param projectName
     * @param username
     * @param password
     * @return
     */
    public static SoapClient getSoapClient(String wsdlUrl, String projectName, String username, String password) throws Exception {
        String key = generateKey(wsdlUrl, projectName, username, password);
        Set<SoapClient> soapClients = getSoapClients(key);
        SoapClient soapClient;
        if (soapClients.isEmpty()) {
            soapClient = createSoapClient(wsdlUrl, projectName, username, password);
        } else {
            soapClient = getSoapClient(soapClients);
        }
        return soapClient;
    }

    /**
     * 获取SOAP客户端并从集合中移除
     *
     * @param soapClients
     * @return
     */
    private static SoapClient getSoapClient(Set<SoapClient> soapClients) {
        SoapClient soapClient = soapClients.iterator().next();
        soapClients.remove(soapClient);
        return soapClient;
    }

    /**
     * 重置请求方法并归还SOAP客户端
     *
     * @param key
     * @param soapClient
     */
    public static void backClient(String key, SoapClient soapClient) {
        Set<SoapClient> soapClients = getSoapClients(key);
        soapClient.reset();
        soapClients.add(soapClient);
    }

    /**
     * 移除客户端集合
     *
     * @param key
     */
    public static void removeSoapClients(String key) {
        soapClientMap.remove(key);
    }

}
