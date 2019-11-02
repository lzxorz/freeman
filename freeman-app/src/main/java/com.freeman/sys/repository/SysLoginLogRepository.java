package com.freeman.sys.repository;


import com.freeman.sys.domain.SysLoginLog;
import com.freeman.sys.domain.SysUser;
import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.spring.data.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface SysLoginLogRepository extends BaseRepository<SysLoginLog,Long> {

    /** 需要写模板查询 */
    /*@TemplateQuery
    Page<SysLoginLog> findAllByTemplate(SysLoginLog sysLoginLog, Pageable pageable);*/

   /**
    * 获取系统总访问次数
    *
    * @return Long
    */
   @Query(nativeQuery = true, value = "select count(1) from sys_login_log")
   Long findTotalVisitCount();

   /**
    * 获取系统今日访问次数
    *
    * @return Long
    */
   @Query(nativeQuery = true, value = " select count(1) from sys_login_log where datediff(login_time,now())=0")
   Long findTodayVisitCount();

   /**
    * 获取系统今日访问 IP数
    *
    * @return Long
    */
   @Query(nativeQuery = true, value = " select count(distinct(ip)) from sys_login_log where datediff(login_time,now())=0")
   Long findTodayIp();

   /**
    * 获取系统近七天来的访问记录
    *
    * @param user 用户
    * @return 系统近七天来的访问记录
    */
   @TemplateQuery
   List<Map<String, Object>> findLastSevenDaysVisitCount(SysUser user);

   /**
    * 获取在线用户
    *
    * @param onlineUserId 在线用户ID
    * @return 在线用户的登录日志信息
    */
   @Query(value = "SELECT * FROM (SELECT * FROM sys_login_log sll WHERE sll.user_id IN (?1) ORDER BY sll.login_time DESC) t GROUP BY t.user_id", nativeQuery = true)
    List<SysLoginLog> findOnlineUserByUserIdIn(List<Long> onlineUserId);
}