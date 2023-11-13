package org.jeecg.modules.flowable.listener.task;

import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.operator;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.HandoverUtils;
import ccm.server.utils.SchemaUtility;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/10/18 17:18
 */
@Component
public class HandoverPackageTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        Object objectUid = delegateTask.getVariable("objectUid");
        Object approved = delegateTask.getVariable("approved");
        if (null != objectUid && StringUtils.hasText(objectUid.toString())) {
            updateStatus(objectUid.toString(), Boolean.TRUE.equals(approved));
        }
    }

    private void updateStatus(String objectUid, boolean status) {
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest start = iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(start, HandoverUtils.CCM_HANDOVER_MAIL);
        iQueryEngine.addUIDForQuery(start, operator.equal, objectUid);
        IObject iObject = iQueryEngine.queryOne(start);
        if (null == iObject) {
            throw new RuntimeException("未找到对应移交单对象!");
        }
        try {
            SchemaUtility.beginTransaction();
            iObject.BeginUpdate();
            iObject.setValue(HandoverUtils.PROPERTY_IS_ALLOW_PACKAGING, status);
            iObject.FinishUpdate();
            SchemaUtility.commitTransaction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
