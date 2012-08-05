package cn.eoe.wiki;

import android.app.Application;
import cn.eoe.wiki.activity.MainActivity;
import cn.eoe.wiki.utils.L;
/**
 * 定义我们整个应用程序的Application对象，在此类中可以实例化一些与整个应用周期有关的变量与属性。<br>
 * 可以作一些简单的缓存，但是不建议做太大的缓存保存
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-2
 * @version 1.0.0
 */
public class WikiApplication extends Application {
	public static 	WikiApplication		application;
	public 	 		MainActivity		mainActivity;
	
	public static WikiApplication getApplication()
	{
		if(application==null)
		{
			throw new NullPointerException("The application may be not running .");
		}
		return application;
	}
	
	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		//ini the application config
		WikiConfig.initConfig(application);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		L.e("onTerminate");
	}

}
