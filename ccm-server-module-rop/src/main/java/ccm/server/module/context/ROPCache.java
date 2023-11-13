package ccm.server.module.context;

import wccm.server.cache.ICache;
import ccm.server.context.CIMContext;
import ccm.server.cache.impl.CacheBase;
import ccm.server.enums.*;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.business.IROPRunningBusinessService;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IROPRuleGroup;
import ccm.server.schema.interfaces.IRel;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service("ropCache")
public class ROPCache extends CacheBase {
    public static ROPCache Instance;
    public static final String CLASSDEF_ROP_RULE_GROUP = "ROPRuleGroup";
    public static final String CLASSDEF_ROP_RULE_GROUP_ITEM = "ROPRuleGroupItem";
    public static final String CLASSDEF_ROP_WORK_STEP = "ROPWorkStep";
    public static final String RELDEF_RULE_GROUP_ITEM = "ROPRuleGroup2Item";
    public static final String RELDEF_RULE_GROUP_ROP_WORK_STEP = "ROPRuleGroup2ROPWorkStep";

    /**
     * @description ROPGroups Key:ROPGroupTargetClassDefUid +"|"+Config 's UID Value:RopGroups
     * @author Chen Jing
     * @date 2022/09/01 10:53:02
     */
    public static final ConcurrentHashMap<String, IObjectCollection> ROPGroups = new ConcurrentHashMap<>();

    /**
     * @description ROPGroupItems Key:ROPGroupOBID Value:关联的RopGroupItem
     * @author Chen Jing
     * @date 2022/09/01 10:53:02
     */
    public static final ConcurrentHashMap<String, IObjectCollection> ROPGroupItems = new ConcurrentHashMap<>();


    /**
     * @description ROPGroupItems Key:ROPGroupOBID Value:关联的RopWorkStep
     * @author Chen Jing
     * @date 2022/09/01 10:53:02
     */
    public static final ConcurrentHashMap<String, IObjectCollection> ROPWorkSteps = new ConcurrentHashMap<>();

    private final List<String> partialCachedClassDefs = new ArrayList<String>() {{
        this.add(CLASSDEF_ROP_RULE_GROUP);
        this.add(CLASSDEF_ROP_RULE_GROUP_ITEM);
        this.add(CLASSDEF_ROP_WORK_STEP);
    }};

    private final List<String> partialCachedRelDefs = new ArrayList<String>() {
        {
            this.add(RELDEF_RULE_GROUP_ITEM);
            this.add(RELDEF_RULE_GROUP_ROP_WORK_STEP);
        }
    };


    public IObjectCollection ropGroupsByTargetClassDefinitionUID(String classDefinitionUID) {
        IObjectCollection result = new ObjectCollection();
        if (!StringUtils.isEmpty(classDefinitionUID)) {
            IObjectCollection items = this.getObjectsByClassDefCache(CLASSDEF_ROP_RULE_GROUP);
            if (items != null && items.size() > 0) {
                Iterator<IObject> e = items.GetEnumerator();
                while (e.hasNext()) {
                    IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                    if (ruleGroup.ROPGroupClassDefinitionUID().equalsIgnoreCase(classDefinitionUID))
                        result.append(ruleGroup);
                }
            }
        }
        return result;
    }

    @Autowired
    private IROPRunningBusinessService runningBusinessService;

    public IROPRunningBusinessService ROPService() {
        return this.runningBusinessService;
    }

    @PostConstruct
    public void doInit() {
        Instance = this;
        Instance.runningBusinessService = this.runningBusinessService;
    }

    @Override
    public String identity() {
        String currentIdentity;
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
        String identity = this.identity();
        this.addMember(CIMContext.Instance.ProcessCache());
        this.addCachedClassDefs(identity, this.partialCachedClassDefs);
        this.addCachedRelDefs(identity, this.partialCachedRelDefs);
        super.initialize();
    }

    @Override
    public int level() {
        return 2;
    }

