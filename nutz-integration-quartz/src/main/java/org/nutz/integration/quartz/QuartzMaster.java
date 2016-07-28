package org.nutz.integration.quartz;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;
import org.quartz.Trigger.TriggerState;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.matchers.GroupMatcher;

@IocBean(name="$quartz") // 仅为了声明名称.
public class QuartzMaster {

    @Inject Scheduler scheduler; // 这个注解只是表意,真正的配置在js里面
    
    public void cron(String cron, Class<?> klass) throws SchedulerException {
        String name = klass.getName();
        this.cron(cron, klass, name, Scheduler.DEFAULT_GROUP);
    }

    public void cron(String cron, Class<?> klass, String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        if (scheduler.checkExists(jobKey))
            delete(jobKey);
        scheduler.scheduleJob(Quartzs.makeJob(name, group, klass), Quartzs.makeCronTrigger(name, group, cron));
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
                        index ++;
                    }
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
        try {
            this.cron(cron, klass, name, group);
        }
        catch (SchedulerException e) {
            throw new RuntimeException(e);
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
}
