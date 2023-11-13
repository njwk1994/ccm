package ccm.server.enums;

import com.xkcoding.http.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum relEndQuantity {
    _0(0),
    _1(1),
    _M(Integer.MAX_VALUE);

    private Integer Target;
    private String uid;

    relEndQuantity(Integer target) {
        this.Target = target;
        this.setUid("relEndQuantity_" + this.toString());
    }

    public String getUid() {
        return this.uid;
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getTarget() {
        return this.Target;
    }

    public void setTarget(Integer target) {
        this.Target = target;
    }

    public static relEndQuantity toEnum(String value) {
        relEndQuantity result = null;
        if (!StringUtil.isEmpty(value)) {
            if (value.startsWith("relEndQuantity_"))
                value = value.replace("relEndQuantity_", "");
            try {
                result = relEndQuantity.valueOf(value);
            } catch (Exception exception) {
                log.error("convert to rel end quantity enum failed", exception);
            }
        }
        return result;
    }
}
