package com.haokan.pubic.http.request;

import android.text.TextUtils;

import com.haokan.pubic.App;
import com.haokan.pubic.http.UrlsUtil;
import com.haokan.pubic.util.SecurityUtil;

import org.json.JSONException;
import org.json.JSONStringer;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class RequestHeader<RequestBody> {
    /**
     * messageID : 交易流水号。由yyyyMMddHHmmss+流水号10位）组成。流水号从0000000001开始计数，步长为1，最大取值为9999999999，循环使用
     * timeStamp : 请求方时间戳，消息发出时系统的当前时间。格式：yyyyMMddHHmmss
     * sign : 对消息包的摘要, 摘要算法为MD5，摘要的内容为（messageID+timeStamp +transactionType+系统密钥+消息体）
     * terminal : 1客户端，2网站，3HTML5
     * version : 终端版本
     * imei : 手机唯一识别号
     * ua : 手机型号
     * companyId : 厂商id
     */
    private String messageID;
    private String timeStamp;
    private String sign;
    private String terminal;
    private String version;
    private String imei;
    private String ua;
    private String companyId;
    private String countryCode;
    private String languageCode;
    private String did;

    /**
     * @param body 请求体
     */
    public RequestHeader(RequestBody body) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        timeStamp = sdf.format(new Date(System.currentTimeMillis()));
        messageID = timeStamp + UrlsUtil.getSerialCode();

        TreeMap<String, Object> treeMap = beanToMap(body);
        JSONStringer stringer = new JSONStringer();
        try {
            stringer.object();
            for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (TextUtils.isEmpty(key) || value == null || TextUtils.isEmpty(value.toString())) {
                    continue;
                }
                stringer.key(entry.getKey()).value(entry.getValue());
            }
            stringer.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String bodyStr = stringer.toString();
//        LogHelper.e("times","before---bodyStr="+bodyStr);
        StringBuilder sb = new StringBuilder();
//        String sign_temp = sb.append(messageID).append(timeStamp).append(transactionType).append(UrlsUtil_Java.SECRET_KEY).append(bodyStr).toString();
        String sign_temp = sb.append(messageID).append(timeStamp).append(UrlsUtil.SECRET_KEY).append(bodyStr).toString();
//        LogHelper.e("times","before---sign_temp="+sign_temp);
        sign = SecurityUtil.md5(sign_temp);
//        LogHelper.e("times","afeter---sign="+sign);
        terminal = "5";
        version = App.APP_VERSION_CODE+"";
        imei = App.sDID;
        companyId = UrlsUtil.COMPANYID;
        languageCode = "zh";
        countryCode = "cn";
        did=App.sDID;
    }

    public static TreeMap<String, Object> beanToMap(Object object){
        if (object == null) {
            return null;
        }
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        try {
            Class cls = object.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if(field.getName().equals("serialVersionUID"))
                    continue;
                map.put(field.getName(), field.get(object));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    /**
     * 如果是发哥写的接口需要改变签名规则
     * @param isFage
     */
    public void isFageHttp(boolean isFage) {
        if (isFage){
            sign = SecurityUtil.md5(timeStamp + imei + companyId);
        }
    }
}
