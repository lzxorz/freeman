package com.freeman;

import cn.hutool.core.util.StrUtil;
import com.freeman.sys.domain.SysOrg;
import com.freeman.utils.StringKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class mainTest {


    /**  */
    private static void splitTrim() {

       String str = ",0,1,101,10101,,,123456,";
       List<String> strings = StrUtil.splitTrim(str, ",");
       strings.add("10086");
       log.info(StrUtil.join(",",strings));

    }

    /** shrio 密码加密 */
    private static void encrypt() {

        String algorithmName = "md5";
        String salt = "31fc4012f2370528";
        // "123456" ==前端md5 1次==> e10adc3949ba59abbe56e057f20f883e
        String passwd = "e10adc3949ba59abbe56e057f20f883e";
        int hashIterations = 2;
        String newPassword = new SimpleHash(algorithmName, passwd, ByteSource.Util.bytes(salt), hashIterations).toHex();
        log.info(newPassword);

    }

    public static void main(String[] args) {
        // encrypt();

    }

}