package ccm.server.schema.model;

public class ClassDefault extends ClassBase {
    public ClassDefault(boolean instantiateRequiredItems) throws Exception {
        super(instantiateRequiredItems);
    }

    public ClassDefault() throws Exception {
        super(true);
    }
}
