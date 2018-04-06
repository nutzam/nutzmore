package org.nutz.integration.quartz;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

public class Quartzs {

    public static void cron(Scheduler scheduler, String cron, Class<?> klass) {
        try {
            String name = klass.getName();
            String group = Scheduler.DEFAULT_GROUP;
            JobKey jobKey = new JobKey(name, group);
            if (scheduler.checkExists(jobKey))
                scheduler.deleteJob(jobKey);
            scheduler.scheduleJob(makeJob(jobKey, klass), makeCronTrigger(name, group, cron));
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void cron(Scheduler scheduler, String cron, Class<?> klass, JobDataMap data,String name,String group) {
        try {
            JobKey jobKey = new JobKey(name, group);
            if (scheduler.checkExists(jobKey))
                scheduler.deleteJob(jobKey);
            scheduler.scheduleJob(makeJob(jobKey, klass,data), makeCronTrigger(name, group, cron));
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void simple(Scheduler scheduler, Class<?> klass, int fixedRate, int count, long initialDelay) {
        try {
            String name = klass.getName();
            String group = Scheduler.DEFAULT_GROUP;
            JobKey jobKey = new JobKey(name, group);
            if (scheduler.checkExists(jobKey))
                scheduler.deleteJob(jobKey);
            scheduler.scheduleJob(makeJob(jobKey, klass), makeSimpleTrigger(name, group, fixedRate, count, initialDelay));
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static CronTrigger makeCronTrigger(String jobName, String jobGroup, String cron) {
        return TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
    }
    
    public static SimpleTrigger makeSimpleTrigger(String jobName, String jobGroup, int fixedRate, int count, long initialDelay) {
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
    
    public static JobDetail makeJob(String jobName, String jobGroup, Class<?> klass) {
        return makeJob(new JobKey(jobName, jobGroup), klass);
    }
    
    public static JobDetail makeJob(String jobName, String jobGroup, Class<?> klass, JobDataMap data) {
        return makeJob(new JobKey(jobName, jobGroup), klass, data);
    }
    
    public static JobDetail makeJob(JobKey jobKey, Class<?> klass) {
        return makeJob(jobKey, klass, null);
    }
    
    @SuppressWarnings("unchecked")
    public static JobDetail makeJob(JobKey jobKey, Class<?> klass, JobDataMap data) {
        if (data == null)
            data = new JobDataMap();
        JobBuilder jb = JobBuilder.newJob((Class<? extends Job>) klass).withIdentity(jobKey);
        if (data != null)
            try {
                jb.setJobData(data);
            }
            catch (NoSuchMethodError e) {
                // nop
            }
        return jb.build();
    }
}
