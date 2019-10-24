package com.cssi.common.app;

/**
 * 启动页实体类
 * <p>
 * Created by 36238 on 2018/1/3.
 */
public class StartModel extends BaseModel {

    private Object object;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public static class Object {

        /**
         * 图片地址
         */
        private String picUrl;

        /**
         * 是否可以议价 0不可以  1可以
         */
        private Integer isQuote;

        /**
         * 自提订单审核功能 0关闭   1开启  无效
         */
        private Integer isPickupAudit;

        /**
         * 最小下单量
         */
        private Double minnum;

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public Integer getIsQuote() {
            if (null == isQuote)
                isQuote = 0;
            return isQuote;
        }

        public void setIsQuote(Integer isQuote) {
            this.isQuote = isQuote;
        }

        public Integer getIsPickupAudit() {
            if (null == isPickupAudit)
                isPickupAudit = 0;
            return isPickupAudit;
        }

        public void setIsPickupAudit(Integer isPickupAudit) {
            this.isPickupAudit = isPickupAudit;
        }

        public Double getMinnum() {
            if (null == minnum)
                minnum = 1.0D;
            return minnum;
        }

        public void setMinnum(Double minnum) {
            this.minnum = minnum;
        }
    }

}
