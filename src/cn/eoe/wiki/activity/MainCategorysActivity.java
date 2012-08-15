package cn.eoe.wiki.activity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.eoe.wiki.R;
import cn.eoe.wiki.WikiConfig;
import cn.eoe.wiki.json.CategoryChild;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.listener.CategoryListener;
import cn.eoe.wiki.listener.CategoryTitleListener;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.AboutDialog;

import com.umeng.fb.UMFeedbackService;
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

	private LinearLayout			mLayoutAbout;
	private LinearLayout			mLayoutRecommand;
	private LinearLayout			mLayoutFeedback;
	private Button					mBtnRecent;
	private ImageView				mIvFavorite;
	private AboutDialog 			aboutDialog;
	private String					mCategoryUrl;

	private boolean					mProgressVisible;
	private Set<CategoryChild> 		mCloseCategorys;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorys);
		mInflater = LayoutInflater.from(mContext);
		mCategoryUrl = WikiConfig.getMainCategoruUrl();
		mCloseCategorys = new HashSet<CategoryChild>();
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
	protected void getCategorysError(String showText)
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
	protected void generateCategorys(CategoryJson responseObject)
	{
		generateCategorys(responseObject, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_try_again:
			showProgressLayout();
			getCategory(mCategoryUrl);
			break;
		case R.id.btn_search:
			break;
		case R.id.layout_about:
			aboutDialog.show();
			break;
		case R.id.layout_recommand:
			recommandToFriend();
			break;
		case R.id.layout_feedback:
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;
		case R.id.btn_recent:
			break;
		case R.id.iv_favorite:
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
		List<CategoryChild> categorys =  responseObject.getContents();
		if(categorys!=null)
		{
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
				tv.setOnClickListener(new CategoryTitleListener(this,category));
				categoryLayout.addView(tv);
				if(operCategory==null || !mCloseCategorys.contains(category))
				{
					//if operCategory==null ,it means the first
					//mCloseCategorys.contains(category) this category is close
					tv.setBackgroundResource(R.drawable.btn_grey_blue_nostroke_top);
				
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
				else
				{
					tv.setBackgroundResource(R.drawable.btn_grey_blue_nostroke);
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
		if(mCloseCategorys.contains(category))
		{
			mCloseCategorys.remove(category);
		}
		else
		{
			mCloseCategorys.add(category);
		}
	}
}
