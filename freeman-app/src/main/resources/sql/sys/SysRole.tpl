### 模板只支持行内注释


#@@# findAllByTemplate
SELECT
    sr.id, sr.parent_id, sr.name, sr.code, sr.remark, sr.sort_no, sr.status, sr.data_scope,
    (SELECT GROUP_CONCAT(srd.dept_id) as data_dept_ids FROM sys_role_dept srd WHERE srd.role_id = sr.id) as data_dept_ids,
    sr.create_time,sr.create_by,sr.update_by,sr.update_time
FROM
    sys_role sr ###LEFT JOIN sys_user su ON sr.create_by = su.id
WHERE 1=1
    #if(name??)　### 属性: name　值: '户'
     AND sr.name LIKE CONCAT('%',#(name),'%')
    #end
    #if(params?? && params.createTime??) ### params(Map), key: 'createTime' , value: {'2019-06-01','2019-09-29'}
     AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1])
    #end
    #if(params?? && params.dataScope??)
     #(params.dataScope)
    #end
    ### AND 1 IN #(dataDeptIds) ### List<Long> dataDeptIds = Arrays.asList(1L,2L,3L,4L);
    ### AND 'a' IN #(stringsIds) ### List<String> stringsIds = Arrays.asList("a","b","c");

### 控制台打印sql如下:
### SELECT
###     sr.*
### FROM
###     sys_role sr
### LEFT JOIN
###     sys_user su
###         ON sr.create_by = su.id
### WHERE
###     1=1
###     AND sr.name LIKE CONCAT('%','户','%')
###     AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN '2019-06-01' AND '2019-09-29'
###     AND 1 IN (1,2,3,4)
###     AND 'a' IN ('a','b','c')





