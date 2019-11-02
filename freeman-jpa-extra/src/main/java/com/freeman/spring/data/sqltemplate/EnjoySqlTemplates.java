package com.freeman.spring.data.sqltemplate;

import cn.hutool.core.util.StrUtil;
import com.freeman.spring.data.NamedTemplateResolver;
import com.freeman.spring.data.TplNamedTemplateResolver;
import com.freeman.utils.EntityMethodKeySource;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从文件中解析sql,并缓存
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/8/10.
 */
public class EnjoySqlTemplates implements ResourceLoaderAware, InitializingBean {
    private final Log logger = LogFactory.getLog(EnjoySqlTemplates.class);

    @PersistenceContext
    private EntityManager em;

    private Engine engine;

    private String encoding = "UTF-8";

    private Map<String, Long> lastModifiedCache = new ConcurrentHashMap<>(); // 记录每个模板文件的 最近修改时间
    private Map<String, List<Resource>> sqlResources = new ConcurrentHashMap<>(); // 存放每个模板文件 资源

    private ResourceLoader resourceLoader;

    private String templateLocation = "classpath:/sql"; // 模板文件位置

    private String templateBasePackage = "**";

    private String suffix = ".tpl"; //默认模板文件后缀

    private Boolean autoCheck = Boolean.TRUE;  //默认开启自动检测SQL文件的更新

    private Map<String, NamedTemplateResolver> suffixResolvers = new HashMap<>();
    { suffixResolvers.put(".tpl", new TplNamedTemplateResolver()); }


    /** 模板引擎解析语法成sql字符串**核心** */
    public String process(String entityName, String methodName, Map<String, Object> model) {
        try {
            if (this.autoCheck && isModified(entityName)) {
                reloadTemplateResource(entityName);
            }
            Template template = engine.getTemplate(new EntityMethodKeySource(getTemplateKey(entityName, methodName)));
            String sqlStr = template.renderToString(model);
            ///
            return sqlStr;
        } catch (Exception e) {
            logger.error("process template error. Entity name: " + entityName + " methodName:" + methodName, e);
            return StrUtil.EMPTY;
        }
    }

    /** 设置完属性值之后，调用 */
    @Override
    public void afterPropertiesSet() throws Exception {
        Set<String> names = new HashSet<>(); //实体类 名称 集合
        Set<EntityType<?>> entities = em.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            names.add(entity.getName());
        }

        String suffixPattern = "/*/*" + suffix;

        if (!names.isEmpty()) {
            String pattern;
            if (StrUtil.isNotBlank(templateBasePackage)) {
                pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(templateBasePackage) + suffixPattern;

                loadPatternResource(names, pattern);
            }
            if (StrUtil.isNotBlank(templateLocation)) {
                pattern = templateLocation.contains(suffix) ? templateLocation : templateLocation + suffixPattern;
                try {
                    loadPatternResource(names, pattern);
                } catch (FileNotFoundException e) {
                    if ("classpath:/sql".equals(templateLocation)) {
                        //warn: default value
                        logger.warn("templateLocation[" + templateLocation + "] not exist!");
                        logger.warn(e.getMessage());
                    } else {
                        //throw: custom value.
                        throw e;
                    }
                }
            }
        }
    }

    /** 设置完属性值之后，载入模板文件 */
    private void loadPatternResource(Set<String> names, String pattern) throws IOException {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resourcePatternResolver.getResources(pattern);
        for (Resource resource : resources) {
            String resourceName = resource.getFilename().replace(suffix, "");
            if (names.contains(resourceName)) {
                //allow multi resource.
                List<Resource> resourceList;
                if (sqlResources.containsKey(resourceName)) {
                    resourceList = sqlResources.get(resourceName);
                } else {
                    resourceList = new LinkedList<>();
                    sqlResources.put(resourceName, resourceList);
                }
                resourceList.add(resource);
            }
        }
    }

    /** 载入/重载模板的sql语句缓存 */
    private void reloadTemplateResource(String entityName) throws Exception {
        List<Resource> resourceList = sqlResources.get(entityName);
        if (resourceList == null) return;
        // 重载一个模板文件的每个sql语句
        for (Resource resource : resourceList) {
            NamedTemplateResolver namedTemplateResolver = suffixResolvers.get(suffix);
            /*new NamedTemplateCallback(){public void process(String templateName, String content) {}}*/
            Iterator<Void> iterator = namedTemplateResolver.doInTemplateResource(resource, (templateName, content) -> {
                        String key = getTemplateKey(entityName, templateName);
                        content = converterInclude(entityName, content);
                        engine.getTemplate(new EntityMethodKeySource(key,content,true));
            });
            while (iterator.hasNext()) {
                iterator.next();
            }
        }
    }


    /****************** 私有方法 ***************/
    /** 获取模板缓存的key */
    private String getTemplateKey(String entityName, String methodName) {
        return entityName + ":" + methodName;
    }

    /** 模板文件是否修改过 */
    private boolean isModified(final String entityName) {
        try {
            Long lastModified = lastModifiedCache.get(entityName);
            List<Resource> resourceList = sqlResources.get(entityName);

            long newLastModified = 0;
            for (Resource resource : resourceList) {
                if (newLastModified == 0) {
                    newLastModified = resource.lastModified();
                } else {
                    //get the last modified.
                    newLastModified = newLastModified > resource.lastModified() ? newLastModified : resource.lastModified();
                }
            }

            //check modified for cache.
            if (lastModified == null || newLastModified > lastModified) {
                lastModifiedCache.put(entityName, newLastModified);
                return true;
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return false;
    }

    /**
     *  sql模板 #include("xxx") 解析替换
     * @author 刘志新
     */
    private String converterInclude(String entityName, String tplContent) {
        Matcher matcher = Pattern.compile("#include\\(\"\\w+\"\\)").matcher(tplContent);
        boolean hasInclude = false;

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            if (!hasInclude) hasInclude = true;
            String matchStr = matcher.group();
            int fromIndex = StrUtil.indexOf(matchStr, '"')+1;
            int lastIndexOf = StrUtil.indexOf(matchStr, '"', fromIndex);
            Template template = engine.getTemplate(new EntityMethodKeySource(getTemplateKey(entityName, StrUtil.sub(matchStr, fromIndex, lastIndexOf))));
            matcher.appendReplacement(sb, template.renderToString(null));
        }
        matcher.appendTail(sb);

        return hasInclude ? sb.toString() : tplContent;
    }

    /****************** setters *****************/
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        this.autoCheck = engine.getDevMode();
    }

    public void setAutoCheck(Boolean autoCheck) {
        this.autoCheck = autoCheck;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public void setTemplateBasePackage(String templateBasePackage) {
        this.templateBasePackage = templateBasePackage;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
