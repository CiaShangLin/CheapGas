package com.shang.gascheap.Main;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shang on 2018/10/25.
 */

public class GasPrediction {

    public static String UnicodeToString(String response){

        int length = response.length();
        int count = 0;
        //正则匹配条件，可匹配“\\u”1到4位，一般是4位可直接使用 String regex = "\\\\u[a-f0-9A-F]{4}";
        String regex = "\\\\u[a-f0-9A-F]{1,4}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            String oldChar = matcher.group();//原本的Unicode字符
            String newChar = unicode2String(oldChar);//转换为普通字符
            int index = response.indexOf(oldChar);

            sb.append(response.substring(count, index));//添加前面不是unicode的字符
            sb.append(newChar);//添加转换后的字符
            count = index+oldChar.length();//统计下标移动的位置
        }
        sb.append(response.substring(count, length));//添加末尾不是Unicode的字符


        String s[]=sb.toString().split("\\\n");


        return s[0];
    }


    private static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
}
