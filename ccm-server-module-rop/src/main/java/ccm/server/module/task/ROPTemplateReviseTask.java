package ccm.server.module.task;

import ccm.server.model.LoaderReport;
import ccm.server.module.business.IROPRunningBusinessService;
import ccm.server.util.CommonUtility;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.concurrent.Callable;

public class ROPTemplateReviseTask implements Callable<LoaderReport> {

    private Integer percentage = 0;
    private String processingMsg = "等待开始...";
    private String startTime;
    private String endTime;
    private String timeSpan;
    private final IROPRunningBusinessService runningBusinessService;

    public ROPTemplateReviseTask(IROPRunningBusinessService runningBusinessService) {
        this.runningBusinessService = runningBusinessService;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public void setStartTime(Date date) {
        this.startTime = CommonUtility.formatDateWithDateFormat(date);
    }

    public void setEndTime(Date date) {
        this.endTime = CommonUtility.formatDateWithDateFormat(date);
    }

    public void setTimeSpan(StopWatch stopWatch) {
        this.timeSpan = stopWatch.getLastTaskTimeMillis() / 1000 + "秒";
    }

    public void setProcessingMsg(String processingMsg) {
        this.processingMsg = processingMsg;
    }

    public Integer getPercentage() {
        return this.percentage;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getTimeSpan() {
        return this.timeSpan;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public String getProcessingMsg() {
        return this.processingMsg;
    }

    @Override
    public LoaderReport call() {
        StopWatch stopWatch = new StopWatch();
        this.setStartTime(new Date());
        stopWatch.start();
        LoaderReport loaderReport = this.runningBusinessService.processROPTemplateRevise(this);
        this.setEndTime(new Date());
        stopWatch.stop();
        this.setTimeSpan(stopWatch);
        return loaderReport;
    }
}
