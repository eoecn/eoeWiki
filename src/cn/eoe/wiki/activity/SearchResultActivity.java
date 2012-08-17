package cn.eoe.wiki.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
		OnClickListener,SliderListener {
	public static final String KEY_SEARCHRESULT = "SearchResultActivity";

	private LinearLayout mSearchResultLayout;
	// private MyListView listView;
	private HashMap<String, Object> maps;
	private LayoutInflater mInflater;
	private ImageButton mBtnBack;

//	private LinkedList<HashMap<String, String>> mListItems;
	private List<HashMap<String, Object>> lists;
	private int lastItem;
	private MyListAdapter adapter;
	// private OnRefreshListener mOnRefreshListener;

	// private boolean mProgressVisible;
	// private String url =
	// "http://wiki.eoeandroid.com/api.php?action=query&list=search&format=json&srlimit=50&sroffset=1&srsearch=";
	// private String url =
	// "http://wiki.eoeandroid.com/api.php?action=query&list=search&format=json&srwhat=text&srsearch=";

	private String url = "http://wiki.eoeandroid.com/api.php?action=query&list=search&srwhat=text&format=json&sroffset=2&srlimit=10&srsearch=";
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
		// listView = (MyListView) findViewById(R.id.listView);
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}

	void initData() {
		showProgressLayout();
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

		SearchJudgeJson searchResultJudge = responseObject.getQuery_continue();
		if (null != searchResultJudge) {
			SearchJudgeChild search = searchResultJudge.getSearch();
			int sroffset = search.getSroffset();
			System.out.println(sroffset);
		}

		SearchResultJson searchResultJson = responseObject.getQuery();
		List<SearchResultChild> results = searchResultJson.getSearch();
		if (results != null) {
			if (0 != results.size()) {
				mSearchResultLayout.removeAllViews();

				// Button button = new Button(mContext);
				// button.setText("tyut");
				// mSearchResultLayout.addView(button);
				// mProgressVisible = false;
				 lists = new ArrayList<HashMap<String, Object>>();
				// MyListView myListView = new MyListView(mContext);
				ListView myListView = new ListView(mContext);

				for (SearchResultChild result : results) {
					maps = new HashMap<String, Object>();
					maps.put("title", result.getTitle());
					maps.put("snippet", result.getSnippet());
					lists.add(maps);
					// TextView tv = new TextView(mContext);
					// tv.setText(result.getTitle());
					// tv.setTextColor(R.color.green);
					// mSearchResultLayout.addView(tv);
					//
					// TextView tvChild = new TextView(mContext);
					// tvChild.setText(result.getSnippet());
					// tvChild.setPadding(50, 20, 0, 0);
					// L.d("tvChild font:" + tvChild.getTextSize());
					// tvChild.setTextSize(tvChild.getTextSize() * 2 / 5);
					// tvChild.setTextColor(WikiUtil.getResourceColor(R.color.black,
					// mContext));
					// // tvChild.setOnClickListener();
					// mSearchResultLayout.addView(tvChild);

				}
				adapter = new MyListAdapter(mContext, lists);
				myListView.setAdapter(adapter);
				mSearchResultLayout.addView(myListView);
				myListView.setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						if (lastItem == adapter.getCount() - 1
								&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
							addMoreData();
						}
					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						Log.i(SearchResultActivity.class.getCanonicalName(),
								firstVisibleItem + "firstVisibleItem");
						Log.i(SearchResultActivity.class.getCanonicalName(),
								visibleItemCount + "visibleItemCount");

						lastItem = firstVisibleItem - 2 + visibleItemCount;
					}
				});

			} else {
				getSearchResultNull();
			}
		}
	}

	private void addMoreData() {
		showDialog(0);
		new Thread() {
			public void run() {
				for (int i = 0; i < 3; i++) {
					/************测试数据********************/
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("title", "中国城市智能手机普及率35%");
					map.put("snippet", "Google今日发布的报告显示");
					lists.add(map);
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {
					public void run() {
						removeDialog(0);
						adapter.notifyDataSetChanged();
					}
				});
			};
		}.start();
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
	@Override
	public void onSidebarOpened() {
		//TODO get the result
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

			// if (position == getCount() - 1) {
			// View view = LayoutInflater.from(getApplicationContext())
			// .inflate(R.layout.listfooter_more, null);
			// return view;
			// }
			ViewHolder holder;
			if (convertView != null
					&& convertView.getId() == R.id.newitem_layout) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.newsitem, null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.news_title);
				holder.tvContent = (TextView) convertView
						.findViewById(R.id.news_from);

				convertView.setTag(holder);
			}
			holder.tvTitle.setText(data.get(position).get("title").toString());
			holder.tvTitle.setPadding(15, 0, 10, 0);
			String title = data.get(position).get("title").toString();
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
			holder.tvTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							WikiContentActivity.class);
					intent.putExtra(WikiContentActivity.WIKI_CONTENT,
							url_toContent);
					getmMainActivity().showView(2, intent);
				}
			});

			String content = data.get(position).get("snippet").toString();
			content = content.replace("<span class='searchmatch'>", "");
			content = content.replace("</span>", "");
			holder.tvContent.setText(content);
			holder.tvContent.setPadding(30, 0, 10, 0);
			return convertView;
		}
	}

	public static class ViewHolder {
		TextView tvTitle;
		TextView tvContent;
	}

}
