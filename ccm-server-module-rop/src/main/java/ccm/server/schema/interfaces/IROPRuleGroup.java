package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

import java.util.List;
import java.util.Map;

public interface IROPRuleGroup extends IObject {

    String ROPGroupClassDefinitionUID();

    void setROPGroupClassDefinitionUID(String value) throws Exception;

    Integer ROPGroupOrder();

    void setROPGroupItemsHasUpdated(boolean hasUpdated) throws Exception;

    boolean ROPGroupItemsHasUpdated();

    void setROPGroupWorkStepHasUpdated(boolean workStepHasUpdated) throws Exception;

    boolean ROPGroupWorkStepHasUpdated();

    void setROPHasHandleChange(boolean hasHandleChange) throws Exception;

    boolean ROPHasHandleChange();

    void setROPGroupItemRevState(String ropGroupRevState) throws Exception;

    String ROPGroupItemRevState();

    void setROPGroupWorkStepRevState(String ropGroupWorkStepRevState) throws Exception;

    String ROPGroupWorkStepRevState();

    void setROPGroupOrder(int value) throws Exception;

    boolean isHintForProvidedObject(IObject pobjDesignObj) throws Exception;

    IObjectCollection getItems();

    IObjectCollection getROPWorkSteps() throws Exception;

    Map<IEnumEnum, IObject> getIssueSteps() throws Exception;

    Map<IEnumEnum, List<IObject>> getROPWorkStepsGroupByPhase() throws Exception;

    void deleteItemsAndStep() throws Exception;

    void setROPInitStatus(boolean needBeginUpdate) throws Exception;

    void setROPRuleGroupHasHandleChangedStatus(boolean needBeginUpdate) throws Exception;

    boolean hasIssueStep() throws Exception;

    void setROPGroupStatusByWorkStepChanged(boolean workStepHasChanged, boolean needBeginUpdate) throws Exception;

    void setROPGroupStatusByItemsChanged(boolean itemsHasChanged, boolean needBeginUpdate) throws Exception;

}
