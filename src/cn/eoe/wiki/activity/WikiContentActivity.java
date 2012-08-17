package cn.eoe.wiki.activity;


import org.codehaus.jackson.type.TypeReference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import cn.eoe.wiki.R;
import cn.eoe.wiki.db.dao.FavoriteDao;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.WikiDetailErrorJson;
import cn.eoe.wiki.json.WikiDetailJson;
import cn.eoe.wiki.json.WikiDetailParent;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.SliderLayer;
import cn.eoe.wiki.view.SliderLayer.SliderListener;

public class WikiContentActivity extends SliderActivity implements OnClickListener,SliderListener{

	private static final int		HANDLER_DISPLAY_WIKIDETAIL 	= 0x0001;
	private static final int		HANDLER_GET_WIKIERROR 	= 0x0002;
	private static final int		HANDLER_GET_WIKIDETAIL_ERROR 	= 0x0003;
	private static final String 	WIKI_URL_PRE = "http://wiki.eoeandroid.com/";
	private static final String 	WIKI_URL_AFTER = "/api.php?action=parse&format=json&page=";
	private boolean 				mIsFullScreen = false;
	private int 					count = 0;
	private long 					first,second = 0l;
	
	private RelativeLayout			mWikiProcessLayout;
	private LayoutInflater 			mInflater;
	private boolean					mProgressVisible;
	
	private ImageButton 			mBtnParentDirectory;
	private ImageButton 			mBtnFullScreen;
	private ImageButton 			mBtnFavorite;
	private ImageButton 			mBtnShare;
	private ImageButton				mBtnBack;
	private TextView				mTvFistCategoryName;
	private TextView				mTvSecondCategoryName;
	
	private RelativeLayout			mWikiDetailTitle;
	private LinearLayout 			mLayoutFunctions;
	
	public static final 	String  WIKI_CONTENT = "wiki_content";
	public static final		String 	KEY_PARENT_TITLE	= "parent_title";
	public static final		String 	KEY_SUB_PARENT_TITLE	= "sub_parent_title";
	private ParamsEntity			mParamsEntity;
	
