package org.nutz.integration.activiti.test;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ProcessStartListener implements ExecutionListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        System.out.println(execution.getProcessBusinessKey());
        System.out.println(execution.getProcessInstanceId());
    }

}