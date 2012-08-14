package cn.eoe.wiki.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import cn.eoe.wiki.Constants;

/**
 * 工具类，主要是提供一些与wiki有密切关系的类。
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-2
 * @version 1.0.0
 */
public class WikiUtil {

	public static  		int 	VERSION_CODE		= 0;
	public static 		String  VERSION_NAME		= null;
	public static 		String  PACKAGE_NAME		= null;


	/**
	 * get the width of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static int getSceenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * get the height of the device screen
	 * 
	 * @param context
	 * @return
	 */
	public static int getSceenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static float getSceenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * convert the dip value to px value
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	/**
	 * get the version code of the application
	 * 
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		if(VERSION_CODE <= 0)
		{
			try {
				VERSION_CODE =  context.getPackageManager().getPackageInfo(getPackageName(context), 0).versionCode;
			} catch (NameNotFoundException e) {
				L.e("not find the name package", e);
			}
		}
		return VERSION_CODE;
	}

	/**
	 * get the version name of the application
	 * 
	 * @param context
	 * @return
	 */
	public static String getVerName(Context context) {
		if(TextUtils.isEmpty(VERSION_NAME))
		{
			try {
				VERSION_NAME =  context.getPackageManager().getPackageInfo(getPackageName(context), 0).versionName;
			} catch (NameNotFoundException e) {
				L.e("not find the name package", e);
			}
		}
		return VERSION_NAME;
	}

	/**
	 * get the package name of the app
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		if(TextUtils.isEmpty(PACKAGE_NAME))
		{
			PACKAGE_NAME =  context.getApplicationInfo().packageName;
		}
		return PACKAGE_NAME;
	}


	/**
	 * hide the input method
	 * 
	 * @param view
	 */
	public static void hideSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
		}
	}

	/**
	 * show the input method
	 * 
	 * @param view
	 */
	public static void showSoftInput(View view) {
		if (view == null)
			return;
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	public static int getSystemVersionCode() {
		return android.os.Build.VERSION.SDK_INT;
	}

	public static int getResourceColor(int resId, Context context)
	{
		return context.getResources().getColor(resId);
	}
}
