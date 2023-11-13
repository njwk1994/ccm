package ccm.server.enums;

public enum joinMode {
    Left(" LEFT JOIN "),
    Inner(" INNER JOIN "),
    Right(" RIGHT JOIN ");

    private String sqlPart;

    joinMode(String sqlPart) {
        this.sqlPart = sqlPart;
    }

    public String getSqlPart() {
        return this.sqlPart;
    }

    private void setSqlPart(String part) {
        this.sqlPart = part;
    }
}
