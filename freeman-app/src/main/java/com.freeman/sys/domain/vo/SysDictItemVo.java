package com.freeman.sys.domain.vo;

import lombok.Data;

@Data
public class SysDictItemVo {

    private String label;

    private String value;

    // private String decorator;

    public SysDictItemVo() { }
    public SysDictItemVo(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
