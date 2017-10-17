package com.haokan.pubic.util;

import android.text.TextUtils;
import android.util.Base64;

import java.security.MessageDigest;

/**
 * 数据安全加密解密的工具类
 */
public class SecurityUtil {
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[aB & 0x0f]);
        }
		return sb.toString();
	}

	/**
	 * 好看自己的加密方式，手机号，验证码，昵称，邀请码，都需要加密后传递
	 */
	public static String haokanEncode(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		String enStr1 = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
		String enStr = enStr1.replaceAll("\r|\n", ""); //去掉换行符
		//Log.d("wangzixu", "haokanencode enstr + = " + enStr + "xxxxxx");
		char[] tempArr = new char[enStr.length()];
		for (int i = 0; i < enStr.length(); i++) {
			tempArr[i] = enStr.charAt(i);
			if (i % 2 == 1) {
				tempArr[i] = tempArr[i - 1];
				tempArr[i - 1] = enStr.charAt(i);
			}
		}
		String nowStr = String.valueOf(tempArr);
		//Log.d("wangzixu", "haokanencode nowstr1 = " + nowStr);
		int half = nowStr.length() / 2;
		nowStr = nowStr.substring(half) + nowStr.substring(0, half);
		//Log.d("wangzixu", "haokanencode nowstr2 = " + nowStr);
		//rot13
		char[] chars = nowStr.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] >= 'a' && chars[i] <= 'm')
				chars[i] += 13;
			else if (chars[i] >= 'A' && chars[i] <= 'M')
				chars[i] += 13;
			else if (chars[i] >= 'n' && chars[i] <= 'z')
				chars[i] -= 13;
			else if (chars[i] >= 'N' && chars[i] <= 'Z')
				chars[i] -= 13;
		}
		nowStr = String.valueOf(chars);
//		Log.d("wangzixu", "haokanencode nowstr2 = " + nowStr);
		return nowStr;
	}

//    //好看简单加密算法
//    function HkEncode($Str){
//        if ( strlen($Str) == 0 ){  //如果输入长度为0 直接返回空
//            return '';
//        }
//        $NowArr = array();  //生成一个空数组
//        $EnStr=base64_encode($Str); //输入的字符串 base64加密
//        $i = 0;
//        //把字符串的奇偶位调换位置
//        while($EnStr[$i]) {
//            $NowArr[$i] = $EnStr[$i];
//            if ($i%2 == 1) {
//                $NowArr[$i] = $NowArr[$i-1];
//                $NowArr[$i-1] = $EnStr[$i];
//            }
//            $i++;
//        }
//        $NowStr = implode('',$NowArr);
//
//
//        $HalfLen = floor(strlen($NowStr) / 2); //字符串长度舍去法取整
//        $NowStr = substr ($NowStr,$HalfLen).substr ($NowStr,0,$HalfLen); //按照上一结果长度对调字符串
//        $NowStr = str_rot13($NowStr);  //rot13调换字符串
//        return $NowStr;
//    }
}
