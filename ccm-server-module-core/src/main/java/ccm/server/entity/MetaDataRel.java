package ccm.server.entity;

import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.objectUpdateState;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.util.CommonUtility;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xkcoding.http.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@ApiModel(description = "OBJECT RELATIONSHIP BASE ENTITY MODEL")
@TableName("OBJREL")
public class MetaDataRel extends MetaData implements Serializable {
    @Override
    public MetaData copy() {
        MetaDataRel metaDataRel = new MetaDataRel();
        metaDataRel.setObjUid(this.objUid);
        metaDataRel.setUid1(this.uid1);
        metaDataRel.setUid2(this.uid2);
        metaDataRel.setName2(this.name2);
        metaDataRel.setName1(this.name1);
        metaDataRel.setTerminationDate(this.getTerminationDate());
        metaDataRel.setTerminationUser(this.getTerminationUser());
        metaDataRel.setCreationDate(this.getCreationDate());
        metaDataRel.setCreationUser(this.getCreationUser());
        metaDataRel.setObid2(this.obid2);
        metaDataRel.setObid1(this.obid1);
        metaDataRel.setObid(this.getObid());
        metaDataRel.setRelDefUid(this.relDefUid);
        metaDataRel.setDomainUid(this.domainUid);
        metaDataRel.setDomainUid1(this.domainUid1);
        metaDataRel.setDomainUid2(this.domainUid2);
        metaDataRel.setClassDefinitionUid1(this.classDefinitionUid1);
        metaDataRel.setClassDefinitionUid2(this.classDefinitionUid2);
        metaDataRel.setUpdateState(this.updateState);
        metaDataRel.setIsRequired(this.isRequired);
        metaDataRel.setOrderValue(this.orderValue);
        metaDataRel.setConfig(this.getConfig());
        return metaDataRel;
    }

    public int getIsRequired() {
        if (this.isRequired == null)
            this.isRequired = false;
        return this.isRequired ? 1 : 0;
    }

    public void setIsRequired(Boolean value) {
        if (value == null)
            value = false;
        this.isRequired = value;
    }

