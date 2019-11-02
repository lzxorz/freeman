package com.freeman.common.router;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.freeman.common.base.domain.Tree;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 构建 Vue路由
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JSONType(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
public class VueRouter implements Serializable {

    private static final long serialVersionUID = -3327478146308500708L;

    @JsonIgnore @JSONField(serialize = false)
    private Long id;

    @JsonIgnore @JSONField(serialize = false)
    private Long parentId;

    private String path;

    private String name;

    private String component;

    private String icon;

    private String redirect;

    private RouterMeta meta;

    private List<VueRouter> children;

    @JsonIgnore @JSONField(serialize = false)
    private boolean hasParent = false;

    @JsonIgnore @JSONField(serialize = false)
    private boolean hasChildren = false;


    public void addChild(VueRouter node) {
        if (null == this.children){
            this.children = new ArrayList();
        }
        this.children.add(node);
    }


}
