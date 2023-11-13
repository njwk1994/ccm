package ccm.server.enums;

public enum ExpansionMode {
    none(classDefinitionType.PropertyDef),
    relationship(classDefinitionType.Rel),
    relatedObject(classDefinitionType.RelDef);

    private final classDefinitionType linkedType;

    private ExpansionMode(classDefinitionType classDefinitionType) {
        this.linkedType = classDefinitionType;
    }

    public classDefinitionType getLinkedType() {
        return this.linkedType;
    }
}
