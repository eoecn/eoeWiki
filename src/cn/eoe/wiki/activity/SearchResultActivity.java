package cn.eoe.wiki.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.eoe.wiki.R;
import cn.eoe.wiki.db.entity.ParamsEntity;
import cn.eoe.wiki.json.SearchJson;
import cn.eoe.wiki.json.SearchJudgeChild;
import cn.eoe.wiki.json.SearchJudgeJson;
import cn.eoe.wiki.json.SearchResultChild;
import cn.eoe.wiki.json.SearchResultJson;
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
public class SearchResultActivity extends SearchActivity implements
		OnClickListener, SliderListener, OnScrollListener {
	public static final String KEY_SEARCHRESULT = "SearchResultActivity";
	private static final int HANDLER_LOADING_MORE = 0x0002;

	private LinearLayout mSearchResultLayout;
	// private MyListView listView;
	private HashMap<String, Object> maps;
	private LayoutInflater mInflater;
	private ImageButton mBtnBack;
	private TextView mTvTitle;

	private List<HashMap<String, Object>> lists;
	// private int lastItem;
	private MyListAdapter adapter;
	private int count_load;
	private SearchJudgeJson searchResultJudge;
	private boolean flg;
	// private OnRefreshListener mOnRefreshListener;

	// private boolean mProgressVisible;
	// private String url =
	// "http://wiki.eoeandroid.com/api.php?action=query&list=search&format=json&srlimit=50&sroffset=1&srsearch=";
	// private String url =
	// "http://wiki.eoeandroid.com/api.php?action=query&list=search&format=json&srwhat=text&srsearch=";

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
		content_search = intent.getStringExtra("et_search");
		url += content_search;
		/* 监听滑块滑动的动作 */
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
		mSearchResultLayout = (LinearLayout) findViewById(R.id.layout_search_result);
		mTvTitle = (TextView) findViewById(R.id.tv_title_parent);
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}

	/**
	 * 初始化loading
	 */
	void initData() {
		flg = true;
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

	@Override
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

	@Override
	protected void generateSearchResult(SearchJson responseObject) {
		// if(WikiConfig.isDebug()) return;

		searchResultJudge = responseObject.getQuery_continue();

		SearchResultJson searchResultJson = responseObject.getQuery();
		List<SearchResultChild> results = searchResultJson.getSearch();
		if (results != null) {
			if (0 != results.size()) {

				lists = new ArrayList<HashMap<String, Object>>();
				ListView myListView = (ListView) findViewById(R.id.ListView);

				for (SearchResultChild result : results) {
					maps = new HashMap<String, Object>();
					maps.put("title", result.getTitle());
					maps.put("snippet", result.getSnippet());
					lists.add(maps);

				}
				adapter = new MyListAdapter(SearchResultActivity.this, lists);
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
		// TODO get the result
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

	public class MyListAdapter extends BaseAdapter {

		private Context mContext;
		private List<HashMap<String, Object>> data;

		public MyListAdapter(Context mContext,
				List<HashMap<String, Object>> data) {
			super();
			this.mContext = mContext;
			this.data = data;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = mInflater.inflate(R.layout.newsitem, null);
			TextView tvTitle = (TextView) convertView
					.findViewById(R.id.news_title);
			TextView tvContent = (TextView) convertView
					.findViewById(R.id.news_from);

			tvTitle.setText(data.get(position).get("title").toString());
			tvTitle.setPadding(15, 0, 10, 0);
			final String title = data.get(position).get("title").toString();
			String[] title_array = title.split(" ");
			StringBuffer title_search = new StringBuffer();
			for (int i = 0; i < title_array.length; i++) {
				if (i == title_array.length - 1) {
					title_search = title_search.append(title_array[i]);
				} else {
					title_search = title_search.append(title_array[i]).append(
							"_");
				}
			}

			final String url_toContent = "http://wiki.eoeandroid.com/api.php?action=parse&format=json&page="
					+ title_search;
			tvTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							WikiContentActivity.class);
					ParamsEntity pe = new ParamsEntity();
					pe.setFirstTitle("搜索");
					pe.setSecondTitle(title);
					pe.setUri(url_toContent);
					intent.putExtra(WikiContentActivity.WIKI_CONTENT, pe);
					getmMainActivity().showView(2, intent);
				}
			});

			String content = data.get(position).get("snippet").toString();
			content = content.replace("<span class='searchmatch'>", "");
			content = content.replace("</span>", "");
			tvContent.setText(content);
			tvContent.setPadding(30, 0, 10, 0);
			return convertView;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
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

			case HANDLER_LOADING_MORE:
				String url_load = "http://wiki.eoeandroid.com/api.php?action=query&list=search&srwhat=text&format=json&srlimit=10&srsearch="
						+ content_search + "&sroffset=";
				// mLayoutPrgogress.setVisibility(View.VISIBLE);
				// new LoadFavoriteFromDb().execute(currentPage+1);

				if (null != searchResultJudge) {
					SearchJudgeChild search = searchResultJudge.getSearch();
					sroffset = search.getSroffset();
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

}
