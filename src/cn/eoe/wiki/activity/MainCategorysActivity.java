package cn.eoe.wiki.activity;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.eoe.wiki.R;
import cn.eoe.wiki.WikiConfig;
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
public class MainCategorysActivity extends CategorysActivity implements OnClickListener{
	
	private LinearLayout	mCategoryLayout;
	private LayoutInflater 	mInflater;
	private Button			mBtnSearch;

	private Button			mBtnAbout;
	private Button			mBtnRecommand;
	private Button			mBtnFeedback;
	private Button			mBtnRecent;
	
	private String			mCategoryUrl;

	private boolean			mProgressVisible;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorys);
		mInflater = LayoutInflater.from(mContext);
		mCategoryUrl = WikiConfig.getMainCategoruUrl();
		initComponent();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	void initComponent() {
		mCategoryLayout = (LinearLayout)findViewById(R.id.layout_category);
		mBtnSearch=(Button)findViewById(R.id.btn_search);
		mBtnSearch.requestFocus();

		mBtnAbout=(Button)findViewById(R.id.btn_about);
		mBtnRecommand=(Button)findViewById(R.id.btn_recommand);
		mBtnFeedback=(Button)findViewById(R.id.btn_feedback);
		mBtnRecent=(Button)findViewById(R.id.btn_recent);
		mBtnSearch.setOnClickListener(this);
		mBtnAbout.setOnClickListener(this);
		mBtnRecommand.setOnClickListener(this);
		mBtnFeedback.setOnClickListener(this);
		mBtnRecent.setOnClickListener(this);
	}

	void initData() {
		showProgressLayout();
		getCategory(mCategoryUrl);
	}
	
	protected void showProgressLayout()
	{
		View progressView = mInflater.inflate(R.layout.loading, null);
		mCategoryLayout.removeAllViews();
		mCategoryLayout.addView(progressView);
		mProgressVisible = true;
	}
	@Override
	protected void getCategorysError(String showText)
	{
		mCategoryLayout.removeAllViews();
		mProgressVisible = false;
		
		View viewError = mInflater.inflate(R.layout.loading_error, null);
		TextView tvErrorTip =  (TextView)viewError.findViewById(R.id.tv_error_tip);
		tvErrorTip.setText(showText);
		tvErrorTip.setTextColor(WikiUtil.getResourceColor(R.color.red, mContext));
		

		Button btnTryAgain =  (Button)viewError.findViewById(R.id.btn_try_again);
		btnTryAgain.setOnClickListener(this);
		mCategoryLayout.addView(viewError);
	}
	@Override
	protected void generateCategorys(CategoryJson responseObject)
	{
		List<CategoryChild> categorys =  responseObject.getContents();
		if(categorys!=null)
		{
			mCategoryLayout.removeAllViews();
			mProgressVisible = false;
			
			for(CategoryChild category:categorys)
			{
				TextView tv = (TextView)mInflater.inflate(R.layout.category_item, null);
				tv.setText(category.getName());
				mCategoryLayout.addView(tv);
				List<CategoryChild> categorysChildren =  category.getChildren();
				if(categorysChildren!=null)
				{
					for(CategoryChild categorysChild:categorysChildren)
					{
						TextView tvChild = (TextView)mInflater.inflate(R.layout.category_item, null);
						tvChild.setText(categorysChild.getName());
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_try_again:
			getCategory(mCategoryUrl);
			break;
		case R.id.btn_search:
			break;
		case R.id.btn_about:
			break;
		case R.id.btn_recommand:
			break;
		case R.id.btn_feedback:
			break;
		case R.id.btn_recent:
			break;
		default:
			break;
		}
	}
}
