package com.haokan.pubic.util;

import android.os.Environment;

import com.haokan.pubic.logsys.LogHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by wangzixu on 2017/11/11.
 */
public class BuildProperties {
    private final Properties mProperties;

    private BuildProperties(){
        mProperties = new Properties();
        try {
            mProperties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

//            Set<Map.Entry<Object, Object>> entrySet = mProperties.entrySet();//返回的属性键值对实体
//            for (Map.Entry<Object, Object> entry : entrySet) {
//                LogHelper.d("wangzixu", "app init : " + entry.getKey() + " = " + entry.getValue());
//            }
        } catch (Exception e) {
            LogHelper.d("wangzixu", "app init 获取不到build.prop");
            e.printStackTrace();
        }
    }

    public boolean containsKey(final Object key) {
        return mProperties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return mProperties.containsValue(value);
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return mProperties.entrySet();
    }

    public String getProperty(final String name) {
        return mProperties.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return mProperties.getProperty(name, defaultValue);
    }

    public boolean isEmpty() {
        return mProperties.isEmpty();
    }

    public Enumeration<Object> keys() {
        return mProperties.keys();
    }

    public Set<Object> keySet() {
        return mProperties.keySet();
    }

    public int size() {
        return mProperties.size();
    }

    public Collection<Object> values() {
        return mProperties.values();
    }

    public static BuildProperties newInstance(){
        return new BuildProperties();
    }


    public static String getSystemProperty(String propName){
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            LogHelper.e("wangzixu", "Unable to read sysprop " + propName);
            return null;
        }
        finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }
}
