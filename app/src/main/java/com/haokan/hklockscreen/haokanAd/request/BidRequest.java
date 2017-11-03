package com.haokan.hklockscreen.haokanAd.request;

import java.util.List;

/**
 * Created by wangzixu on 2017/11/3.
 * 广告请求的整体对象
 */
public class BidRequest {
    public String id;
    public List<Imp> imp;
    public AdApp app;
    public Device device;
    public Geo geo;
}
