package cn.eoe.wiki.activity;

import org.codehaus.jackson.type.TypeReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import cn.eoe.wiki.R;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.SearchJson;
import cn.eoe.wiki.utils.L;

/**
 * search-related
 * 
 * @author tyutNo4
 * @data 2012-8-11
 * @version 1.0.0
 */
public abstract class SearchActivity extends SliderActivity {

	private static final int HANDLER_DISPLAY_SEARCH = 0x0001;
	private static final int HANDLER_GET_SEARCH_ERROR = 0x0002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	void getSearchResult(String url) {
		if (TextUtils.isEmpty(url))
			throw new IllegalArgumentException("You must give a not empty url.");

		HttpManager manager = new HttpManager(url, null, HttpManager.GET,
				getSearchResultTransaction);
		manager.start();
	}

	abstract void getSearchResultError(String showText);

	abstract void generateSearchResult(SearchJson responseObject);

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DISPLAY_SEARCH:
				generateSearchResult((SearchJson) msg.obj);
				break;
			case HANDLER_GET_SEARCH_ERROR:
				getSearchResultError(getString(R.string.tip_get_search_error));
				break;
			default:
				break;
			}
		}

	};

	public ITransaction getSearchResultTransaction = new ITransaction() {

		@Override
		public void transactionOver(String result) {
			SearchJson responseObject;
			try {
				responseObject = mObjectMapper.readValue(result,
						new TypeReference<SearchJson>() {
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
