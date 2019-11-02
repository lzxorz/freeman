
#@@# findLastSevenDaysVisitCount
SELECT
    date_format(l.login_time, '%m-%d') days, count(1) count
FROM
    (SELECT * FROM sys_login_log where date_sub(curdate(), interval 7 day) <= date(login_time) ) as l
WHERE 1=1
    #if(userId??)
        AND l.user_id = #(userId)
    #end
GROUP BY days


### 变量 userId 值 传入 1
### 打印sql: SELECT date_format(l.login_time, '%m-%d') days,  count(1) count
###         FROM (SELECT * FROM sys_login_log where date_sub(curdate(), interval 7 day) <= date(login_time) ) as l
###         WHERE 1=1  AND l.user_id = 1 GROUP BY days