package cn.eoe.wiki.activity;

import org.codehaus.jackson.type.TypeReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.eoe.wiki.R;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.utils.L;
/**
 * 用来处理最外层分类的界面
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public abstract class CategorysActivity extends SliderActivity{
	
	private static final int	HANDLER_DISPLAY_CATEGORY 	= 0x0001;
	private static final int	HANDLER_GET_CATEGORY_ERROR 	= 0x0002;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	void getCategory(String url)
	{
		if(TextUtils.isEmpty(url))
			throw new IllegalArgumentException("You must give a not empty url.");
		System.out.println(url);
		HttpManager manager = new HttpManager(url,null, HttpManager.GET, getCategorysTransaction);
		manager.start();
	}
	abstract void getCategorysError(String showText);
	abstract void generateCategorys(CategoryJson responseObject);

	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DISPLAY_CATEGORY:
				generateCategorys((CategoryJson)msg.obj);
				break;
			case HANDLER_GET_CATEGORY_ERROR:
				getCategorysError(getString(R.string.tip_get_category_error));
				break;
			default:
				break;
			}
		}
		
	};
	
	public ITransaction getCategorysTransaction = new ITransaction() {
		

		@Override
		public void transactionOver(String result) {
			
			CategoryJson responseObject;
			try {
				responseObject = mObjectMapper.readValue(result, new TypeReference<CategoryJson>() { });
				mHandler.obtainMessage(HANDLER_DISPLAY_CATEGORY, responseObject).sendToTarget();
			} catch (Exception e) {
				L.e("getGiftsTransaction exception", e);
				mHandler.obtainMessage(HANDLER_GET_CATEGORY_ERROR).sendToTarget();
			}
		}
		
		@Override
		public void transactionException(int erroCode,String result, Exception e) {
			mHandler.obtainMessage(HANDLER_GET_CATEGORY_ERROR).sendToTarget();
		}
	};
}
