package cn.eoe.wiki.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.eoe.wiki.R;
import cn.eoe.wiki.db.entity.UpdateEntity;
import cn.eoe.wiki.http.HttpManager;
import cn.eoe.wiki.http.ITransaction;
import cn.eoe.wiki.json.CategoryChild;
import cn.eoe.wiki.json.RecentlyUpdatedJson;
import cn.eoe.wiki.listener.RecentlyUpdatedListener;
import cn.eoe.wiki.listener.SubCategoryListener;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.SliderLayer;
import cn.eoe.wiki.view.SliderLayer.SliderListener;
/**
 * 最新更新文章列表的Activity
 * @author <a href="mailto:realh3@gmail.com">Real Xu</a>
 * @data  2012-8-17
 * @version 1.0.0
 */
public class RecentlyUpdatedActivity extends SliderActivity implements SliderListener{

	private static final String WIKI_URL_HOST = "http://wiki.eoeandroid.com/";
	private static final String WIKI_URL_DETAIL = "api.php?action=parse&format=json&page=";
	private static final String WIKI_URL_LOCATION = "api.php?action=query&list=recentchanges&rclimit=30&format=json";
	
	final int HANDLER_LOAD_CONTENT_NET = 0;
	final int HANDLER_DISPLAY_CONTENT = 1;
	final int HANDLER_LOAD_ERROR = 2;
	
	private LinearLayout	mContentLayout;
	private LayoutInflater 	mInflater;
	private ImageButton		mBtnBack;
	private TextView		mTvParentName;
	
	private boolean			mProgressVisible;
	private CategoryChild	mParentCategory;
	private String			mParentName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recently_updated);
		mInflater = LayoutInflater.from(mContext);
		Intent intent = getIntent();
		if(intent==null)
		{
			throw new NullPointerException("Must give a CategoryChild in the intent");
		}

		getmMainActivity().getSliderLayer().addSliderListener(this);
		initComponent();
		initData();
	}
	
	void initComponent() {
		mTvParentName = (TextView)findViewById(R.id.tv_title_parent);

		mContentLayout = (LinearLayout)findViewById(R.id.layout_category);
		mBtnBack=(ImageButton)findViewById(R.id.btn_back);
		//mBtnBack.setOnClickListener(this);
	}

	void initData() {
		mTvParentName.setText("最近更新");
		showProgressLayout();
		new HttpManager(WIKI_URL_HOST + WIKI_URL_LOCATION, null, HttpManager.GET, getRecentlyUpdatedTransaction).start();
	}
	
	protected void showProgressLayout()
	{
		View progressView = mInflater.inflate(R.layout.loading, null);
		mContentLayout.removeAllViews();
		mContentLayout.addView(progressView);
		mProgressVisible = true;
	}
	
	private Handler mHandler = new Handler(){

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_LOAD_CONTENT_NET:
				new HttpManager(WIKI_URL_HOST + WIKI_URL_LOCATION, null, HttpManager.GET, getRecentlyUpdatedTransaction).start();
				break;
			case HANDLER_DISPLAY_CONTENT:
				generateRecentlyUpdated((List<RecentlyUpdatedJson>)msg.obj);
				break;
			case HANDLER_LOAD_ERROR:
				break;
			default:
				break;
			}
		}
	};
	
	public ITransaction getRecentlyUpdatedTransaction = new ITransaction() {
		
		@Override
		public void transactionOver(String result) {
			mapperJson(result,true);
			L.d("get the category from the net");
		}
		
		@Override
		public void transactionException(int erroCode, String result, Exception e) {
			mHandler.obtainMessage(HANDLER_LOAD_ERROR).sendToTarget();
		}
	};
	
	private void mapperJson(String result, boolean fromNet)
	{
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readValue(result, JsonNode.class);
			// 取最新更新项目
			Iterator<JsonNode> it = root.get("query").get("recentchanges").getElements();
			
			List<RecentlyUpdatedJson> items = new ArrayList<RecentlyUpdatedJson>();
			HashSet<Integer> set = new HashSet<Integer>(); // 用于标记重复项
			while(it.hasNext()){
				RecentlyUpdatedJson item = mObjectMapper.readValue(
					it.next().toString(),
					new TypeReference<RecentlyUpdatedJson>() {}
				);
				// 去除重复项
				if(!set.contains(item.getPageid())){
					items.add(item);
					set.add(item.getPageid());
				}
			}
			mHandler.obtainMessage(HANDLER_DISPLAY_CONTENT, items).sendToTarget();
		} catch (Exception e) {
			L.e("getCategorysTransaction exception", e);
		}
	}
	
	private void generateRecentlyUpdated(List<RecentlyUpdatedJson> recentlyUpdatedJsons){
		
		mContentLayout.removeAllViews();
		
		LinearLayout contentLayout = new LinearLayout(mContext);
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int paddind = WikiUtil.dip2px(mContext, 1);
		contentLayout.setPadding(paddind, paddind, paddind, paddind);
		contentLayout.setLayoutParams(titleParams);
		contentLayout.setBackgroundResource(R.drawable.bg_stroke_grey_blue);
		mContentLayout.addView(contentLayout);
		
		TextView tv = (TextView)mInflater.inflate(R.layout.category_title, null);
		tv.setText("最近更新");
		tv.setBackgroundResource(R.drawable.bg_nostroke_grey_blue_top);
		
		contentLayout.addView(tv);
		
		for(int i=0; i<recentlyUpdatedJsons.size(); i++){
			//add the line first
			View lineView = new View(mContext);
			LayoutParams blankParams = new LayoutParams(LayoutParams.MATCH_PARENT, WikiUtil.dip2px(mContext, 1));
			lineView.setLayoutParams(blankParams);
			lineView.setBackgroundResource(R.color.grey_stroke);
			contentLayout.addView(lineView);
			//add the text
			RecentlyUpdatedJson item = recentlyUpdatedJsons.get(i);
			
			TextView tvChild = (TextView)mInflater.inflate(R.layout.category_item, null);
			tvChild.setText(item.getTitle());
			
			tvChild.setOnClickListener(
				new RecentlyUpdatedListener(
					"最新更新", 
					item.getTitle(), 
					WIKI_URL_HOST + WIKI_URL_DETAIL + item.getTitle().replace(' ', '_'), 
					this
				)
			);
			if(i == recentlyUpdatedJsons.size() - 1)
			{
				tvChild.setBackgroundResource(R.drawable.bg_nostroke_white_blue_bottom);
			}
			else
			{
				tvChild.setBackgroundResource(R.drawable.bg_nostroke_white_blue_nocorners);
			}
//			mCategoryLayout.addView(tvChild);
			contentLayout.addView(tvChild);
		}
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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