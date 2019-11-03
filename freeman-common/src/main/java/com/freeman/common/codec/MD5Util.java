package com.freeman.common.codec;


import java.security.MessageDigest;

/** Md5加密方法*/
public class MD5Util {

	private static final String CHARSET = "UTF-8";



	public static String encode(String str) {
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();

            StringBuffer buf = new StringBuffer("");
            for (int i = 0, n; i < b.length; i++) {
                n = b[i];
                if (n < 0)
                    n += 256;
                if (n < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(n));
            }
            str = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return str;
	}

	/**
	 * 带盐值加密
	 *
	 * @param str 待加密字符串
	 * @param salt 盐值
	 */
	public static String encode(String str, String salt) {
		return encode(str + salt);
	}

	/*public static void main(String[] args) {
		String salt = UUID.randomUUID().toString().replace("-", "");
		System.out.println("salt：" + salt);
		System.out.println(encode("hao123456" + "a323210d922540cb8b4686b7bbc08d49"));
	}*/

}
