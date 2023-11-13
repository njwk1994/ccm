package ccm.server.model;

import ccm.server.context.CIMContext;
import ccm.server.schema.interfaces.IEnumEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertyHierarchyVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String displayName;
    private final int level;
    private final List<String> objectIds;

    private final String propertyDefinitionUid;

    private final List<PropertyHierarchyVo> children = new ArrayList<>();

    public PropertyHierarchyVo(String propertyDefinitionUid, String propertyValue, List<String> objectIds, int level) {
        this.propertyDefinitionUid = propertyDefinitionUid;
        this.displayName = propertyValue;
        this.level = level;
        this.objectIds = objectIds != null ? objectIds : new ArrayList<>();
    }

    public void release() throws Exception {
        this.objectIds.clear();
        this.resetDisplayName();
        if (this.children.size() > 0) {
            for (PropertyHierarchyVo child : this.children) {
                child.release();
                child.resetDisplayName();
            }
        }
    }

    private void resetDisplayName() throws Exception {
        if (CIMContext.Instance.ProcessCache().isEnumListTypeProperty(this.propertyDefinitionUid)) {
            IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(this.propertyDefinitionUid, this.displayName);
            if (enumEnum != null) {
                this.displayName = enumEnum.Name() + "," + enumEnum.Description();
                /*if (StringUtils.isEmpty(this.displayName))
                    this.displayName = enumEnum.Name();*/
            }
        }
    }

    public void append(PropertyHierarchyVo item) {
        if (item != null) {
            this.children.add(item);
        }
    }
}
