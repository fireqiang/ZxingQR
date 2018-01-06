/**
 * Company:wz
 * Title:QrCodeUtil.java
 * Description:
 * Date:2017年2月7日下午6:07:43
 * author:wei 
 * Version:V1.0
 */

package com.ren.myqrtest.zxing.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/** 二维码工具类
 * @Description
 * @author wei 
 * @Date 2017年2月7日下午6:07:43
 */
public class QrCodeUtil {

	/** 
	 * 生成二维码Bitmap 
	 * 
	 * @param content   文本内容 
	 * @param size 二维码宽高
	 * @return 合成后的bitmap 
	 */
	public static Bitmap createQRImage(String data, int size) {
		if (size <= 0 || size <= 0) {
			size = 400;
		}
		try {
			if (data == null || data.length() == 0) {
				return null;
			}

			//配置参数   
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别   
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度   
			hints.put(EncodeHintType.MARGIN, 0); //default is 4   

			// 图像数据转换，使用了矩阵转换   
			BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
			int[] pixels = new int[size * size];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，   
			// 两个for循环是图片横列扫描的结果   
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * size + x] = 0xff000000;
					} else {
						pixels[y * size + x] = 0xffffffff;
					}
				}
			}

			// 生成二维码图片的格式，使用ARGB_8888   
			Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
			bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

			return bitmap;
			//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！   
			//return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));   
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 生成带logo的二维码，logo默认为二维码的1/10
	 *
	 * @param text 需要生成二维码的文字、网址等
	 * @param mBitmap logo文件
	 * @param size 需要生成二维码的大小（）
	 * @return bitmap
	 */
	public static Bitmap createQRCodeWithLogo(String text, Bitmap mBitmap, int size) {
		try {
			int logoImageHalfSize = size / 10;
			Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			/*
			 * 设置容错级别，默认为ErrorCorrectionLevel.L
			 * 因为中间加入logo所以建议你把容错级别调至H,否则可能会出现识别不了
			 */
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度   
			hints.put(EncodeHintType.MARGIN, 0); //default is 4   
			BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);

			int width = bitMatrix.getWidth();//矩阵高度
			int height = bitMatrix.getHeight();//矩阵宽度
			int halfW = width / 2;
			int halfH = height / 2;

			Matrix m = new Matrix();
			float sx = (float) 2 * logoImageHalfSize / mBitmap.getWidth();
			float sy = (float) 2 * logoImageHalfSize / mBitmap.getHeight();
			m.setScale(sx, sy);
			//设置缩放信息
			//将logo图片按martix设置的信息缩放
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);

			int[] pixels = new int[size * size];
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					if (x > halfW - logoImageHalfSize && x < halfW + logoImageHalfSize && y > halfH - logoImageHalfSize
							&& y < halfH + logoImageHalfSize) {
						//该位置用于存放图片信息
						//记录图片每个像素信息
						pixels[y * width + x] = mBitmap.getPixel(x - halfW + logoImageHalfSize, y - halfH + logoImageHalfSize);
					} else {
						if (bitMatrix.get(x, y)) {
							pixels[y * size + x] = 0xff000000;
						} else {
							pixels[y * size + x] = 0xffffffff;
						}
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}

}
