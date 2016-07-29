package org.nutz.integration.quartz;

import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public class SimpleJob implements Job, InterruptableJob {
    
    public static int icount;
    public static int iexec;

    public void interrupt() throws UnableToInterruptJobException {
        icount ++;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        iexec++;
    }

}
