package ccm.server.schema.interfaces.generated;

import ccm.server.enums.relDefinitionType;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.ICIMDisplayItem;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.ICIMSection;
import ccm.server.schema.interfaces.IRel;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ICIMSectionBase extends InterfaceDefault implements ICIMSection {
    public ICIMSectionBase(boolean instantiateRequiredProperties) {
        super("ICIMSection", instantiateRequiredProperties);
    }

    @Override
    public List<ICIMDisplayItem> getOrderedDisplayItems() throws Exception {
        List<ICIMDisplayItem> result = new ArrayList<>();
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.section2DisplayItems.toString());
        if (relCollection != null && relCollection.hasValue()) {
            List<IRel> iRels = relCollection.toList().stream().map(c -> c.toInterface(IRel.class)).sorted(new Comparator<IRel>() {
                @SneakyThrows
                @Override
                public int compare(IRel o1, IRel o2) {
                    return Integer.compare(o1.OrderValue(), o2.OrderValue());
                }
            }).collect(Collectors.toList());
            for (IRel rel : iRels) {
                result.add(rel.GetEnd2().toInterface(ICIMDisplayItem.class));
            }
        }
        return result;
    }

    @Override
    public ICIMForm getForm() throws Exception {
        IRel rel = this.GetEnd2Relationships().GetRel(relDefinitionType.form2Sections.toString());
        if (rel != null)
            return rel.GetEnd1().toInterface(ICIMForm.class);
        return null;
    }

    @Override
    public String LabelName() {
        IProperty property = this.getProperty("ICIMSection", "LabelName");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setLabelName(String value) throws Exception {
        this.Interfaces().item("ICIMSection", true).Properties().item("LabelName", true).setValue(value);
    }

    @Override
    public IObjectCollection getDisplayItems() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.section2DisplayItems.toString());
        if (relCollection != null && relCollection.hasValue())
            return relCollection.GetEnd2s();
        return null;
    }
}
