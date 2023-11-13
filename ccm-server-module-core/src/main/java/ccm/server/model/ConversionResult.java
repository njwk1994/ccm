package ccm.server.model;

import lombok.Data;

@Data
public class ConversionResult<T> {
    private Boolean result = false;
    private T value;
}