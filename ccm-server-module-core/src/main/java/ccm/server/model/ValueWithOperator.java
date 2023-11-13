package ccm.server.model;

import ccm.server.comparator.impl.ValueComparatorService;
import ccm.server.enums.operator;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Data
@Slf4j
public class ValueWithOperator {
    private String originalValue;
    private boolean fitAll = false;
    private operator operator = ccm.server.enums.operator.equal;
    private String valuePattern = "";

    public ValueWithOperator(String value) {
        this.originalValue = value;
        this.init();
    }

    private String[] array() {
        return CommonUtility.valueArray(this.valuePattern);
    }

    private Object[] array2() {
        return CommonUtility.valueArrayWith2Elements(this.valuePattern);
    }

    public boolean inHint(Object value) {
        boolean result = false;
        String stringValue = CommonUtility.valueToString(value);
        switch (this.operator) {
            case in:
                result = Arrays.stream(array()).anyMatch(c -> c.equalsIgnoreCase(stringValue));
                break;
            case notIn:
                result = Arrays.stream(array()).noneMatch(c -> c.equalsIgnoreCase(stringValue));
                break;
            case largeOrEqualThan:
                result = ValueComparatorService.Instance.largeThan(value, this.valuePattern, true);
                break;
            case lessOrEqualThan:
                result = ValueComparatorService.Instance.lessThan(value, this.valuePattern, true);
                break;
            case largeThan:
                result = ValueComparatorService.Instance.largeThan(value, this.valuePattern, false);
                break;
            case lessThan:
                result = ValueComparatorService.Instance.lessThan(value, this.valuePattern, false);
                break;
            case notEqual:
                result = !ValueComparatorService.Instance.equal(value, this.valuePattern);
                break;
            case between:
                Object[] array2 = array2();
                result = ValueComparatorService.Instance.between(value, array2[0], array2[1]);
                break;
            case like:
                result = ValueComparatorService.Instance.like(value, this.valuePattern);
                break;
            case _is:
                result = value == null || StringUtils.isEmpty(value.toString());
                break;
            case _isNOT:
                result = value != null;
                break;
            case notLike:
                result = !ValueComparatorService.Instance.like(value, this.valuePattern);
                break;
            case equal:
                result = ValueComparatorService.Instance.equal(value, this.valuePattern);
                break;
            case notBetween:
                Object[] array12 = array2();
                result = !ValueComparatorService.Instance.between(value, array12[0], array12[1]);
        }
        return result;
    }

    private void  init() {
        if (this.originalValue != null && !StringUtils.isEmpty(this.originalValue)) {
            if (StringUtils.isEmpty(this.originalValue.replace("*", "").trim()))
                this.fitAll = true;
            else {
                if (this.originalValue.startsWith("!")) {
                    this.operator = ccm.server.enums.operator.notEqual;
                    this.valuePattern = this.originalValue.substring(1);
                } else
                    this.valuePattern = this.originalValue;

                //in with ()
                //between with []
                //large with > >=
                //less with < <=
                //like with *
                //is with _
                if (this.valuePattern.contains("*")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.notLike;
                    else
                        this.operator = ccm.server.enums.operator.like;
                    this.valuePattern = CommonUtility.replaceValueFromSTARIntoPERCENT(this.valuePattern);
                } else if (this.valuePattern.startsWith("(") && this.valuePattern.endsWith(")")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.notIn;
                    else
                        this.operator = ccm.server.enums.operator.in;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf("(") + 1, this.valuePattern.lastIndexOf(")"));
                } else if (this.valuePattern.startsWith("[") && this.valuePattern.endsWith("]")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.notBetween;
                    else
                        this.operator = ccm.server.enums.operator.between;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf("[") + 1, this.valuePattern.lastIndexOf("]"));
                } else if (this.valuePattern.startsWith(">")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.lessOrEqualThan;
                    else
                        this.operator = ccm.server.enums.operator.largeThan;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf(">") + 1);
                } else if (this.valuePattern.startsWith(">=")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.lessThan;
                    else
                        this.operator = ccm.server.enums.operator.largeOrEqualThan;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf(">=") + 2);
                } else if (this.valuePattern.startsWith("<")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.largeOrEqualThan;
                    else
                        this.operator = ccm.server.enums.operator.lessThan;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf("<") + 1);
                } else if (this.valuePattern.startsWith("<=")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator.largeThan;
                    else
                        this.operator = ccm.server.enums.operator.lessOrEqualThan;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf("<=") + 2);
                } else if (this.valuePattern.startsWith("_")) {
                    if (this.operator == ccm.server.enums.operator.notEqual)
                        this.operator = ccm.server.enums.operator._isNOT;
                    else
                        this.operator = ccm.server.enums.operator._is;
                    this.valuePattern = this.valuePattern.substring(this.valuePattern.indexOf("_") + 1);
                }
            }
        }
    }
}
