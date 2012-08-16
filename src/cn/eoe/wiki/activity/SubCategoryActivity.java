package cn.eoe.wiki.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.eoe.wiki.R;
import cn.eoe.wiki.json.CategoryChild;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.listener.SubCategoryListener;
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
public class SubCategoryActivity extends CategoryActivity implements OnClickListener,SliderListener{
	public static final		String 	KEY_CATEGORY		= "category";
	public static final		String 	KEY_PARENT_TITLE	= "parent_title";
	
	private LinearLayout	mCategoryLayout;
	private LayoutInflater 	mInflater;
	private ImageButton		mBtnBack;
	private TextView		mTvParentName;
	private TextView		mTvTitleName;
	private TextView		mTvDescription;
	
	private boolean			mProgressVisible;
	private CategoryChild	mParentCategory;
	private String			mParentName;
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
		mParentName = intent.getStringExtra(KEY_PARENT_TITLE);
		if(mParentCategory==null || TextUtils.isEmpty(mParentName))
		{
			throw new NullPointerException("Must give a CategoryChild and the parent name in the intent");
		}
		getmMainActivity().getSliderLayer().addSliderListener(this);
		initComponent();
		initData();
	}

	
	@Override
	protected void onPause() {
		L.d("sub category onPause");
		super.onPause();
	}


	@Override
	protected void onResume() {
		L.d("sub category onResume");
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		L.d("sub category destroy");
		super.onDestroy();
	}

	void initComponent() {
		mTvParentName = (TextView)findViewById(R.id.tv_title_parent);
		mTvTitleName = (TextView)findViewById(R.id.tv_title);
		mTvDescription = (TextView)findViewById(R.id.tv_description);
		mCategoryLayout = (LinearLayout)findViewById(R.id.layout_category);
		mBtnBack=(ImageButton)findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}

	void initData() {
		mTvParentName.setText(mParentName);
		mTvTitleName.setText(mParentCategory.getName());
		mTvDescription.setText(mParentCategory.getDescription());
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
	protected void getCategoriesError(String showText)
	{
		mCategoryLayout.removeAllViews();
		mProgressVisible = false;
		
		View viewError = mInflater.inflate(R.layout.loading_error, null);
		LayoutParams errorParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		errorParams.topMargin = WikiUtil.dip2px(mContext, 10);
		viewError.setLayoutParams(errorParams);
		
		TextView tvErrorTip =  (TextView)viewError.findViewById(R.id.tv_error_tip);
		tvErrorTip.setText(showText);
		Button btnTryAgain =  (Button)viewError.findViewById(R.id.btn_try_again);
		btnTryAgain.setOnClickListener(this);
		mCategoryLayout.addView(viewError);
	}
	@Override
	protected void generateCategories(CategoryJson responseObject)
	{
		mCategoryLayout.removeAllViews();
		mProgressVisible = false;
		List<CategoryChild> categories =  responseObject.getContents();
		if(categories!=null)
		{
			for(CategoryChild category:categories)
			{
				LinearLayout categoryLayout = new LinearLayout(mContext);
				categoryLayout.setOrientation(LinearLayout.VERTICAL);
				LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				int paddind = WikiUtil.dip2px(mContext, 1);
				categoryLayout.setPadding(paddind, paddind, paddind, paddind);
				categoryLayout.setLayoutParams(titleParams);
				categoryLayout.setBackgroundResource(R.drawable.bg_stroke_grey_blue);
				mCategoryLayout.addView(categoryLayout);
				
				TextView tv = (TextView)mInflater.inflate(R.layout.category_title, null);
				tv.setText(category.getName());
				tv.setBackgroundResource(R.drawable.bg_nostroke_grey_blue_top);
				tv.setOnClickListener(new SubCategoryListener(category.getUri(), SubCategoryActivity.this));
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
						tvChild.setOnClickListener(new SubCategoryListener(categorysChild.getUri(), SubCategoryActivity.this));
						if(i==(size-1))
						{
							tvChild.setBackgroundResource(R.drawable.bg_nostroke_white_blue_bottom);
						}
						else
						{
							tvChild.setBackgroundResource(R.drawable.bg_nostroke_white_blue_nocorners);
						}
//						mCategoryLayout.addView(tvChild);
						categoryLayout.addView(tvChild);
					}
				}

				View blankView = new View(mContext);
				LayoutParams blankParams = new LayoutParams(LayoutParams.MATCH_PARENT, WikiUtil.dip2px(mContext, 8));
				blankView.setLayoutParams(blankParams);
				mCategoryLayout.addView(blankView);
			}
		}
		else
		{
			View noCategoryView = mInflater.inflate(R.layout.no_category, null);
			LayoutParams noCategoryParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			noCategoryParams.topMargin = WikiUtil.dip2px(mContext, 10);
			noCategoryView.setLayoutParams(noCategoryParams);
			mCategoryLayout.addView(noCategoryView);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_try_again:
			showProgressLayout();
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
		getmMainActivity().getSliderLayer().removeSliderListener(this);
	}

	@Override
	public void onSidebarClosed() {
		
	}

	@Override
	public boolean onContentTouchedWhenOpening() {
		return false;
	}
}
