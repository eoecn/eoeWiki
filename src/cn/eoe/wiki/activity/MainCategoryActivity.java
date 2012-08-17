package cn.eoe.wiki.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.eoe.wiki.R;
import cn.eoe.wiki.WikiConfig;
import cn.eoe.wiki.json.CategoryChild;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.listener.CategoryListener;
import cn.eoe.wiki.listener.CategoryTitleListener;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.AboutDialog;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
/**
 * 用来处理最外层分类的界面
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class MainCategoryActivity extends CategoryActivity implements OnClickListener{
	
	private LinearLayout	mCategoryLayout;
	private LayoutInflater 	mInflater;
	private Button			mBtnSearch;
	private EditText        mEditText;

	private LinearLayout			mLayoutAbout;
	private LinearLayout			mLayoutRecommand;
	private LinearLayout			mLayoutFeedback;
	private Button					mBtnRecent;
	private ImageView				mIvFavorite;
	private AboutDialog 			aboutDialog;
	private String					mCategoryUrl;

	private boolean					mProgressVisible;
	private Set<CategoryChild> 		mCloseCategories;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories);
		mInflater = LayoutInflater.from(mContext);
		mCategoryUrl = WikiConfig.getMainCategoruUrl();
		mCloseCategories = new HashSet<CategoryChild>();
		initComponent();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	void initComponent() {
		ImageView ivCategoryLog = (ImageView)findViewById(R.id.category_logo);
		ivCategoryLog.requestFocus();
		mCategoryLayout = (LinearLayout)findViewById(R.id.layout_category);
		mBtnSearch=(Button)findViewById(R.id.btn_search);
		mEditText = (EditText) findViewById(R.id.et_search);
		
		aboutDialog = new AboutDialog(this);
		mLayoutAbout=(LinearLayout)findViewById(R.id.layout_about);
		mLayoutRecommand=(LinearLayout)findViewById(R.id.layout_recommand);
		mLayoutFeedback=(LinearLayout)findViewById(R.id.layout_feedback);
		mBtnRecent=(Button)findViewById(R.id.btn_recent);
		mIvFavorite=(ImageView)findViewById(R.id.iv_favorite);
		mBtnSearch.setOnClickListener(this);
		mLayoutAbout.setOnClickListener(this);
		mLayoutRecommand.setOnClickListener(this);
		mLayoutFeedback.setOnClickListener(this);
		mBtnRecent.setOnClickListener(this);
		mIvFavorite.setOnClickListener(this);
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
		generateCategorys(responseObject, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_try_again:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_try_again");
			showProgressLayout();
			getCategory(mCategoryUrl);
			break;
		case R.id.btn_search:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_search");
			String searchText = mEditText.getText().toString();
			if (TextUtils.isEmpty(searchText)) {
				Toast.makeText(mContext, "请输入检索关键字", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent_toSearchResult = new Intent(mContext, SearchResultActivity.class);
				intent_toSearchResult.putExtra(SearchResultActivity.KEY_SEARCH_TEXT, searchText);
				getmMainActivity().showView(1, intent_toSearchResult);
				
			}
			break;
		case R.id.layout_about:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_about");
			aboutDialog.show();
			break;
		case R.id.layout_recommand:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_recommand");
			recommandToFriend();
			break;
		case R.id.layout_feedback:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_feedback");
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;
		case R.id.btn_recent:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_recent");
			break;
		case R.id.iv_favorite:
			//umeng event
			MobclickAgent.onEvent(this, "home", "btn_favorite");
			Intent intent = new Intent (mContext,FavoriteActivity.class);
			getmMainActivity().showView(1, intent);
			break;
		default:
			break;
		}
	}
	
	public void recommandToFriend(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
			getResources().getString(R.string.content_recommand));
	
		Intent itn = Intent.createChooser(intent, getResources().getString(R.string.recomment_chooser_title));
		startActivity(itn);
	}

	public void generateCategorys(CategoryJson responseObject,CategoryChild operCategory)
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
				tv.setOnClickListener(new CategoryTitleListener(this,category));
				categoryLayout.addView(tv);
				if(operCategory==null || !mCloseCategories.contains(category))
				{
					//if operCategory==null ,it means the first
					//mCloseCategorys.contains(category) this category is close
					tv.setBackgroundResource(R.drawable.bg_nostroke_grey_blue_top);
				
					List<CategoryChild> categorysChildren =  category.getChildren();
					if(categorysChildren!=null)
					{
	//					for(CategoryChild categorysChild:categorysChildren)
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
							tvChild.setOnClickListener(new CategoryListener(this, categorysChild,category.getName()));
							if(i==(size-1))
							{
								tvChild.setBackgroundResource(R.drawable.bg_nostroke_white_blue_bottom);
							}
							else
							{
								tvChild.setBackgroundResource(R.drawable.bg_nostroke_white_blue_nocorners);
							}
							categoryLayout.addView(tvChild);
						}
					}
				}
				else
				{
					tv.setBackgroundResource(R.drawable.bg_nostroke_grey_blue);
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
	

	public void refreshCategory(CategoryChild category)
	{
		if(mCloseCategories.contains(category))
		{
			mCloseCategories.remove(category);
		}
		else
		{
			mCloseCategories.add(category);
		}
		generateCategorys(mResponseObject, category);
	}
}
