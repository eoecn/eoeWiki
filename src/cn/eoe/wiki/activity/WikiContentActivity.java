package cn.eoe.wiki.activity;


import org.codehaus.jackson.type.TypeReference;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.eoe.wiki.R;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.WikiDetailJson;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.view.SliderLayer;
import cn.eoe.wiki.view.SliderLayer.SliderListener;

public class WikiContentActivity extends SliderActivity implements OnClickListener,SliderListener{

	private static final int	HANDLER_DISPLAY_WIKIDETAIL 	= 0x0001;
	private static final int	HANDLER_GET_WIKIDETAIL_ERROR 	= 0x0002;
	private static final String WIKI_URL_PRE = "http://wiki.eoeandroid.com/";
	private boolean mIsFullScreen = false;
	
	private ImageView mLayoutParentDirectory;
	private ImageView mLayoutFullScreen;
	private ImageView mLayoutFavorite;
	private ImageView mLayoutShare;
	
	private RelativeLayout mWikiDetailTitle;
	private LinearLayout mLayoutFunctions;
	
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
		mLayoutParentDirectory = (ImageView)findViewById(R.id.layout_parent_directory);
		mLayoutFullScreen = (ImageView)findViewById(R.id.layout_fullscreen);
		mLayoutFavorite = (ImageView)findViewById(R.id.layout_favorite);
		mLayoutShare = (ImageView)findViewById(R.id.layout_share);
		
		mWikiDetailTitle = (RelativeLayout)findViewById(R.id.wiki_detail_title);
		mLayoutFunctions = (LinearLayout)findViewById(R.id.layout_functions);
		
		mLayoutParentDirectory.setOnClickListener(this);
		mLayoutFullScreen.setOnClickListener(this);
		mLayoutFavorite.setOnClickListener(this);
		mLayoutShare.setOnClickListener(this);
		
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
		mWebView.loadDataWithBaseURL("about:blank", html1,  "text/html","utf-8", null);
        mWebView.setBackgroundColor(R.color.deep_grey);
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
		switch (v.getId()) {
		case R.id.layout_parent_directory:
			fallbackToPreLayer();
			break;
		case R.id.layout_fullscreen:
			fullScreen();
			break;
		case R.id.layout_favorite:
			//do something
			break;
		case R.id.layout_share:
			shareToFriend();
			break;
		default:
			break;
		}
	}

	private void fullScreen(){
		if(!mIsFullScreen){
			mWikiDetailTitle.setVisibility(View.GONE);
			mLayoutFunctions.setVisibility(View.GONE);
			Toast.makeText(WikiContentActivity.this, getString(R.string.screen_back), 1000).show();
			mIsFullScreen = true;
		}else{
			mWikiDetailTitle.setVisibility(View.VISIBLE);
			mLayoutFunctions.setVisibility(View.VISIBLE);
			mIsFullScreen = false;
		}
	}
	
	private void fallbackToPreLayer(){
		SliderLayer layer = getmMainActivity().getSliderLayer();
		layer.closeSidebar(layer.openingLayerIndex());
	}
	
	private void shareToFriend(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		String title = responseObject.getParse().getTitle();
		String titleForUrl = title.replace(" ", "_");
		intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.content_share,new Object[]{title,WIKI_URL_PRE+titleForUrl}));
		Intent it = Intent.createChooser(intent, "分享给好友");
		startActivity(it);
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
