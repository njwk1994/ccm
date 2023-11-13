package ccm.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
@TableName("OBJPRDETAILS")
public class MetaDataObjPropertyDetails extends MetaData implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "属性标识码")
    private String propertyObid;

    @ApiModelProperty(value = "属性值")
    private String details;

    @Override
    public MetaData copy() {
        MetaDataObjPropertyDetails metaDataObjPropertyDetails = new MetaDataObjPropertyDetails();
        metaDataObjPropertyDetails.setPropertyObid(this.propertyObid);
        metaDataObjPropertyDetails.setCreationDate(this.getCreationDate());
        metaDataObjPropertyDetails.setCreationUser(this.getCreationUser());
        metaDataObjPropertyDetails.setTerminationDate(this.getTerminationDate());
        metaDataObjPropertyDetails.setTerminationUser(this.getTerminationUser());
        return metaDataObjPropertyDetails;
    }

    public MetaDataObjPropertyDetails() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!this.getClass().toString().equalsIgnoreCase(o.getClass().toString()))
            return false;
        MetaDataObjPropertyDetails that = (MetaDataObjPropertyDetails) o;
        return propertyObid.equalsIgnoreCase(that.propertyObid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyObid);
    }

    @Override
    public String toString() {
        return "objPropDetails{" +
                "propertyObid='" + propertyObid + '\'' +
                '}';
    }

    @Override
    public boolean fromDb() {
        return !StringUtils.isEmpty(this.getObid());
    }

    @Override
    public String getPrimaryKey() {
        return this.getObid();
    }

    @Override
    public void setPrimaryKey(String primaryKey) {
        this.setObid(primaryKey);
    }

    @Override
    public String getUniqueIdentity() {
        return this.toString();
    }
}
