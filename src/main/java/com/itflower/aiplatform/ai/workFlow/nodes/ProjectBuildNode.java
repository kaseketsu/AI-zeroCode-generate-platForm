package com.itflower.aiplatform.ai.workFlow.nodes;

import com.itflower.aiplatform.ai.workFlow.WorkFlowContext;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.core.build.VueProjectBuilder;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import com.itflower.aiplatform.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.io.File;

/**
 * 项目构建节点
 */
@Slf4j
public class ProjectBuildNode {

    public static AsyncNodeAction<MessagesState<String>> create() {
        return AsyncNodeAction.node_async(state -> {
            log.info("当前节点 - 项目构建");
            WorkFlowContext workFlowContext = WorkFlowContext.getWorkFlowContext(state);
            String codeGenerateDir = workFlowContext.getCodeGenerateDir();
            GenTypeEnums genType = workFlowContext.getGenType();
            String buildDir;
            if (genType == GenTypeEnums.VUE_MULTI) {
                try {
                    VueProjectBuilder builder = SpringContextUtils.getBean(VueProjectBuilder.class);
                    boolean res = builder.buildProject(new File(codeGenerateDir));
                    if (res) {
                        buildDir = codeGenerateDir + File.separator + "dist";
                        log.info("项目构建成功，目录为: {}", buildDir);
                    } else {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "项目构建节点项目构建失败");
                    }
                } catch (Exception e) {
                     log.error("构建项目失败，原因是: {}", e.getMessage());
                     buildDir = codeGenerateDir;
                }
            } else {
                buildDir = codeGenerateDir;
            }
            workFlowContext.setCurrentStep("项目构建");
            workFlowContext.setBuildDir(buildDir);
            return WorkFlowContext.buildWorkFlowContext(workFlowContext);
        });
    }
}
