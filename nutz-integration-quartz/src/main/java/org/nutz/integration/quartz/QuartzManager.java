package org.nutz.integration.quartz;

import java.util.List;

import org.nutz.dao.pager.Pager;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger.TriggerState;

/**
 * Quartz管理器,统管Job的增删改查操作
 * @author wendal
 *
 */
public interface QuartzManager {

    /**
     * 查询计划任务列表
     * @param namePatten 需要匹配的任务名,可以是null, 代表全匹配
     * @param groupPatten 需要匹配的任务组名,可以是null, 代表全匹配
     * @param pager 分页,可以是null
     * @return 符合条件的计划任务列表
     */
    List<QuartzJob> query(String namePatten, String groupPatten, Pager pager);

    /**
     * 清除一个Job
     */
    boolean delete(QuartzJob qj);

    /**
     * 清除一个Job
     */
    boolean delete(JobKey jobKey);

    /**
     * 添加一个新Job
     * @param name 任务名称
     * @param group 任务分组
     * @param cron 计划任务表达式
     * @param klass Job类
     */
    void add(String name, String group, String cron, Class<?> klass);

    /**
     * 新增一个任务,如果存在就覆盖
     */
    void add(QuartzJob qj);

    /**
     * 是否存在特定的任务
     */
    boolean exist(QuartzJob qj);

    /**
     * 是否存在特定的任务
     */
    boolean exist(JobKey jobKey);

    /**
     * 恢复一个被暂停的任务
     */
    void resume(JobKey jobKey);

    /**
     * 恢复一个被暂停的任务
     */
    void resume(QuartzJob qj);

    /**
     * 清除所有的任务
     */
    void clear();

    /**
     * 暂停一个任务
     */
    void pause(QuartzJob qj);

    /**
     * 暂停一个任务
     */
    void pause(JobKey jobKey);

    /**
     * 触发一个中断, 对于的Job类必须实现InterruptableJob 
     */
    void interrupt(JobKey jobKey);

    /**
     * 触发一个中断, 对于的Job类必须实现InterruptableJob 
     */
    void interrupt(QuartzJob qj);

    /**
     * 获取一个Job的状态, 当前仅支持StdScheduler
     */
    TriggerState getState(QuartzJob qj);

    void setScheduler(Scheduler scheduler);
    
    QuartzJob fetch(String name, String group);

    //
    void cron(String cron, Class<?> klass);
    
    void cron(String cron, Class<?> klass, String name, String group);
}