    public void setDateForMeta(Date date) {
        if (date != null) {
            switch (this.updateState) {
                case created:
                case updated:
                    this.setCreationDate(date);
                    break;
                case terminated:
                    this.setTerminationDate(date);
                    break;
            }
        }
    }

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "左端对象ID", hidden = false)
    private String uid1 = "";

    @ApiModelProperty(value = "左端对象ID", hidden = false)
    private String obid1 = "";

    @ApiModelProperty(value = "关系标识", hidden = false)
    private String objUid = "";

    @ApiModelProperty(value = "所属域", hidden = false)
    private String domainUid = "";

    @ApiModelProperty(value = "一端类型定义", hidden = false)
    private String classDefinitionUid1 = "";

    @ApiModelProperty(value = "二端类型定义", hidden = false)
    private String classDefinitionUid2 = "";

    @ApiModelProperty(value = "右端对象ID", hidden = false)
    private String uid2 = "";
    @ApiModelProperty(value = "右端对象ID", hidden = false)
    private String obid2 = "";

    @ApiModelProperty(value = "关系类型", hidden = false)
    private String relDefUid = "";

    @ApiModelProperty(value = "一端对象所属域", hidden = false)
    private String domainUid1 = "";

    @ApiModelProperty(value = "二端对象所属域", hidden = false)
    private String domainUid2 = "";

    @ApiModelProperty(value = "左端对象名称", hidden = false)
    private String name1 = "";

    @ApiModelProperty(value = "右端对象名称", hidden = false)
    private String name2 = "";

    @ApiModelProperty(value = "关系唯一标识码前缀", hidden = false)
    private String prefix = "REL";

    @ApiModelProperty(value = "标识关系是否为必须的", hidden = false)
    private Boolean isRequired = false;

    @ApiModelProperty(value = "标识关系的序列", hidden = false)
    private Integer orderValue = -1;

    @TableField(exist = false)
    private objectUpdateState updateState;

    public void refreshUserInfo(String userName) {
        if (!StringUtils.isEmpty(userName)) {
            if (this.getUpdateState() == objectUpdateState.created) {
                this.setCreationUser(userName);
            } else if (this.getUpdateState() == objectUpdateState.terminated)
                this.setTerminationUser(userName);
            else if (this.getUpdateState() == objectUpdateState.updated)
                this.setCreationUser(userName);
        }
    }

    public Integer getOrderValue() {
        if (this.orderValue != null)
            return this.orderValue;
        return -1;
    }

    public MetaDataRel() {
        this.objUid = "";
    }

    public List<ObjectItemDTO> toObjectItemDTOs() {
        List<ObjectItemDTO> result = new ArrayList<>();
        result.add(new ObjectItemDTO(propertyDefinitionType.OBID.toString(), "Obid", "", this.getObid(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.UID1.toString(), "end1 Obid", "", this.getUid1(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.Name1.toString(), "end 1Name", "", this.getName1(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.RelDefUID.toString(), "rel Definition Uid", "", this.getRelDefUid(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.UID2.toString(), "end2 Obid", "", this.getUid2(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        result.add(new ObjectItemDTO(propertyDefinitionType.Name2.toString(), "end2 Name", "", this.getName2(), propertyValueType.StringType.toString(), CommonUtility.PROPERTY_GROUP_GENERAL, result.size() + 1, true));
        return result;
    }

    public String getObjUid() {
        if (this.objUid == null || StringUtils.isEmpty(this.objUid))
            this.objUid = String.join(".", new String[]{this.getPrefix(), this.getClassDefinitionUid1(), this.getUid1(), this.getClassDefinitionUid2(), this.getUid2(), this.relDefUid, this.getConfig()});
        return this.objUid;
    }

    public String getClassDefinitionUid2() {
        if (this.classDefinitionUid2 == null)
            this.classDefinitionUid2 = "";
        return this.classDefinitionUid2;
    }

    public String getClassDefinitionUid1() {
        if (this.classDefinitionUid1 == null)
            this.classDefinitionUid1 = "";
        return this.classDefinitionUid1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!this.getClass().toString().equalsIgnoreCase(o.getClass().toString()))
            return false;
        MetaDataRel metaRel = (MetaDataRel) o;
        return ((uid1.equals(metaRel.uid1) &&
                domainUid1.equals(metaRel.domainUid1) &&
                this.getClassDefinitionUid1().equalsIgnoreCase(metaRel.getClassDefinitionUid1()) &&
                uid2.equals(metaRel.uid2) &&
                this.getClassDefinitionUid2().equalsIgnoreCase(metaRel.getClassDefinitionUid2()) && domainUid2.equals(metaRel.domainUid2)) || (obid1.equals(metaRel.obid1) && obid2.equals(metaRel.obid2)))
                && this.terminationComparison(metaRel) &&
                relDefUid.equals(metaRel.relDefUid) &&
                getConfig().equalsIgnoreCase(metaRel.getConfig());
    }

    @Override
    public String toString() {
        return "rel{" +
                "uid1='" + uid1 + '\'' +
                ", uid2='" + uid2 + '\'' +
                ", relDefUid='" + relDefUid + '\'' +
                ", classDefinitionUid1='" + classDefinitionUid1 + '\'' +
                ", classDefinitionUid2='" + classDefinitionUid2 + '\'' +
                ", config='" + getConfig() + '\'' +
                ", obid1='" + obid1 + '\'' +
                ", obid2='" + obid2 + '\'' +
                ", domainUID1='" + domainUid1 + '\'' +
                ", domainUID2='" + domainUid2 + '\'' + this.terminationInfo() +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid1, uid2, relDefUid, classDefinitionUid1, classDefinitionUid2, this.terminationDateString(), this.terminationUserString(), this.getConfig(), obid1, obid2);
    }

    @Override
    public void selfCheck() throws Exception {
        if (StringUtil.isEmpty(getUid1()) || StringUtils.isEmpty(getObid1()) || StringUtils.isEmpty(getClassDefinitionUid1()))
            throw new Exception("invalid end1 obid as it is null");

        if (StringUtil.isEmpty(getUid2()) || StringUtils.isEmpty(getObid2()) || StringUtils.isEmpty(getClassDefinitionUid2()))
            throw new Exception("invalid end2 obid as it is null");

        if (StringUtil.isEmpty(relDefUid))
            throw new Exception("invalid  rel definition uid as it is null");
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(this.getUid1()) &&
                !StringUtils.isEmpty(this.getUid2()) &&
                !StringUtils.isEmpty(this.getRelDefUid()) &&
                !StringUtils.isEmpty(this.getObid2()) &&
                !StringUtils.isEmpty(this.getObid1()) &&
                !StringUtils.isEmpty(this.getDomainUid1()) &&
                !StringUtils.isEmpty(this.getDomainUid2()) &&
                !StringUtils.isEmpty(this.getClassDefinitionUid1()) &&
                !StringUtils.isEmpty(this.getClassDefinitionUid2());
    }

    public static Map<String, MetaDataRel> mapByKey(List<MetaDataRel> rels) {
        if (CommonUtility.hasValue(rels)) {
            Map<String, MetaDataRel> result = new HashMap<>();
            for (MetaDataRel c : rels) {
                if (result.put(c.getUniqueIdentity(), c) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
            return result;
        }
        return null;
    }

    public static List<MetaDataRel> filter(List<MetaDataRel> rels, String objUid) {
        if (CommonUtility.hasValue(rels) && !StringUtils.isEmpty(objUid))
            return rels.stream().filter(c -> c.getUid1().equalsIgnoreCase(objUid) || c.getUid2().equalsIgnoreCase(objUid)).collect(Collectors.toList());
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
