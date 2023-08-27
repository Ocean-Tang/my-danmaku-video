package com.study.mydanmakuvideo.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.study.mydanmakuvideo.common.controller.SuperController;
import com.study.mydanmakuvideo.common.model.entity.BaseEntity;

/**
 * @author Ocean
 * @apiNote
 */
public class CodeGenerator {
    public static void main(String[] args) {
        final String moduleName = "video";
        final String[] tableName = {"t_video_history"};

        DataSourceConfig dataSourceConfig = new DataSourceConfig
                .Builder("",
                "", "")
                .build();

        GlobalConfig globalConfig = new GlobalConfig.Builder()
                .outputDir("D:\\project\\my-danmaku-video\\src\\main\\java")
                .author("ocean")
                .dateType(DateType.TIME_PACK)
                .commentDate("yyyy-MM-dd")
                .enableSwagger()
                .build();

        PackageConfig packageConfig = new PackageConfig.Builder()
                .parent("com.study.mydanmakuvideo.modules")
                .moduleName(moduleName)
                .entity("model.entity")
                .service("service")
                .serviceImpl("service.impl")
                .mapper("mapper")
                .xml("mapper.xml")
                .controller("controller")
                .build();

        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .addInclude(tableName)
                .addTablePrefix("t_", "c_")
                .controllerBuilder()
                .superClass(SuperController.class)
                .formatFileName("%sRestController")
                .enableRestStyle()
                .enableHyphenStyle()
                .entityBuilder()
                .naming(NamingStrategy.underline_to_camel)
                .superClass(BaseEntity.class)
                .addSuperEntityColumns("id", "create_time", "update_time", "is_delete")
                .enableChainModel()
                .enableLombok()
                .enableActiveRecord()
                .columnNaming(NamingStrategy.underline_to_camel)
                .idType(IdType.ASSIGN_ID)
                .logicDeleteColumnName("is_delete")
                .addTableFills(new Column("create_time", FieldFill.INSERT),
                        new Column("update_time", FieldFill.UPDATE))
                .formatFileName("%sEntity")
                .build();


        new AutoGenerator(dataSourceConfig).global(globalConfig).packageInfo(packageConfig)
                .strategy(strategyConfig).execute();
    }
}
