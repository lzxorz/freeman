

### 可以传Sort/Page
#@@# findAllByTemplate
SELECT * FROM sys_org so WHERE 1=1
#if(id??)
 AND so.id = #(id)
#end
#if(name??)
 AND so.name LIKE CONCAT('%',#(name),'%')
#end
#if(params?? && params.createTime??)
 AND date_format(so.create_time,'%Y-%m-%d') BETWEEN #(params.createTime[0]) AND #(params.createTime[1])
#end
#if(params?? && params.dataScope??)
 AND #(params.dataScope)
#end





