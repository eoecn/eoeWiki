package cn.eoe.wiki.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.eoe.wiki.R;
import cn.eoe.wiki.activity.adapter.SearchResultAdapter;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.SearchItemJson;
import cn.eoe.wiki.json.SearchJson;
import cn.eoe.wiki.json.SearchOffsetJson;
import cn.eoe.wiki.json.SearchQueryContinusJson;
import cn.eoe.wiki.json.SearchResultJson;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.SliderLayer;
import cn.eoe.wiki.view.SliderLayer.SliderListener;

/**
 * 检索结果展示
 * 
 * @author tyutNo4
 * @data 2012-8-11
 * @version 1.0.0
 */
public class SearchResultActivity extends SliderActivity implements OnClickListener, SliderListener, OnScrollListener {
	public static final String KEY_SEARCH_TEXT 			= "search_text";
	private static final int HANDLER_DISPLAY_SEARCH 	= 0x0001;
	private static final int HANDLER_GET_SEARCH_ERROR 	= 0x0002;
	private static final int HANDLER_LOADING_MORE 		= 0x0003;

	private LinearLayout 			mSearchResultLayout;
	// private MyListView listView;
	private HashMap<String, Object> maps;
	private LayoutInflater 			mInflater;
	private ImageButton 			mBtnBack;
	private TextView 				mTvTitle;

	private List<HashMap<String, Object>> lists;
	// private int lastItem;
	private SearchResultAdapter 	adapter;
	private int 					count_load;
	private SearchQueryContinusJson searchResultJudge;

	private String url = "http://wiki.eoeandroid.com/api.php?action=query&list=search&srwhat=text&format=json&sroffset=0&srlimit=10&srsearch=";
	private String content_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		mInflater = LayoutInflater.from(mContext);
		Intent intent = getIntent();
		if (intent == null) {
			throw new NullPointerException("Must give a keyword in the intent");
		}
		content_search = intent.getStringExtra(KEY_SEARCH_TEXT);
		url += content_search;
		/* 监听滑块滑动的动作 */
		getmMainActivity().getSliderLayer().addSliderListener(this);
		initComponent();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	void initComponent() {
		mSearchResultLayout = (LinearLayout) findViewById(R.id.layout_search_result);
		mTvTitle = (TextView) findViewById(R.id.tv_title_parent);
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}

	/**
	 * 初始化loading
	 */
	void initData() {
		count_load = 0;
		mTvTitle.setText(R.string.button_search);
		// showProgressLayout();
	}

	protected void showProgressLayout() {
		View progressView = mInflater.inflate(R.layout.loading, null);
		mSearchResultLayout.removeAllViews();
		mSearchResultLayout.addView(progressView);
		// mProgressVisible = true;
	}
	void getSearchResult(String url) {
		if (TextUtils.isEmpty(url))
			throw new IllegalArgumentException("You must give a not empty url.");

		HttpManager manager = new HttpManager(url, null, HttpManager.GET,
				getSearchResultTransaction);
		manager.start();
	}
	protected void getSearchResultError(String showText) {
		mSearchResultLayout.removeAllViews();
		// mProgressVisible = false;

		View viewError = mInflater.inflate(R.layout.loading_error, null);
		TextView tvErrorTip = (TextView) viewError
				.findViewById(R.id.tv_error_tip);
		tvErrorTip.setText(showText);
		tvErrorTip.setTextColor(WikiUtil
				.getResourceColor(R.color.red, mContext));

		Button btnTryAgain = (Button) viewError
				.findViewById(R.id.btn_try_again);
		btnTryAgain.setOnClickListener(this);
		mSearchResultLayout.addView(viewError);
	}

	protected void generateSearchResult(SearchResultJson responseObject) {
		// if(WikiConfig.isDebug()) return;

		searchResultJudge = responseObject.getQueryContinue();

		SearchJson searchResultJson = responseObject.getQuery();
		List<SearchItemJson> results = searchResultJson.getSearch();
		if (results != null) {
			if (0 != results.size()) {

				lists = new ArrayList<HashMap<String, Object>>();
				ListView myListView = (ListView) findViewById(R.id.ListView);

				for (SearchItemJson result : results) {
					maps = new HashMap<String, Object>();
					maps.put("title", result.getTitle());
					maps.put("snippet", result.getSnippet());
					lists.add(maps);

				}
				adapter = new SearchResultAdapter(SearchResultActivity.this, lists);
				myListView.setAdapter(adapter);
				myListView.setOnScrollListener(this);

			} else {
				getSearchResultNull();
			}
		}
	}

