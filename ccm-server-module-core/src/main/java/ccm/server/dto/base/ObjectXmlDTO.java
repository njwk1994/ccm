package ccm.server.dto.base;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class ObjectXmlDTO implements Serializable {

    private String xmlInfo;

    private String domainUID;

    private String classDefinitionUID;

    private String uid;

    private String name;

    private String obid;

    private List<ObjectXmlDTO> children;

    public void appendChild(ObjectXmlDTO objectXmlDTO) {
        if (objectXmlDTO != null && !children.contains(objectXmlDTO)) {
            children.add(objectXmlDTO);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectXmlDTO xmlDTO = (ObjectXmlDTO) o;
        return Objects.equals(obid, xmlDTO.obid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obid);
    }
}
