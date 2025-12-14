package com.itflower.aiplatform.ai.workFlow.nodes;

import com.itflower.aiplatform.ai.ImageCollectService;
import com.itflower.aiplatform.ai.workFlow.WorkFlowContext;
import com.itflower.aiplatform.utils.SpringContextUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

/**
 * AI 工作流 - 图片收集节点
 */
@Slf4j
public class ImageCollectNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return AsyncNodeAction.node_async(state -> {
            log.info("执行节点 - 图片收集");
            WorkFlowContext workFlowContext = WorkFlowContext.getWorkFlowContext(state);
            String originalPrompt = workFlowContext.getOriginalPrompt();
            String imageStr = "";
            try {
                ImageCollectService service = SpringContextUtils.getBean(ImageCollectService.class);
                imageStr = service.fetchImageList(originalPrompt);
            } catch (Exception e) {
                log.error("收集图片失败，原因是: {}", e.getMessage());
            }
            workFlowContext.setCurrentStep("图片收集");
            workFlowContext.setImageListStr(imageStr);
            log.info("收集图片信息: {}", imageStr);
            return WorkFlowContext.buildWorkFlowContext(workFlowContext);
        });
    }
}
