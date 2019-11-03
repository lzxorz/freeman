### 这里是单行注释

#@@# columns
su.id,
su.code,
su.username,
su.realname,
su.nickname,
su.password,
su.salt,
su.avatar,
su.sex,
su.age,
su.phone,
su.birthday,
su.email,
su.description,
su.last_login_time,
su.qqid,
su.wxid,
su.wbid,
su.status,
su.create_by,
su.create_time,
su.update_by,
su.update_time,
suc.user_id  AS `config.userId`,
suc.theme  AS `config.theme`,
suc.layout  AS `config.layout`,
suc.multi_page  AS `config.multiPage`,
suc.fix_siderbar  AS `config.fixSiderbar`,
suc.fix_header  AS `config.fixHeader`,
suc.color  AS `config.color`,
(SELECT fnCommpanyIdByUid(su.id) FROM DUAL) AS companyId,
so.name AS deptName,
GROUP_CONCAT(sr.id) AS roleIds,
GROUP_CONCAT(sr.name) AS roleNames


### include示例
#@@# findAllByTemplate
SELECT
    #include("columns")
FROM
    sys_user su
    LEFT JOIN sys_user_config suc ON suc.user_id = su.id
    LEFT JOIN sys_org so ON so.id = su.dept_id
    LEFT JOIN sys_user_role sur ON sur.user_id = su.id
    LEFT JOIN sys_role sr ON sr.id = sur.role_id
WHERE 1=1
#if(ids??)
 AND su.id IN #(ids)
#end
#if(deptId??)
 AND su.dept_id = #(deptId)
#end
#if(realname??)
 AND su.realname LIKE CONCAT('%',:realname,'%')
#end
#if(sex??)
 AND su.sex = #(sex)
#end
#if(status??)
 AND su.status = #(status)
#end
#if(params?? && params.createTime??)
 AND date_format(su.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1])
#end
#if(params?? && params.dataScope??)
 AND #(params.dataScope)
#end
GROUP BY su.id


