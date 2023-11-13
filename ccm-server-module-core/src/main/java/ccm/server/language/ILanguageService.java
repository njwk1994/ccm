package ccm.server.language;

import ccm.server.module.service.base.IUtilityService;

public interface ILanguageService extends IUtilityService {

    String getCurrentLocalization();

    void amount();

    String get(Integer code);

    void add(Integer code, String local, String value);

    void remove(Integer code);

    void save();
}
