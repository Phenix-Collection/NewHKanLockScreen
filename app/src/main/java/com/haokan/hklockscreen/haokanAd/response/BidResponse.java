package com.haokan.hklockscreen.haokanAd.response;

import java.util.List;

/**
 * Created by wangzixu on 2017/11/3.
 id required string 对应 bidreqeust 中的 id
 seatbid optional object array DSP 席位,目前一个
 bidid optional string DSP 竞价 id
 */
public class BidResponse {
    public String id;
    public List<Seat> seatbid;
}
