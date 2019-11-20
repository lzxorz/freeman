package com.freeman.spring.data.utils.request;


import com.freeman.common.utils.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * Controller层 接收查询请求的 分页参数、排序参数
 *   请求示例： http://localhost:8998/user?pageNum=1&pageSize=10&sortField=username&sortOrder=descend
 *
 * 可以设置默认排序字段(查询请求没传入时 生效)
 *   示例： PageRequest pageRequest = queryRequest.defaultSort("createTime", false).getPageRequest();
 *
 * @author 刘志新
 * @email  lzxorz@163.com
 */
// @Getter @Setter
public class QueryRequest implements Serializable {

    private Integer pageNo;
    private Integer pageSize;
    private String  sortField;  // 排序属性名称
    private String  sortOrder;  // 兼容ant-design-vue 默认排序方向参数 descend/ascend
    private Boolean isAsc;      // 是否升序排序


    /**
     * 设置默认排序字段, 前段没传排序字段 才生效
     * @param sortField 排序字段
     * @param isAsc 是否升序
     **/
    public QueryRequest defaultSort(String sortField, boolean isAsc) {
        if (StrUtil.isBlank(sortField)) {
            this.sortField = sortField;
            this.isAsc = isAsc;
        }
        return this;
    }

    /*************************************************
     * NativeSql调用
     *************************************************/
    public Integer getPageNo() {
        return (pageNo == null || pageNo <1) ? 1 : pageNo; //默认页码为1
    }

    /*************************************************
     * NativeSql调用
     *************************************************/
    public Integer getPageSize() {
        return (pageSize == null || pageSize < 1) ? 20 :pageSize; //默认页容量为20
    }


    /** 获取 Sort 对象 */
    public Sort getSort() {
        return hasSort() ? new Sort( (isAsc ? Sort.Direction.ASC : Sort.Direction.DESC), sortField) : null;
    }

    /** 获取 PageRequest 对象(包含分页和排序) */
    public PageRequest getPageRequest() {
        Sort sort = getSort();
        return null == sort ? PageRequest.of(getPageNo()-1, pageSize) : PageRequest.of(getPageNo()-1,getPageSize(),sort);
    }


    /**************************************************
     * NativeSql调用
     * @param tableAliase 表别名(例如: su)
     * @return 表别名.排序列名 排序方向(例如: su.age asc)
     **************************************************/
    public String getOrderBy(String tableAliase) {
        return hasSort() ? StrUtil.format("{}.{} {}", tableAliase, StrUtil.camelStr2UnderlineStr(sortField), isAsc?"ASC":"DESC") : null;
    }

    /**
     * 是否有排序字段及排序方向
     * 顺便替换掉空白字符，防止sql注入
     */
    private boolean hasSort(){
        //转换为是否升序bool值 //兼容ant-design-vue 默认排序方向参数 descend/ascend
        if (StrUtil.isNotBlank(sortOrder)) {
            isAsc = "ascend".equals(sortOrder.replaceAll("\\s", ""));
        }

        if (StrUtil.isNotBlank(sortField)) {
            sortField = sortField.replaceAll("\\s", "");
        }

        return !(null==sortField || null==isAsc);
    }
}
