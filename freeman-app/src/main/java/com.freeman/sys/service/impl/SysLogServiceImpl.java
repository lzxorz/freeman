package com.freeman.sys.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freeman.common.log.Log;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.StrUtil;
import com.freeman.common.utils.network.IPUtil;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysLog;
import com.freeman.sys.repository.SysLogRepository;
import com.freeman.sys.service.ISysLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Transactional(readOnly = true)
@Service
public class SysLogServiceImpl extends BaseServiceImpl<SysLogRepository, SysLog,Long> implements ISysLogService {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Page<SysLog> findPage(QueryRequest queryRequest, SysLog sysLog) {

        PageRequest pageRequest = queryRequest.getPageRequest();
        Object createTime = sysLog.getParams("createTime"); //传进来是数组

        NativeSqlQuery nativeSql = NativeSqlQuery.builder()
                .from("sys_log")
            .eq("username", sysLog.getUsername())
            .contains("operation", sysLog.getOperation())
            .contains("location", sysLog.getLocation())
            .between( "date_format(create_time,'%Y-%m-%d')", createTime)
            .build();

        return dao.findAllByNativeSql(nativeSql, SysLog.class, pageRequest);
    }

    @Override
    @Transactional
    public void saveLog(ProceedingJoinPoint joinPoint, SysLog log) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        // 注解上的参数
        log.setOperation(logAnnotation.title());

        // 请求的类名
        String className = signature.getDeclaringTypeName(); // joinPoint.getTarget().getClass().getName();
        // 请求的方法名
        String methodName = signature.getName();
        log.setMethod(className + "." + methodName + "()");

        // 请求的方法参数值
        /*Object[] args = joinPoint.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            params = handleParams(params, args, Arrays.asList(paramNames));
            log.setParameter(params.toString());
        }*/

        log.setCreateTime(new Date());
        // 操作地点
        log.setLocation(IPUtil.getCityInfo(DbSearcher.BTREE_ALGORITHM, log.getIp()));
        // 保存系统日志
        save(log);
    }

    private StringBuilder handleParams(StringBuilder params, Object[] args, List paramNames) throws JsonProcessingException {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Map) {
                Set set = ((Map) args[i]).keySet();
                List<Object> list = new ArrayList<>();
                List<Object> paramList = new ArrayList<>();
                for (Object key : set) {
                    list.add(((Map) args[i]).get(key));
                    paramList.add(key);
                }
                return handleParams(params, list.toArray(), paramList);
            } else {
                if (args[i] instanceof Serializable) {
                    Class<?> aClass = args[i].getClass();
                    try {
                        aClass.getDeclaredMethod("toString", new Class[]{null});
                        // 如果不抛出 NoSuchMethodException 异常则存在 toString 方法 ，安全的 writeValueAsString ，否则 走 Object的 toString方法
                        params.append(" ").append(paramNames.get(i)).append(": ").append(objectMapper.writeValueAsString(args[i]));
                    } catch (NoSuchMethodException e) {
                        params.append(" ").append(paramNames.get(i)).append(": ").append(objectMapper.writeValueAsString(args[i].toString()));
                    }
                } else if (args[i] instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) args[i];
                    params.append(" ").append(paramNames.get(i)).append(": ").append(file.getName());
                } else {
                    params.append(" ").append(paramNames.get(i)).append(": ").append(args[i]);
                }
            }
        }
        return params;
    }

}


