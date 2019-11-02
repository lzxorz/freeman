package com.freeman.spring.data.request;


import cn.hutool.core.convert.Convert;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * 接收查询请求的 分页参数 和 排序参数(支持多个排序字段)
 *   请求示例： http://localhost:8998/user?pageNum=1&pageSize=10&sortField=username,age&sortOrder=descend,ascend
 *
 * 可以设置默认排序字段(查询请求没传入时 生效)
 *   示例： PageRequest pageRequest = queryRequest.setDefaultSortField("createTime", false).getPageRequest();
 *
 * @author 刘志新
 * @email  lzxorz@163.com
 */
@Getter
@Setter
public class QueryRequest implements Serializable {

    private Integer pageNo;
    private Integer pageSize;
    private String[] sortField;  // 排序属性名称
    private String[] sortOrder;  // descend,ascend //兼容ant-design-vue 默认方式
    private Boolean[] isAsc;     //是否升序排序

    public void setSortOrder(String[] sortOrder) {
        if (ObjectUtils.isEmpty(sortOrder)) return;
        isAsc = new Boolean[sortOrder.length];
        for (int i = 0; i < sortOrder.length; i++) {
            isAsc[i] = sortOrder[i].equalsIgnoreCase("ascend");
        }

    }

    /**
     * 设置默认排序字段, 前段没传排序字段 才生效
     * @author 刘志新
     * @email  lzxorz@163.com
     * @param sortField 排序字段
     * @param isAsc 是否升序
     **/
    public QueryRequest setDefaultSortField(String sortField, boolean isAsc) {
        if (ObjectUtils.isEmpty(sortField)) {
            this.sortField = new String[]{sortField};
            this.isAsc = new Boolean[]{isAsc};
        }
        return this;
    }

    /**
     * 设置默认排序字段, 前段没传排序字段 才生效
     * sortField.length 必须等于 isAsc.length
     *
     * @author 刘志新
     * @email  lzxorz@163.com
     * @param sortField 排序字段
     * @param isAsc 是否升序
     */
    public QueryRequest setDefaultSortField(String[] sortField, boolean[] isAsc) {
        if (ObjectUtils.isEmpty(sortField)) {
            this.sortField = sortField;
            this.isAsc = Convert.toBooleanArray(isAsc);
        }
        return this;
    }

    /**
     * 获取 PageRequest 对象(包含分页和排序)
     * @author 刘志新
     * @email  lzxorz@163.com
     */
    public PageRequest getPageRequest() {
        return PageUtil.buildPageRequest(this);
    }


    /**
     * 获取 Sort 对象
     * @author 刘志新
     * @email  lzxorz@163.com
     */
    public Sort getSort() {
        return PageUtil.buildSort(this);
    }
}
