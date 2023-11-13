    package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

public interface IROPCalculate extends IObject {

    void generateWorkStep() throws Exception;//生成工作步骤

    IObjectCollection getWorkSteps(String stageName) throws Exception;//获取工作步骤

    Object calculate(String stageName) throws Exception;//计算预估权重

    Boolean isConsumesMaterial(String stageName) throws Exception;//是否耗材

    IObjectCollection getAllWorkStep() throws Exception;
}
