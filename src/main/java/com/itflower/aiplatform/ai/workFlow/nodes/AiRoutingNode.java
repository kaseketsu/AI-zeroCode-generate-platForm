package com.itflower.aiplatform.ai.workFlow.nodes;

import com.itflower.aiplatform.ai.AiRoutingService;
import com.itflower.aiplatform.ai.workFlow.WorkFlowContext;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import com.itflower.aiplatform.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

/**
 * 智能路由节点
 */
@Slf4j
public class AiRoutingNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return AsyncNodeAction.node_async(state -> {
            log.info("当前节点 - 智能路由");
            WorkFlowContext workFlowContext = WorkFlowContext.getWorkFlowContext(state);
            String originalPrompt = workFlowContext.getOriginalPrompt();
            GenTypeEnums genTypeEnums;
            try {
                AiRoutingService routingService = SpringContextUtils.getBean(AiRoutingService.class);
                genTypeEnums = routingService.aiRouting(originalPrompt);
                log.info("根据提示词: {}, 智能路由选择 {} 节点", originalPrompt, genTypeEnums);
            } catch (Exception ex) {
                log.error("智能路由节点执行失败，原因是: {}", ex.getMessage());
                log.error("将自动选择 HTML 节点");
                genTypeEnums = GenTypeEnums.HTML;
            }
            workFlowContext.setGenType(genTypeEnums);
            workFlowContext.setCurrentStep("智能路由");
            return WorkFlowContext.buildWorkFlowContext(workFlowContext);
        });
    }
}
