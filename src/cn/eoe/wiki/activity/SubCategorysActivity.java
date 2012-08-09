package cn.eoe.wiki.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import cn.eoe.wiki.R;
import cn.eoe.wiki.json.CategoryChild;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.SliderLayer;
import cn.eoe.wiki.view.SliderLayer.SliderListener;
/**
 * 用来处理第二层分类的界面
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class SubCategorysActivity extends CategorysActivity implements OnClickListener,SliderListener{
	public static final		String 	KEY_CATEGORY		= "category";
	
	private LinearLayout	mCategoryLayout;
	private LayoutInflater 	mInflater;
	private Button			mBtnBack;
	
	private boolean			mProgressVisible;
	private CategoryChild	mParentCategory;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_categorys);
		mInflater = LayoutInflater.from(mContext);
		Intent intent  = getIntent();
		if(intent==null)
		{
			throw new NullPointerException("Must give a CategoryChild in the intent");
		}
		mParentCategory = intent.getParcelableExtra(KEY_CATEGORY);
		if(mParentCategory==null)
		{
			throw new NullPointerException("Must give a CategoryChild in the intent");
		}
		getmMainActivity().getSliderLayer().addSliderListener(this);
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
		mBtnBack=(Button)findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}

	void initData() {
		showProgressLayout();
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
//		if(WikiConfig.isDebug()) return;
		List<CategoryChild> categorys =  responseObject.getContents();
		if(categorys!=null)
		{
			mCategoryLayout.removeAllViews();
			mProgressVisible = false;
			
			for(CategoryChild category:categorys)
			{
				LinearLayout categoryLayout = new LinearLayout(mContext);
				categoryLayout.setOrientation(LinearLayout.VERTICAL);
				LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				int paddind = WikiUtil.dip2px(mContext, 1);
				categoryLayout.setPadding(paddind, paddind, paddind, paddind);
				categoryLayout.setLayoutParams(titleParams);
				categoryLayout.setBackgroundResource(R.drawable.btn_grey_blue_stroke);
				mCategoryLayout.addView(categoryLayout);
				
				TextView tv = (TextView)mInflater.inflate(R.layout.category_title, null);
				tv.setText(category.getName());
				tv.setBackgroundResource(R.drawable.btn_grey_blue_nostroke_top);
				
				categoryLayout.addView(tv);
				List<CategoryChild> categorysChildren =  category.getChildren();
				if(categorysChildren!=null)
				{
					int size = categorysChildren.size();
					for (int i = 0; i < size; i++)
					{
						//add the line first
						View lineView = new View(mContext);
						LayoutParams blankParams = new LayoutParams(LayoutParams.MATCH_PARENT, WikiUtil.dip2px(mContext, 1));
						lineView.setLayoutParams(blankParams);
						lineView.setBackgroundResource(R.color.grey_stroke);
						categoryLayout.addView(lineView);
						//add the text
						CategoryChild categorysChild = categorysChildren.get(i);
						
						TextView tvChild = (TextView)mInflater.inflate(R.layout.category_item, null);
						tvChild.setText(categorysChild.getName());
						tvChild.setPadding(50, 0, 0, 0);
						tvChild.setTextColor(WikiUtil.getResourceColor(R.color.black, mContext));
//						tvChild.setOnClickListener();
						if(i==(size-1))
						{
							tvChild.setBackgroundResource(R.drawable.btn_white_blue_nostroke_bottom);
						}
						else
						{
							tvChild.setBackgroundResource(R.drawable.btn_white_blue_nostroke_nocorners);
						}
						categoryLayout.addView(tvChild);
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_try_again:
			getCategory(mParentCategory.getUri());
			break;
		case R.id.btn_back:
			SliderLayer layer = getmMainActivity().getSliderLayer();
			layer.closeSidebar(layer.openingLayerIndex());
			break;
		default:
			break;
		}
	}

	@Override
	public void onSidebarOpened() {
		if(!mProgressVisible)
		{
			showProgressLayout();
		}
		getCategory(mParentCategory.getUri());
	}

	@Override
	public void onSidebarClosed() {
		
	}

	@Override
	public boolean onContentTouchedWhenOpening() {
		return false;
	}
}
