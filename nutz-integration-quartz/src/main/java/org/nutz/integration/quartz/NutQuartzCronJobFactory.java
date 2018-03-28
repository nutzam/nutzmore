package org.nutz.integration.quartz;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

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
                log.debug("found cron job packages = " + conf.get(key));
                for (String pkg : Strings.splitIgnoreBlank(conf.get(key), ",")) {
                    addPackage(pkg);
                }
                continue;
            }
            String cron = conf.get(key);
            log.debugf("job define name=%s cron=%s", name, cron);
            Class<?> klass = null;
            if (name.contains(".")) {
                klass = Lang.loadClass(name);
            } else {
                klass = Lang.loadClass(getClass().getPackage().getName() + ".job." + name);
            }
            Quartzs.cron(scheduler, cron, klass);
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
    
    public void add(Class<?> klass, Scheduled scheduled) throws SchedulerException {
        String name = klass.getName();
        if (!Strings.isBlank(scheduled.cron())) {
            log.debugf("job define name=%s cron=%s", name, scheduled.cron());
            Quartzs.cron(scheduler, scheduled.cron(), klass);
        }
        else if (scheduled.fixedRate() > 0){
            log.debugf("job define name=%s fixedRate=%s count=%s initialDelay=%s", 
                    name, scheduled.fixedRate(), scheduled.count(), scheduled.initialDelay());
            Quartzs.simple(scheduler, klass, scheduled.fixedRate(), scheduled.count(), scheduled.initialDelay());
        }
    }
    
    public void add(Class<?> klass, String cron, JobDataMap data,String jobKeyName,String jobKeyGroup) throws SchedulerException {
        String name = klass.getName();
        if (!Strings.isBlank(cron)) {
            log.debugf("job define name=%s cron=%s", name, cron);
            Quartzs.cron(scheduler, cron, klass, data ,jobKeyName ,jobKeyGroup);
        }
    }
}
