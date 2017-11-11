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
    private final Properties properties;

    private BuildProperties(){
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

            Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();//返回的属性键值对实体
            for (Map.Entry<Object, Object> entry : entrySet) {
                LogHelper.d("wangzixu", "app init : " + entry.getKey() + " = " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsKey(final Object key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(final Object value) {
        return properties.containsValue(value);
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    public String getProperty(final String name) {
        return properties.getProperty(name);
    }

    public String getProperty(final String name, final String defaultValue) {
        return properties.getProperty(name, defaultValue);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Enumeration<Object> keys() {
        return properties.keys();
    }

    public Set<Object> keySet() {
        return properties.keySet();
    }

    public int size() {
        return properties.size();
    }

    public Collection<Object> values() {
        return properties.values();
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
                }
                catch (IOException e) {
                }
            }
        }
        return line;
    }
}
