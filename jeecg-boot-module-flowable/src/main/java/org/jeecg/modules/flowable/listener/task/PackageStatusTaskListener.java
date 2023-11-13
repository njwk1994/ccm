package org.jeecg.modules.flowable.listener.task;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.impl.el.FixedValue;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/10/17 17:03
 */
@Component
public class PackageStatusTaskListener implements TaskListener {

    private FixedValue status;

    @Override
    public void notify(DelegateTask delegateTask) {
        Map<String, Object> variables = delegateTask.getVariables();
        Object objectUid = delegateTask.getVariable("objectUid");

    }
}
