package com.itflower.aiplatform.ai.workFlow.nodes;

import com.itflower.aiplatform.ai.workFlow.WorkFlowContext;
import com.itflower.aiplatform.constant.AppConstant;
import com.itflower.aiplatform.core.AiGeneratorServiceFacade;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import com.itflower.aiplatform.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 代码生成节点
 */
@Slf4j
public class CodeGenerateNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return AsyncNodeAction.node_async(state -> {
            log.info("当前节点 - 代码生成");
            WorkFlowContext workFlowContext = WorkFlowContext.getWorkFlowContext(state);
            String enhancedPrompt = workFlowContext.getEnhancedPrompt();
            GenTypeEnums genType = workFlowContext.getGenType();
            Long appId = 0L;
            try {
                AiGeneratorServiceFacade facade = SpringContextUtils.getBean(AiGeneratorServiceFacade.class);
                Flux<String> stringFlux = facade.generateAndSaveFileStream(enhancedPrompt, genType, appId);
                stringFlux.blockLast(Duration.ofMinutes(10));
                String outputDir = String.format(
                        "%s/%s_%s", AppConstant.OUT_PUT_PATH, genType.getValue(), appId
                );
                log.info("生成完成，输出目录为: {}", outputDir);
                workFlowContext.setCurrentStep("代码生成");
                workFlowContext.setCodeGenerateDir(outputDir);
            } catch (Exception ex) {
                log.error("输出失败，原因是: {}", ex.getMessage());
            }
            return WorkFlowContext.buildWorkFlowContext(workFlowContext);
        });
    }
}
