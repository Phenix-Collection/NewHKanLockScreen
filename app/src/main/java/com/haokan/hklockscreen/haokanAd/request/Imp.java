package com.haokan.hklockscreen.haokanAd.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangzixu on 2017/11/3.
 * 广告位对象 Imp
 id required string Imp 的序号,如 1、2、3
 tagid required string 广告位唯一标示ID
 bidfloor required int 每千次曝光底价,单位是分。默认币种为人民币。可在对
 接前协商具体币种。
 adviewtype required int 广告展示类型。0.未知;1.固定;2.浮动;3.弹出式;4.插 屏;5.
 全屏;6.视频贴片(instream);7.视频暂停;8.叠加 广告
 (overlay)9.播放器外的广告(companion);10.信息流
 (feeds);11.无线墙。1~5 为 banner 形式;6~9 为视频
 形式;10~11 为移动 原生形式
 adlocation optional int 广告位置;0.未知;1.首屏;2.非首屏
 banner optional object 描述Banner类型广告的对象
 video optional object 描述Video类型广告的对象
 native object object 描述Native类型广告的对象
 pmp optional object 描述Pmp类型广告的对象
 */
public class Imp {
    public String id;
    public String tagid ;
    public int bidfloor ;
    public int adviewtype ;
    public int adlocation ;
    public BannerReq banner;
    @SerializedName("native")
    public NativeReq nativeX;
}
