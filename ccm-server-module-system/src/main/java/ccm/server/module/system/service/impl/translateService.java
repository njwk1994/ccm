package ccm.server.module.system.service.impl;

import ccm.server.module.system.service.ITranslateService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class translateService implements ITranslateService {
    private String localization;

    @Override
    public String getLocalization() {
        return null;
    }

    @Override
    public void setLocalization(String localization) {
        this.localization = localization;
        if (!StringUtils.isEmpty(this.localization)) {
            if (this.localization.equalsIgnoreCase("zh")) {

            } else if (this.localization.equalsIgnoreCase("en")) {

            }
        }
    }

    @Override
    public String getString(String key) {
        return null;
    }
}
