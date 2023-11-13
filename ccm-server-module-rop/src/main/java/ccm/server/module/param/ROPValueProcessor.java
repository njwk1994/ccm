package ccm.server.module.param;

import ccm.server.enums.operator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class ROPValueProcessor {

    @Data
    public static class ROPValueOperator {
        private operator operator;
        private Object value;

        public ROPValueOperator(operator operator, Object object) {
            this.operator = operator;
            this.value = object;
        }

        public boolean isHint(Object v) {
            switch (this.operator) {
                case equal:
                    if (v == null && this.value == null)
                        return true;
                    else if (v == null || this.value == null)
                        return false;
                    else if (v.toString().equalsIgnoreCase(this.value.toString()))
                        return true;
                    else {
                        try {
                            double double1 = Double.parseDouble(v.toString());
                            double double2 = Double.parseDouble(this.value.toString());
                            return double1 == double2;
                        } catch (Exception exception) {
                            log.error("parse number format failed", exception);
                            // 2022.08.08 HT 规则组匹配失败问题修复 数字比对失败时如果两个String值不等返回false
                            if (!v.toString().equalsIgnoreCase(this.value.toString())) {
                                return false;
                            }
                            // 2022.08.08 HT 规则组匹配失败问题修复 数字比对失败时如果两个String值不等返回false
                        }
                    }
                    break;
                case lessThan:
                    try {
                        double double1 = Double.parseDouble(v.toString());
                        double double2 = Double.parseDouble(this.value.toString());
                        return double1 < double2;
                    } catch (Exception exception) {
                        log.error("parse number format failed", exception);
                    }
                    break;
                case largeThan:
                    try {
                        double double1 = Double.parseDouble(v.toString());
                        double double2 = Double.parseDouble(this.value.toString());
                        return double1 > double2;
                    } catch (Exception exception) {
                        log.error("parse number format failed", exception);
                    }
                    break;
                case lessOrEqualThan:
                    try {
                        double double1 = Double.parseDouble(v.toString());
                        double double2 = Double.parseDouble(this.value.toString());
                        return double1 <= double2;
                    } catch (Exception exception) {
                        log.error("parse number format failed", exception);
                    }
                    break;
                case largeOrEqualThan:
                    try {
                        double double1 = Double.parseDouble(v.toString());
                        double double2 = Double.parseDouble(this.value.toString());
                        return double1 >= double2;
                    } catch (Exception exception) {
                        log.error("parse number format failed", exception);
                    }
                    break;
                case in:
                    if (this.value != null && v != null) {
                        return Arrays.stream(this.value.toString().split(",")).anyMatch(c -> c.equalsIgnoreCase(v.toString()));
                    }
                    break;
            }
            return false;
        }

        @Override
        public String toString() {
            return this.operator.getActualOperator() + this.value;
        }
    }

    private final List<ROPValueOperator> operators = new ArrayList<>();
    private String valueCriteria;

    public ROPValueProcessor(String valueCriteria) throws Exception {
        this.valueCriteria = valueCriteria;
        this.doInit();
    }

    @Override
    public String toString() {
        return this.operators.stream().map(ROPValueOperator::toString).collect(Collectors.joining("\r\n"));
    }

    public boolean isHint(Object value) {
        boolean result = true;
        for (ROPValueOperator operator : this.operators) {
            boolean hint = operator.isHint(value);
            if (!hint) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void doInit() throws Exception {
        if (!StringUtils.isEmpty(this.valueCriteria)) {
            if (this.valueCriteria.startsWith("{") && this.valueCriteria.endsWith("}")) {
                String inValue = this.valueCriteria.substring(this.valueCriteria.indexOf("{") + 1, this.valueCriteria.lastIndexOf("}")).replace("|", ",");
                this.operators.add(new ROPValueOperator(operator.in, inValue));
            } else if (this.valueCriteria.startsWith("(") && this.valueCriteria.endsWith(")")) {
                String rangeValue = this.valueCriteria.substring(this.valueCriteria.indexOf("(") + 1, this.valueCriteria.lastIndexOf(")"));
                String[] strings = rangeValue.split(",");
                if (strings.length != 2)
                    throw new Exception("invalid value criteria " + this.valueCriteria + " as         is invalid");
                if (!StringUtils.isEmpty(strings[0]))
                    this.operators.add(new ROPValueOperator(operator.largeThan, strings[0]));
                if (!StringUtils.isEmpty(strings[1]))
                    this.operators.add(new ROPValueOperator(operator.lessThan, strings[1]));
            } else if (this.valueCriteria.startsWith("(") && this.valueCriteria.endsWith("]")) {
                String rangeValue = this.valueCriteria.substring(this.valueCriteria.indexOf("(") + 1, this.valueCriteria.lastIndexOf("]"));
                String[] strings = rangeValue.split(",");
                if (strings.length != 2)
                    throw new Exception("invalid value criteria " + this.valueCriteria + " as it is invalid");
                if (!StringUtils.isEmpty(strings[0]))
                    this.operators.add(new ROPValueOperator(operator.largeThan, strings[0]));
                if (!StringUtils.isEmpty(strings[1]))
                    this.operators.add(new ROPValueOperator(operator.lessOrEqualThan, strings[1]));
            } else if (this.valueCriteria.startsWith("[") && this.valueCriteria.endsWith(")")) {
                String rangeValue = this.valueCriteria.substring(this.valueCriteria.indexOf("[") + 1, this.valueCriteria.lastIndexOf(")"));
                String[] strings = rangeValue.split(",");
                if (strings.length != 2)
                    throw new Exception("invalid value criteria " + this.valueCriteria + " as it is invalid");
                if (!StringUtils.isEmpty(strings[0]))
                    this.operators.add(new ROPValueOperator(operator.largeOrEqualThan, strings[0]));
                if (!StringUtils.isEmpty(strings[1]))
                    this.operators.add(new ROPValueOperator(operator.lessThan, strings[1]));
            } else if (this.valueCriteria.startsWith("[") && this.valueCriteria.endsWith("]")) {
                String rangeValue = this.valueCriteria.substring(this.valueCriteria.indexOf("[") + 1, this.valueCriteria.lastIndexOf("]"));
                String[] strings = rangeValue.split(",");
                if (strings.length != 2)
                    throw new Exception("invalid value criteria " + this.valueCriteria + " as it is invalid");
                if (!StringUtils.isEmpty(strings[0]))
                    this.operators.add(new ROPValueOperator(operator.largeOrEqualThan, strings[0]));
                if (!StringUtils.isEmpty(strings[1]))
                    this.operators.add(new ROPValueOperator(operator.lessOrEqualThan, strings[1]));
            } else
                this.operators.add(new ROPValueOperator(operator.equal, this.valueCriteria));
        }
    }
}
