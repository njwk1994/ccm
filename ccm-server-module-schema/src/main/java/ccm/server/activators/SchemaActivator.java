package ccm.server.activators;

import ccm.server.schema.interfaces.defaults.*;
import ccm.server.schema.model.ClassDefault;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.schema.model.PropertyBase;
import ccm.server.utils.SchemaUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
public class SchemaActivator {
    private final Map<String, Integer> hardCodeTypes;

    public SchemaActivator() {
        this.hardCodeTypes = new HashMap<>();
        this.init();
    }

    private void init() {
        this.hardCodeTypes.putIfAbsent(ClassDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(InterfaceDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.ClassDef.Default.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.Default.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.PropertyDef.Default.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.Rel.Default.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.RelDef.Default.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.IObject.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMColumnSet.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMConfigurationItem.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMPlant.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMFile.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMFileType.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMFileComposition.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMForm.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMSection.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMDisplayItem.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMObjClass.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMRevisionScheme.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMWorkflow.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ccm.server.schema.classes.InterfaceDef.ICIMWorkflowItem.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(IClassDefDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(IInterfaceDefDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(IObjectDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(IPropertyDefDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(IRelDefDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(IRelDefDefault.class.getName(), this.hardCodeTypes.size() + 1);
        this.hardCodeTypes.putIfAbsent(ISchemaObjectDefault.class.getName(), this.hardCodeTypes.size() + 1);
    }

    public Object newInstance(String primaryType, Object[] args) {
        return this.newInstance(primaryType, null, null, args);
    }

    public Object newInstance(String primaryType, String secondType, Object[] args) {
        return this.newInstance(primaryType, secondType, null, args);
    }

    public Object newInstance(String primaryType, String secondType, String thirdType, Object[] args) {
        Class<?> type = SchemaUtility.getTopLevelType(primaryType);
        if (type == null && !StringUtils.isEmpty(secondType))
            type = SchemaUtility.getTopLevelType(secondType);
        if (type == null && !StringUtils.isEmpty(thirdType))
            type = SchemaUtility.getTopLevelType(thirdType);
        if (type == null) {
            String errMsg = String.join(",", new String[]{"new instance failed as no type found for instantiate", primaryType, secondType, thirdType});
            log.error(errMsg);
            return null;
        }
        String lstrType = type.getName();
        Object result = null;
//        if (this.hardCodeTypes.containsKey(lstrType)) {
//            switch (this.hardCodeTypes.get(lstrType)) {
//                case 1:
//                    result = new ClassDefault(false);
//                    break;
//                case 2:
//                    result = new InterfaceDefault(false);
//                    break;
//                case 3:
//                    result = new ccm.server.schema.classes.ClassDef.Default(false);
//                    break;
//                case 4:
//                    result = new ccm.server.schema.classes.InterfaceDef.Default(false);
//                    break;
//                case 5:
//                    result = new ccm.server.schema.classes.PropertyDef.Default(false);
//                    break;
//                case 6:
//                    result = new ccm.server.schema.classes.Rel.Default(false);
//                    break;
//                case 7:
//                    result = new ccm.server.schema.classes.RelDef.Default(false);
//                    break;
//                case 8:
//                    result = new ccm.server.schema.classes.InterfaceDef.IObject(false);
//                    break;
//                case 9:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMColumnSet(false);
//                    break;
//                case 10:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMConfigurationItem(false);
//                    break;
//                case 11:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMPlant(false);
//                    break;
//                case 12:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMFile(false);
//                    break;
//                case 13:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMFileType(false);
//                    break;
//                case 14:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMFileComposition(false);
//                    break;
//                case 15:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMForm(false);
//                    break;
//                case 16:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMSection(false);
//                    break;
//                case 17:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMDisplayItem(false);
//                    break;
//                case 18:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMObjClass(false);
//                    break;
//                case 19:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMRevisionScheme(false);
//                    break;
//                case 20:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMWorkflow(false);
//                    break;
//                case 21:
//                    result = new ccm.server.schema.classes.InterfaceDef.ICIMWorkflowItem(false);
//                    break;
//                case 22:
//                    result = new IClassDefDefault(false);
//                    break;
//                case 23:
//                    result = new IInterfaceDefDefault(false);
//                    break;
//                case 24:
//                    result = new IObjectDefault(false);
//                    break;
//                case 25:
//                    result = new IPropertyDefDefault(false);
//                    break;
//                case 26:
//                    result = new IRelDefDefault(false);
//                    break;
//                case 27:
//                    result = new IRelDefault(false);
//                    break;
//                case 28:
//                    result = new ISchemaObjectDefault(false);
//                    break;
//            }
//        }
        try {
            if (PropertyBase.class.isAssignableFrom(type))
                result = type.getDeclaredConstructor(new Class[]{String.class}).newInstance("");
            else {
                Constructor<?> constructor = this.doCachedConstructor(type);
                result = constructor != null ? constructor.newInstance(args) : null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    protected static final ConcurrentHashMap<String, Map<Class<?>, Constructor<?>>> cachedConstructors = new ConcurrentHashMap<>();

    protected Constructor<?> doCachedConstructor(Class<?> type) throws NoSuchMethodException {
        Constructor<?> result = null;
        if (type != null) {
            String name = type.getName();
            if (cachedConstructors.containsKey(name)) {
                Map<Class<?>, Constructor<?>> classConstructorMap = cachedConstructors.get(name);
                result = classConstructorMap.getOrDefault(type, null);
            }
            if (result == null) {
//                log.info("cached " + name + "'s constructor");
                HashMap<Class<?>, Constructor<?>> content = new HashMap<>();
                result = type.getDeclaredConstructor(boolean.class);
                content.put(type, result);
                cachedConstructors.putIfAbsent(name, content);
            }
        }
        return result;
    }
}
