package com.itflower.aiplatform.ai.workFlow.nodes;

import com.itflower.aiplatform.ai.workFlow.WorkFlowContext;
import com.itflower.aiplatform.model.entity.ImageResource;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.List;

/**
 * 提示词增强节点
 */
@Slf4j
public class PromptEnhanceNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return AsyncNodeAction.node_async(state -> {
            log.info("当前节点 - 提示词增强");
            WorkFlowContext workFlowContext = WorkFlowContext.getWorkFlowContext(state);
            String originalPrompt = workFlowContext.getOriginalPrompt();
            List<ImageResource> imageList = workFlowContext.getImageList();
            String imageListStr = workFlowContext.getImageListStr();
            boolean hasList = CollectionUtils.isNotEmpty(imageList);
            boolean hasListStr = StringUtils.isNotBlank(imageListStr);
            StringBuilder sb = new StringBuilder(originalPrompt);
            sb.append("""
                    ## 可用素材资源 \n
                    请在生成网站使用以下图片资源，将这些图片合理地嵌入到网站的相应位置中。 \n
                    """);
            if (hasList || hasListStr) {
                if (hasList) {
                    for (ImageResource imageResource : imageList) {
                        sb.append("- ")
                                .append(imageResource.getImageType())
                                .append(": ")
                                .append(imageResource.getDescription())
                                .append(" (")
                                .append(imageResource.getImageUrl())
                                .append(")\n");
                    }
                } else {
                    sb.append(imageListStr);
                }
            }
            String enhancedPrompt = sb.toString();
            log.info("增强提示词: {}", enhancedPrompt);
            workFlowContext.setEnhancedPrompt(enhancedPrompt);
            workFlowContext.setCurrentStep("提示词增强");
            return WorkFlowContext.buildWorkFlowContext(workFlowContext);
        });
    }
}
