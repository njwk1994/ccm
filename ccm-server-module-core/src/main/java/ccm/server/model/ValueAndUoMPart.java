package ccm.server.model;

import lombok.Data;

@Data
public class ValueAndUoMPart {
    private String valuePart;
    private String uomPart;
    private String unParsedPart;
    public static ValueAndUoMPart start() {
        return new ValueAndUoMPart();
    }
}
