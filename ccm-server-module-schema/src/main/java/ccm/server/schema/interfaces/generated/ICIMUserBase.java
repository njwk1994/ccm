package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.KeyValuePair;
import ccm.server.models.LiteObject;
import ccm.server.models.scope.ScopeConfiguration;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.ICIMUser;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class ICIMUserBase extends InterfaceDefault implements ICIMUser {
    public ICIMUserBase(boolean instantiateRequiredProperties) {
        super(ICIMUser.class.getSimpleName(), instantiateRequiredProperties);
    }

    public final static String RELDEF_USER_DEFAULT_INFO = "User2DefaultInfo";

    @Override
    public IObjectCollection getDefaultInfo() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(RELDEF_USER_DEFAULT_INFO, false);
        if (relCollection != null) {
            return relCollection.GetEnd2s();
        }
        return null;
    }

    @Override
    public List<KeyValuePair> getUserDefaultInfo() throws Exception {
        List<KeyValuePair> keyValuePairs = new ArrayList<>();
        ICIMConfigurationItem createConfig = this.getCreateConfig(false);
        if (createConfig == null) {
            IObjectCollection configurationItems = CIMContext.Instance.getConfigurationItems();
            if (configurationItems != null && configurationItems.hasValue()) {
                createConfig = configurationItems.get(0).toInterface(ICIMConfigurationItem.class);
                this.changeConfig(createConfig, null);
            }
        }
        keyValuePairs.add(new KeyValuePair("createConfig", createConfig != null ? createConfig.toObjectDTO() : ""));
        return keyValuePairs;
    }

    @Override
    public void saveScope(IObject createConfig, IObjectCollection queryConfigs) throws Exception {
        this.changeConfig(createConfig, queryConfigs);
    }

    @Override
    public void saveScope(ScopeConfiguration scopeConfiguration) throws Exception {
        if (scopeConfiguration == null)
            this.clearScope();
        else {
            LiteObject createConfig = scopeConfiguration.getCreateConfig();
            List<LiteObject> queryConfigs = scopeConfiguration.getQueryConfigs();
            IObject lobjCreateConfig = this.parseToIObject(createConfig);
            IObjectCollection lcolQueryConfig = this.parseToIObjectCollection(queryConfigs);
            this.changeConfig(lobjCreateConfig, lcolQueryConfig);
        }
    }

    protected IObject parseToIObject(LiteObject liteObject) throws Exception {
        if (liteObject != null) {
            return CIMContext.Instance.getObjectConversion().convert(liteObject);
        }
        return null;
    }

    protected IObjectCollection parseToIObjectCollection(List<LiteObject> liteObjects) throws Exception {
        if (liteObjects != null) {
            return CIMContext.Instance.getObjectConversion().convert(liteObjects);
        }
        return null;
    }

    @Override
    public void clearScope() throws Exception {
        this.changeConfig(null, null);
    }

    @Override
    public ScopeConfiguration getCurrentScope() throws Exception {
        ICIMConfigurationItem createConfig = this.getCreateConfig(false);
        IObjectCollection queryConfig = this.getQueryConfig();
        ScopeConfiguration scopeConfiguration = new ScopeConfiguration();
        if (createConfig == null) {
            scopeConfiguration.setCreateConfig(ScopeConfiguration.getDefaultScopedNotSet());
        } else
            scopeConfiguration.setCreateConfig(CIMContext.Instance.getObjectConversion().convertToLiteObject(createConfig));
        return scopeConfiguration;
    }

    @Override
    public ICIMConfigurationItem getCreateConfig(boolean cacheOnly) throws Exception {
        IRel rel = this.GetEnd1Relationships().GetRel(HardCodeHelper.RELDEF_USER_TO_CREATE_SCOPE, cacheOnly);
        if (rel != null) {
            IObject iObject = rel.GetEnd2();
            if (iObject != null) {
                return iObject.toInterface(ICIMConfigurationItem.class);
            }
        }
        return null;
    }

    @Override
    public IObjectCollection getQueryConfig() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(HardCodeHelper.RELDEF_USER_TO_QUERY_SCOPE, false);
        if (relCollection != null) {
            return relCollection.GetEnd2s();
        }
        return null;
    }

    protected ScopeConfiguration generateScopeConfiguration(IObject createConfig, IObjectCollection queryConfigs) throws Exception {
        if (queryConfigs == null || queryConfigs.size() == 0) {
            queryConfigs = new ObjectCollection();
            if (createConfig != null)
                queryConfigs.append(createConfig);
        }
        ScopeConfiguration scopeConfiguration = new ScopeConfiguration();
        if (createConfig != null)
            scopeConfiguration.setCreateConfig(CIMContext.Instance.getObjectConversion().convertToLiteObject(createConfig));
        else
            scopeConfiguration.setCreateConfig(ScopeConfiguration.getDefaultScopedNotSet());

        scopeConfiguration.addQueryConfig(queryConfigs.toList().stream().filter(Objects::nonNull).map(c -> {
            try {
                return CIMContext.Instance.getObjectConversion().convertToLiteObject(c);
            } catch (Exception exception) {
                log.error("convert IObject to lite object failed", exception);
                return ScopeConfiguration.getDefaultScopedNotSet();
            }
        }).collect(Collectors.toList()));
        return scopeConfiguration;
    }

    protected void setCreateConfig(IObject configurationItem) throws Exception {
        IRelCollection rels = this.GetEnd1Relationships().GetRels(HardCodeHelper.RELDEF_USER_TO_CREATE_SCOPE, false);
        if (rels != null) {
            if (configurationItem != null) {
                boolean flag = true;
                Iterator<IObject> iObjectIterator = rels.GetEnumerator();
                while (iObjectIterator.hasNext()) {
                    IRel rel = iObjectIterator.next().toInterface(IRel.class);
                    if (!rel.OBID2().equalsIgnoreCase(configurationItem.OBID())) {
                        rels.Delete();
                    } else
                        flag = false;
                }
                if (flag) {
                    IRel rel1 = SchemaUtility.newRelationship(HardCodeHelper.RELDEF_USER_TO_CREATE_SCOPE, this, configurationItem, true);
                    rel1.ClassDefinition().FinishCreate(rel1);
                }
            } else
                rels.Delete();
        } else {
            if (configurationItem != null) {
                IRel rel1 = SchemaUtility.newRelationship(HardCodeHelper.RELDEF_USER_TO_CREATE_SCOPE, this, configurationItem, true);
                rel1.ClassDefinition().FinishCreate(rel1);
            }
        }
    }

    protected void setQueryConfig(IObjectCollection configs) throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(HardCodeHelper.RELDEF_USER_TO_QUERY_SCOPE, false);
        if (configs == null)
            configs = new ObjectCollection();
        List<String> obids = configs.listOfOBID();
        if (relCollection != null && relCollection.size() > 0) {
            Iterator<IObject> iterator = relCollection.GetEnumerator();
            while (iterator.hasNext()) {
                IRel rel1 = iterator.next().toInterface(IRel.class);
                String linkObid = rel1.OBID2();
                if (obids.stream().noneMatch(c -> c.equalsIgnoreCase(linkObid)))
                    rel1.Delete();
                else
                    configs.remove(linkObid);
            }
        }
        if (configs.size() > 0) {
            Iterator<IObject> iterator = configs.GetEnumerator();
            while (iterator.hasNext()) {
                IObject value = iterator.next();
                IRel rel1 = SchemaUtility.newRelationship(HardCodeHelper.RELDEF_USER_TO_QUERY_SCOPE, this, value, true);
                rel1.ClassDefinition().FinishCreate(rel1);
            }
        }
    }

    protected void changeConfig(IObject createConfigurationItem, IObjectCollection queryConfigs) {
        if (queryConfigs == null || queryConfigs.size() == 0) {
            queryConfigs = new ObjectCollection();
            if (createConfigurationItem != null)
                queryConfigs.append(createConfigurationItem);
        }
        try {
            CIMContext.Instance.Transaction().start();
            this.setCreateConfig(createConfigurationItem);
            this.setQueryConfig(queryConfigs);
            CIMContext.Instance.Transaction().commit();
        } catch (Exception exception) {
            log.error("change scope failed", exception);
            CIMContext.Instance.Transaction().rollBack();
        }
    }
}
