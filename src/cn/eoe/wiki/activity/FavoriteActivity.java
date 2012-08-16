package cn.eoe.wiki.activity;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.eoe.wiki.R;
import cn.eoe.wiki.activity.adapter.FavoriteAdapter;
import cn.eoe.wiki.db.dao.FavoriteDao;
import cn.eoe.wiki.db.entity.FavoriteEntity;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.view.SliderLayer;
import cn.eoe.wiki.view.SliderLayer.SliderListener;

/**
 * displya the favorite page
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @date Aug 15, 2012
 * @version 1.0.0
 *
 */
public class FavoriteActivity extends SliderActivity implements OnClickListener,SliderListener{
	public static final 	int		PAGE_COUNT = 20;
	
	private ListView		mListView;
	private FavoriteAdapter	mAdapter;
	
	private LinearLayout	mLayoutLoading;
	private View			mNoFavorite;
	private ImageButton		mBtnBack;
	private TextView		mTvTitle;
	
	private FavoriteDao		mFavoriteDao;
	
	private int 			currentPage;
	private List<FavoriteEntity> mFavorites;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);
		mFavoriteDao = new FavoriteDao(mContext);
		getmMainActivity().getSliderLayer().addSliderListener(this);
		initComponent();
		initData();
	}

	void initComponent() {
		mTvTitle = (TextView)findViewById(R.id.tv_title_parent);
		mListView = (ListView)findViewById(R.id.ListView);
		mListView.setDividerHeight(0);
		mLayoutLoading = (LinearLayout)findViewById(R.id.layout_loading);
		mNoFavorite = findViewById(R.id.layout_no_favorite);
		mBtnBack=(ImageButton)findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}

	void initData() {
		mTvTitle.setText(R.string.title_favorite);
		mLayoutLoading.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
		mNoFavorite.setVisibility(View.GONE);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			SliderLayer layer = getmMainActivity().getSliderLayer();
			layer.closeSidebar(layer.openingLayerIndex());
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSidebarOpened() {
		L.e("favorite onSidebarOpened");
		new LoadFavoriteFromDb().execute();
		getmMainActivity().getSliderLayer().removeSliderListener(this);
	}

	@Override
	public void onSidebarClosed() {
		
	}

	@Override
	public boolean onContentTouchedWhenOpening() {
		return false;
	}
	class LoadFavoriteFromDb extends AsyncTask<String, Integer, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... params) {
			L.d("favorite doInBackground");
			if(currentPage==0)
			{
				currentPage = 1;
			}
			mFavorites = mFavoriteDao.getFavorites(currentPage, PAGE_COUNT);
			if(mFavorites==null || mFavorites.size()==0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			L.d("favorite onPostExecute:"+result.booleanValue());
			mLayoutLoading.setVisibility(View.GONE);
			if(result.booleanValue())
			{
				mNoFavorite.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				if(mAdapter==null)
				{
					mAdapter = new FavoriteAdapter(mContext, mFavorites);
					mListView.setAdapter(mAdapter);
				}
				else
				{
					mAdapter.setFavorites(mFavorites);
				}
			}
			else
			{
				mListView.setVisibility(View.GONE);
				mNoFavorite.setVisibility(View.VISIBLE);
			}
		}
		
	}
}
