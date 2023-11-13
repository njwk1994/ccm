package ccm.server.schema.interfaces.generated;

import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.schema.interfaces.ICIMMinioConfig;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;

public abstract class ICIMMinioConfigBase extends InterfaceDefault implements ICIMMinioConfig {
    public ICIMMinioConfigBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMMinioConfig.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMMinioUsername(String username) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioUsername.toString(), username);
    }

    @Override
    public String getCIMMinioUsername() {
        return this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioUsername.toString()).Value().toString();
    }

    @Override
    public void setCIMMinioPassword(String password) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioPassword.toString(), password);
    }

    @Override
    public String getCIMMinioPassword() {
        return this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioPassword.toString()).Value().toString();
    }

    @Override
    public void setCIMMinioIP(String ip) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioIP.toString(), ip);
    }

    @Override
    public String getCIMMinioIP() {
        return this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioIP.toString()).Value().toString();
    }

    @Override
    public void setCIMMinioInterIP(String interIP) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioInterIP.name(), interIP);
    }

    @Override
    public String getCIMMinioInterIP() {
        IProperty property = this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioInterIP.name());
        if (null == property){
            return null;
        }
        Object value = property.Value();
        return null == value ? null : value.toString();
    }

    @Override
    public void setCIMMinioPort(Integer port) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioPort.toString(), port);
    }

    @Override
    public Integer getCIMMinioPort() {
        return Integer.parseInt(this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioPort.toString()).Value().toString());
    }

    @Override
    public void setCIMMinioSSL(Boolean ssl) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioSSL.toString(), ssl);
    }

    @Override
    public Boolean getCIMMinioSSL() {
        return Boolean.parseBoolean(this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioSSL.toString()).Value().toString());
    }

    @Override
    public void setCIMMinioBucket(String bucket) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioBucket.toString(), bucket);
    }

    @Override
    public String getCIMMinioBucket() {
        return this.getProperty(interfaceDefinitionType.ICIMMinioConfig.toString(), propertyDefinitionType.CIMMinioBucket.toString()).Value().toString();
    }
}
