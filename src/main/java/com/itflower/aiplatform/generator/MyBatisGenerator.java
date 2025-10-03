package com.itflower.aiplatform.generator;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

public class MyBatisGenerator {

    private static final String[] TABLE_NAMES = {"app"};

    public static void main(String[] args) {

        //获取配置信息
        Dict dict = YamlUtil.loadByPath("application.yml");
        Map<String, Object> datasourceConfig = dict.getByPath("spring.datasource");
        String url = String.valueOf(datasourceConfig.get("url"));
        String username = String.valueOf(datasourceConfig.get("username"));
        String password = String.valueOf(datasourceConfig.get("password"));

        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfig();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }


    public static GlobalConfig createGlobalConfig() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.itflower.aiplatform.generalResult");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                //设置逻辑删除
                .setLogicDeleteColumn("is_delete");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        //设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();

        //设置生成 service
        globalConfig.enableService();
        globalConfig.enableServiceImpl();

        //设置生成 controller
        globalConfig.enableController();

        globalConfig.getJavadocConfig()
                .setAuthor("F1ower")
                .setSince("");

        return globalConfig;
    }
}