package ccm.server.model;

import lombok.Data;

@Data
public class KeyValuePair {
    private String key;
    private Object value;

    public KeyValuePair() {
        this.key = "";
        this.value = "";
    }

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValuePair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.join(":", new String[]{key, (value != null ? value.toString() : "")});
    }
}
