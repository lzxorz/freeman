### 模板只支持行内注释

### 可以传Sort/Page
#@@# findAllByTemplate
SELECT
    sp.id,
    sp.parent_id,
    sp.name,
    sp.path,
    sp.component,
    sp.perms,
    sp.icon,
    sp.type,
    sp.sort_no,
    sp.hide,
    sp.description,
    sp.create_by,
    sp.create_time,
    sp.update_by,
    sp.update_time
FROM
    sys_permission sp
WHERE 1=1
    #if(type??)
     AND sp.type = #(type)
    #end
    #if(params?? && params.createTime??)
     AND date_format(sr.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1])
    #end

### 可以传Sort/Page
#@@# findTree
SELECT
    sp.id AS id,
    sp.id AS `key`,
    sp.parent_id AS parentId,
    sp.name AS `label`,
    sp.perms AS perms,
    sp.icon AS icon,
    sp.type AS type,
    sp.sort_no AS sortNo,
    sp.path,
    sp.component,
    sp.create_time AS createTime,
    sp.update_time AS updateTime
FROM
    sys_permission sp
WHERE 1=1
    #if(name??)
     AND sp.name LIKE CONCAT('%',#(name),'%')
    #end
    #if(type??)
     AND sp.type = #(type)
    #end
    #if(params?? && params.createTime??)
     AND date_format(sp.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1])
    #end