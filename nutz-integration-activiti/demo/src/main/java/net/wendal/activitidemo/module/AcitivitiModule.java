package net.wendal.activitidemo.module;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

@At("/activiti")
@IocBean
@Fail("http:500")
public class AcitivitiModule {

    @Inject
    protected ProcessEngine processEngine;
    
    @Inject
    protected HistoryService historyService;
    
    @At("/hcount")
    @Ok("json")
    public long historyCount() {
        return historyService.createHistoricProcessInstanceQuery().count();
    }
}
