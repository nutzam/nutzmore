package net.wendal.quartzdemo.job;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@IocBean
@Scheduled(cron="15 * * * * ?") //直接使用注解来声明cron
public class SexyJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Just do it");
    }

}
