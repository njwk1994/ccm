package org.jeecg.modules.flowable.listener.task;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 通用任务监听器
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/10/18 14:36
 */
@Component
public class GeneralTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        Object objectUid = delegateTask.getVariable("objectUid");
    }
}
