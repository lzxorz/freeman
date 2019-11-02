package com.freeman.utils;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.source.ISource;

public class EntityMethodKeySource implements ISource {
    private String cacheKey;
    private StringBuilder content;

    public EntityMethodKeySource(String cacheKey) {
        if (StrKit.isBlank(cacheKey)) {
            throw new IllegalArgumentException("content can not be blank");
        }else {
            this.cacheKey = cacheKey;
        }

    }

    public EntityMethodKeySource(String cacheKey, String content, boolean cache) {
        if (StrKit.isBlank(content)) {
            throw new IllegalArgumentException("content can not be blank");
        } else {
            this.content = new StringBuilder(content);
            this.cacheKey = cache ? (cacheKey != null ? cacheKey : HashKit.md5(content)) : null;
        }
    }


    public EntityMethodKeySource(String cacheKey, StringBuilder content, boolean cache) {
        if (content != null && content.length() != 0) {
            this.content = content;
            this.cacheKey = cache ? (cacheKey != null ? cacheKey : HashKit.md5(content.toString())) : null;
        } else {
            throw new IllegalArgumentException("content can not be blank");
        }
    }

    public boolean isModified() {
        return false;//StrKit.notBlank(this.content.toString());
    }

    public String getCacheKey() {
        return this.cacheKey;
    }

    public StringBuilder getContent() {
        return this.content;
    }

    public String getEncoding() {
        return "UTF-8";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("cacheKey : ").append(this.cacheKey).append("\n");
        sb.append("content : ").append(this.content).append("\n");
        return sb.toString();
    }
}
