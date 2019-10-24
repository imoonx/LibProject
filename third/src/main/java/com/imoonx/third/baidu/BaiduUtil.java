package com.imoonx.third.baidu;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.imoonx.util.XLog;

/**
 * 百度地图工具类
 */
public class BaiduUtil {
    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     *
     * @param sourceLatLng gps 坐标
     * @return 转换后的点
     */
    public static LatLng gpsToBaidu(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    /**
     * 移动APP调起Android百度地图方式
     *
     * @param context     上下文
     * @param startName   起点名称
     * @param endName     终点名称
     * @param startLatLng 起点
     * @param endLatLng   终点
     */
    public static void startBaiduMap(Context context, String startName, String endName, LatLng startLatLng, LatLng endLatLng) {
        if (null == context)
            return;
        // 构建 route搜索参数以及策略，起终点也可以用name构造
        RouteParaOption para = new RouteParaOption()
                .startPoint(startLatLng)
                .startName(startName)
                .endPoint(endLatLng)
                .endName(endName);
        try {
            BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
        } catch (Exception e) {
            XLog.e(BaiduUtil.class, e);
        }
    }

    /**
     * 移动APP调起Android高德地图方式
     *
     * @param context     上下文
     * @param startName   起点名称
     * @param endName     终点名称
     * @param startLatLng 起点
     * @param endLatLng   终点
     */
    public static void startGaoDeMap(Context context, String startName, String endName, LatLng startLatLng, LatLng endLatLng) {
        if (null == context)
            return;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(android.net.Uri.parse("amapuri://route/plan/?sid=BGVIS1&slat=" + startLatLng.latitude + "&slon=" + startLatLng.longitude
                + "&sname=" + startName + "&did=BGVIS2&dlat="
                + endLatLng.latitude + "&dlon=" + endLatLng.longitude + "&dname=" + endName + "&dev=0&t=0"));
        intent.setPackage("com.autonavi.minimap");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 百度经纬度坐标BD09ll转国测局坐标GCJ02ll
     *
     * @param sourceLatLng 待转换的位置点
     * @return 转换后的位置点 可能为null
     */
    public static LatLng baiduToGCJ02ll(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.BD09LL);
        converter.coord(sourceLatLng);
        return converter.convert();
    }

    /**
     * 调用腾讯地图app驾车导航
     *
     * @param context     上下文
     * @param startName   起点地址
     * @param startLatLng 起点
     * @param endName     终点地址
     * @param endLatLng   终点
     */
    public static void startTencentMap(Context context, String packageName, String startName, String endName, LatLng startLatLng, LatLng endLatLng) {
        if (null == context)
            return;
        String stringBuffer = "qqmap://map/" +
                "routeplan?" +
                "type=" +
                "drive" +
                "&from=" + startName +
                "&fromcoord=" +
                startLatLng.latitude + "," + startLatLng.longitude +
                "&to=" +
                endName +
                "&tocoord=" +
                endLatLng.latitude + "," + endLatLng.longitude +
                "&referer=" +
                packageName + "0";
        Intent intent = new Intent();
        intent.setData(Uri.parse(stringBuffer));
        context.startActivity(intent);
    }
}
