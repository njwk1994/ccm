package ccm.server.queue.service;

import ccm.server.queue.handler.IQueueTaskHandler;
import ccm.server.util.CommonUtility;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QueueGenerationService {

    public static QueueGenerationService Instance;
    // LinkedBlockingQueue构造的时候若没有指定大小，则默认大小为Integer.MAX_VALUE
    private final LinkedBlockingQueue<IQueueTaskHandler> tasks = new LinkedBlockingQueue<>(50000);

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<IQueueTaskHandler>> ropTemplateReviseProcessingTasks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<IQueueTaskHandler>> docRetrieveProcessingTasks = new ConcurrentHashMap<>();
    // 类似于一个线程总管 保证所有的任务都在队列之中
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    // 检查服务是否运行
    //private volatile boolean running = true;
    //线程状态
    // private Future<?> serviceThreadStatus = null;

    @PostConstruct
    public void init() {
        Instance = this;
//        serviceThreadStatus = service.submit(new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (running) {
//                    try {
//                        //开始一个任务
//                        QueueTaskHandler task = tasks.take();
//                        try {
//                            task.process();
//                        } catch (Exception e) {
//                            log.error("任务处理发生错误", e);
//                        }
//                    } catch (InterruptedException e) {
//                        log.error("服务停止，退出", e);
//                        running = false;
//                    }
//                }
//            }
//        }, "save data thread"));
    }

    public void addData(IQueueTaskHandler dataHandler) throws InterruptedException {
//        if (!running) {
//            log.warn("service is stop");
//            return false;
//        }
        //offer 队列已经满了，无法再加入的情况下
        tasks.put(dataHandler);
        log.info(Thread.currentThread().getName() + "---------------------接受发布数据队列添加任务完成,开始执行任务");
        this.execute();
    }

    private void execute() throws InterruptedException {
        while (this.tasks.size() > 0) {
            IQueueTaskHandler task = this.tasks.take();
            if (task.type().equalsIgnoreCase("ropTemplateRevise")) {
                if (this.ropTemplateReviseProcessingTasks.containsKey(task.creationUser())) {
                    List<IQueueTaskHandler> queueTaskHandlers = this.ropTemplateReviseProcessingTasks.get(task.creationUser());
                    queueTaskHandlers.add(task);
                } else {
                    this.ropTemplateReviseProcessingTasks.put(task.creationUser(), new CopyOnWriteArrayList<IQueueTaskHandler>() {{
                        add(task);
                    }});
                }
            } else {
                if (this.docRetrieveProcessingTasks.containsKey(task.creationUser())) {
                    List<IQueueTaskHandler> queueTaskHandlers = this.docRetrieveProcessingTasks.get(task.creationUser());
                    queueTaskHandlers.add(task);
                } else {
                    this.docRetrieveProcessingTasks.put(task.creationUser(), new CopyOnWriteArrayList<IQueueTaskHandler>() {{
                        add(task);
                    }});
                }
            }
            executorService.execute(() -> {
                try {
                    log.info(Thread.currentThread().getName() + "---------------------队列开始执行任务-------------------");
                    task.execute();
                    //从执行的容器中移除,添加进历史容器
                    removeTaskFromProcessingContainer(task);
                    addTaskIntoHistoryContainer(task);
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    log.error("队列执行错误:", ex);
                }
            });
        }
    }

    private void removeTaskFromProcessingContainer(IQueueTaskHandler task) {
        if (task.type().equalsIgnoreCase("ropTemplateRevise")) {
            if (this.ropTemplateReviseProcessingTasks.containsKey(task.creationUser())) {
                this.ropTemplateReviseProcessingTasks.get(task.creationUser()).remove(task);
            }
        } else {
            if (this.docRetrieveProcessingTasks.containsKey(task.creationUser())) {
                this.docRetrieveProcessingTasks.get(task.creationUser()).remove(task);
            }
        }
    }

    private void addTaskIntoHistoryContainer(IQueueTaskHandler queueTaskHandler) {
        if (queueTaskHandler.type().equalsIgnoreCase("ropTemplateRevise")) {
            if (redisTemplate.hasKey(CommonUtility.ROP_REVISE_TASK_KEY)) {
                JSONObject jsonObject = JSONObject.parseObject((String) redisTemplate.opsForValue().get(CommonUtility.ROP_REVISE_TASK_KEY));
                if (null == jsonObject) {
                    jsonObject = new JSONObject();
                }
                if (jsonObject.containsKey(queueTaskHandler.creationUser())) {
                    List<JSONObject> iQueueTaskHandlers = CommonUtility.toJSONObjList(jsonObject.getJSONArray(queueTaskHandler.creationUser()));
                    iQueueTaskHandlers.add(JSONObject.parseObject(JSON.toJSONString(queueTaskHandler)));
                    jsonObject.put(queueTaskHandler.creationUser(), iQueueTaskHandlers);
                } else {
                    jsonObject.put(queueTaskHandler.creationUser(), new ArrayList<JSONObject>() {{
                        add(JSONObject.parseObject(JSON.toJSONString(queueTaskHandler)));
                    }});
                }
                redisTemplate.opsForValue().set(CommonUtility.ROP_REVISE_TASK_KEY, jsonObject.toJSONString());
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(queueTaskHandler.creationUser(), new ArrayList<JSONObject>() {{
                    add(JSONObject.parseObject(JSON.toJSONString(queueTaskHandler)));
                }});
                redisTemplate.opsForValue().set(CommonUtility.ROP_REVISE_TASK_KEY, jsonObject.toJSONString());
            }
        } else {
            if (redisTemplate.hasKey(CommonUtility.DOC_RETRIEVE_TASK_KEY)) {
                JSONObject jsonObject = JSONObject.parseObject((String) redisTemplate.opsForValue().get(CommonUtility.DOC_RETRIEVE_TASK_KEY));
                if (null == jsonObject) {
                    jsonObject = new JSONObject();
                }
                if (jsonObject.containsKey(queueTaskHandler.creationUser())) {
                    List<JSONObject> iQueueTaskHandlers = CommonUtility.toJSONObjList(jsonObject.getJSONArray(queueTaskHandler.creationUser()));
                    iQueueTaskHandlers.add(JSONObject.parseObject(JSON.toJSONString(queueTaskHandler)));
                    jsonObject.put(queueTaskHandler.creationUser(), iQueueTaskHandlers);
                } else {
                    jsonObject.put(queueTaskHandler.creationUser(), new ArrayList<JSONObject>() {{
                        add(JSONObject.parseObject(JSON.toJSONString(queueTaskHandler)));
                    }});
                }
                redisTemplate.opsForValue().set(CommonUtility.DOC_RETRIEVE_TASK_KEY, jsonObject.toJSONString());
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(queueTaskHandler.creationUser(), new ArrayList<JSONObject>() {{
                    add(JSONObject.parseObject(JSON.toJSONString(queueTaskHandler)));
                }});
                redisTemplate.opsForValue().set(CommonUtility.DOC_RETRIEVE_TASK_KEY, jsonObject.toJSONString());
            }
        }
    }

    public IQueueTaskHandler getROPTemplateReviseProcessingHandler(String pstrUUID, String creationUser) {
        if (!StringUtils.isEmpty(pstrUUID) && !StringUtils.isEmpty(creationUser)) {
            List<IQueueTaskHandler> queueTaskHandlers = this.ropTemplateReviseProcessingTasks.get(creationUser);
            if (CommonUtility.hasValue(queueTaskHandlers)) {
                return queueTaskHandlers.stream().filter(r -> r.UUID().equalsIgnoreCase(pstrUUID)).findFirst().orElse(null);
            }
        }
        return null;
    }

    public List<IQueueTaskHandler> getROPTemplateReviseProcessingHandler(String config) {
        List<IQueueTaskHandler> lcolResult = new ArrayList<>();
        if (this.ropTemplateReviseProcessingTasks.size() > 0) {
            for (List<IQueueTaskHandler> value : this.ropTemplateReviseProcessingTasks.values()) {
                lcolResult.addAll(value);
            }
        }
        if (!StringUtils.isEmpty(config)) {
            lcolResult = lcolResult.stream().filter(r -> config.equalsIgnoreCase(r.config())).collect(Collectors.toList());
        }
        return lcolResult;
    }

    public JSONArray getHisTasks(String pstrType) {
        if (!StringUtils.isEmpty(pstrType)) {
            if (pstrType.equalsIgnoreCase("ropTemplateRevise")) {
                if (redisTemplate.hasKey(CommonUtility.ROP_REVISE_TASK_KEY)) {
                    JSONArray container = new JSONArray();
                    JSONObject jsonObject = JSONObject.parseObject((String) redisTemplate.opsForValue().get(CommonUtility.ROP_REVISE_TASK_KEY));
                    if (null == jsonObject)
                        return container;
                    for (String creationUser : jsonObject.keySet()) {
                        JSONArray tasks = jsonObject.getJSONArray(creationUser);
                        container.addAll(tasks);
                    }
                    return container;
                }
            } else {
                if (redisTemplate.hasKey(CommonUtility.DOC_RETRIEVE_TASK_KEY)) {
                    JSONArray container = new JSONArray();
                    JSONObject jsonObject = JSONObject.parseObject((String) redisTemplate.opsForValue().get(CommonUtility.DOC_RETRIEVE_TASK_KEY));
                    if (null == jsonObject)
                        return container;
                    for (String creationUser : jsonObject.keySet()) {
                        JSONArray tasks = jsonObject.getJSONArray(creationUser);
                        container.addAll(tasks);
                    }
                    return container;
                }
            }
        }
        return null;
    }

    public IQueueTaskHandler getDocRetrieveProcessingHandler(String pstrUUID, String creationUser) {
        if (!StringUtils.isEmpty(pstrUUID) && !StringUtils.isEmpty(creationUser)) {
            List<IQueueTaskHandler> queueTaskHandlers = this.docRetrieveProcessingTasks.get(creationUser);
            if (CommonUtility.hasValue(queueTaskHandlers)) {
                return queueTaskHandlers.stream().filter(r -> r.UUID().equalsIgnoreCase(pstrUUID)).findFirst().orElse(null);
            }
        }
        return null;
    }

    public List<IQueueTaskHandler> getDocRetrieveProcessingHandler(String config) {
        List<IQueueTaskHandler> lcolResult = new ArrayList<>();
        if (this.docRetrieveProcessingTasks.size() > 0) {
            for (List<IQueueTaskHandler> value : this.docRetrieveProcessingTasks.values()) {
                lcolResult.addAll(value);
            }
        }
        if (!StringUtils.isEmpty(config)) {
            lcolResult = lcolResult.stream().filter(r -> config.equalsIgnoreCase(r.config())).collect(Collectors.toList());
        }
        return lcolResult;
    }

    public void clearROPTemplateReviseProcessingTasks(String pstrLoginUser) {
        if (!StringUtils.isEmpty(pstrLoginUser))
            this.ropTemplateReviseProcessingTasks.remove(pstrLoginUser);
    }


    //判断队列是否有任务
//    public boolean isEmpty() {
//        return tasks.isEmpty();
//    }

//    public boolean checkServiceRun() {
//        return running && !service.isShutdown() && !serviceThreadStatus.isDone();
//    }
//
//    public void activeService() {
//        running = true;
//        if (service.isShutdown()) {
//            service = Executors.newSingleThreadExecutor();
//            init();
//            log.info("线程池关闭，重新初始化线程池及任务");
//        }
//        if (serviceThreadStatus.isDone()) {
//            init();
//            log.info("线程池任务结束，重新初始化任务");
//        }
//    }
//
//    @PreDestroy
//    public void destory() {
//        running = false;
//        service.shutdownNow();
//    }
}
