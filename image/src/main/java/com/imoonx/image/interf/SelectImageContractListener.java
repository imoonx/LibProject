package com.imoonx.image.interf;


/**
 * 图片选择器建立契约关系，将权限操作放在Activity，具体数据放在Fragment 申请权限接口
 */
public interface SelectImageContractListener {

    interface Operator {

        void requestCamera();

        void requestExternalStorage();

        void onBack();

        void setDataView(View view);

    }

    interface View {

        void onOpenCameraSuccess();

        void onCameraPermissionDenied();
    }
}
