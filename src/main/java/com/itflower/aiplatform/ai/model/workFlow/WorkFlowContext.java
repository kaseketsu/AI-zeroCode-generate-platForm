package com.itflower.aiplatform.ai.model.workFlow;

import com.itflower.aiplatform.model.entity.ImageResource;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WorkFlowContext {

    private static final String WORK_FLOW_KEY = "workFlow";

    private int currentStep;

    private String imageListStr;

    private List<ImageResource> imageList;

    private String enhancedPrompt;

    private GenTypeEnums genType;

    private String codeGenerateDir;

    private String buildDir;

    private String errorMsg;

    public static WorkFlowContext getWorkFlowContext(MessagesState<String> state) {
        return (WorkFlowContext) state.data().get(WORK_FLOW_KEY);
    }

    public static Map<String, Object> buildWorkFlowContext(WorkFlowContext workFlowContext) {
        return Map.of(WORK_FLOW_KEY, workFlowContext);
    }
}
