package com.freeman.common.base.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @description BaseController 貌似没啥用
 * @author 刘志新
 * @email  lzxorz@163.com
 * @date   19-6-10 上午10:15
 * @Param
 * @return
 **/
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /** 将前台传递过来的日期格式的字符串，自动转化为Date类型 */
    /*@InitBinder
    public void initBinder(WebDataBinder binder){
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport(){
            @Override
            public void setAsText(String text){
                setValue(DateUtil.parse(text));
            }
        });
    }*/

}
