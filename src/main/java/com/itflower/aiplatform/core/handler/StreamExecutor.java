package com.itflower.aiplatform.core.handler;

import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class StreamExecutor {

    @Resource
    private JsonStreamHandler jsonStreamHandler;

    @Resource
    private SimpleStreamHandler simpleStreamHandler;

    /**
     * 流处理统一入口
     *
     * @param originFlux   原始流
     * @param appId        应用 id
     * @param loginUser    登录用户
     * @param genTypeEnums 生成模式
     * @return 处理后的流
     */
    public Flux<String> doHandle(
            Flux<String> originFlux,
            Long appId, User loginUser, GenTypeEnums genTypeEnums
    ) {
        switch (genTypeEnums) {
            case VUE_MULTI -> {
                return jsonStreamHandler.doHandle(originFlux, appId, loginUser);
            }
            case HTML_MULTI, HTML -> {
                return simpleStreamHandler.doHandle(originFlux, appId, loginUser);
            }
            default -> {
                String msg = String.format("不支持的流类型，发生在 StreamExecutor, 类型为: %s", genTypeEnums.getValue());
                log.error("不支持的流类型，发生在 StreamExecutor, 类型为: {}", genTypeEnums.getValue());
                throw new BusinessException(ErrorCode.PARAMS_ERROR, msg);
            }
        }
    }
}
