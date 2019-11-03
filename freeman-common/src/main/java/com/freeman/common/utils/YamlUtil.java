package com.freeman.common.utils;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * springboot获取yaml配置工具类
 * @author 刘志新
 *
 * 使用方式: YamlUtil.get("spring.data.database"); //得到对应值或null
 *
 * 初始化方法: YamlUtil.init(String... names); // 非必要操作,首次调用get()时,按照如下 方式1 默认约定初始化, 最好在项目启动后就初始化一次
 *  方式1: YamlUtil.init(); // 不传参
 *      ==>  主配置->application.yml,环境配置(0到n个)->application-dev.yml,application-prod.yml,application-test.yml,...
 *      ==>  加载逻辑依赖->主配置application.yml中的 spring.profiles.active 和 环境配置dev/prod/test中的 spring.profiles.include
 *      ==>  如果多个生效的配置文件中有重复的配置, 比如: 当前active环境是dev -> dev中include=aaa,bbb 三个配置都有spring.data.properties.hibernate.format_sql
 *      ==>     dev配置中的值会被aaa中的值覆盖,再被bbb中的值覆盖
 *      ==>  同一个配置即使在多个配置中include,也只加载一遍
 *      ==>  其实,以上这么多废话总结成一句: 与springboot默认的加载顺序一致(效果参考方式: @Value("${spring.data.properties.hibernate.format_sql}")).
 *
 *  方式2: YamlUtil.init("application.yml","application-dev.yml","application-prod.yml","application-test.yml"); // 传参
 *      ==>  按照方式1(不根据传参)初始化一遍, 如果还有(传过来名字,但是还)没加载的配置, 再加载.
 *
 *
 */
public class YamlUtil {
    private static Properties yamlProperties = new Properties();
    private YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
    private String template = "application-{}.yml";
    private List<Properties> propertiesList = new ArrayList();
    private List<String> inNamesList = new ArrayList(); //初始化传参名称
    private List<String> loadedNameList = new ArrayList(); //已经加载的配置名称

    private YamlUtil() {}

    /**
     * 根据key获取值
     * @param key 例如: spring.data.database
     * @return
     */
    public static String get(String key) {
        if(yamlProperties.size()==0)
            init();
        return (String)yamlProperties.get(key);
    }

    /**
     * 加载配置先加载application.yml==>获取"spring.profiles.active"==>加载active配置==>获取active配置中的"spring.profiles.include"==>递归加载include配置...
     *
     * @param names "application.yml","application-dev.yml",...
     */
    public static void init(String... names) {
        //// "application.yml" "spring.profiles.active" "spring.profiles.include"
        ////Map<String, String> map = (Map<String, String>) new Yaml().load(getClass().getClassLoader().getResourceAsStream("application-dev.yml"));
        ////System.out.println("=====================> "+map.get("spring.data.database"));
        YamlUtil ycu = new YamlUtil();
        // 如果有传参,先记下名称, 加载一个删除一个
        if (names.length>0) {
            for (int i = 0; i < names.length ; i++) {
                String name = names[i];
                if (name.isEmpty() && !name.matches("application-[\\w-]{1,16}.yml"))
                    continue;
                ycu.inNamesList.add(name);
            }
        }
        // 按 文档注释的套路加载
        ycu.recursive(1);
        ycu.appendYamlProperties(ycu.propertiesList.toArray(new Properties[ycu.propertiesList.size()]));
        // 传进来配置名称还有没加载的,
        ycu.inNamesList.removeAll(ycu.loadedNameList);
        if (names.length>0 && ycu.inNamesList.size() > 0) {
            for (String name : ycu.inNamesList) {
                ycu.appendYamlProperties(ycu.propNametoProperties(name));
            }
        }
    }

    private void recursive(int level, String... names) {
        if (level>9)  return; // 遍历深度限制 // 其实没啥用,不想改了
        //首次加载 主配置
        if (0 == names.length && 1 == level) {
            String pname = "application.yml";
            if (!new ClassPathResource(pname).exists()) {
                template = "config/"+template;
                pname = "config/application.yml";
            }
            Properties app = propNametoProperties(pname);
            if (null != app) {
                propertiesList.add(app);
            }
            String active_name = (String) app.get("spring.profiles.active");
            if (null != active_name && !active_name.isEmpty()) {
                recursive(level + 1, template.replace("{}", active_name)); //递归加载配置
            }
        // active --> active的includes配置a b c --> a b c的includes --> ...
        }else {
            // 一个配置
            Properties prop = propNametoProperties(names[0]);
            if (null != prop){
                propertiesList.add(prop); // 一个配置 自己
                // 一个配置 的includes
                String includes = (String)prop.get("spring.profiles.include");
                if (null != includes){
                    String[] includesArr = includes.replaceAll("\\s", "").split(",");
                    for (String n : includesArr) {
                        if (n.isEmpty()) {
                            continue;
                        }
                        recursive(level+1, template.replace("{}",n));
                    }
                }
            }
        }
    }

    /**
     * 根据ClassPath目录下(或classpath:config目录下)的yaml配置文件名称(例如: application.yml) 生成 Properties
     * @param name 配置文件名称
     * @return
     */
    private Properties propNametoProperties(String name) {
        if(!loadedNameList.contains(name))
            loadedNameList.add(name);
        yamlFactory.setResources(new ClassPathResource(name));
        return yamlFactory.getObject();
    }

    /**
     * 追加配置, 如果多个配置文件有相同的key, 前面赋的值会被覆盖掉
     * @param props
     * @return
     */
    private void appendYamlProperties(Properties... props) {
        if (props.length == 0)
            return;

        for (int i = 0; i < props.length; i++) {
            Properties p = props[i];
            for ( Enumeration<?> enumeration = p.propertyNames(); enumeration.hasMoreElements(); ){
                String k = (String)enumeration.nextElement();
                //log.info("k=>{},v=>{}",k,p.getProperty(k));
                yamlProperties.put(k, p.getProperty(k));
            }
        }
        //log.info("=====================> "+prop.get("app.yml.prop"));
    }

}