    @Override
    public void onInitializing() throws Exception {
        String identity = this.identity();
        IObjectCollection objects = this.queryObjectsByClassDefinitions(this.partialCachedClassDefs);
        IObjectCollection relationships = this.queryRelsByRelDefs(this.partialCachedRelDefs);
        this.members().get(0).addLocally(identity, objects);
        this.members().get(0).addLocally(identity, relationships);
        log.info(ROPCache.class.getSimpleName() + " completed to amount ==============" + (objects != null ? objects.size() : 0) + "=========objects and ===========" + (relationships != null ? relationships.size() : 0) + "===============relationships");
        this.initializeROPInfo(objects, relationships);
    }

    private void initializeROPInfo(IObjectCollection pcolObjects, IObjectCollection pcolRels) throws Exception {
        //先判断删除是否有已经删除的对象
        clearHasDeletedObjFromCache(pcolObjects);
        if (SchemaUtility.hasValue(pcolObjects) && SchemaUtility.hasValue(pcolRels)) {
            Map<String, List<IObject>> lmapGroupByClassDef = pcolObjects.toList().stream().collect(Collectors.groupingBy(IObject::ClassDefinitionUID));
            //处理ROPGroup
            List<IObject> lcolROPGroups = lmapGroupByClassDef.get(classDefinitionType.ROPRuleGroup.name());
            if (CommonUtility.hasValue(lcolROPGroups)) {
                //按照目标类型分组
                Map<String, List<IROPRuleGroup>> lcolGroupByTargetClassDefAndConfig = lcolROPGroups.stream().map(r -> r.toInterface(IROPRuleGroup.class)).collect(Collectors.groupingBy(r -> r.ROPGroupClassDefinitionUID() + "|" + r.Config()));
                for (Map.Entry<String, List<IROPRuleGroup>> entry : lcolGroupByTargetClassDefAndConfig.entrySet()) {
                    String lstrTargetClassDefUidAndConfig = entry.getKey();
                    IObjectCollection ropGroups = ObjectCollection.toObjectCollection(entry.getValue().stream().map(r -> r.toInterface(IObject.class)).collect(Collectors.toSet()));
                    if (ROPGroups.containsKey(lstrTargetClassDefUidAndConfig)) {
                        IObjectCollection tempROPGroups = ROPGroups.get(lstrTargetClassDefUidAndConfig);
                        tempROPGroups.addRangeUniquely(ropGroups);
                        ropGroups = tempROPGroups;
                    }
                    ROPGroups.put(lstrTargetClassDefUidAndConfig, ropGroups);
                }
                for (IRel lobjRel : pcolRels.toList(IRel.class)) {
                    String lstrOBID1 = lobjRel.OBID1();
                    String lstrOBID2 = lobjRel.OBID2();
                    IObject lobjEnd2 = pcolObjects.itemByOBID(lstrOBID2);
                    String lstrRelDefUid = lobjRel.RelDefUID();
                    //处理ROPGroupItem
                    if (lstrRelDefUid.equalsIgnoreCase(relDefinitionType.ROPRuleGroup2Item.name())) {
                        if (ROPGroupItems.containsKey(lstrOBID1)) {
                            IObjectCollection temp = ROPGroupItems.get(lstrOBID1);
                            temp.addRangeUniquely(lobjEnd2);
                            ROPGroupItems.put(lstrOBID1, temp);
                        } else {
                            ROPGroupItems.put(lstrOBID1, lobjEnd2.toIObjectCollection());
                        }
                    } else if (lstrRelDefUid.equalsIgnoreCase(relDefinitionType.ROPRuleGroup2ROPWorkStep.name())) {
                        //处理ROPWorkStep
                        if (ROPWorkSteps.containsKey(lstrOBID1)) {
                            IObjectCollection temp = ROPWorkSteps.get(lstrOBID1);
                            temp.addRangeUniquely(lobjEnd2);
                            ROPWorkSteps.put(lstrOBID1, temp);
                        } else {
                            ROPWorkSteps.put(lstrOBID1, lobjEnd2.toIObjectCollection());
                        }
                    }
                }
            }
            log.info("ROP信息初始化分组完成!");
        }
    }

