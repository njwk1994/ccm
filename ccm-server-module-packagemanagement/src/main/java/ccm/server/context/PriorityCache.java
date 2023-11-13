package ccm.server.context;

import ccm.server.cache.ICache;
import ccm.server.cache.impl.CacheBase;
import ccm.server.enums.classDefinitionType;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICCMPriority;
import ccm.server.schema.interfaces.IEnumListType;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.PackagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("priorityCache")
@Slf4j
public class PriorityCache extends CacheBase {

    public static final String CLASSDEF_PRIORITY = "CCMPriority";
    public static final String CLASSDEF_PRIORITY_ITEM = "CCMPriorityItem";
    public static final String RELDEF_PRIORITY2ITEM = "CCMPriority2Item";

    private final List<String> partialCachedClassDefs = new ArrayList<String>() {{
        this.add(CLASSDEF_PRIORITY);
        this.add(CLASSDEF_PRIORITY_ITEM);
    }};

    private final List<String> partialCachedRelDefs = new ArrayList<String>() {
        {
            this.add(RELDEF_PRIORITY2ITEM);
        }
    };


    public ICCMPriority getPriorityByOBID(String obid) {
        if (!StringUtils.isEmpty(obid)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, obid);
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, CLASSDEF_PRIORITY);
            IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            return object != null ? object.toInterface(ICCMPriority.class) : null;
        }
        return null;
    }

    public String getPurposeForCaseOfNotEntryUID(String purpose) throws Exception {
        if (!StringUtils.isEmpty(purpose)) {
            IObject scopedBy = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(PackagesUtils.PROPERTY_PURPOSE);
            if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListType.toString())) {
                IObjectCollection entries = scopedBy.toInterface(IEnumListType.class).getEntries();
                if (entries != null && entries.hasValue()) {
                    Iterator<IObject> iObjectIterator = entries.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IObject entry = iObjectIterator.next();
                        String displayAs = entry.Name() + (!StringUtils.isEmpty(entry.Description()) ? "," + entry.Description() : "");
                        if (entry.Name().equalsIgnoreCase(purpose) || entry.UID().equalsIgnoreCase(purpose) || purpose.equalsIgnoreCase(displayAs)) {
                            return entry.UID();
                        }
                    }
                }
            } else if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.EnumListLevelType.toString())) {

            }
        }
        return purpose;
    }

    public static PriorityCache Instance;

    @Override
    public void onInitializing() throws Exception {
        IObjectCollection objects = this.queryObjectsByClassDefinitions(partialCachedClassDefs);
        IObjectCollection relationships = this.queryRelsByRelDefs(partialCachedRelDefs);
        this.members().get(0).addLocally(this.identity(), objects);
        this.members().get(0).addLocally(this.identity(), relationships);
    }

    @Override
    public void setScopePrefixForQueryRequestHandler(QueryRequest queryRequest) {
        super.setScopePrefixForQueryRequestHandler(queryRequest);
        if (queryRequest != null) {
            this.setScopePrefixForQueryRequest(this.getObjectsByClassDefCache(classDefinitionType.CIMPlant.toString()), queryRequest);
        }
    }

    @Override
    public String identity() {
        String currentIdentity = "";
        Class<?>[] interfaces = this.getClass().getInterfaces();
        if (interfaces.length > 0)
            currentIdentity = interfaces[0].getSimpleName();
        else
            currentIdentity = this.getClass().getSimpleName();
        if (this.members().size() > 0) {
            for (ICache member : this.members()) {
                String identity = member.identity();
                if (!StringUtils.isEmpty(identity)) {
                    if (!StringUtils.isEmpty(currentIdentity))
                        currentIdentity = currentIdentity + "," + identity;
                    else
                        currentIdentity = identity;
                }
            }
        }
        return currentIdentity;
    }

    @Override
    public void initialize() throws Exception {
        this.addMember(CIMContext.Instance.ProcessCache());
        this.addCachedClassDefs(this.partialCachedClassDefs);
        this.addCachedRelDefs(this.partialCachedRelDefs);
        super.initialize();
    }

    @Override
    public int level() {
        return 2;
    }

    @PostConstruct
    public void doInit() {
        Instance = this;
    }
}
