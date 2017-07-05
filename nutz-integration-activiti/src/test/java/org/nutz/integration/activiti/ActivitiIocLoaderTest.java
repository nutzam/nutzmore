package org.nutz.integration.activiti;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;

public class ActivitiIocLoaderTest {

    @Test
    public void testActivitiIocLoader() throws Exception {
        Ioc ioc = new NutIoc(new ComboIocLoader("*js", "ioc/", "*anno", "org.nutz.integration.activiti.test", "*activiti"));
        
        ioc.get(ProcessEngine.class);
        // 卸载老定义,重新部署. 仅测试用途...
        RepositoryService repositoryService = ioc.get(RepositoryService.class);

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : list) {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId());;
        }
        DeploymentBuilder db = repositoryService.createDeployment();
        db.name("financialReport");
        db.addClasspathResource("bpmn/xxx.bpmn20.xml");
        db.deploy();
        String processDefinitionId = repositoryService.createProcessDefinitionQuery().list().get(0).getId();
        
        RuntimeService runtimeService = ioc.get(RuntimeService.class);
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinitionId);
        
        System.out.println("流程实例id:"+pi.getId());  
        System.out.println("流程定义id:"+pi.getProcessDefinitionId());  
        
        ioc.depose();
    }

}
