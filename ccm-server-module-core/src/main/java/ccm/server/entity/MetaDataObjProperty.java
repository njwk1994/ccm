package ccm.server.entity;

import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.propertyValueUpdateState;
import ccm.server.util.CommonUtility;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xkcoding.http.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@ApiModel(description = "MetaDataObjProperty BASE ENTITY MODEL")
@Slf4j
@TableName("OBJPR")
public class MetaDataObjProperty extends MetaData implements Serializable {

    private static final long serialVersionUID = 1L;

    public void setDateForMeta(Date date) {
        if (date != null) {
            switch (this.updateState) {
                case updated:
                case created:
                case revive:
                    this.setCreationDate(date);
                    break;
                case terminated:
                    this.setTerminationDate(date);
                    break;

            }
        }
    }

    public void refreshUserInfo(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            if (this.updateState == propertyValueUpdateState.created) {
                this.setCreationUser(userName);
            } else if (this.updateState == propertyValueUpdateState.revive || this.updateState == propertyValueUpdateState.updated)
                this.setCreationUser(userName);
            else if (this.updateState == propertyValueUpdateState.terminated)
                this.setTerminationUser(userName);
        }
    }

    @Override
    public MetaData copy() {
        MetaDataObjProperty metaDataObjProperty = new MetaDataObjProperty();
        metaDataObjProperty.setInterfaceObid(this.interfaceObid);
        metaDataObjProperty.setInterfaceDefUid(this.interfaceDefUid);
        metaDataObjProperty.setUpdateState(this.updateState);
        metaDataObjProperty.setPropertyDefUid(this.propertyDefUid);
        metaDataObjProperty.setObjObid(this.objObid);
        metaDataObjProperty.setObid(this.getObid());
        metaDataObjProperty.setUom(this.uom);
        metaDataObjProperty.setStrValue(this.strValue);
        metaDataObjProperty.setCreationDate(this.getCreationDate());
        metaDataObjProperty.setCreationUser(this.getCreationUser());
        metaDataObjProperty.setConfig(this.getConfig());
        metaDataObjProperty.setTerminationDate(this.getTerminationDate());
        metaDataObjProperty.setTerminationUser(this.getTerminationUser());
        metaDataObjProperty.setObjClassDefinitionUid(this.getObjClassDefinitionUid());
        return metaDataObjProperty;
    }

    @ApiModelProperty(value = "所属对象的类型定义")
    @TableField(exist = false)
    private String objClassDefinitionUid;

    @ApiModelProperty(value = "属性编码")
    private String propertyDefUid;

    @ApiModelProperty(value = "所属对象Id")
    private String objObid;

    @ApiModelProperty(value = "属性值")
    private String strValue;

    @ApiModelProperty(value = "所属接口的标识码")
    private String interfaceObid;

    @ApiModelProperty(value = "所属接口的标识码")
    private String interfaceDefUid;

    @ApiModelProperty(value = "单位型属性具备的单位信息")
    private String uom = "";

    public String getUom() {
        if (this.uom == null)
            this.uom = "";
        return this.uom;
    }

    @TableField(exist = false)
    private propertyValueUpdateState updateState;

    public MetaDataObjProperty() {

    }

    public MetaDataObjProperty(String objObid) {
        this.setObjObid(objObid);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!this.getClass().toString().equalsIgnoreCase(o.getClass().toString()))
            return false;
        MetaDataObjProperty objpr = (MetaDataObjProperty) o;
        return propertyDefUid.equalsIgnoreCase(objpr.propertyDefUid) &&
                   objObid.equalsIgnoreCase(objpr.objObid) &&
                   interfaceDefUid.equalsIgnoreCase(objpr.interfaceDefUid) &&
                   this.terminationComparison(objpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyDefUid, objObid, interfaceDefUid, this.terminationDateString(), this.terminationUserString());
    }

    @Override
    public String toString() {
        return "MetaDataObjProperty{" +
                   "propertyDefUid='" + propertyDefUid + '\'' +
                   ", objId='" + objObid + '\'' +
                   ", interfaceDefUid='" + interfaceDefUid + '\'' +
                   this.terminationInfo() +
                   '}';
    }

    public MetaDataObjProperty(ObjectItemDTO objectItemDTO) {
        this(objectItemDTO.getObjObid());
        this.setObid(objectItemDTO.getObid());
        this.setPropertyDefUid(objectItemDTO.getDefUID());
        this.setStrValue(objectItemDTO.toValue());
    }

    @Override
    public void selfCheck() throws Exception {
        if (StringUtil.isEmpty(this.getPropertyDefUid()))
            throw new Exception("invalid property definition uid as it is null");
        if (StringUtil.isEmpty(this.getObjObid()))
            throw new Exception("invalid object obid as it is null");
        if (StringUtils.isEmpty(this.getInterfaceObid()))
            throw new Exception("invalid interface obid as it is null");
        if (StringUtils.isEmpty(this.getInterfaceDefUid()))
            throw new Exception("invalid interface definition as it is null");
    }

    public ObjectItemDTO toObjectItemDTO() {
        return new ObjectItemDTO(this);
    }

    public static List<MetaDataObjProperty> filter(List<MetaDataObjProperty> metaObjProperties, String objObid) {
        if (CommonUtility.hasValue(metaObjProperties) && !StringUtils.isEmpty(objObid))
            return metaObjProperties.stream().filter(c -> c.getObjObid().equalsIgnoreCase(objObid)).collect(Collectors.toList());
        return null;
    }

    public static Map<String, List<MetaDataObjProperty>> groupByObjObid(List<MetaDataObjProperty> properties) {
        if (CommonUtility.hasValue(properties))
            return properties.stream().collect(Collectors.groupingBy(MetaDataObjProperty::getObjObid));
        return null;
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
