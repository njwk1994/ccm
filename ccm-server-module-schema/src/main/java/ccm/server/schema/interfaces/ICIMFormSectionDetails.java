package ccm.server.schema.interfaces;

public interface ICIMFormSectionDetails extends IRel, ICIMRenderInfo {

    String EffectFormPurpose();

    void setEffectFormPurpose(String value) throws Exception;
}
