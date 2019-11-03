package com.freeman.spring.data.utils.request;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * 构建排序对象 Sort 工具类
 * @author 刘志新
 * @email  lzxorz@163.com
 */
public class SortUtil {

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private List<Sort.Order> orders;

        public Builder() {
            this.orders = new ArrayList<>();
        }

        public Builder asc(String property) {
            return asc(true, property);
        }

        public Builder desc(String property) {
            return desc(true, property);
        }

        public Builder asc(boolean condition, String property) {
            if (condition) {
                orders.add(new Sort.Order(ASC, property));
            }
            return this;
        }

        public Builder desc(boolean condition, String property) {
            if (condition) {
                orders.add(new Sort.Order(DESC, property));
            }
            return this;
        }

        public Sort build() {
            return Sort.by(orders);
        }
    }
}
