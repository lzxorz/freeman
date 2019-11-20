package com.freeman.spring.data.utils.request;

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

    public static PageUtil.Builder builder() { return new PageUtil.Builder(); }

    public static final class Builder {

        private int pageNo = 1;    //默认页码为1
        private int pageSize = 20; //默认页容量为20

        private List<Sort.Order> orders;

        public Builder() {
            this.orders = new ArrayList<>();
        }

        public PageUtil.Builder pageNo(int pageNo) {
            this.pageNo = pageNo < 1 ? 1 :  pageNo;
            return this;
        }

        public PageUtil.Builder pageSize(int pageSize) {
            this.pageSize = pageSize < 1 ? 20 :pageSize;
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
            return orders.size()>0 ? PageRequest.of(pageNo-1, pageSize, Sort.by(orders)) : PageRequest.of(pageNo-1, pageSize);
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
