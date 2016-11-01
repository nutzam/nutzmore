package net.wendal.quartzdemo.job;

import java.util.Date;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

// 这个类的cron表达式定义在custom/cron.properties
@IocBean
public class SayhiJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("hi... " + Times.sDTms(new Date()));
    }

}