	protected WikiDetailJson 		responseObject = null;
	protected WikiDetailErrorJson	responseError = null;
	private WebView 				mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wiki_detail);
		mInflater = LayoutInflater.from(mContext);
		Intent intent = getIntent();
		if(intent == null){
			throw new NullPointerException("Must give a Wiki Uri in the intent");
		}
		mParamsEntity = (ParamsEntity)intent.getParcelableExtra(WIKI_CONTENT);
		if(mParamsEntity == null){
			throw new NullPointerException("Must give a Wiki Uri in the intent");
		}
		getmMainActivity().getSliderLayer().addSliderListener(this);
		initComponent();
		initData();
	}

	void initComponent() {
		mWikiProcessLayout = (RelativeLayout)findViewById(R.id.layout_wiki_detail_process);
		
		mTvFistCategoryName = (TextView)findViewById(R.id.tv_title_parent);
		mTvSecondCategoryName = (TextView)findViewById(R.id.tv_second_parent_title);
		mBtnBack=(ImageButton)findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
		mWebView = (WebView)findViewById(R.id.wiki_detail_content);
		mBtnParentDirectory = (ImageButton)findViewById(R.id.btn_parent_directory);
		mBtnFullScreen = (ImageButton)findViewById(R.id.btn_fullscreen);
		mBtnFavorite = (ImageButton)findViewById(R.id.btn_favorite);
		mBtnShare = (ImageButton)findViewById(R.id.btn_share);
		
		mWikiDetailTitle = (RelativeLayout)findViewById(R.id.wiki_detail_title);
		mLayoutFunctions = (LinearLayout)findViewById(R.id.layout_functions);
		
		mBtnParentDirectory.setOnClickListener(this);
		mBtnFullScreen.setOnClickListener(this);
		mBtnFavorite.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		
	}
	
	void initData(){
		//TODO set the first parent title
		//需要上层传过来
		mTvFistCategoryName.setText(mParamsEntity.getFirstTitle());
		//TODO set the second parent title
		mTvSecondCategoryName.setText(mParamsEntity.getSecondTitle());
		showProgressLayout();
	}
	
	void getWikiDetail()
	{
		HttpManager manager = new HttpManager(mParamsEntity.getUri(),null, HttpManager.GET, getWikiDetailTransaction);
		manager.start();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DISPLAY_WIKIDETAIL:
				generateWiki((WikiDetailJson)msg.obj);
				break;
			case HANDLER_GET_WIKIERROR:
				generateWikiError((WikiDetailErrorJson)msg.obj);
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
		mWikiProcessLayout.removeAllViews();
		mProgressVisible = false;
		String html = pWikiDetailJson.getParse().getText().getHtml();
		String html1 = "<!DOCTYPE html PUBLIC "
                  + "-//W3C//DTD XHTML 1.0 Transitional//EN"
                  + "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                  + ">" + "<html xmlns=" + "http://www.w3.org/1999/xhtml" + ">"
                  + "<head>" + "<meta http-equiv=" + "Content-Type" + " content="
                  + "text/html; charset=utf-8" + "/>" + "<body>"
                  + html + "</body></html>";
		mWebView.loadDataWithBaseURL("about:blank", html1,  "text/html","utf-8", null);

        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        
        mWebView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				System.out.println("I am here");
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					count++;
					if(count == 1){
						first = System.currentTimeMillis();
					}else if(count == 2){
						second = System.currentTimeMillis();
						if(second - first <= 500){
							fullScreen();
							count = 0;
							first = 0l;
							second = 0l;
						}
					}
				}
				return false;
			}
		});
        
        mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("http://") || url.startsWith("www.")){
					Uri uri = Uri.parse(url);  
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
					return true;
				}
				mParamsEntity.setUri(WIKI_URL_PRE+WIKI_URL_AFTER+url.substring(1));
				getWikiDetail();
				return true;
			}
        	
        });
		//要使用下面这种方式
        mWebView.setBackgroundColor(WikiUtil.getResourceColor(R.color.deep_grey, mContext));
	}
	
	private void generateWikiError(WikiDetailErrorJson pWikiDetailErrorJson){
		ScrollView sw = (ScrollView)findViewById(R.id.sv_wiki_scroll);
		sw.setVisibility(View.GONE);
		if("missingtitle".equals(pWikiDetailErrorJson.getError().getCode())){
			getWikiError(getString(R.string.no_such_article));
		}else{
			getWikiError(getString(R.string.unknow_error));
		}
	}
	
	private void getWikiError(String pError){
		mWikiProcessLayout.removeAllViews();
		mProgressVisible = false;
		
		View viewError = mInflater.inflate(R.layout.loading_error, null);
		LayoutParams errorParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		errorParams.topMargin = WikiUtil.dip2px(mContext, 10);
		viewError.setLayoutParams(errorParams);
		
		TextView tvErrorTip =  (TextView)viewError.findViewById(R.id.tv_error_tip);
		tvErrorTip.setText(pError);
		Button btnTryAgain =  (Button)viewError.findViewById(R.id.btn_try_again);
		btnTryAgain.setOnClickListener(this);
		mWikiProcessLayout.addView(viewError);
	}
	
	public ITransaction getWikiDetailTransaction = new ITransaction() {
		
		@Override
		public void transactionOver(String result) {
			try {
				responseError = mObjectMapper.readValue(result, new TypeReference<WikiDetailErrorJson>() { });
				if(responseError.getError() == null){
					responseObject = mObjectMapper.readValue(result, new TypeReference<WikiDetailJson>() { });
					mHandler.obtainMessage(HANDLER_DISPLAY_WIKIDETAIL, responseObject).sendToTarget();
				}else{
					mHandler.obtainMessage(HANDLER_GET_WIKIERROR, responseError).sendToTarget();
				}
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
		case R.id.btn_back:
		case R.id.btn_parent_directory:
			fallbackToPreLayer();
			break;
		case R.id.btn_fullscreen:
			fullScreen();
			break;
		case R.id.btn_favorite:
			L.d("press favorite icon");
			FavoriteDao favoriteDao = new FavoriteDao(mContext);
			favoriteDao.addFavorite(responseObject.getParse().getRevid(), responseObject.getParse().getDisplayTitle(), mParamsEntity.getUri());
			break;
		case R.id.btn_share:
			shareToFriend();
			break;
		default:
			break;
		}
	}

	protected void showProgressLayout()
	{
		View progressView = mInflater.inflate(R.layout.loading, null);
		mWikiProcessLayout.removeAllViews();
		mWikiProcessLayout.addView(progressView);
		mProgressVisible = true;
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
		if(!mProgressVisible)
		{
			showProgressLayout();
		}
		getWikiDetail();
		getmMainActivity().getSliderLayer().removeSliderListener(this);
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
