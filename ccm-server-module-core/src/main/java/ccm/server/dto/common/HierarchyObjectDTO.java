package ccm.server.dto.common;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

@Data
@Slf4j
public class HierarchyObjectDTO extends ObjectDTO implements Serializable {

    private static final long serializableId = 1L;
    private HierarchyObjectDTO parent;
    private final List<HierarchyObjectDTO> children = new ArrayList<>();

    private String id;
    private String name;
    private String description;
    private String tooltip;

    private boolean isExpand;
    private boolean readOnly;

    private ObjectDTO definitionObj;

    public HierarchyObjectDTO() {
        super();
        this.toSetValue(propertyDefinitionType.OBID.toString(), UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public boolean hasChildren() {
        return this.children.stream().allMatch(Objects::nonNull);
    }

    public boolean hasChildren(String name) {
        if (this.hasChildren()) {
            return this.children.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name));
        }
        return false;
    }

    public HierarchyObjectDTO getChild(String name) {
        if (this.hasChildren())
            return this.children.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return null;
    }

    public void remove(String name) {
        if (!StringUtils.isEmpty(name)) {
            HierarchyObjectDTO current = this.getChild(name);
            if (current != null)
                this.children.remove(current);
        }
    }

    public void appendChild(List<HierarchyObjectDTO> subNodes) {
        if (CommonUtility.hasValue(subNodes)) {
            for (HierarchyObjectDTO o : subNodes)
                this.appendChild(o);
        }
    }

    public void appendChild(HierarchyObjectDTO o) {
        if (o != null) {
            Optional<HierarchyObjectDTO> first = this.children.stream().filter(c -> c.getName().equalsIgnoreCase(o.getName())).findFirst();
            first.ifPresent(this.children::remove);
            this.children.add(o);
            o.setParent(this);
        }
    }
}
