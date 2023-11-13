package ccm.server.enums;

public enum operator {
    equal(" = "),
    largeThan(" > "),
    lessThan(" < "),
    lessOrEqualThan(" <= "),
    largeOrEqualThan(" >= "),
    notEqual(" <> "),
    in(" IN "),
    notIn(" NOT IN "),
    between(" BETWEEN "),
    notBetween(" NOT BETWEEN "),

    like(" LIKE "),

    notLike(" NOT LIKE "),

    _is(" IS "),
    _isNOT(" IS NOT ");

    private String actualOperator;

    operator(String pstrOperator) {
        this.setActualOperator(pstrOperator);
    }

    public static boolean leftJoinIndicator(operator operator) {
        if (operator != null) {
            switch (operator) {
                case notIn:
                case notLike:
                case notEqual:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public static operator ValueOf(String actualOperator) {
        for (operator o : operator.values()
        ) {
            if (o.getActualOperator().equalsIgnoreCase(actualOperator))
                return o;
        }
        return null;
    }

    public String getActualOperator() {
        return this.actualOperator;
    }

    private void setActualOperator(String pstrActualOperator) {
        this.actualOperator = pstrActualOperator;
    }
}
