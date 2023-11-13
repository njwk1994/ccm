package ccm.server.entity;

import ccm.server.enums.interfaceUpdateState;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@ApiModel(description = "MetaDataObjInterface BASE ENTITY MODEL")
@Slf4j
@TableName("OBJIF")
public class MetaDataObjInterface extends MetaData implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "接口名称")
    private String interfaceDefUid;
    @ApiModelProperty(value = "所属对象Id")
    private String objObid;

    public void setDateForMeta(Date date) {
        if (date != null) {
            switch (this.updateState) {
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
            if (this.updateState == interfaceUpdateState.created) {
                this.setCreationUser(userName);
            } else if (this.updateState == interfaceUpdateState.revive)
                this.setCreationUser(userName);
            else if (this.updateState == interfaceUpdateState.terminated)
                this.setTerminationUser(userName);
        }
    }

    @Override
    public MetaData copy() {
        MetaDataObjInterface metaDataObjInterface = new MetaDataObjInterface();
        metaDataObjInterface.setInterfaceDefUid(this.interfaceDefUid);
        metaDataObjInterface.setObjObid(this.objObid);
        metaDataObjInterface.setObid(this.getObid());
        metaDataObjInterface.setUpdateState(this.updateState);
        metaDataObjInterface.setTerminationUser(this.getTerminationUser());
        metaDataObjInterface.setTerminationDate(this.getTerminationDate());
        metaDataObjInterface.setCreationUser(this.getCreationUser());
        metaDataObjInterface.setCreationDate(this.getCreationDate());
        metaDataObjInterface.setConfig(this.getConfig());
        return metaDataObjInterface;
    }

    public MetaDataObjInterface() {

    }

    public MetaDataObjInterface(String objObid) {
        this.setObjObid(objObid);
    }

    @TableField(exist = false)
    private interfaceUpdateState updateState;

    @Override
    public void selfCheck() throws Exception {
        if (StringUtils.isEmpty(this.getInterfaceDefUid()))
            throw new Exception("invalid interface definition uid as it is null");
        if (StringUtils.isEmpty(this.getObjObid()))
            throw new Exception("invalid object obid as it is null");
    }

    @Override
    public String toString() {
        return "objInterface{" +
                   "interfaceDefUid='" + interfaceDefUid + '\'' +
                   ", objObid='" + objObid + '\'' + this.terminationInfo() +
                   '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!this.getClass().toString().equalsIgnoreCase(o.getClass().toString()))
            return false;
        MetaDataObjInterface that = (MetaDataObjInterface) o;
        return interfaceDefUid.equals(that.interfaceDefUid) &&
                   objObid.equals(that.objObid) && this.terminationComparison(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interfaceDefUid, objObid, this.terminationDateString(), this.terminationUserString());
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
