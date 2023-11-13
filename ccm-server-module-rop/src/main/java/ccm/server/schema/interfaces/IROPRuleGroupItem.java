package ccm.server.schema.interfaces;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;

public interface IROPRuleGroupItem extends IObject {

    String ROPTargetPropertyDefinitionUID();

    void setROPTargetPropertyDefinitionUID(String value) throws Exception;

    String ROPCalculationValue();

    void setROPCalculationValue(String value) throws Exception;

    String ROPTargetPropertyValueUoM();

    void setROPTargetPropertyValueUoM(String value) throws Exception;

    IROPRuleGroup getGroup() throws Exception;

    boolean isHintForProvidedObject(IObject object) throws Exception;

    String generateCalculatePropAndValueStr();

    String generateCalculatePropAndValueStr(@NotNull JSONObject jsonObject);
}