    /**
     * @param pcolObjects 最新的对象集合
     * @description
     * @author Chen Jing
     * @date 2022/09/01 02:04:36
     */
    private void clearHasDeletedObjFromCache(IObjectCollection pcolObjects) {
        if (SchemaUtility.hasValue(pcolObjects)) {
            removeNonExistObj(ROPGroups, pcolObjects);
            removeNonExistObj(ROPGroupItems, pcolObjects);
            removeNonExistObj(ROPWorkSteps, pcolObjects);
        } else {
            ROPGroups.clear();
            ROPGroupItems.clear();
            ROPWorkSteps.clear();
        }
    }

    private void removeNonExistObj(ConcurrentHashMap<String, IObjectCollection> ropGroups, IObjectCollection pcolObjects) {
        for (IObjectCollection value : ropGroups.values()) {
            Iterator<IObject> e = value.GetEnumerator();
            while (e.hasNext()) {
                IObject next = e.next();
                if (!pcolObjects.containsByOBID(next.OBID())) {
                    value.remove(next.OBID());
                }
            }
        }
    }


    @Override
    public void setScopePrefixForQueryRequestHandler(QueryRequest queryRequest) {
        super.setScopePrefixForQueryRequestHandler(queryRequest);
        if (queryRequest != null) {
            this.setScopePrefixForQueryRequest(this.getObjectsByClassDefCache(classDefinitionType.CIMPlant.toString()), queryRequest);
        }
    }

    /**
     * 当创建或更新刷新ROP规则组缓存
     *
     * @param pobjRopRuleGroup ROP规则组
     * @date 2022/09/08 10:30:21
     * @author CHEN JING
     */
    public synchronized void refreshROPRuleGroupCacheWhenCreateOrUpdate(IObject pobjRopRuleGroup) throws Exception {
        if (pobjRopRuleGroup != null) {
            IROPRuleGroup ruleGroup = pobjRopRuleGroup.toInterface(IROPRuleGroup.class);
            ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
            Objects.requireNonNull(configurationItem,"未找到当前项目信息");
            String lstrTargetClassDefUid = ruleGroup.ROPGroupClassDefinitionUID() + "|" + configurationItem.UID();
            if (ROPGroups.containsKey(lstrTargetClassDefUid)) {
                IObjectCollection lcolROPGroups = ROPGroups.get(lstrTargetClassDefUid);
                if (lcolROPGroups.containsByOBID(pobjRopRuleGroup.OBID())) {
                    lcolROPGroups.remove(pobjRopRuleGroup.OBID());
                }
                lcolROPGroups.append(pobjRopRuleGroup);
                ROPGroups.put(lstrTargetClassDefUid, lcolROPGroups);
            } else {
                ROPGroups.put(lstrTargetClassDefUid, new ObjectCollection() {{
                    append(pobjRopRuleGroup);
                }});
            }
        }
    }

    /**
     * 当创建或更新刷新规则组条目缓存
     *
     * @param pobjROPGroupItem pobj 规则组条目
     * @param pstrROPGroupOBID pstr 规则组OBID
     * @date 2022/09/08 10:47:06
     * @author CHEN JING
     */
    public synchronized void refreshROPGroupItemCacheWhenCreateOrUpdate(IObject pobjROPGroupItem, String pstrROPGroupOBID) {
        if (pobjROPGroupItem != null && !StringUtils.isEmpty(pstrROPGroupOBID)) {
            if (ROPGroupItems.containsKey(pstrROPGroupOBID)) {
                IObjectCollection lcolROPGroupItems = ROPGroupItems.get(pstrROPGroupOBID);
                if (lcolROPGroupItems.containsByOBID(pobjROPGroupItem.OBID())) {
                    lcolROPGroupItems.remove(pobjROPGroupItem.OBID());
                }
                lcolROPGroupItems.append(pobjROPGroupItem);
                ROPGroupItems.put(pstrROPGroupOBID, lcolROPGroupItems);
            } else {
                ROPGroupItems.put(pstrROPGroupOBID, new ObjectCollection() {{
                    append(pobjROPGroupItem);
                }});
            }
        }
    }

