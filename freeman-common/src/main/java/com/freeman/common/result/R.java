package com.freeman.common.result;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 统一响应结果封装
 */
//@ApiModel(value="通用返回对象",description="ResponseData")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R {

	//@ApiModelProperty(value="返回状态码")
    private int code;
	//@ApiModelProperty(value="返回消息")
    private String message;
	//@ApiModelProperty(value="返回数据体{} or []")
    private Object data;
    //@ApiModelProperty(value="返回分页信息")
    private HashMap<String,Object> pageInfo;

    public R(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }
    public R setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }
    public R setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }
    public R setData(Object data) {
        /** 分页数据返回, 兼容jpa　Page */
        if (data instanceof Page){
            Page page = (Page)data;
            this.pageInfo=new HashMap<String,Object>(){{
                put("pageNo",     page.getNumber()+1);          // 当前页
                put("pageSize",    page.getSize());             // 每页的记录数
                put("totalCount",  page.getTotalElements());    // 符合查询条件的总记录数
                put("totalPage",   page.getTotalPages());       // 符合查询条件的总页数
                put("firstPage",   page.isFirst());             // 是否为第一页
                put("lastPage",    page.isLast());              // 是否为最后一页
                put("hasPrePage",  page.hasPrevious());         // 是否有前一页
                put("hasNextPage", page.hasNext());             // 是否有下一页
                put("hasSize",     page.getNumberOfElements()); // 当前页的记录数
                // put("list", list);
            }};
            this.data = page.getContent();                      // 当前页结果集
            return this;
        }
        /** 逻辑分页数据返回 */
        if (data instanceof LinkedHashMap){
            LinkedHashMap map = (LinkedHashMap)data;
            this.data = map.remove("data");
            this.pageInfo = map;
            return this;
        }

        this.data = data;
        return this;
    }

    public HashMap<String, Object> getPageInfo() {
        return pageInfo;
    }


    /** 快捷方式--成功 */
    public static R ok() {
        return new R(AssertUtil.SUCCESS.getCode(), AssertUtil.SUCCESS.getMessage());
    }
    public static R ok(String message) {
        return new R(AssertUtil.SUCCESS.getCode(), message);
    }
    public static R ok(Object data) {
        return new R(AssertUtil.SUCCESS.getCode(), AssertUtil.SUCCESS.getMessage()).setData(data);
    }
    public static R ok(String message, Object data) {
        return new R(AssertUtil.SUCCESS.getCode(),message).setData(data);
    }
    /** 快捷方式--失败 */
    public static R error() {
        return new R(AssertUtil.UNKNOWN_ERROR.getCode(),AssertUtil.UNKNOWN_ERROR.getMessage());
    }
    public static R error(String message) {
        return new R(AssertUtil.UNKNOWN_ERROR.getCode(),message);
    }
    public static R error(Object data) {
        return new R(AssertUtil.UNKNOWN_ERROR.getCode(),AssertUtil.UNKNOWN_ERROR.getMessage()).setData(data);
    }
    public static R error(String message, Object data) {
        return new R(AssertUtil.UNKNOWN_ERROR.getCode(),message).setData(data);
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}
