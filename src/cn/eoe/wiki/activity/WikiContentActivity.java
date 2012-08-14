package cn.eoe.wiki.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.codehaus.jackson.type.TypeReference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import cn.eoe.wiki.R;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.CategoryJson;
import cn.eoe.wiki.json.WikiDetailJson;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.view.SliderLayer.SliderListener;

public class WikiContentActivity extends SliderActivity implements OnClickListener,SliderListener{

	private static final int	HANDLER_DISPLAY_WIKIDETAIL 	= 0x0001;
	private static final int	HANDLER_GET_WIKIDETAIL_ERROR 	= 0x0002;
	
	public static final String WIKI_CONTENT = "wiki_content";
	private String mUri;
	
	protected WikiDetailJson responseObject = null;
	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wiki_detail);
		Intent intent = getIntent();
		if(intent == null){
			throw new NullPointerException("Must give a Wiki Uri in the intent");
		}
		mUri = intent.getStringExtra(WIKI_CONTENT);
		if(mUri == null){
			throw new NullPointerException("Must give a Wiki Uri in the intent");
		}
		getmMainActivity().getSliderLayer().addSliderListener(this);
		initComponent();
		initData();
	}

	void initComponent() {
		mWebView = (WebView)findViewById(R.id.wiki_detail_content);
	}
	
	void initData(){
		getWikiDetail();
	}
	
	void getWikiDetail()
	{
		HttpManager manager = new HttpManager(mUri,null, HttpManager.GET, getWikiDetailTransaction);
		manager.start();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DISPLAY_WIKIDETAIL:
				generateWiki((WikiDetailJson)msg.obj);
				break;
			case HANDLER_GET_WIKIDETAIL_ERROR:
				getWikiError(getString(R.string.tip_get_category_error));
				break;
			default:
				break;
			}
		}
		
	};
	
	private void generateWiki(WikiDetailJson pWikiDetailJson){
		String html = pWikiDetailJson.getParse().getText().getHtml();
		String html1 = "<!DOCTYPE html PUBLIC "
                  + "-//W3C//DTD XHTML 1.0 Transitional//EN"
                  + "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                  + ">" + "<html xmlns=" + "http://www.w3.org/1999/xhtml" + ">"
                  + "<head>" + "<meta http-equiv=" + "Content-Type" + " content="
                  + "text/html; charset=utf-8" + "/>" + "<body>"
                  + html + "</body></html>";
        mWebView.loadData(html1, "text/html","utf-8");
	}
	
	private void getWikiError(String pError){
		
	}
	
	public ITransaction getWikiDetailTransaction = new ITransaction() {
		
		@Override
		public void transactionOver(String result) {
			try {
				responseObject = mObjectMapper.readValue(result, new TypeReference<WikiDetailJson>() { });
				mHandler.obtainMessage(HANDLER_DISPLAY_WIKIDETAIL, responseObject).sendToTarget();
			} catch (Exception e) {
				L.e("getWikiDetailTransaction exception", e);
				mHandler.obtainMessage(HANDLER_GET_WIKIDETAIL_ERROR).sendToTarget();
			}
		}
		
		@Override
		public void transactionException(int erroCode,String result, Exception e) {
			mHandler.obtainMessage(HANDLER_GET_WIKIDETAIL_ERROR).sendToTarget();
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSidebarOpened() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSidebarClosed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onContentTouchedWhenOpening() {
		// TODO Auto-generated method stub
		return false;
	}


}
