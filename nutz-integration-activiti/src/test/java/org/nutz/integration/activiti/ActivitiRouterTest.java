package org.nutz.integration.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.nutz.integration.activiti.router.CallBackRouterService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;

public class ActivitiRouterTest {

	@Test
    public void testCallBack() throws Exception {
        Ioc ioc = new NutIoc(new ComboIocLoader("*js", "ioc/", "*anno", "org.nutz.integration.activiti.test","org.nutz.integration.activiti.router", "*activiti"));
        
        ioc.get(ProcessEngine.class);
        // 重新部署
        RepositoryService repositoryService = ioc.get(RepositoryService.class);

        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : list) {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId());;
        }
        DeploymentBuilder db = repositoryService.createDeployment();
        db.name("TEST_ROUNTER");
        db.addClasspathResource("bpmn/router.bpmn20.xml");
        db.deploy();
        String processDefinitionId = repositoryService.createProcessDefinitionQuery().list().get(0).getId();
        
        RuntimeService runtimeService = ioc.get(RuntimeService.class);
        TaskService taskService = ioc.get(TaskService.class);
        CallBackRouterService callBackRouterService = ioc.get(CallBackRouterService.class);
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinitionId);
        FormService formService = ioc.get(FormService.class);
        
        System.out.println("流程实例id:"+pi.getId());  
        System.out.println("流程定义id:"+pi.getProcessDefinitionId());
        
        Map<String, String> form = new HashMap<>();
        form.put("approve1", "refuse");
        String taskId = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult().getId();
        formService.submitTaskFormData(taskId, form);
        Task currentTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("审批拒绝，当前节点："+currentTask.getName());
        
        callBackRouterService.callBack(currentTask.getId(), taskId);
        currentTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("节点回退，当前节点："+currentTask.getName());
        
        form.put("approve1", "agree");
        taskId = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult().getId();
        formService.submitTaskFormData(taskId, form);
        
        currentTask = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        if(currentTask == null) {
        	 System.out.println("流程结束");  
        }
        
        ioc.depose();
    }
}
