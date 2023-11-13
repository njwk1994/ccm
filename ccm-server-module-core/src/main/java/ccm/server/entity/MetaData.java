package ccm.server.entity;

import ccm.server.util.CommonUtility;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
@TableName("META")
public class MetaData implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(exist = false)
    private String tablePrefix;

    public void setDateForMeta(Date date) {
    }

    public boolean terminationComparison(MetaData metaData) {
        return this.terminationDateString().equalsIgnoreCase(metaData.terminationDateString()) &&
                this.terminationUserString().equalsIgnoreCase(metaData.terminationUserString());
    }

    protected String terminationDateString() {
        return CommonUtility.valueToString(this.getTerminationDate());
    }

    protected String terminationUserString() {
        return CommonUtility.valueToString(this.getTerminationUser());
    }

    public List<String> UniqueTablePrefix() {
        return this.tablePrefixes.stream().distinct().collect(Collectors.toList());
    }

    public MetaData copy() {
        return null;
    }

    public <T extends MetaData> T copy(Class<T> classz) {
        MetaData metaData = this.copy();
        if (metaData != null)
            return (T) metaData;
        return null;
    }

    @TableField(exist = false)
    private final List<String> tablePrefixes = new ArrayList<>();

    public void refreshUserInfo(String userName) {

    }

    public void setTablePrefix(String value) {
        if (!this.tablePrefixes.contains(value))
            this.tablePrefixes.add(value);
        this.tablePrefix = value;
    }

    public String terminationInfo() {
        return ", terminationUser='" + CommonUtility.valueToString(this.getTerminationUser()) + '\'' +
                ", terminationDate='" + CommonUtility.valueToString(this.getTerminationDate()) + '\'';
    }

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期", hidden = true, required = true)
    private Date creationDate;

    @ApiModelProperty(value = "创建人", hidden = false, required = true)
    private String creationUser;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "终结日期", hidden = true, required = false)
    private Date terminationDate = null;

    @ApiModelProperty(value = "终结人", hidden = true, required = false)
    private String terminationUser = "";

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "对象系统主键，唯一标识码")
    private String obid;

    public String getObid() {
        if (this.obid == null)
            this.obid = "";
        return this.obid;
    }

    @ApiModelProperty(value = "项目/工厂", required = false, hidden = false)
    private String config;

    public String getConfig() {
        if (this.config == null)
            this.config = "";
        return this.config;
    }

    @TableField(exist = false)
    private final Map<String, MetaDataObjInterface> interfaceMap = new HashMap<>();

    @TableField(exist = false)
    private final Map<String, MetaDataObjProperty> propertyMap = new HashMap<>();

    public boolean fromDb() {
        return false;
    }

    public String getPrimaryKey() {
        return this.getObid();
    }

    public void selfCheck() throws Exception {

    }

    public void setPrimaryKey(String primaryKey) {

    }

    public String getUniqueIdentity() {
        return this.toString();
    }

    public Date getTerminationDate() {
        if (null == this.terminationDate) {
            LocalDateTime dateTime = LocalDateTime.parse("9999-12-31 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = dateTime.atZone(zone).toInstant();
            this.terminationDate = Date.from(instant);
        }
        return terminationDate;
    }
}
