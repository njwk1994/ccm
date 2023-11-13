package ccm.server.enums;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public enum classDefinitionType {
    ClassDef,
    PropertyDef,
    PropertyType,
    EnumListType,
    EnumEnum,
    EnumListLevelType,
    RelDef,
    SpecialRelDef,
    EdgeDef,
    InterfaceDef,
    ViewDef,
    UoMListType,
    UoMEnum,
    GraphDef,
    CIMPlant,
    Rel,
    CIMRevisionScheme,
    CIMDocumentMaster,
    CIMDocumentRevision,
    CIMDocumentVersion,
    CCMPipeComponent,
    CCMWorkStep,
    CCMSpool,
    CCMPipe,
    CCMPipeLine,
    CCMBolt,
    CCMGasket,
    CCMEquip,
    CCMSubEquip,
    CCMCableTray,
    CCMCableTrayComponent,
    CCMCable,
    CCMInstrument,
    CCMJunctionBox,
    CCMDuctLine,
    CCMDuctComponent,
    CCMSTPart,
    CCMSTComponent,
    CCMSTBlock,
    CCMSupport,
    CCMWeld,
    CIMFile,
    CCMDocument,
    CIMFileType,
    CIMMinioConfig,
    Domain,
    CIMForm,
    CIMSection,
    CIMDisplayItem,
    ROPRuleGroup,
    ROPRuleGroupItem,
    ROPWorkStep,
    CCMPTPackageMaterialSpecification,
    CCMPTPackageMaterialSpecificationItem,
    CCMPTPackageMaterialTemplate,
    CCMPTPackageMaterial,
    CCMPressureTestPackage,
    CIMUser,
    /*系统级配置*/
    CIMSystemConfig,
    /*项目级配置*/
    CIMProjectConfig,
    ALL;

    public static List<String> getRequiredToCachedClassDefinitionForRunner() {
        List<String> result = new ArrayList<>();
        result.add(ClassDef.toString());
        result.add(InterfaceDef.toString());
        result.add(PropertyDef.toString());
        result.add(RelDef.toString());
        result.add(EdgeDef.toString());
        result.add(EnumListType.toString());
        result.add(EnumEnum.toString());
        result.add(EnumListLevelType.toString());
        result.add(Domain.toString());
        result.add(CIMPlant.toString());
        result.add(CIMUser.toString());
        result.add(ViewDef.toString());
        result.add(UoMListType.toString());
        result.add(UoMEnum.toString());
        result.add(GraphDef.toString());
        result.add(CIMForm.toString());
        result.add(CIMSection.toString());
        result.add(CIMDisplayItem.toString());
        result.add(PropertyType.toString());
        return result;
    }

    public static classDefinitionType ValueOf(String value) {
        classDefinitionType result = null;
        try {
            result = classDefinitionType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            log.error(exception.getMessage());
        }
        return result;
    }

    public static boolean contains(String value) {
        if (!StringUtils.isEmpty(value))
            return Arrays.stream(classDefinitionType.values()).anyMatch(c -> c.toString().equalsIgnoreCase(value));
        return false;
    }
}