	protected void getSearchResultNull() {
		mSearchResultLayout.removeAllViews();

		View viewNull = mInflater.inflate(R.layout.loading_null, null);
		TextView tvNullTip = (TextView) viewNull.findViewById(R.id.tv_null_tip);
		tvNullTip.setText(R.string.tip_get_search_null);
		tvNullTip
				.setTextColor(WikiUtil.getResourceColor(R.color.red, mContext));
		tvNullTip.setPadding(0, 20, 0, 0);

		Button btnToBack = (Button) viewNull.findViewById(R.id.btn_toBack);
		btnToBack.setOnClickListener(this);
		mSearchResultLayout.addView(viewNull);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_try_again:
			getSearchResult(url);
			break;
		case R.id.btn_back:
			SliderLayer layer = getmMainActivity().getSliderLayer();
			layer.closeSidebar(layer.openingLayerIndex());
			// SearchResultActivity.this.finish();
			break;
		case R.id.btn_toBack:
			SliderLayer layer1 = getmMainActivity().getSliderLayer();
			layer1.closeSidebar(layer1.openingLayerIndex());
			break;
		default:
			break;
		}
	}

	/**
	 * 滑动全部展开后会调用 在这里开始去加载数据
	 * 
	 */
	@Override
	public void onSidebarOpened() {
		WikiUtil.hideSoftInput(mBtnBack);
		getSearchResult(url);
		getmMainActivity().getSliderLayer().removeSliderListener(this);
	}

	@Override
	public void onSidebarClosed() {

	}

	@Override
	public boolean onContentTouchedWhenOpening() {
		return false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_FLING
				|| scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1) {
				count_load++;
				mHandler.sendEmptyMessage(HANDLER_LOADING_MORE);
			}
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			int sroffset = 0;
			switch (msg.what) {

			case HANDLER_DISPLAY_SEARCH:
				generateSearchResult((SearchResultJson) msg.obj);
				break;
			case HANDLER_GET_SEARCH_ERROR:
				getSearchResultError(getString(R.string.tip_get_search_error));
				break;
			case HANDLER_LOADING_MORE:
				String url_load = "http://wiki.eoeandroid.com/api.php?action=query&list=search&srwhat=text&format=json&srlimit=10&srsearch="
						+ content_search + "&sroffset=";
				// mLayoutPrgogress.setVisibility(View.VISIBLE);
				// new LoadFavoriteFromDb().execute(currentPage+1);

				if (null != searchResultJudge) {
					SearchOffsetJson searchOffset = searchResultJudge.getSearch();
					sroffset = searchOffset.getSroffset();
					System.out.println(sroffset);
					if (sroffset < 10) {
						url_load += String.valueOf(sroffset);
					} else {
						url_load += String.valueOf(10 * count_load);
					}

					getSearchResult(url_load);
				}

				break;

			default:
				break;
			}
		}

	};

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
	public ITransaction getSearchResultTransaction = new ITransaction() {

		@Override
		public void transactionOver(String result) {
			SearchResultJson responseObject;
			try {
				responseObject = mObjectMapper.readValue(result,
						new TypeReference<SearchResultJson>() {
						});
				mHandler.obtainMessage(HANDLER_DISPLAY_SEARCH, responseObject)
						.sendToTarget();
			} catch (Exception e) {
				L.e("getGiftsTransaction exception", e);
				mHandler.obtainMessage(HANDLER_GET_SEARCH_ERROR).sendToTarget();
			}
		}

		@Override
		public void transactionException(int erroCode, String result,
				Exception e) {
			mHandler.obtainMessage(HANDLER_GET_SEARCH_ERROR).sendToTarget();
		}
	};
}
