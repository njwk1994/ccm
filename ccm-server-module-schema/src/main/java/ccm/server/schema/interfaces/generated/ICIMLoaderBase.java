package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.model.LoaderReport;
import ccm.server.enums.domainInfo;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.objectUpdateState;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.helper.HardCodeHelper;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.InterfaceBase;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class ICIMLoaderBase extends InterfaceDefault implements ICIMLoader {
    public ICIMLoaderBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    public ICIMLoaderBase(boolean instantiateRequiredProperties) {
        super("ICIMLoader", instantiateRequiredProperties);
    }


    protected String getClassDefinitionUID(JSONObject jsonObject) throws Exception {
        if (jsonObject != null) {
            String value = jsonObject.getString(SchemaUtility.CLASS_DEFINITION_UID);
            if (value == null || StringUtils.isEmpty(value))
                throw new Exception("invalid input data format as class definition UID is invalid");
            return value;
        }
        return "";
    }

    protected List<Map.Entry<String, Object>> parseProperties(JSONObject jsonObject) {
        List<Map.Entry<String, Object>> result = new ArrayList<>();
        if (jsonObject != null) {
            JSONObject jsonObject1 = jsonObject.getJSONObject(SchemaUtility.PROPERTIES);
            if (jsonObject1 != null) {
                for (Map.Entry<String, Object> stringObjectEntry : jsonObject1.entrySet()) {
                    Map.Entry<String, Object> stringObjectSimpleEntry = new AbstractMap.SimpleEntry<>(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                    result.add(stringObjectSimpleEntry);
                }
            }
        }
        return result;
    }

    protected String[] parseInterfaces(JSONObject jsonObject) {
        List<String> result = new ArrayList<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(SchemaUtility.INTERFACES);
            if (jsonArray != null && jsonArray.size() > 0) {
                for (Object o : jsonArray) {
                    result.add(o.toString());
                }
            }
        }
        return CommonUtility.convertToArray(result);
    }

    protected void loadData(JSONObject jsonObject, LoaderReport loaderReport) throws Exception {
        if (jsonObject != null) {
            if (loaderReport == null)
                loaderReport = new LoaderReport();
            String classDefinitionUID = this.getClassDefinitionUID(jsonObject);
            String[] interfaces = this.parseInterfaces(jsonObject);
            List<Map.Entry<String, Object>> properties = this.parseProperties(jsonObject);
            if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                this.preProcessRelationship(interfaces, properties, loaderReport);
            } else {
                this.preProcessObject(classDefinitionUID, interfaces, properties, loaderReport);
            }
        }
    }

    protected boolean verifyInterfaces(String[] parrInterfaces) throws Exception {
        if (parrInterfaces != null && parrInterfaces.length > 0) {
            for (String anInterface : parrInterfaces) {
                IObject item = CIMContext.Instance.ProcessCache().item(anInterface, domainInfo.SCHEMA.toString(), false);
                if (item == null)
                    throw new Exception("invalid interface definition:" + anInterface);
            }
            return true;
        }
        return false;
    }

    protected boolean verifyProperties(List<String> pcolPropertyDefs) throws Exception {
        if (pcolPropertyDefs != null && pcolPropertyDefs.size() > 0) {
            for (String propertyDef : pcolPropertyDefs) {
                IObject item = CIMContext.Instance.ProcessCache().item(propertyDef, domainInfo.SCHEMA.toString(), false);
                if (item == null)
                    throw new Exception("invalid property definition:" + propertyDef);
            }
            return true;
        }
        return false;
    }

    protected String getRelDefUID(List<Map.Entry<String, Object>> pcolProperties) throws Exception {
        Map.Entry<String, Object> objectEntry = pcolProperties.stream().filter(c -> c.getKey().equalsIgnoreCase(propertyDefinitionType.RelDefUID.toString())).findFirst().orElse(null);
        String relDefUID = null;
        if (objectEntry != null && objectEntry.getValue() != null) {
            relDefUID = objectEntry.getValue().toString();
        }
        if (relDefUID == null || StringUtils.isEmpty(relDefUID))
            throw new Exception("invalid relationship definition as it is NULL or EMPTY");
        return relDefUID;
    }

    protected String getPropertyValue(List<Map.Entry<String, Object>> properties, String propertyDefinitionUid) {
        if (properties != null && properties.size() > 0 && !StringUtils.isEmpty(propertyDefinitionUid)) {
            for (Map.Entry<String, Object> property : properties) {
                if (property.getKey().equalsIgnoreCase(propertyDefinitionUid)) {
                    Object value = property.getValue();
                    if (value != null)
                        return value.toString();
                }
            }
        }
        return "";
    }

    protected boolean verifyRelDef(String relDefUID) {
        if (!StringUtils.isEmpty(relDefUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(relDefUID, domainInfo.SCHEMA.toString(), false);
            return item != null && item.Interfaces().hasInterface(IRelDef.class.getSimpleName());
        }
        return false;
    }

    private String getSpecialProperty(String pstrPropertyDef, List<Map.Entry<String, Object>> properties) {
        if (properties != null && properties.size() > 0 && !StringUtils.isEmpty(pstrPropertyDef)) {
            for (Map.Entry<String, Object> property : properties) {
                if (property.getKey().equalsIgnoreCase(pstrPropertyDef)) {
                    Object value = property.getValue();
                    if (value != null)
                        return value.toString();
                }
            }
        }
        return "";
    }

    protected IObject getRequiredObjectByInterfaceDef(IObjectCollection pcolSource, String pstrInterfaceDef) {
        if (pcolSource != null) {
            if (!StringUtils.isEmpty(pstrInterfaceDef)) {
                Iterator<IObject> iterator = pcolSource.GetEnumerator();
                while ((iterator.hasNext())) {
                    IObject next = iterator.next();
                    if (next.Interfaces().hasInterface(pstrInterfaceDef))
                        return next;
                }
            }
            return pcolSource.get(0);
        }
        return null;
    }

    protected void preProcessRelationship(String[] interfaces, List<Map.Entry<String, Object>> properties, LoaderReport loaderReport) throws Exception {
        if (loaderReport != null) {
            if (this.verifyInterfaces(interfaces) && this.verifyProperties(properties.stream().map(Map.Entry::getKey).collect(Collectors.toList()))) {
                if (properties.size() > 0) {
                    String relDefUID = this.getRelDefUID(properties);
                    if (StringUtils.isEmpty(relDefUID))
                        throw new Exception("invalid relationship definition as it is NULL");

                    if (!this.verifyRelDef(relDefUID))
                        throw new Exception("invalid relationship definition as it was not defined in database");

                    IRelDef relDef = CIMContext.Instance.ProcessCache().item(relDefUID, domainInfo.SCHEMA.toString()).toInterface(IRelDef.class);
                    IObject end1 = null;
                    IObject end2 = null;

                    String uid1 = this.getSpecialProperty(propertyDefinitionType.UID1.toString(), properties);
                    if (!StringUtils.isEmpty(uid1)) {
                        IObjectCollection collection = this.getObjectByUID(uid1, loaderReport);
                        end1 = this.getRequiredObjectByInterfaceDef(collection, relDef.UID1());
                    }

                    if (end1 == null) {
                        String name1 = this.getSpecialProperty(propertyDefinitionType.Name1.toString(), properties);
                        if (!StringUtils.isEmpty(name1)) {
                            end1 = this.getRequiredObjectByInterfaceDef(this.getObjectByName(name1, loaderReport), relDef.UID1());
                        } else
                            throw new Exception("invalid end1 object information as system cannot identify it either Name" + name1 + " or UID " + uid1);
                    }

                    String uid2 = this.getSpecialProperty(propertyDefinitionType.UID2.toString(), properties);
                    if (!StringUtils.isEmpty(uid2)) {
                        end2 = this.getRequiredObjectByInterfaceDef(this.getObjectByUID(uid2, loaderReport), relDef.UID2());
                    }

                    if (end2 == null) {
                        String name2 = this.getSpecialProperty(propertyDefinitionType.Name2.toString(), properties);
                        if (!StringUtils.isEmpty(name2)) {
                            end2 = this.getRequiredObjectByInterfaceDef(this.getObjectByName(name2, loaderReport), relDef.UID2());
                        } else
                            throw new Exception("invalid end2 object information as system cannot identify it either Name" + name2 + " or UID " + uid2);
                    }

                    IRel relationship = SchemaUtility.newRelationship(relDefUID, end1, end2, false);
                    relationship.fillingInterfaces(interfaces);
                    relationship.fillingProperties(true, properties);
                    loaderReport.addRelationships(relationship);
                }
            }
        }
    }

    protected IObjectCollection getObjectByName(String name, LoaderReport loaderReport) throws Exception {
        if (!StringUtils.isEmpty(name)) {
            IObject result = null;
            if (loaderReport != null)
                result = loaderReport.getObjectByName(name);
            if (result == null) {
                IObjectCollection objectCollection = CIMContext.Instance.ProcessCache().getByNameOrDescription(name, "");
                if (objectCollection != null && objectCollection.hasValue())
                    return objectCollection;
            } else {
                return result.toIObjectCollection();
            }
        }
        return null;
    }

    protected IObjectCollection getObjectByUID(String uid, LoaderReport loaderReport) throws Exception {
        if (!StringUtils.isEmpty(uid)) {
            IObject result = null;
            if (loaderReport != null) {
                result = loaderReport.getObjectByUID(uid);
            }
            if (result == null) {
                return CIMContext.Instance.ProcessCache().getByUID(uid);
            } else
                return result.toIObjectCollection();
        }
        return null;
    }

    protected boolean verifyClassDef(String classDefUID) throws Exception {
        if (!StringUtils.isEmpty(classDefUID)) {
            IObject item = CIMContext.Instance.ProcessCache().item(classDefUID, domainInfo.SCHEMA.toString());
            return item != null && item.IsTypeOf(IClassDef.class.getSimpleName());
        }
        return false;
    }

    protected void preProcessObject(String classDefUID, String[] interfaces, List<Map.Entry<String, Object>> properties, LoaderReport loaderReport) throws Exception {
        if (!StringUtils.isEmpty(classDefUID)) {
            if (this.verifyClassDef(classDefUID)) {
                IClassDef classDef = CIMContext.Instance.ProcessCache().item(classDefUID, domainInfo.SCHEMA.toString()).toInterface(IClassDef.class);
                if (classDef == null)
                    throw new Exception("invalid class definition as  it is not initialized");
                IObject item = (IObject) classDef.Instantiate(true);
                if (item == null)
                    throw new Exception("initializing object failed");
                item.fillingInterfaces(interfaces);
                item.fillingProperties(true, properties);
                item.ClassDefinition().FinishCreate(item);
                loaderReport.addObject(item);
            } else
                throw new Exception("invalid class definition " + classDefUID + " as it is not exist in database");
        }
    }

    @Override
    public LoaderReport loadData(JSONObject jsonObject) throws Exception {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(SchemaUtility.CONTAINERS);
            if (jsonArray != null && jsonArray.size() > 0) {
                for (Object o : jsonArray) {
                    if (o instanceof JSONObject) {
                        JSONObject o1 = (JSONObject) o;
                        JSONArray jsonArray1 = o1.getJSONArray(SchemaUtility.ITEMS);
                        if (jsonArray1 != null && jsonArray1.size() > 0) {
                            LoaderReport loaderReport = new LoaderReport();
                            CIMContext.Instance.Transaction().start();
                            for (Object o2 : jsonArray1) {
                                JSONObject c2 = (JSONObject) o2;
                                this.loadData(c2, loaderReport);
                            }
                            loaderReport.commit();
                            return loaderReport;
                        }
                    }
                }
            } else {
                jsonArray = jsonObject.getJSONArray(SchemaUtility.ITEMS);
                if (jsonArray != null && jsonArray.size() > 0) {
                    CIMContext.Instance.Transaction().start();
                    LoaderReport loaderReport = new LoaderReport();
                    for (Object o : jsonArray) {
                        JSONObject c2 = (JSONObject) o;
                        this.loadData(c2, loaderReport);
                    }
                    loaderReport.commit();
                    return loaderReport;
                }
            }
        }
        return null;
    }

    @Override
    public LoaderReport loadDataByXml(@NotNull MultipartFile file) throws Exception {
        LoaderReport loaderReport = new LoaderReport();
        InputStream inputStream = file.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(isr);
            List<Map<String, Map<String, Map<String, String>>>> objInfoContainer = new ArrayList<>();
            Element container = document.getRootElement();
            Iterator<Element> elementIterator = container.elementIterator();
            while (elementIterator.hasNext()) {
                Map<String, Map<String, Map<String, String>>> objInfos = new HashMap<>();
                Element classDefElement = elementIterator.next();
                String classDef = classDefElement.getName();
                Map<String, Map<String, String>> objInterfaceAndProps = new HashMap<>();//解析CLassDef 下的接口和属性
                Iterator<Element> e = classDefElement.elementIterator();
                while (e.hasNext()) {
                    Element interfaceElement = e.next();
                    String interfaceDefUID = interfaceElement.getName();
                    List<Attribute> propElements = interfaceElement.attributes();
                    if (propElements != null && propElements.size() > 0) {
                        Map<String, String> propertyAndValue = new HashMap<>();
                        for (Attribute attribute : propElements) {
                            propertyAndValue.put(attribute.getName(), attribute.getValue());
                        }
                        objInterfaceAndProps.put(interfaceDefUID, propertyAndValue);
                    } else
                        objInterfaceAndProps.put(interfaceDefUID, null);
                }
                objInfos.put(classDef, objInterfaceAndProps);
                objInfoContainer.add(objInfos);
            }
            if (objInfoContainer.size() > 0) {
                SchemaUtility.beginTransaction();
                IObjectCollection existObjs = SchemaUtility.getExistObjByXmlInfoFromSystem(objInfoContainer);
                this.loadDataByMapContainer(existObjs, objInfoContainer, loaderReport);
                loaderReport.commit();
            }
        } finally {
            inputStream.close();
        }
        return loaderReport;
    }

    private void loadDataByMapContainer(IObjectCollection existObjs, @NotNull List<Map<String, Map<String, Map<String, String>>>> objContainer, @NotNull LoaderReport loaderReport) throws Exception {
        StopWatch stopWatch = PerformanceUtility.start();
        log.info("enter to load data by map container " + objContainer.size());
        int i = 0;
        List<Map<String, Map<String, String>>> relationships = new ArrayList<>();
        StopWatch stopWatch1 = PerformanceUtility.start();
        for (Map<String, Map<String, Map<String, String>>> obj : objContainer) {
            for (Map.Entry<String, Map<String, Map<String, String>>> objInfo : obj.entrySet()) {
                i++;
                String classDef = objInfo.getKey();
                Map<String, Map<String, String>> interfaceAdnPropValues = objInfo.getValue();
                if (classDef.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                    relationships.add(interfaceAdnPropValues);
                else {
                    this.loadDataByInterfaceAndPropsInfos(classDef, interfaceAdnPropValues, existObjs, loaderReport);
                    if ((i % 2000 == 0) || i == objContainer.size()) {
                        log.info("load progress currently:" + i + " / " + objContainer.size() + PerformanceUtility.stop(stopWatch1));
                        stopWatch1.start();
                    }
                }
            }
        }
        stopWatch1.stop();
        stopWatch1.start();
        if (relationships.size() > 0) {
            i = 0;
            for (Map<String, Map<String, String>> relationship : relationships) {
                i++;
                this.loadDataByInterfaceAndPropsInfos(HardCodeHelper.CLASSDEF_REL, relationship, existObjs, loaderReport);
                if ((i % 2000 == 0) || i == objContainer.size()) {
                    log.info("load progress currently:" + i + " / " + objContainer.size() + PerformanceUtility.stop(stopWatch1));
                    stopWatch1.start();
                }
            }
        }
        stopWatch1.stop();
        log.info("finish to load data" + PerformanceUtility.stop(stopWatch));
    }

    private void loadDataByInterfaceAndPropsInfos(@NotNull String classDef, Map<String, Map<String, String>> interfaceAdnPropValues, IObjectCollection existObj, LoaderReport loaderReport) throws Exception {
        if (!this.verifyClassDef(classDef)) {
            throw new Exception(classDef + " is invalid !");
        }
        if (!classDef.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            this.preProcessObject(classDef, interfaceAdnPropValues, loaderReport, existObj);
        }
        if (classDef.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            this.preProcessRelationship(interfaceAdnPropValues, loaderReport, existObj);
        }
    }

    private void preProcessRelationship(@NotNull Map<String, Map<String, String>> objInterfaceAndProps, @NotNull LoaderReport loaderReport, IObjectCollection existObjs) throws Exception {
        Map<String, String> relProps = objInterfaceAndProps.get(interfaceDefinitionType.IRel.toString());
        String uid1 = relProps.get(propertyDefinitionType.UID1.toString());
        if (StringUtils.isEmpty(uid1))
            throw new Exception("uid1 can not be null");
        String uid2 = relProps.get(propertyDefinitionType.UID2.toString());
        if (StringUtils.isEmpty(uid2))
            throw new Exception("uid2 can not be null");
        Map<String, String> objProps = objInterfaceAndProps.get(interfaceDefinitionType.IObject.toString());
        String uid = objProps.get(propertyDefinitionType.UID.toString());
        if (StringUtils.isEmpty(uid))
            throw new Exception("uid can not be null");
        String relDefUid = relProps.get(propertyDefinitionType.RelDefUID.toString());
        if (StringUtils.isEmpty(relDefUid))
            throw new Exception("relDefUid can not be null");
        IObject existRel = null;
        if (SchemaUtility.hasValue(existObjs)) existRel = existObjs.item(uid);
        if (existRel == null) {
            IObject relDef = CIMContext.Instance.ProcessCache().item(relDefUid, domainInfo.SCHEMA.toString());
            if (relDef == null)
                throw new Exception("invalid rel definition:" + relDefUid);

            IObject lobjEnd1 = loaderReport.getObjectByUID(uid1);
            if (lobjEnd1 == null)
                lobjEnd1 = existObjs.item(uid1);
            if (lobjEnd1 == null)
                throw new Exception("end1 object can not be null with " + uid1);
            IObject lobjEnd2 = loaderReport.getObjectByUID(uid2);
            if (lobjEnd2 == null) lobjEnd2 = existObjs.item(uid2);
            if (lobjEnd2 == null)
                throw new Exception("end2 object cannot be null,uid:" + uid2);
            IRel iRel = SchemaUtility.newRelationship(relDefUid, lobjEnd1, lobjEnd2, ValueConversionUtility.toBoolean(relProps.get(propertyDefinitionType.IsRequired.toString())));
            ((InterfaceBase) iRel).ClassBase().setUpdateState(objectUpdateState.created);
            iRel.fillingInterfaceAndProperties(objInterfaceAndProps);
            iRel.ClassDefinition().FinishCreate(iRel);
            loaderReport.addRelationships(iRel);
        } else {
            IRel iRel = existRel.toInterface(IRel.class);
            iRel.BeginUpdate();
            iRel.fillingInterfaceAndProperties(objInterfaceAndProps);
            iRel.FinishUpdate();
            loaderReport.addRelationships(iRel);
        }
    }

    private void preProcessObject(@NotNull String classDefUid, @NotNull Map<String, Map<String, String>> objInterfaceAndProps, @NotNull LoaderReport loaderReport, IObjectCollection existObjs) throws Exception {
        String name = CommonUtility.getPropertyValueFromInfoContainer(objInterfaceAndProps, propertyDefinitionType.Name.toString(), interfaceDefinitionType.IObject.toString());
        String desc = CommonUtility.getPropertyValueFromInfoContainer(objInterfaceAndProps, propertyDefinitionType.Description.toString(), interfaceDefinitionType.IObject.toString());
        String uid = CommonUtility.getPropertyValueFromInfoContainer(objInterfaceAndProps, propertyDefinitionType.UID.toString(), interfaceDefinitionType.IObject.toString());
        if (StringUtils.isEmpty(name))
            throw new Exception("object's name can not be null, classDef:" + classDefUid);
        if (StringUtils.isEmpty(uid))
            throw new Exception("uid can not be null");
        IObject existObj = null;
        if (SchemaUtility.hasValue(existObjs)) existObj = existObjs.item(uid);
        if (existObj == null) {
            existObj = SchemaUtility.newIObject(classDefUid, name, !StringUtils.isEmpty(desc) ? desc : name, null, uid);
            if (existObj == null)
                throw new Exception("initializing object failed");
            this.toRemoveSetValueFromProvided(objInterfaceAndProps, new ArrayList<String>() {{
                add(propertyDefinitionType.Name.toString());
                add(propertyDefinitionType.Description.toString());
                add(propertyDefinitionType.UID.toString());
            }});
            existObj.ClassBase().setUpdateState(objectUpdateState.created);
            existObj.fillingInterfaceAndProperties(objInterfaceAndProps);
            existObj.ClassDefinition().FinishCreate(existObj);
        } else {
            existObj.BeginUpdate();
            existObj.fillingInterfaceAndProperties(objInterfaceAndProps);
            existObj.FinishUpdate();
        }
        loaderReport.addObject(existObj);
    }

    private void toRemoveSetValueFromProvided(Map<String, Map<String, String>> objInterfaceAndProps, List<String> removedPropList) {
        if (objInterfaceAndProps != null && objInterfaceAndProps.size() > 0 && CommonUtility.hasValue(removedPropList)) {
            Map<String, String> propsOrDefault = objInterfaceAndProps.getOrDefault(interfaceDefinitionType.IObject.toString(), null);
            if (propsOrDefault != null) {
                for (String s : removedPropList) {
                    propsOrDefault.remove(s);
                }
                if (objInterfaceAndProps.containsKey(interfaceDefinitionType.IObject.toString()))
                    objInterfaceAndProps.replace(interfaceDefinitionType.IObject.toString(), propsOrDefault);
                else
                    objInterfaceAndProps.put(interfaceDefinitionType.IObject.toString(), propsOrDefault);
            }
        }
    }
}