    /**
     * 当创建或更新缓存刷新ROPStep
     *
     * @param pobjROPWorkStep  pobj 工步
     * @param pstrROPGroupOBID pstr 规则组OBID
     * @date 2022/09/08 10:46:39
     * @author CHEN JING
     */
    public synchronized void refreshROPWorkStepCacheWhenCreateOrUpdate(IObject pobjROPWorkStep, String pstrROPGroupOBID) {
        if (pobjROPWorkStep != null && !StringUtils.isEmpty(pstrROPGroupOBID)) {
            if (ROPWorkSteps.containsKey(pstrROPGroupOBID)) {
                IObjectCollection lcolROPGroupItems = ROPGroupItems.get(pstrROPGroupOBID);
                if (lcolROPGroupItems.containsByOBID(pobjROPWorkStep.OBID())) {
                    lcolROPGroupItems.remove(pobjROPWorkStep.OBID());
                }
                lcolROPGroupItems.append(pobjROPWorkStep);
                ROPWorkSteps.put(pstrROPGroupOBID, lcolROPGroupItems);
            } else {
                ROPWorkSteps.put(pstrROPGroupOBID, new ObjectCollection() {{
                    append(pobjROPWorkStep);
                }});
            }
        }
    }

    /**
     * 删除操作后刷新ROP缓存信息
     *
     * @param pstrOBID     删除对象的OBID
     * @param pstrClassDef 删除对象的ClassDef
     * @param pstrParam    ROPGroup的OBID或者TargetClassDef
     * @date 2022/09/08 10:44:29
     * @author CHEN JING
     */
    public synchronized void refreshROPInfoWhenDelete(String pstrOBID, String pstrClassDef, String pstrParam) {
        assert !StringUtils.isEmpty(pstrClassDef) && !StringUtils.isEmpty(pstrOBID);
        if (CLASSDEF_ROP_RULE_GROUP.equalsIgnoreCase(pstrClassDef)) {
            if (StringUtils.isEmpty(pstrParam)) {
                throw new RuntimeException("param不能为空");
            }
            IObjectCollection lcolROPGroups = ROPGroups.get(pstrParam);
            if (SchemaUtility.hasValue(lcolROPGroups) && lcolROPGroups.containsByOBID(pstrOBID)) {
                lcolROPGroups.remove(pstrOBID);
            }
            ROPGroups.put(pstrParam, lcolROPGroups);
            ROPGroupItems.remove(pstrOBID);
            ROPWorkSteps.remove(pstrOBID);
        } else if (CLASSDEF_ROP_RULE_GROUP_ITEM.equalsIgnoreCase(pstrClassDef)) {
            IObjectCollection lcolROPGroupItems = ROPGroupItems.get(pstrParam);
            if (SchemaUtility.hasValue(lcolROPGroupItems) && lcolROPGroupItems.containsByOBID(pstrOBID)) {
                lcolROPGroupItems.remove(pstrOBID);
            }
            ROPGroupItems.put(pstrParam, lcolROPGroupItems);
        } else {
            IObjectCollection lcolROPSteps = ROPWorkSteps.get(pstrParam);
            if (SchemaUtility.hasValue(lcolROPSteps) && lcolROPSteps.containsByOBID(pstrOBID)) {
                lcolROPSteps.remove(pstrOBID);
            }
            ROPWorkSteps.put(pstrParam, lcolROPSteps);
        }
    }


}
