package org.nutz.integration.quartz;

import java.util.Date;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

public class NutQuartzCronJobFactory {
	
	private static final Log log = Logs.get();

	protected PropertiesProxy conf;
	
	protected Scheduler scheduler;
	
	public void init() throws Exception {
		String prefix = "cron.";
		for (String key : conf.getKeys()) {
			if (key.length() < prefix.length()+1 || !key.startsWith(prefix))
				continue;
			String name = key.substring(prefix.length());
			if ("pkgs".equals(name)) {
			    for (String pkg : Strings.splitIgnoreBlank(name, ",")) {
                    addPackage(pkg);
                }
			}
			String cron = conf.get(key);
			log.debugf("job define name=%s cron=%s", name, cron);
			Class<?> klass = null;
			if (name.contains(".")) {
				klass = Lang.loadClass(name);
			} else {
				klass = Lang.loadClass(getClass().getPackage().getName() + ".job." + name);
			}
			cron(cron, klass);
		}
	}

	public void addPackage(String pkg) {
	    for (Class<?> klass : Scans.me().scanPackage(pkg)) {
	        Scheduled scheduled = klass.getAnnotation(Scheduled.class);
            if (scheduled != null) {
                try {
                    add(klass, scheduled);
                }
                catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            }
        }
	}
	
	@SuppressWarnings("unchecked")
    public void add(Class<?> klass, Scheduled scheduled) throws SchedulerException {
	    if (!Strings.isBlank(scheduled.cron())) {
	        try {
                cron(scheduled.cron(), klass);
                return;
            }
            catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
	    }
	    if (scheduled.fixedRate() > 0){

	        String name = klass.getName();
	        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
	        if (scheduled.fixedRate() > 0)
	            schedule.withIntervalInSeconds(scheduled.fixedRate());
	        if (scheduled.count() > 0) {
	            schedule.withRepeatCount(scheduled.count());
	        } else {
	            schedule.repeatForever();
	        }
	        TriggerBuilder<SimpleTrigger> trigger = TriggerBuilder.newTrigger().withIdentity(name).withSchedule(schedule);
            if (scheduled.initialDelay() > 0) 
                trigger.startAt(new Date(System.currentTimeMillis() + scheduled.initialDelay()*1000));
	        
	        JobDetail job = JobBuilder.newJob((Class<? extends Job>) klass).withIdentity(name).build();
	        scheduler.scheduleJob(job, trigger.build());
	    }
	}
	
	@SuppressWarnings("unchecked")
    public void cron(String cron, Class<?> klass) throws SchedulerException {
	    String name = klass.getName();
	    JobDetail job = JobBuilder.newJob((Class<? extends Job>) klass).withIdentity(name).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        scheduler.scheduleJob(job, trigger);
	}
}
