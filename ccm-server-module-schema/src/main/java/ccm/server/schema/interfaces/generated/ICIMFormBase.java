package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.domainInfo;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.model.displayItemWrapper;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.utils.ValueConversionUtility;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class ICIMFormBase extends InterfaceDefault implements ICIMForm {
    public ICIMFormBase(boolean instantiateRequiredProperties) {
        super("ICIMForm", instantiateRequiredProperties);
    }

    @Override
    public List<ICIMSection> getOrderedSections() throws Exception {
        IObjectCollection sections = this.getSections();
        List<ICIMSection> result = new ArrayList<>();
        if (sections != null && sections.hasValue()) {
            IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.form2Sections.toString(), true);
            if (relCollection != null && relCollection.hasValue()) {
                List<IRel> rels = relCollection.toList().stream().map(c -> c.toInterface(IRel.class)).sorted(new Comparator<IRel>() {
                    @SneakyThrows
                    @Override
                    public int compare(IRel o1, IRel o2) {
                        return Integer.compare(o1.OrderValue(), o2.OrderValue());
                    }
                }).collect(Collectors.toList());
                for (IRel rel : rels) {
                    if (rel != null) {
                        result.add(rel.GetEnd2().toInterface(ICIMSection.class));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public IObjectCollection getSections() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.form2Sections.toString(), true);
        if (relCollection != null && relCollection.hasValue())
            return relCollection.GetEnd2s();
        return null;
    }

    @Override
    public IInterfaceDef getEffectInterfaceDef() throws Exception {
        IRel rel = this.GetEnd2Relationships().GetRel(relDefinitionType.interfaceDefEffectForm.toString(), true);
        if (rel != null)
            return rel.GetEnd1().toInterface(IInterfaceDef.class);
        return null;
    }

    @Override
    public String FormPurpose() {
        IProperty property = this.getProperty("ICIMForm", "FormPurpose");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setFormPurpose(String value) throws Exception {
        this.Interfaces().item("ICIMForm", true).Properties().item("FormPurpose", true).setValue(value);
    }

    @Override
    public IObjectCollection getEffectClassDefs() throws Exception {
        IRelCollection relCollection = this.GetEnd2Relationships().GetRels(relDefinitionType.classDefForms.toString());
        if (relCollection != null && relCollection.hasValue()) {
            return relCollection.GetEnd1s();
        }
        return null;
    }


    protected boolean isHintForProvidedFormPurposes(String purposeSet, String[] requiredPurpose) {
        if (purposeSet != null && requiredPurpose != null && requiredPurpose.length > 0) {
            String[] strings = purposeSet.split(",");
            for (String string : strings) {
                if (Arrays.stream(requiredPurpose).anyMatch(c -> c.equalsIgnoreCase(string)))
                    return true;
            }
        }
        return false;
    }

    protected Comparator<IRel> relComparator() {
        return Comparator.comparingInt(IRel::OrderValue);
    }

    protected Comparator<IObject> objectOrderValueComparator() {
        return Comparator.comparingInt(o -> o.toInterface(IRel.class).OrderValue());
    }

    protected void setDefaultValue(ObjectItemDTO objectItemDTO, ICIMDisplayItem displayItem, String formPurpose) throws Exception {
        if (objectItemDTO != null && !StringUtils.isEmpty(formPurpose) && displayItem != null) {
            String defaultValue = null;
            if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Create.toString())) {
                defaultValue = displayItem.DefaultCreateValue();
            } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Update.toString())) {
                defaultValue = displayItem.DefaultUpdateValue();
            } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Query.toString())) {
                defaultValue = displayItem.DefaultQueryValue();
            }
            if (!StringUtils.isEmpty(defaultValue)) {
                if (objectItemDTO.getDefType() == classDefinitionType.PropertyDef) {
                    IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(objectItemDTO.getDefUID(), defaultValue);
                    if (enumEnum != null)
                        defaultValue = enumEnum.DisplayName();
                } else if (objectItemDTO.getDefType() == classDefinitionType.RelDef) {

                } else if (objectItemDTO.getDefType() == classDefinitionType.EdgeDef) {

                }
                objectItemDTO.setDisplayValue(defaultValue);
            }
        }
    }

    @Override
    public ObjectDTO generatePopup(String formPurpose) throws Exception {
        log.trace("enter to generate form with purpose:" + formPurpose + " under " + this.Name());
        if (!StringUtils.isEmpty(formPurpose)) {
            StopWatch stopWatch = PerformanceUtility.start();
            ObjectDTO result = new ObjectDTO();
            result.add(result.generateProperties());
            String[] requiredPurpose = formPurpose.split(",");
            IRelCollection formSectionsDetails = this.GetEnd1Relationships().GetRels(relDefinitionType.form2Sections.toString(), true);
            if (formSectionsDetails != null && formSectionsDetails.hasValue()) {
                log.trace("get form:" + this.Name() + " 's section with quantity:" + formSectionsDetails.size());
                for (IObject formSection : formSectionsDetails.toList().stream().sorted(this.objectOrderValueComparator()).collect(Collectors.toList())) {
                    IRel rel = formSection.toInterface(IRel.class);
                    ICIMFormSectionDetails formSectionDetail = rel.toInterface(ICIMFormSectionDetails.class);
                    if (formSectionDetail != null && this.isHintForProvidedFormPurposes(formSectionDetail.EffectFormPurpose(), requiredPurpose)) {
                        ICIMSection section = rel.GetEnd2().toInterface(ICIMSection.class);
                        IRelCollection sectionDisplayItemDetails = section.GetEnd1Relationships().GetRels(relDefinitionType.section2DisplayItems.toString(), true);
                        if (sectionDisplayItemDetails != null && sectionDisplayItemDetails.hasValue()) {
                            log.trace("get section " + section.Name() + " 's display Item(s) quantity:" + sectionDisplayItemDetails.size());
                            for (IObject sd : sectionDisplayItemDetails.toList().stream().sorted(this.objectOrderValueComparator()).collect(Collectors.toList())) {
                                IRel rel1 = sd.toInterface(IRel.class);
                                ICIMSectionDisplayItemDetails sectionDisplayItemDetail = rel1.toInterface(ICIMSectionDisplayItemDetails.class);
                                if (sectionDisplayItemDetail != null) {
                                    ICIMDisplayItem displayItem = rel1.GetEnd2().toInterface(ICIMDisplayItem.class);
                                    log.trace("construct display item for " + displayItem.SchemaDefinitionUID());
                                    ObjectItemDTO itemDTO = new ObjectItemDTO();
                                    displayItemWrapper displayItemWrapper = new displayItemWrapper(displayItem.ItemType(), displayItem.SchemaDefinitionUID());
                                    displayItemWrapper.doInit(formPurpose);
                                    itemDTO.setWidth(sectionDisplayItemDetail.Width());
                                    itemDTO.setDefUID(displayItemWrapper.getSchemaDefinition());
                                    itemDTO.setRelDirection(displayItemWrapper.getRelDirection());
                                    itemDTO.setMandatory(sectionDisplayItemDetail.IsRequired());
                                    itemDTO.setDefType(displayItemWrapper.getItemType());
                                    itemDTO.setLabel(sectionDisplayItemDetail.DisplayAs());
                                    itemDTO.setSectionOrderValue(formSectionDetail.OrderValue());
                                    itemDTO.setPropertyValueType(displayItemWrapper.getPropertyValueType().toString());
                                    itemDTO.setHidden(!sectionDisplayItemDetail.Visible());
                                    itemDTO.setGroupHeader(formSectionDetail.DisplayAs());
                                    itemDTO.setOrderValue(sectionDisplayItemDetail.OrderValue());
                                    itemDTO.setColumnSpan(sectionDisplayItemDetail.ColumnSpan());
                                    itemDTO.setReadOnly(sectionDisplayItemDetail.ReadOnly());
                                    this.tryToFillOptions(displayItemWrapper, itemDTO);
                                    this.setDefaultValue(itemDTO, displayItem, formPurpose);
                                    itemDTO.specialProgressForDisplayItem();
                                    result.add(itemDTO);
                                }
                            }
                        }
                    }
                }
            }
            log.info("form generation completed" + PerformanceUtility.stop(stopWatch));
            return result;
        }
        return null;
    }

    protected void tryToFillOptions(displayItemWrapper displayItem, ObjectItemDTO objectItemDTO) throws Exception {
        if (displayItem != null) {
            String schemaDefinition = displayItem.getPropertyDef();
            IObject item = CIMContext.Instance.ProcessCache().item(CommonUtility.toActualDefinition(schemaDefinition), domainInfo.SCHEMA.toString(), false);
            if (item == null)
                throw new Exception(schemaDefinition + " is not valid schema info as it is not exist in database");
            if (item.Interfaces().hasInterface(interfaceDefinitionType.IPropertyDef.toString())) {
                List<OptionItemDTO> options = item.toInterface(IPropertyDef.class).generateOptions();
                if (CommonUtility.hasValue(options))
                    objectItemDTO.getOptions().addAll(options);
            }
        }
    }

    @Override
    public void setInfoWithProvidedObject(ObjectDTO objectDTO, IObject o) {
        if (objectDTO != null && o != null) {
            for (ObjectItemDTO objectDTOItem : objectDTO.getItems()) {
                String defUid = objectDTOItem.getDefUID();
                String value = o.getValue(defUid);
                if (value != null && !StringUtils.isEmpty(value)) {
                    if (objectDTOItem.hasOptions()) {
                        String finalValue = value;
                        OptionItemDTO optionItemDTO = objectDTOItem.getOptions().stream().filter(c -> c.getName().equalsIgnoreCase(finalValue) || c.getUid().equalsIgnoreCase(finalValue)).findFirst().orElse(null);
                        if (optionItemDTO != null)
                            value = optionItemDTO.getName();
                    }
                    objectDTOItem.setDisplayValue(value);
                } else
                    objectDTOItem.setDisplayValue("");
            }
        }
    }

    @Override
    public ObjectDTO generatePopup(String formPurpose, IObject o) throws Exception {
        ObjectDTO form = this.generatePopup(formPurpose);
        if (o != null) {
            if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Create.toString())) {
            } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Update.toString())) {
                this.setInfoWithProvidedObject(form, o);
            } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Query.toString())) {
            } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Info.toString())) {
                this.setInfoWithProvidedObject(form, o);
                form.setAllPropertiesToBeReadOnly();
            }
        }
        return form;
    }
}
