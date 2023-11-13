package ccm.server.propertyTypes;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Data
public class objectTypes {
    private String values;

    private final List<Object> types = new ArrayList<>();

    public objectTypes(String values) {
        super();
        this.values = values;
        this.onParse(values);
    }

    public boolean hasValue() {
        return this.types.size() > 0 && this.types.stream().noneMatch(Objects::isNull);
    }

    public Iterator<Object> GetEnumerator() {
        return this.types.iterator();
    }

    private void onParse(String values) {
        if (!StringUtils.isEmpty(values)) {
            String[] strings = values.split(",");
            this.types.addAll(Arrays.asList(strings));
        }
    }

    public objectTypes() {

    }

}
