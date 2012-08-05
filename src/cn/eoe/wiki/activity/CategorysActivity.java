package cn.eoe.wiki.activity;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.eoe.wiki.Constants;
import cn.eoe.wiki.R;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.HttpUrl;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.CategoryChild;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.listener.CategoryListener;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
/**
 * 用来处理最外层分类的界面
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class CategorysActivity extends SliderActivity {
	
	private static final int	HANDLER_DISPLAY_CATEGORY = 0x0001;
	
	private LinearLayout	mCategoryLayout;
	private LayoutInflater 	mInflater;
	private Button			mBtnSearch;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorys);
		mInflater = LayoutInflater.from(mContext);
		initComponent();
		initData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	void initComponent() {
		mCategoryLayout = (LinearLayout)findViewById(R.id.layout_category);
		mBtnSearch=(Button)findViewById(R.id.btn_search);
		mBtnSearch.requestFocus();
	}

	void initData() {
		getCategory();
		//display the loading progress bar
		showProgressLayout();
	}
	private void getCategory()
	{

		HttpUrl url = new HttpUrl(Constants.API_PATH_INDEX);
		url.setAction(Constants.API_ACTION_RAW);
		url.addParam("title", "Api_cate_kris");
		HttpManager manager = new HttpManager(url, HttpManager.GET, getCategorysTransaction);
		manager.start();
	}
	private void showProgressLayout()
	{
		View progressView = mInflater.inflate(R.layout.loading, null);
		mCategoryLayout.removeAllViews();
		mCategoryLayout.addView(progressView);
	}
	private void generateCategory(CategoryJson responseObject)
	{
		List<CategoryChild> categorys =  responseObject.getContents();
		if(categorys!=null)
		{
			mCategoryLayout.removeAllViews();
			for(CategoryChild category:categorys)
			{
				TextView tv = (TextView)mInflater.inflate(R.layout.category_item, null);
				tv.setText(category.getText());
				mCategoryLayout.addView(tv);
				List<CategoryChild> categorysChildren =  category.getChildren();
				if(categorysChildren!=null)
				{
					for(CategoryChild categorysChild:categorysChildren)
					{
						TextView tvChild = (TextView)mInflater.inflate(R.layout.category_item, null);
						tvChild.setText(categorysChild.getText());
						tvChild.setPadding(50, 0, 0, 0);
						L.d("tvChild font:"+tvChild.getTextSize());
						tvChild.setTextSize(tvChild.getTextSize()*2/5);
						tvChild.setTextColor(WikiUtil.getResourceColor(R.color.black, mContext));
						tvChild.setOnClickListener(new CategoryListener(this, categorysChild));
						mCategoryLayout.addView(tvChild);
					}
				}
			}
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DISPLAY_CATEGORY:
				generateCategory((CategoryJson)msg.obj);
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
				Message msg = mHandler.obtainMessage(HANDLER_DISPLAY_CATEGORY, responseObject);
				msg.sendToTarget();
			} catch (Exception e) {
				L.e("getGiftsTransaction exception", e);
			}
		}
		
		@Override
		public void transactionException(int erroCode,String result, Exception e) {
			
		}
	};
}
