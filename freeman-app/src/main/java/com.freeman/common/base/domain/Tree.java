package com.freeman.common.base.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JSONType(serialize = false) // fastjson怎么(只)让这个类的null值属性不序列化??
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Tree {

    private Long id;
    private Long key;
    private Long value;

    private Long parentId;

    private String label;
    private String title;

    private String icon;
    private String perms;
    private String type;
    private Integer sortNo;
    private String path;
    private String component;
    private List<Long> ids;
    private Date createTime;
    private Date updateTime;
    private List<Tree> children;
    private boolean hasParent = false;
    private boolean hasChildren = false;


    public void addChild(Tree node) {
        if (null == this.children){
            this.children = new ArrayList();
        }
        this.children.add(node);
    }
}
