package com.freeman.common.utils.serializer;

import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

import java.io.UnsupportedEncodingException;

public class StringSerializer implements org.springframework.data.redis.serializer.RedisSerializer<String>,org.crazycake.shiro.serializer.RedisSerializer<String> {
    private String charset = "UTF-8";


    public byte[] serialize(@Nullable String str) throws SerializationException {
        try {
            return str == null ? null : str.getBytes(this.charset);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("serializer error, string=" + str, e);
        }
    }


    public String deserialize(@Nullable byte[] bytes) throws SerializationException {
        try {
            return bytes == null ? null : new String(bytes, this.charset);
        } catch (UnsupportedEncodingException e) {
            throw new SerializationException("deserialize error", e);
        }
    }


    public String getCharset() {
        return this.charset;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
}