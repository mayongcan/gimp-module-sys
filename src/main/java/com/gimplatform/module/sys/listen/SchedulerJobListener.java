package com.gimplatform.module.sys.listen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import com.gimplatform.core.service.SchedulerService;
import com.gimplatform.core.utils.SpringContextHolder;

/**
 * 定时任务监听器
 * @author zzd
 */
public class SchedulerJobListener implements JobListener {

    private static final Logger logger = LogManager.getLogger(SchedulerJobListener.class);

    private SchedulerService schedulerService = SpringContextHolder.getBean(SchedulerService.class);

    public String name;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Scheduler在JobDetail即将被执行，但又被TriggerListener否决了时调用
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        logger.info("[jobName:" + context.getJobDetail().getKey().getName() + ",jobGroup:" + context.getJobDetail().getKey().getGroup() + "] 已经被否决而且没有执行。");
    }

    /**
     * Scheduler在JobDetail将要被执行时调用此方法 说明：在执行任务前，在调度历史表中插入一条调度执行历史记录
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        logger.info("执行任务[" + context.getJobDetail().getKey().getName() + "," + context.getJobDetail().getKey().getGroup() + "]开始");
        schedulerService.saveJobHistory(context, "add");
    }

    /**
     * Scheduler在JobDetail被执行之后调用此方法 说明：在执行任务后，在调度历史表中更新对应的调度执行历史记录
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        logger.info("执行任务[" + context.getJobDetail().getKey().getName() + "," + context.getJobDetail().getKey().getGroup() + "]完成");
        schedulerService.saveJobHistory(context, "update");
    }

}
