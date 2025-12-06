package com.itflower.aiplatform.ai.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.model.entity.ImageResource;
import com.itflower.aiplatform.model.enums.ImageTypeEnum;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Slf4j
@Component
public class ImageFetchTool {

    private static final String contentImageUrl = "https://api.pexels.com/v1/search";

    private static final String illustrationUrl = "https://undraw.co/_next/data/N6M_hYvpIPjDtR8MHPCqU/search/%s.json?term=%s";

    @Value("${pexels.api-key}")
    private String apiKey;

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
}
