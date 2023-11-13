package ccm.server.enums;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public enum relDefinitionType {
    exposes,
    contains,
    scopedBy,
    implies,
    realizes,
    baseEnumListTypeForEnumListLevelType,
    usedEdgeDefForEnumListLevelType,
    CIMObjClassEnumEnum,
    CCMDesignObjHierarchy,
    CIMDocumentRevisions,
    CIMDocumentRevisionVersions,
    CIMDocumentRevisionScheme,
    CIMPrimaryClassification,
    CIMFileComposition,
    classDefContainsENSDefs,
    classDefForms,
    CCMDesignObj2DesignObj,
    CCMDesignObj2WorkStep,
    CCMDocument2DesignObj,
    ROPRuleGroup2Item,
    CCMConstructionType2ComponentCategory,
    ROPRuleGroup2ROPWorkStep,
    interfaceDefEffectForm,
    CCMTaskPackage2Document,
    CCMWorkPackage2Document,
    CCMPressureTestPackage2Document,
    form2Sections,
    section2DisplayItems,
    classDefDomainInfo,
    columnSet2Items,
    user2ConfigurationItem,
    User2QueryConfig,
    CCMPTPMS2MSItem,
    CCMTaskPackage2DesignObj,
    CCMWorkPackage2DesignObj,
    CCMPressureTestPackage2DesignObj,
    ROPNewRuleGroupItem2OriItem,
    CCMWorkPackage2WorkStep,
    CCMPTPackage2WorkStep,
    CCMPTPackageMaterialSpecification2Material,
    CCMPTPackage2PTPMaterial,
    CCMBidSection2CWAEnum,
    CIMFileFileType;

    public static relDefinitionType toEnum(String value) {
        relDefinitionType result = null;
        try {
            result = relDefinitionType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            log.error(exception.getMessage());
        }
        return result;
    }

    public static List<String> getRequiredToCachedRelDefs() {
        List<String> result = new ArrayList<>();
        result.add(realizes.toString());
        result.add(exposes.toString());
        result.add(implies.toString());
        result.add(contains.toString());
        result.add(scopedBy.toString());
        result.add(baseEnumListTypeForEnumListLevelType.toString());
        result.add(usedEdgeDefForEnumListLevelType.toString());
        result.add(classDefDomainInfo.toString());
        result.add(form2Sections.toString());
        result.add(interfaceDefEffectForm.toString());
        result.add(classDefForms.toString());
        result.add(section2DisplayItems.toString());
        result.add(user2ConfigurationItem.toString());
        result.add(User2QueryConfig.toString());
        result.add(columnSet2Items.toString());
        return result;
    }
}
