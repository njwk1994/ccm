package ccm.server.executors;

import ccm.server.business.IDataProviderService;
import ccm.server.entity.MetaDataObjProperty;
import ccm.server.model.PropertySummaryVo;
import ccm.server.schema.interfaces.IDomain;
import ccm.server.util.ISharedCommon;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Data
public class PropertySummaryExecutor implements Callable<PropertySummaryVo> {
    private final String propertyDefinitionUid;
    private final List<String> tablePrefixes;
    private int level;
    private final IDataProviderService dataProviderService;

    public PropertySummaryExecutor(IDataProviderService dataProviderService, String propertyDefinitionUid, String configurationPrefix, List<IDomain> domains, int level) {
        this.dataProviderService = dataProviderService;
        this.propertyDefinitionUid = propertyDefinitionUid;
        this.level = level;
        if (domains != null && domains.size() > 0) {
            List<String> temp = new ArrayList<>();
            for (IDomain domain : domains) {
                String currentDomainPrefix = domain.TablePrefix();
                if (!StringUtils.isEmpty(currentDomainPrefix)) {
                    if (domain.ScopeWiseInd()) {
                        temp.add(configurationPrefix + ISharedCommon.COMMON_CONNECTOR + domain.TablePrefix());
                    } else
                        temp.add(domain.TablePrefix());
                }
            }
            this.tablePrefixes = temp;
        } else
            this.tablePrefixes = null;
        if (this.dataProviderService == null)
            throw new IllegalArgumentException("invalid data service as it is null");
        if (StringUtils.isEmpty(propertyDefinitionUid))
            throw new IllegalArgumentException("invalid property definition uid as it is null");
    }

    @Override
    public PropertySummaryVo call() throws Exception {
        PropertySummaryVo result = new PropertySummaryVo(this.propertyDefinitionUid, this.level);
        List<MetaDataObjProperty> propertySummary = this.dataProviderService.getPropertySummary(this.propertyDefinitionUid, this.tablePrefixes);
        if (propertySummary != null && propertySummary.size() > 0)
            result.getDataProperties().addAll(propertySummary);
        return result;
    }
}
