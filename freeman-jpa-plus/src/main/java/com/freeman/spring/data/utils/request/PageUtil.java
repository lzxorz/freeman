package com.freeman.spring.data.utils.request;

import cn.hutool.core.util.StrUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;


/**
 * 构建分页对象 PageRequest 工具类
 * @author 刘志新
 * @email  lzxorz@163.com
 */
public class PageUtil {

    /**
     * 构建分页请求对象 PageRequest
     * 使用方法示例： PageRequest pageRequest = PageUtil.getPageRequest(pageIndex,pageSize,sortField,isAsc);
     *
     * @param pageNo    当前页
     * @param pageSize  每页数据量
     * @param sortField 排序属性名
     * @param isAsc     是否升序排序
     */
    public static PageRequest buildPageRequest(Integer pageNo, Integer pageSize, String sortField, Boolean isAsc){
        Sort sort = StrUtil.isBlank(sortField) ? null : new Sort( (isAsc ? Sort.Direction.ASC : Sort.Direction.DESC), sortField);
        return buildPageRequest(pageNo,pageSize,sort);
    }

    /**
     * 构建分页请求对象 PageRequest
     * 使用方法示例： PageRequest pageRequest = PageUtil.getPageRequest(pageIndex,pageSize,sort);
     *
     * @param pageNo    当前页      默认1
     * @param pageSize  每页数据量   默认20
     * @param sort      排序
     */
    private static PageRequest buildPageRequest(Integer pageNo, Integer pageSize, Sort sort){
        //默认页面为0
        pageNo = (pageNo == null || pageNo < 1) ? 0 :  pageNo-1;
        //默认页面大小20
        pageSize = (pageSize==null || pageSize < 1) ? 20 :pageSize;

        if (null == sort) {
            return PageRequest.of(pageNo, pageSize);
        }
        return PageRequest.of(pageNo,pageSize,sort);
    }

    /** QueryRequest 调用 */
    public static PageRequest buildPageRequest(QueryRequest queryRequest){
        return buildPageRequest(queryRequest.getPageNo(), queryRequest.getPageSize(), buildSort(queryRequest));
    }

    /** QueryRequest 调用, 构建排序对象 */
    public static Sort buildSort(QueryRequest queryRequest){
        String[] orderFieldArr = queryRequest.getSortField();
        Boolean[] isAscArr = queryRequest.getIsAsc();
        // 不需要排序
        if (null==orderFieldArr || null==isAscArr){
            return null;
        }
        // 排序字段和排序方向 数量不一致
        if (orderFieldArr.length!=isAscArr.length){
            //log.error("排序字段和排序方向 数量不一致!!");
            return null;
        }
        // 没有排序字段
        if (0 == orderFieldArr.length) {
            return null;
        }
        // 1个排序字段
        if (1 == orderFieldArr.length) {
            return new Sort( (isAscArr[0] ? Sort.Direction.ASC : Sort.Direction.DESC), orderFieldArr[0]);
        }

        // 多个排序字段
        List<Sort.Order> orders = new ArrayList();
        for (int i = 0; i < orderFieldArr.length; i++) {
            if (isAscArr[i]) {
                orders.add(new Sort.Order(Sort.Direction.ASC,orderFieldArr[i]));
            }else {
                orders.add(new Sort.Order(Sort.Direction.DESC,orderFieldArr[i]));
            }
        }
        return Sort.by(orders);
    }



    // =================================================================================== //

    public static PageUtil.Builder builder() { return new PageUtil.Builder(); }

    public static final class Builder {

        private int pageNo = 0; //默认页面为0

        private int pageSize = 20; //默认页面大小20

        private List<Sort.Order> orders;

        public Builder() {
            this.orders = new ArrayList<>();
        }

        public PageUtil.Builder pageNo(int pageNo) {
            this.pageNo = pageNo;
            return this;
        }

        public PageUtil.Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PageUtil.Builder asc(String property) {
            return asc(true, property);
        }

        public PageUtil.Builder desc(String property) {
            return desc(true, property);
        }

        public PageUtil.Builder asc(boolean condition, String property) {
            if (condition) {
                orders.add(new Sort.Order(ASC, property));
            }
            return this;
        }

        public PageUtil.Builder desc(boolean condition, String property) {
            if (condition) {
                orders.add(new Sort.Order(DESC, property));
            }
            return this;
        }

        public PageRequest build() {
            return orders.size()>0 ? PageRequest.of(pageNo, pageSize, Sort.by(orders)) : PageRequest.of(pageNo, pageSize);
        }
    }

    // =================================================================================== //

    /**
     * 封装逻辑分页数据返回，返回 Result对象
     * @param list     未分页的全部数据
     * @param pageNum  当前页
     * @param pageSize 每页的数量
     * @return
     */
    /*public static R logicPage2Result(List<?> list, Integer pageNum, Integer pageSize){
        pageNum  = (null==pageNum || pageNum<0)?0:pageNum;      // 当前页
        pageSize = (null==pageSize || pageSize<0)?20:pageSize;  // 每页的数量
        if (null==list || list.size()<1){
            return R.ok();
        }
        long    totalCount= list.size();                        // 符合查询条件的总记录数
        long    totalPage=(totalCount+(pageSize-1))/pageSize;   // 符合查询条件的总页数
        int     formIndex=(pageNum-1)*pageSize;                 // 逻辑分页：起始索引
        int     toIndex=pageNum*pageSize;                       // 逻辑分页：结束索引
        List<?> data = (formIndex<totalCount && toIndex<totalCount) ? list.subList(formIndex, toIndex) : new ArrayList(); // 当前页结果集
        int     hasSize=data.size();                            // 当前页的实际数据量
        boolean firstPage = pageNum==1;                         // 是否为第一页
        boolean lastPage = pageNum==totalPage;                  // 是否为最后一页
        boolean hasPrePage = pageNum > 1;                       // 是否有前一页
        boolean hasNextPage = pageNum < totalPage;              // 是否有下一页

        LinkedHashMap<String, Object> pageInfo = new LinkedHashMap<String, Object>();
        pageInfo.put("pageNo", pageNum+1);             // 当前页
        pageInfo.put("pageSize", pageSize);             // 每页的记录数
        pageInfo.put("totalCount", totalCount);         // 符合查询条件的总记录数
        pageInfo.put("totalPage", totalPage);           // 符合查询条件的总页数
        pageInfo.put("firstPage", firstPage);           // 是否为第一页
        pageInfo.put("lastPage", lastPage);             // 是否为最后一页
        pageInfo.put("hasPrePage", hasPrePage);         // 是否有前一页
        pageInfo.put("hasNextPage", hasNextPage);       // 是否有下一页
        pageInfo.put("hasSize", hasSize);               // 当前页的记录数
        pageInfo.put("data", data);                     // 当前页结果集

        return R.ok(pageInfo);
    }*/

}
