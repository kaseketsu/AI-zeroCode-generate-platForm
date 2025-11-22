package com.itflower.aiplatform.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

@Slf4j
public class WebScreenShotUtil {

//    private static volatile WebDriver driver;
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final int DEFAULT_WIDTH = 1600;
    private static final int DEFAULT_HEIGHT = 900;

    static {
        System.setProperty(
                "wdm.chromeDriverMirrorUrl",
                "https://registry.npmmirror.com/binary.html?path=chromedriver"
        );
    }

    private static WebDriver getDriver() {
        WebDriver webDriver = driverThreadLocal.get();
        if (webDriver == null) {
            webDriver = initWebDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            driverThreadLocal.set(webDriver);
        }
        return webDriver;
    }

//    private static void getDriver() {
//        if (driver == null) {
//            synchronized (WebScreenShotUtil.class) {
//                if (driver == null) {
//                    driver = initWebDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
//                }
//            }
//        }
//    }

//    @PreDestroy
//    public void destroy() {
//        driver.quit();
//    }

    private static WebDriver initWebDriver(int width, int height) {
        try {
//            System.setProperty("wdm.chromeDriverMirrorUrl", "https://registry.npmmirror.com/binary.html?path=chromedriver");
            // 自动配置
            WebDriverManager.chromedriver().useMirror().setup();
            // 设置 options 参数
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments(String.format("--window-size=%s,%s", width, height));
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建渠道
            WebDriver driver = new ChromeDriver(chromeOptions);
            // 设置超时时间
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置元素查找时间
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            String errMsg = String.format("创建 webDriver 失败，原因是: %s", e.getMessage());
            log.error(errMsg);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, errMsg);
        }
    }

    private static void saveImage(byte[] imageBytes, String imagePath) {
        try {
            FileUtil.writeBytes(imageBytes, imagePath);
        } catch (BusinessException ex) {
            log.error("保存图片失败，原因是: {}", ex.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存图片失败");
        }
    }

    private static void waitForPageLoad(WebDriver driver) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            webDriverWait.until(webDriver -> {
                String res = (String) ((JavascriptExecutor) webDriver).executeScript("return document.body.readyState");
                return res != null && res.equals("complete");
            });
            // 额外等待 2 秒，确保完成
            Thread.sleep(2000);
            log.info("页面加载完成");
        } catch (Exception e) {
            log.error("等待页面加载出现异常，原因是: {}", e.getMessage());
        }
    }

    private static void compressImage(String from, String to) {
        try {
            final float COMPRESS_FACTOR = 0.3f;
            ImgUtil.compress(
                    new File(from),
                    new File(to),
                    COMPRESS_FACTOR
            );
        } catch (IORuntimeException e) {
            log.error("图片压缩失败，原因是: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片压缩失败，原因是: " + e.getMessage());
        }
    }

    /**
     * 屏幕截图
     *
     * @param webUrl 网站地址
     * @return 截图地址
     */
    public static String doScreenShot(String webUrl) {
        try {
            // 校验参数
            ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网站地址为空");
            // 创建临时目录
            String tempDir = System.getProperty("user.dir") + File.separator + "tmp" + File.separator + "screenshots"
                    + File.separator + UUID.randomUUID().toString().substring(0, 8);
            FileUtil.mkdir(tempDir);
            WebDriver driver = getDriver();
            // 访问网站
            driver.get(webUrl);
            waitForPageLoad(driver);
            // 截图
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            // 创建截图地址
            String source = tempDir + File.separator + RandomUtil.randomString(5) + ".png";
            // 保存图片
            saveImage(screenshot, source);
            // 创建压缩地址
            String to = tempDir + File.separator + RandomUtil.randomString(5) + "_compressed.png";
            compressImage(source, to);
            log.info("原片压缩成功，地址为: {}", to);
            // 删除原图
            FileUtil.del(source);
            return to;
        } catch (Exception e) {
            log.error("截图失败，原因是: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "截图失败，原因是: " + e.getMessage());
        } finally {
            release();
        }
    }

    private static void release() {
        WebDriver webDriver = driverThreadLocal.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception e) {
                log.error("driver quit error: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
