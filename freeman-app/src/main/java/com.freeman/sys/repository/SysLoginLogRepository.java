package com.freeman.sys.repository;


import com.freeman.spring.data.repository.BaseRepository;
import com.freeman.sys.domain.SysLoginLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface SysLoginLogRepository extends BaseRepository<SysLoginLog,Long> {

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
    * @return 系统近七天来的访问记录
    */
   @Query(value = "SELECT date_format(l.login_time, '%m-%d') days, count(1) count " +
           "FROM (SELECT * FROM sys_login_log where date_sub(curdate(), interval 7 day) <= date(login_time) ) as l",nativeQuery = true)
   List<Map<String, Object>> findLastSevenDaysVisitCount();

   @Query(value = "SELECT date_format(l.login_time, '%m-%d') days, count(1) count " +
           "FROM (SELECT * FROM sys_login_log where date_sub(curdate(), interval 7 day) <= date(login_time) ) as l WHERE l.user_id = ?1",nativeQuery = true)
   List<Map<String, Object>> findLastSevenDaysVisitCount(Long userId);

   /**
    * 获取在线用户
    *
    * @param onlineUserId 在线用户ID
    * @return 在线用户的登录日志信息
    */
   @Query(value = "SELECT * FROM (SELECT * FROM sys_login_log sll WHERE sll.user_id IN (?1) ORDER BY sll.login_time DESC) t GROUP BY t.user_id", nativeQuery = true)
    List<SysLoginLog> findOnlineUserByUserIdIn(List<Long> onlineUserId);
}