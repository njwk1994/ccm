package org.jeecg.modules.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/3/4 18:27
 */
public class TableCreate {

    public static void main(String[] args) {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration();
        cfg.setJdbcDriver(args[0]);
        cfg.setJdbcUrl(args[1]);
        cfg.setJdbcUsername(args[2]);
        cfg.setJdbcPassword(args[3]);
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        //cfg.setDatabaseTablePrefix("myflowable");
        ProcessEngine processEngine = cfg.buildProcessEngine();
    }
}
