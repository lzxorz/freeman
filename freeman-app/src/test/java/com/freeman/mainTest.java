package com.freeman;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        int count = 56;   //红包数量数
        int money = 280000;    //总金额 分 (应该有上限)

        int sum = 0;
        int[] result = allocateRedPacket(count, money);
        for (int i = 0; i < result.length; i++) {
            System.out.println(fenToYuan(result[i]));
            sum += result[i];
        }
        System.out.println("\n总金额==> " + fenToYuan(sum));

    }


    /**
     * 发 拼手气红包
     *
     * @Param  count 红包个数
     * @Param  money 红包总金额(单位: 分, 上限: 500000分)
     * @return
     */
    private static int[] allocateRedPacket(int count, int money) {
        Assert.checkBetween(count, 1, 5000); // 最少1个红包,最多5000个(群人数上限)
        Assert.checkBetween(money, count, 500000); // 金额下限: 每个红包最少1分钱, 金额上限5千元(500000分)

        int[] result = new int[count];

        int allocateSum = money; // 按每人一分钱预留，剩下的再分配

        //第二步，循环遍历如果剩余金额>0 则一直分配
        //创建一个随机分配对象
        Random random = new Random();
        while (allocateSum>1 && count>0) {
            int temp = random.nextInt(allocateSum/count*2) + 1;  //1 ~ 剩余平均值*2之间
            result[count-1] = temp<allocateSum ? temp : allocateSum;
            allocateSum -= result[count-1];
            //System.out.println("当前红包金额: "+result[count-1]+", 余额: "+allocateSum);
            count--;
        }

        //判断剩余未分配的金额如果大于0，可以把剩下未分配金额塞到最后一个红包中
        if (allocateSum > 0){
            result[result.length-1] +=  allocateSum;
        }

        return result;
    }


    private static String fenToYuan(int amount){
        NumberFormat numberFormat = NumberFormat.getInstance();
        try{
            Number number = numberFormat.parse(String.valueOf(amount));
            double temp = number.doubleValue() / 100.0;
            numberFormat.setGroupingUsed(false);
            // 设置返回的小数部分所允许的最大位数
            numberFormat.setMaximumFractionDigits(2);
            return numberFormat.format(temp);
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

}