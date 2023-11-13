package ccm.server.schema.interfaces.generated;

import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.schema.interfaces.ICIMFileType;
import ccm.server.schema.model.InterfaceDefault;

public abstract class ICIMFileTypeBase extends InterfaceDefault implements ICIMFileType {
    @Override
    public void setCIMFileExtension(String fileExtension) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFileType.toString(), propertyDefinitionType.CIMFileExtension.toString(), fileExtension);
    }

    @Override
    public String getCIMFileExtension() {
        return this.getProperty(interfaceDefinitionType.ICIMFileType.toString(), propertyDefinitionType.CIMFileExtension.toString()).Value().toString();
    }

    @Override
    public void setCIMFileViewable(boolean fileViewable) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFileType.toString(), propertyDefinitionType.CIMFileViewable.toString(), fileViewable);
    }

    @Override
    public boolean getCIMFileViewable() {
        return Boolean.parseBoolean(this.getProperty(interfaceDefinitionType.ICIMFileType.toString(), propertyDefinitionType.CIMFileExtension.toString()).Value().toString());
    }

    @Override
    public void setCIMFileEditable(boolean fileEditable) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFileType.toString(), propertyDefinitionType.CIMFileEditable.toString(), fileEditable);
    }

    @Override
    public boolean getCIMFileEditable() {
        return Boolean.parseBoolean(this.getProperty(interfaceDefinitionType.ICIMFileType.toString(), propertyDefinitionType.CIMFileEditable.toString()).Value().toString());
    }

    public ICIMFileTypeBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMFileType.toString(), instantiateRequiredProperties);
    }
}
