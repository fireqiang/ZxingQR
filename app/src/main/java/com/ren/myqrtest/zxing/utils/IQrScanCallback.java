/**
 * Company:wz
 * Title:IQrScanListener.java
 * Description:
 * Date:2016年12月29日上午10:14:35
 * author:wei 
 * Version:V1.0
 */

package com.ren.myqrtest.zxing.utils;

import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;

/** 
 * @Description
 * @author wei 
 * @Date 2016年12月29日上午10:14:35
 */
public interface IQrScanCallback {
	Size onGetCameraPreviewSize();

	Handler onGetHandler();

	Rect onGetCropRect();

	void onHandleDecode(com.google.zxing.Result result, Bundle bundle);

	void onSetScanResult(int activityResult, Intent intent);
}
