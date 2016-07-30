package org.nutz.integration.quartz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.dao.pager.Pager;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.UnableToInterruptJobException;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;

public class QuartzManagerImpl implements QuartzManager {

    protected Scheduler scheduler; // 通过注入得到
    
    public QuartzJob fetch(String name, String group) {
        try {
            JobKey jobKey = new JobKey(name, group);
            if (!scheduler.checkExists(jobKey))
                return null;
            JobDetail jd = scheduler.getJobDetail(jobKey);
            Trigger trigger = scheduler.getTrigger(new TriggerKey(name, group));
            QuartzJob qj = new QuartzJob(jobKey, trigger, jd);
            return qj;
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void cron(String cron, Class<?> klass) {
        String name = klass.getName();
        this.cron(cron, klass, name, Scheduler.DEFAULT_GROUP);
    }

    public void cron(String cron, Class<?> klass, String name, String group) {
        QuartzJob qj = new QuartzJob();
        qj.setClassName(klass.getName());
        qj.setJobName(name);
        qj.setJobGroup(group);
        qj.setCron(cron);
        add(qj);
    }
    
    // 类Dao方法

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#query(java.lang.String, java.lang.String, org.nutz.dao.pager.Pager)
     */
    public List<QuartzJob> query(String namePatten, String groupPatten, Pager pager) {
        try {
            int offset = pager == null ? 0 : pager.getOffset();
            int size = pager == null ? 0 : pager.getPageSize();
            int index = 0;
            Pattern nameP = Strings.isBlank(namePatten) ? null : Pattern.compile(namePatten);
            Pattern groupP = Strings.isBlank(groupPatten) ? null : Pattern.compile(groupPatten);
            List<QuartzJob> jobs = new ArrayList<QuartzJob>();
            for (String groupName : scheduler.getJobGroupNames()) {
                if (groupName != null && groupP != null && !groupP.matcher(groupName).find())
                    continue;
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    if (nameP != null && !nameP.matcher(jobKey.getName()).find())
                        continue;
                    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    if (size == 0 || (index >= offset && index < (offset+size))) {
                        jobs.add(new QuartzJob(jobKey, triggers.get(0), scheduler.getJobDetail(jobKey)));
                    }
                    index ++;
                }
            }
            return jobs;
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#delete(org.nutz.integration.quartz.QuartzJob)
     */
    public boolean delete(QuartzJob qj) {
        return delete(qj.getJobKey());
    }

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#delete(org.quartz.JobKey)
     */
    public boolean delete(JobKey jobKey) {
        try {
            if (scheduler.checkExists(jobKey))
                return scheduler.deleteJob(jobKey);
            return false;
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#add(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
     */
    public void add(String name, String group, String cron, Class<?> klass) {
        this.cron(cron, klass, name, group);
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#add(org.nutz.integration.quartz.QuartzJob)
     */
    public void add(QuartzJob qj) {
        try {
            Class<?> klass = Class.forName(qj.getClassName());
            JobKey jobKey = qj.getJobKey();
            Trigger trigger = qj.getTrigger();
            Set<Trigger> triggers = new HashSet<Trigger>();
            triggers.add(trigger);
            NutMap tmp = null;
            if (!Strings.isBlank(qj.getDataMap()))
                tmp = Json.fromJson(NutMap.class, qj.getDataMap());
            JobDataMap data = tmp == null ? new JobDataMap() : new JobDataMap(tmp);
            scheduler.scheduleJob(Quartzs.makeJob(jobKey, klass, data), triggers, true);
        }
        catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
        catch (SchedulerException e) {
            throw Lang.wrapThrow(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#exist(org.nutz.integration.quartz.QuartzJob)
     */
    public boolean exist(QuartzJob qj) {
        return exist(qj.getJobKey());
    }

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#exist(org.quartz.JobKey)
     */
    public boolean exist(JobKey jobKey) {
        try {
            return scheduler.checkExists(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#resume(org.quartz.JobKey)
     */
    public void resume(JobKey jobKey) {
        try {
            scheduler.resumeJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#resume(org.nutz.integration.quartz.QuartzJob)
     */
    public void resume(QuartzJob qj) {
        resume(qj.getJobKey());
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#clear()
     */
    public void clear() {
        try {
            scheduler.clear();
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#pause(org.nutz.integration.quartz.QuartzJob)
     */
    public void pause(QuartzJob qj) {
        this.pause(qj.getJobKey());
    }

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#pause(org.quartz.JobKey)
     */
    public void pause(JobKey jobKey) {
        try {
            scheduler.pauseJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#interrupt(org.quartz.JobKey)
     */
    public void interrupt(JobKey jobKey) {
        try {
            scheduler.interrupt(jobKey);
        }
        catch (UnableToInterruptJobException e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#interrupt(org.nutz.integration.quartz.QuartzJob)
     */
    public void interrupt(QuartzJob qj) {
        interrupt(qj.getJobKey());
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#getState(org.nutz.integration.quartz.QuartzJob)
     */
    public TriggerState getState(QuartzJob qj) {
        try {
            if (scheduler instanceof StdScheduler) {
                QuartzScheduler qs = (QuartzScheduler)Mirror.me(scheduler).getEjecting("sched").eject(scheduler);
                return qs.getTriggerState(qj.getTrigger().getKey());
            }
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        throw Lang.noImplement();
    }
    
    /* (non-Javadoc)
     * @see org.nutz.integration.quartz.QuartzManager#setScheduler(org.quartz.Scheduler)
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
}
