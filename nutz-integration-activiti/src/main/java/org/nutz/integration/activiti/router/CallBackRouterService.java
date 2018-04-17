package org.nutz.integration.activiti.router;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 回退路由
 */
@IocBean
public class CallBackRouterService {
	
	@Inject
    protected HistoryService historyService;
	
	@Inject
    protected RuntimeService runtimeService;
	
	@Inject
	protected RepositoryService repositoryService;
	
	@Inject
	protected TaskService taskService;
	
	/**
	 * 回退到指定节点
	 * @param currentTaskId 当前任务节点
	 * @param toTaskId 回退到达任务节点
	 */
	public void callBack(String currentTaskId, String toTaskId) {
		
		//获取当前任务
		Task currentTask = taskService
				.createTaskQuery()
                .taskId(currentTaskId)
                .singleResult();
		
		if(currentTask == null) {
			throw new RuntimeException("currentTask can not find");
		}
		String processInstanceId = currentTask.getProcessInstanceId();
		
		//判断当前流程状态
		ProcessInstance instance = runtimeService
				.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
		
		if(instance == null) {
			throw new RuntimeException("instance closed");
		}
		
		//获取上一个节点
		List<HistoricActivityInstance> historicActivityInstances = historyService
				.createHistoricActivityInstanceQuery()  
	            .processInstanceId(processInstanceId)
	            .finished()
	            .orderByHistoricActivityInstanceEndTime()
	            .desc()
	            .list();
		
		HistoricActivityInstance lastActivity = null;
		for (HistoricActivityInstance act : historicActivityInstances) {
			if (toTaskId == null) {
				// 无指定回退节点，则返回上一个节点
				lastActivity = act;
				break;
			} else {
				// 寻找指定节点
				if (toTaskId.equals(act.getTaskId())) {
					lastActivity = act;
					break;
				}
			}
		}
		
		if(lastActivity == null) {
			throw new RuntimeException("can not get last node");
		}
		
		//清空当前节点连接
		ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                  .getDeployedProcessDefinition(currentTask.getProcessDefinitionId());
		ActivityImpl currentActivity = ((ProcessDefinitionImpl) definition).findActivity(currentTask.getTaskDefinitionKey());
		List<PvmTransition> currentTransitions = currentActivity.getOutgoingTransitions();
		List<PvmTransition> backupTransitions = new ArrayList<>();
		backupTransitions.addAll(currentActivity.getOutgoingTransitions());
		
		for (Iterator<PvmTransition> it = currentTransitions.iterator(); it.hasNext();) {
			PvmTransition transition = it.next();
			PvmActivity activity = transition.getDestination();
			List<PvmTransition> inTrans = activity.getIncomingTransitions();
			for (Iterator<PvmTransition> itIn = inTrans.iterator(); itIn.hasNext();) {
				PvmTransition inTransition = itIn.next();
				if (inTransition.getSource().getId().equals(currentActivity.getId())) {
					itIn.remove();
				}
			}
		}
		currentActivity.getOutgoingTransitions().clear();
		
		//创建新的连接
		ActivityImpl destActivity= definition.findActivity(lastActivity.getActivityId());  
	    TransitionImpl transitionImpl = currentActivity.createOutgoingTransition();  
	    transitionImpl.setDestination(destActivity);  
		
		//完成新连接的跳转
	    taskService.complete(currentTaskId);
	    
	    //还原节点连接
	    currentActivity.getOutgoingTransitions().clear();  
	    currentActivity.getOutgoingTransitions().addAll(backupTransitions);  
		
	}

}
