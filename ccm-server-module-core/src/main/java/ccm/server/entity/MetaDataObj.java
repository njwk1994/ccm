package ccm.server.entity;

import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.objectUpdateState;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.util.CommonUtility;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xkcoding.http.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
@ApiModel(description = "MetaDataObj BASE ENTITY MODEL")
@TableName("OBJ")
public class MetaDataObj extends MetaData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public MetaData copy() {
        MetaDataObj metaDataObj = new MetaDataObj();
        metaDataObj.setName(this.name);
        metaDataObj.setDescription(this.description);
        metaDataObj.setTerminationUser(this.getTerminationUser());
        metaDataObj.setTerminationDate(this.getTerminationDate());
        metaDataObj.setLastUpdateUser(this.lastUpdateUser);
        metaDataObj.setLastUpdateDate(this.lastUpdateDate);
        metaDataObj.setCreationUser(this.getCreationUser());
        metaDataObj.setCreationDate(this.getCreationDate());
        metaDataObj.setUpdateState(this.updateState);
        metaDataObj.setClassDefinitionUid(this.classDefinitionUid);
        metaDataObj.setUniqueKey(this.uniqueKey);
        metaDataObj.setObjUid(this.objUid);
        metaDataObj.setObid(this.getObid());
        metaDataObj.setConfig(this.getConfig());
        metaDataObj.setDomainUid(this.domainUid);
        return metaDataObj;
    }

    public void setDateForMeta(Date date) {
        if (date != null) {
            switch (this.updateState) {
                case updated:
                    this.setLastUpdateDate(date);
                    break;
                case terminated:
                    this.setTerminationDate(date);
                    break;
                case created:
                    this.setCreationDate(date);
                    break;
            }
        }
    }

    @ApiModelProperty(value = "编码/名称", required = true, hidden = false)
    private String name;

    public String getName() {
        if (this.name == null)
            this.name = "";
        return this.name;
    }

    @ApiModelProperty(value = "描述说明/名称", hidden = false, required = false)
    private String description;

    @ApiModelProperty(value = "对象类型定义，系统用", hidden = true, required = true)
    private String classDefinitionUid;

    @ApiModelProperty(value = "域标识码", hidden = true, required = true)
    private String domainUid;

    public String getDomainUid() {
        if (this.domainUid == null)
            this.domainUid = "";
        return this.domainUid;
    }

    @ApiModelProperty(value = "唯一键值，通过给定的规则生成的，作用于一个Config下的全部对象判断", hidden = true, required = false)
    private String uniqueKey;

    @ApiModelProperty(value = "唯一键值，可通过规则生成，可来源于其他系统唯一标识，默认为UUID生成(使用OBID给值)", hidden = true, required = false)
    private String objUid;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "最后更新日期", hidden = true, required = false)
    private Date lastUpdateDate;

    @ApiModelProperty(value = "最后更新人", hidden = false, required = false)
    private String lastUpdateUser;

    @TableField(exist = false)
    private objectUpdateState updateState;

    public MetaDataObj() {
        this.objUid = UUID.randomUUID().toString();
    }

    public static List<MetaDataObj> sort(List<MetaDataObj> items) {
        if (CommonUtility.hasValue(items)) {
            return items.stream().sorted(new Comparator<MetaDataObj>() {
                @Override
                public int compare(MetaDataObj o1, MetaDataObj o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    public String getObjUid() {
        if (this.objUid == null)
            this.objUid = UUID.randomUUID().toString();
        return this.objUid;
    }

    public void refreshUserInfo(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            if (this.getUpdateState() == objectUpdateState.created) {
                this.setCreationUser(userName);
            } else if (this.getUpdateState() == objectUpdateState.updated)
                this.setLastUpdateUser(userName);
            else if (this.getUpdateState() == objectUpdateState.terminated)
                this.setTerminationUser(userName);
        }
    }

    @Override
    public void selfCheck() throws Exception {
        if (StringUtil.isEmpty(this.getObjUid()))
            throw new Exception("invalid system uid info as it is null, NULL is not allow for such property");

        if (StringUtil.isEmpty(this.getClassDefinitionUid()))
            throw new Exception("invalid class definition uid as it is null, NULL is not allow for such property");

        if (StringUtils.isEmpty(this.getDomainUid()))
            throw new Exception("invalid domain UID as it is null,NULL is not allow for such property");

        if (StringUtils.isEmpty(this.getObid()))
            throw new Exception("invalid obid as it is null");
    }

    public List<ObjectItemDTO> toObjectItemDTOs() {
        List<ObjectItemDTO> result = new ArrayList<>();
        result.add(new ObjectItemDTO(propertyDefinitionType.OBID.toString(),
                "Obid", "unique Obid", this.getObid(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.Name.toString(),
                "Name", "object name", this.getName(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.Description.toString(),
                "Description", "object description", this.getDescription(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, false));
        result.add(new ObjectItemDTO(propertyDefinitionType.ClassDefinitionUID.toString(),
                "Class Definition UID", "Class Definition UID", this.getClassDefinitionUid(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.UniqueKey.toString(),
                "Unique Key", "Global Object's Unique Key", this.getUniqueKey(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.UID.toString(),
                "Unique Uid", "Global Object's Unique Uid", this.getUniqueKey(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.DomainUID.toString(),
                "Domain Uid", "Global Object's Domain Uid", this.getDomainUid(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!this.getClass().toString().equalsIgnoreCase(o.getClass().toString()))
            return false;
        MetaDataObj metaObj = (MetaDataObj) o;
        return this.getConfig().equals(metaObj.getConfig()) &&
                classDefinitionUid.equals(metaObj.classDefinitionUid) &&
                objUid.equals(metaObj.objUid) &&
                getDomainUid().equalsIgnoreCase(metaObj.getDomainUid()) &&
                getObid().equalsIgnoreCase(metaObj.getObid()) && this.terminationComparison(metaObj);

    }

    @Override
    public int hashCode() {
        return Objects.hash(getConfig(), getObid(), classDefinitionUid, objUid, getDomainUid(), this.terminationDateString(), this.terminationUserString());
    }

    @Override
    public String toString() {
        return "Obj{" +
                "classDef='" + classDefinitionUid + '\'' +
                ", domainUid='" + domainUid + '\'' +
                ", objUid='" + objUid + '\'' +
                ", obid='" + getObid() + '\'' +
                ", config='" + getConfig() + '\'' + this.terminationInfo() +
                '}';
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
