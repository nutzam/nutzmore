package org.nutz.integration.quartz;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

public class Quartzs {

    
    public static CronTrigger makeCronTrigger(String jobName, String jobGroup, String cron) {
        return TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
    }
    
    public static SimpleTrigger makeSimpleTrigger(String jobName, String jobGroup, int fixedRate, int count, long initialDelay ) {
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
        if (fixedRate > 0)
            schedule.withIntervalInSeconds(fixedRate);
        if (count > 0) {
            schedule.withRepeatCount(count);
        } else {
            schedule.repeatForever();
        }
        TriggerBuilder<SimpleTrigger> trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(schedule);
        if (initialDelay > 0) 
            trigger.startAt(new Date(System.currentTimeMillis() + initialDelay*1000));
        return trigger.build();
    }
    
    @SuppressWarnings("unchecked")
    public static JobDetail makeJob(String jobName, String jobGroup, Class<?> klass) {
        return JobBuilder.newJob((Class<? extends Job>) klass).withIdentity(jobName, jobGroup).build();
    }
}
