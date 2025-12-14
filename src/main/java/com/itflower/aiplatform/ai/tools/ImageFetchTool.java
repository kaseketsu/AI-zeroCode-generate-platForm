package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.cos.CosManager;
import com.itflower.aiplatform.model.entity.ImageResource;
import com.itflower.aiplatform.model.enums.ImageTypeEnum;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Data
@Slf4j
@Component
public class ImageFetchTool {

    private static final String contentImageUrl = "https://api.pexels.com/v1/search";

    private static final String illustrationUrl = "https://undraw.co/_next/data/N6M_hYvpIPjDtR8MHPCqU/search/%s.json?term=%s";

    @Resource
    private CosManager cosManager;

    @Value("${pexels.api-key}")
    private String apiKey;

    @Value("${aliyun.api-key}")
    private String aliyunApiKey;

    @Value("${aliyun.model}")
    private String aliyunModel;

    @Tool("根据关键词获取内容图片")
    public List<ImageResource> fetchContentImageList(@P("搜索关键词") String keyword) {
        // 填充默认选项
        keyword = StringUtils.isBlank(keyword) ? "scene" : keyword;
        List<ImageResource> res = new ArrayList<>();
        try (
                HttpResponse resp = HttpRequest.get(contentImageUrl).header("Authorization", apiKey)
                        .form("page", 1)
                        .form("per_page", 12)
                        .form("query", keyword)
                        .execute()
        ) {
            boolean isOk = resp.isOk() && Objects.nonNull(resp.body());
            if (isOk) {
                String searchRes = resp.body();
                if (StringUtils.isNotBlank(searchRes)) {
                    JSONObject searchResJo = JSONUtil.parseObj(searchRes);
                    boolean hasPhoto = Objects.nonNull(searchResJo.get("photos"));
                    if (hasPhoto) {
                        JSONArray photos = JSONUtil.parseArray(searchResJo.getStr("photos"));
                        for (int i = 0; i < photos.size(); i++) {
                            JSONObject photo = photos.getJSONObject(i);
                            boolean hasSrc = Objects.nonNull(photo.get("src"));
                            if (hasSrc) {
                                ImageResource imageResource = new ImageResource();
                                imageResource.setDescription(photo.getStr("alt", keyword));
                                imageResource.setImageType(ImageTypeEnum.CONTENT);
                                JSONObject srcJo = JSONUtil.parseObj(photo.getStr("src"));
                                imageResource.setImageUrl(srcJo.getStr("medium"));
                                res.add(imageResource);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String msg = String.format("获取图片失败，原因是: %s", e.getMessage());
            log.error(msg, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, msg);
        }
        return res;
    }

    @Tool("根据关键词获取插画图片")
    public List<ImageResource> fetchIllustrationList(@P("搜索关键词") String keyword) {
        // 填充默认选项
        keyword = StringUtils.isBlank(keyword) ? "scene" : keyword;
        int pageSize = 12;
        List<ImageResource> res = new ArrayList<>();
        String url = String.format(illustrationUrl, keyword, keyword);
        try (
                HttpResponse resp = HttpRequest.get(url).timeout(10000).execute()
        ) {
            boolean isOk = resp.isOk() && Objects.nonNull(resp.body());
            if (isOk) {
                JSONObject respJo = JSONUtil.parseObj(resp.body());
                String pageProps = respJo.getStr("pageProps");
                if (StringUtils.isBlank(pageProps)) {
                    return res;
                }
                JSONObject propsJo = JSONUtil.parseObj(pageProps);
                String initialResults = propsJo.getStr("initialResults");
                if (StringUtils.isBlank(initialResults)) {
                    return res;
                }
                JSONArray resultsJo = JSONUtil.parseArray(initialResults);
                for (int i = 0; i < Math.min(pageSize, resultsJo.size()); i++) {
                    JSONObject result = resultsJo.getJSONObject(i);
                    res.add(
                            ImageResource.builder()
                                    .imageType(ImageTypeEnum.ILLUSTRATION)
                                    .description(result.getStr("title", keyword))
                                    .imageUrl(result.getStr("media"))
                                    .build()
                    );
                }
            }
        } catch (Exception e) {
            String msg = String.format("获取图片失败，原因是: %s", e.getMessage());
            log.error(msg, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, msg);
        }
        return res;
    }

    @Tool("根据 mermaid 代码生成架构图")
    public List<ImageResource> fetchMermaidPicList(@P("mermaid 代码") String mermaidCode, @P("架构图描述信息") String description) {
        File file = null;
        try {
            if (StringUtils.isBlank(mermaidCode)) {
                return new ArrayList<>();
            }
            // 生成架构图
            file = convertCodeToSvg(mermaidCode);
            // 上传到 cos
            String key = String.format("/mermaid/%s/%s", RandomUtil.randomString(5), file.getName());
            String cosUrl = cosManager.uploadFile(file, key);
            // 返回 imageSource
            if (StringUtils.isNotBlank(cosUrl)) {
                return Collections.singletonList(
                        ImageResource.builder()
                                .imageType(ImageTypeEnum.ARCHITECTURE)
                                .description(description)
                                .imageUrl(cosUrl)
                                .build()
                );
            }
        } catch (Exception e) {
            String errorMsg = String.format("创建架构图出错，原因是: %s", e.getMessage());
            log.error(errorMsg, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, errorMsg);
        } finally {
            FileUtil.del(file);
        }
        return new ArrayList<>();
    }

    /**
     * 转换 code -> svg
     *
     * @param mermaidCode 架构图代码
     * @return 架构图
     */
    private File convertCodeToSvg(String mermaidCode) {
        File tempFileInput = null;
        File tempFileOutput = null;
        try {
            // 创建临时文件并写入数据
            tempFileInput = FileUtil.createTempFile("tempFile_input", ".mmd", true);
            FileUtil.writeUtf8String(mermaidCode, tempFileInput);
            // 创建输出文件
            tempFileOutput = FileUtil.createTempFile("tempFile_output", ".svg", true);
            // 创建 command
            String command = SystemUtil.getOsInfo().isWindows() ? "mmdc.cmd" : "mmdc";
            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            ProcessBuilder pb = new ProcessBuilder(
                    command,
                    "-i", tempFileInput.getAbsolutePath(),
                    "-o", tempFileOutput.getAbsolutePath(),
                    "-b", "transparent"
            );
            pb.environment().put("PUPPETEER_EXECUTABLE_PATH", chromePath);
            pb.redirectErrorStream(true);
            // 执行 command
            Process process = pb.start();
            String msg = IoUtil.read(process.getInputStream(), StandardCharsets.UTF_8);
            log.info("mermaid 输出: {}", msg);
            int exitCode = process.waitFor();
            ThrowUtils.throwIf(exitCode != 0, ErrorCode.OPERATION_ERROR, "指令执行失败");
            // 判空
            ThrowUtils.throwIf(
                    !tempFileOutput.exists() || tempFileOutput.length() == 0,
                    ErrorCode.OPERATION_ERROR,
                    "创建架构图失败"
            );
            return tempFileOutput;
        } catch (Exception e) {
            // 出错了删输出文件
            FileUtil.del(tempFileOutput);
            String errorMsg = String.format("创建架构图出错，原因是: %s", e.getMessage());
            log.error(errorMsg, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, errorMsg);
        } finally {
            FileUtil.del(tempFileInput);
        }
    }

    @Tool("根据描述生成 logo 图片")
    public List<ImageResource> fetchLogoImageList(@P("logo 相关描述") String description) {
        List<ImageResource> res = new ArrayList<>();
        try {
            String logoPrompt = String.format("请生成 logo 图片，注意 logo 中不能有任何文字或数字，logo 描述: %s", description);
            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .apiKey(aliyunApiKey)
                    .model(aliyunModel)
                    .prompt(logoPrompt)
                    .size("512*512")
                    .n(1)
                    .build();
            ImageSynthesis imageSynthesis = new ImageSynthesis();
            ImageSynthesisResult result = imageSynthesis.call(param);
            if (result != null && result.getOutput() != null && result.getOutput().getResults() != null) {
                List<Map<String, String>> results = result.getOutput().getResults();
                for (Map<String, String> map : results) {
                    String url = map.get("url");
                    if (StringUtils.isNotBlank(url)) {
                        res.add(
                                ImageResource.builder()
                                        .imageUrl(url)
                                        .description(description)
                                        .imageType(ImageTypeEnum.LOGO)
                                        .build()
                        );
                    }
                }
            }
            return res;
        } catch (Exception e) {
            String errorMsg = String.format("创建架构图出错，原因是: %s", e.getMessage());
            log.error(errorMsg, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, errorMsg);
        }
    }
}
