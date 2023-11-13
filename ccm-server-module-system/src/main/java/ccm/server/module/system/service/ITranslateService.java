package ccm.server.module.system.service;

public interface ITranslateService {

    String getLocalization();

    void setLocalization(String localization);

    String getString(String key);
}
