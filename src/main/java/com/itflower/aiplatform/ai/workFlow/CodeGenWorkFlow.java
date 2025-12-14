package com.itflower.aiplatform.ai.workFlow;

import com.itflower.aiplatform.ai.workFlow.nodes.*;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphRepresentation;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;

@Slf4j
public class CodeGenWorkFlow {

    public CompiledGraph<MessagesState<String>> createWorkFlow() {
        try {
            return new MessagesStateGraph<String>()
                    .addNode("image_collection", ImageCollectNode.create())
                    .addNode("prompt_enhancer", PromptEnhanceNode.create())
                    .addNode("router", AiRoutingNode.create())
                    .addNode("code_generate", CodeGenerateNode.create())
                    .addNode("build_project", ProjectBuildNode.create())
                    .addEdge(START, "image_collection")
                    .addEdge("image_collection", "prompt_enhancer")
                    .addEdge("prompt_enhancer", "router")
                    .addEdge("router", "code_generate")
                    .addEdge("code_generate", "build_project")
                    .addEdge("build_project", END)
                    .compile();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建工作流失败");
        }
    }

    public WorkFlowContext executeWorkFlow(String originalPrompt) {
        CompiledGraph<MessagesState<String>> workFlow = createWorkFlow();
        WorkFlowContext initialContext = WorkFlowContext.builder()
                .originalPrompt(originalPrompt)
                .currentStep("初始化")
                .build();
        GraphRepresentation graph = workFlow.getGraph(GraphRepresentation.Type.MERMAID);
        log.info("工作流结构图: \n{}", graph.content());
        log.info("开始执行代码生成工作流");
        WorkFlowContext finalContext = initialContext;
        int step = 1;
        for (NodeOutput<MessagesState<String>> node: workFlow.stream(Map.of(WorkFlowContext.WORK_FLOW_KEY, initialContext))) {
            log.info("第 {} 步完成", step);
            WorkFlowContext workFlowContext = WorkFlowContext.getWorkFlowContext(node.state());
            if (workFlowContext == null) {
                finalContext = workFlowContext;
                log.info("当前步骤上下文: {}", workFlowContext);
            }
            step++;
        }
        log.info("工作流执行完毕");
        return finalContext;
    }
}
