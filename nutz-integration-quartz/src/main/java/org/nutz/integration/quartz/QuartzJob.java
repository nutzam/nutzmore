package org.nutz.integration.quartz;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@Table("t_quartz_job")
public class QuartzJob {
    
    @Id
    protected int id;
    
    @Column("jn")
    protected String jobName;
    @Column("jg")
    protected String jobGroup;
    @Column
    protected String cron;
    @Column
    protected String scheduled;
    @Column("klass")
    protected String className;
    @Column("dm")
    protected String dataMap;
    @Column("cm")
    protected String comment;
    
    public QuartzJob() {}
    
    public QuartzJob(JobKey jobKey, Trigger trigger, JobDetail jobDetail) {
        setJobKey(jobKey);
        setTrigger(trigger);
        className = jobDetail.getJobClass().getName();
        dataMap = Json.toJson(jobDetail.getJobDataMap(), JsonFormat.compact());
    }
    
    public void setJobKey(JobKey jobKey) {
        setJobName(jobKey.getName());
        setJobGroup(jobKey.getGroup());
    }
    
    public JobKey getJobKey() {
        return JobKey.jobKey(jobName, jobGroup);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String schedule) {
        this.scheduled = schedule;
    }

    public Trigger getTrigger() {
        if (Strings.isBlank(cron)) {
            NutMap map = Json.fromJson(NutMap.class, scheduled);
            return Quartzs.makeSimpleTrigger(jobName, jobGroup, map.getInt("rate"), map.getInt("count"), map.getLong("delay") ,map.getTime("startTime"),map.getTime("endTime"));
        } else {
            return Quartzs.makeCronTrigger(jobName, jobGroup, cron);
        }
    }

    public void setTrigger(Trigger trigger) {
        if (trigger instanceof CronTrigger) {
            cron = ((CronTrigger)trigger).getCronExpression();
        } else if (trigger instanceof SimpleTrigger) {
            // TODO 怎么玩
            NutMap tmp = new NutMap();
            SimpleTrigger st = (SimpleTrigger)trigger;
            tmp.put("rate", st.getRepeatInterval());
            tmp.put("count", st.getRepeatCount());
            tmp.put("startTime",st.getStartTime());
            tmp.put("endTime",st.getEndTime());
            scheduled = Json.toJson(tmp, JsonFormat.compact());
        }
    }
    
    public TriggerKey getTriggerKey() {
        return new TriggerKey(jobName, jobGroup);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDataMap() {
        return dataMap;
    }

    public void setDataMap(String dataMap) {
        this.dataMap = dataMap;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
