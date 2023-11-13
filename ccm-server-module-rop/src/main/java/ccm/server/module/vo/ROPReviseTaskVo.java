package ccm.server.module.vo;

import ccm.server.module.task.ROPTemplateReviseTask;
import ccm.server.queue.handler.IQueueTaskHandler;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class ROPReviseTaskVo implements Serializable {

    private final static long serializableId = 1L;


    private String uuid;

    private String message;

    private Integer percentage;

    private String startTime;
    private String config;
    private String endTime;

    private String timeSpan;
    private String creationUser;

    public ROPReviseTaskVo() {

    }

    public ROPReviseTaskVo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.uuid = jsonObject.getString("uuid");
            this.creationUser = jsonObject.getString("creationUser");
            this.config = jsonObject.getString("config");
            JSONObject callable = jsonObject.getJSONObject("callable");
            if (callable != null) {
                this.endTime = callable.getString("endTime");
                this.startTime = callable.getString("startTime");
                this.timeSpan = callable.getString("timeSpan");
                this.percentage = callable.getInteger("percentage");
                this.message = callable.getString("processingMsg");

            }
        }
    }

    public ROPReviseTaskVo(IQueueTaskHandler queueTaskHandler) {
        if (queueTaskHandler != null) {
            this.setUuid(queueTaskHandler.UUID());
            this.setConfig(queueTaskHandler.config());
            this.setCreationUser(queueTaskHandler.creationUser());
            ROPTemplateReviseTask task = (ROPTemplateReviseTask) queueTaskHandler.Task();
            this.setMessage(task.getProcessingMsg());
            this.setPercentage(task.getPercentage());
            this.setStartTime(task.getStartTime());
            this.setEndTime(task.getEndTime());
            this.setTimeSpan(task.getTimeSpan());
        }
    }
}
