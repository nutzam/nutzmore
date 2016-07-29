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
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.UnableToInterruptJobException;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;

/**
 * Quartz管理器,统管Job的增删改查操作
 * @author wendal
 *
 */
public class QuartzManager {

    protected Scheduler scheduler; // 通过注入得到
    
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

    /**
     * 查询计划任务列表
     * @param namePatten 需要匹配的任务名,可以是null, 代表全匹配
     * @param groupPatten 需要匹配的任务组名,可以是null, 代表全匹配
     * @param pager 分页,可以是null
     * @return 符合条件的计划任务列表
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
    
    /**
     * 清除一个Job
     */
    public boolean delete(QuartzJob qj) {
        return delete(qj.getJobKey());
    }

    /**
     * 清除一个Job
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

    /**
     * 添加一个新Job
     * @param name 任务名称
     * @param group 任务分组
     * @param cron 计划任务表达式
     * @param klass Job类
     */
    public void add(String name, String group, String cron, Class<?> klass) {
        this.cron(cron, klass, name, group);
    }
    
    /**
     * 新增一个任务,如果存在就覆盖
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
    
    /**
     * 是否存在特定的任务
     */
    public boolean exist(QuartzJob qj) {
        return exist(qj.getJobKey());
    }

    /**
     * 是否存在特定的任务
     */
    public boolean exist(JobKey jobKey) {
        try {
            return scheduler.checkExists(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 恢复一个被暂停的任务
     */
    public void resume(JobKey jobKey) {
        try {
            scheduler.resumeJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 恢复一个被暂停的任务
     */
    public void resume(QuartzJob qj) {
        resume(qj.getJobKey());
    }
    
    /**
     * 清除所有的任务
     */
    public void clear() {
        try {
            scheduler.clear();
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 暂停一个任务
     */
    public void pause(QuartzJob qj) {
        this.pause(qj.getJobKey());
    }

    /**
     * 暂停一个任务
     */
    public void pause(JobKey jobKey) {
        try {
            scheduler.pauseJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 触发一个中断, 对于的Job类必须实现InterruptableJob 
     */
    public void interrupt(JobKey jobKey) {
        try {
            scheduler.interrupt(jobKey);
        }
        catch (UnableToInterruptJobException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 触发一个中断, 对于的Job类必须实现InterruptableJob 
     */
    public void interrupt(QuartzJob qj) {
        interrupt(qj.getJobKey());
    }
    
    /**
     * 获取一个Job的状态, 当前仅支持StdScheduler
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
    
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
}
