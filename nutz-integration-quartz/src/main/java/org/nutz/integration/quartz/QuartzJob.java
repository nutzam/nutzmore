package org.nutz.integration.quartz;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.triggers.CronTriggerImpl;

public class QuartzJob {

    protected JobKey jobKey;
    protected List<? extends Trigger> triggers;

    public QuartzJob() {}
    
    public QuartzJob(JobKey jobKey, List<? extends Trigger> triggers) {
        super();
        this.jobKey = jobKey;
        this.triggers = triggers;
    }



    public JobKey getJobKey() {
        return jobKey;
    }
    public void setJobKey(JobKey jobKey) {
        this.jobKey = jobKey;
    }
    public List<? extends Trigger> getTriggers() {
        return triggers;
    }
    public void setTriggers(List<? extends Trigger> triggers) {
        this.triggers = triggers;
    }
    
    // 帮助方法
    public String getCron() {
        for (Trigger trigger : triggers) {
            ScheduleBuilder<? extends Trigger> sb = trigger.getScheduleBuilder();
            if (sb instanceof CronScheduleBuilder) {
                CronScheduleBuilder csb = (CronScheduleBuilder)sb;
                CronTriggerImpl cti = (CronTriggerImpl)csb.build();
                return cti.getCronExpression();
            }
        }
        return null;
    }
    
    public String getName() {
        return jobKey.getName();
    }
    
    public String getGroup() {
        return jobKey.getGroup();
    }
}
