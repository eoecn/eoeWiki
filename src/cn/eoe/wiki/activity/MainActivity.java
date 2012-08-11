package cn.eoe.wiki.activity;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import cn.eoe.wiki.R;
import cn.eoe.wiki.WikiApplication;
import cn.eoe.wiki.utils.L;
import cn.eoe.wiki.utils.WikiUtil;
import cn.eoe.wiki.view.SliderEntity;
import cn.eoe.wiki.view.SliderLayer;

/**
 * 应用程序的主界面
 * 
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data 2012-8-5
 * @version 1.0.0
 */
public class MainActivity extends ActivityGroup {
	
	private static WikiApplication 	mWikiApplication;
	private  	MainActivity 		mMainActivity;

	private LocalActivityManager 	mActivityManager;

	private SliderLayer 			mSliderLayers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWikiApplication = WikiApplication.getApplication();
		mMainActivity = this;
		mWikiApplication.setMainActivity(mMainActivity);
		setContentView(R.layout.main);

		mActivityManager = getLocalActivityManager();

		mSliderLayers = (SliderLayer) findViewById(R.id.animation_layout);

		int sceenWidth = WikiUtil.getSceenWidth(mMainActivity);
		ViewGroup layerOne = (ViewGroup) findViewById(R.id.animation_layout_one);
		layerOne.setPadding(1,1, WikiUtil.dip2px(mMainActivity, 23), 0);
		ViewGroup layerTwo = (ViewGroup) findViewById(R.id.animation_layout_two);
		layerTwo.setPadding(10, 10, WikiUtil.dip2px(mMainActivity, 15), 0);
		ViewGroup layerThree = (ViewGroup) findViewById(R.id.animation_layout_three);
		layerThree.setPadding(0, 0, WikiUtil.dip2px(mMainActivity, 10), 0);
		mSliderLayers.addLayer(new SliderEntity(layerOne, 0, sceenWidth, 0));
		mSliderLayers.addLayer(new SliderEntity(layerTwo, 0, sceenWidth - WikiUtil.dip2px(mMainActivity, 23), 0));
		mSliderLayers.addLayer(new SliderEntity(layerThree, 0, sceenWidth - WikiUtil.dip2px(mMainActivity, 20), 0));

		Intent intent = new Intent(this, MainCategorysActivity.class);
		showView(0, intent);
	}

	public void showView(final int index, Intent intent) {
		if (intent.getFlags() == 0) {
			// 这里用不用标志都无所谓了，我们给了不了不同的id ,则都会去重新生成一个
			// 这样就可以把flag解放出来可以让intent携带更多的数据
			// 但是要注意，如果是自己定义的flag，则保证与系统的不一至，所以还是不建议使用此方法来携带参数
			// the FLASG_ACTIVITY_CLEAR_TOP is the detals flags
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.putExtra("index", index);
		// 这里id是最关键的，不能重复。
		// 如果看过一个wiki想快速加载，我们只能通过读取数据库来实现。
		// 我们不想通过保存上一次的activity来作一个快速的显示，因为页面可能有一些的改动。
		// 而后那样我们这里的处理步骤也是会复杂一点的
		String id = String.valueOf(System.currentTimeMillis());
		View view = mActivityManager.startActivity(id, intent).getDecorView();
		ViewGroup currentView = mSliderLayers.getLayer(index);
		currentView.removeAllViews();
		currentView.addView(view);
		// if the index ==0 , no need to open .
		if (index == 0)
			return;
		view.post(new Runnable() {
			@Override
			public void run() {
				//为什么要在这里才进行打开这个动作 
				//如果直接在addview后执行此动作，会造成在动画的时候又在绘图
				//界面就会乱掉
				mSliderLayers.openSidebar(index);
			}
		});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		L.e("MainActivity dispatchKeyEvent:"+event.getKeyCode());
		int keyCode = event.getKeyCode();
		int keyAction = event.getAction();
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			L.e("MainActivity　keyAction:"+keyAction);
			if( keyAction == KeyEvent.ACTION_DOWN)
			{

				int index = mSliderLayers.openingLayerIndex();
				if (index > 0) {
					mSliderLayers.closeSidebar(index);
				} else {
					//发送一个广播，通知其它所有的页面，要结束该应用程序了。
					//baseActivity里面接收这个广播，并作相应的处理。
					//这也是为什么要求所有的activity都必需直接或者间隔继承于baseactivity的原因
					sendBroadcast(new Intent(BaseActivity.ACTION_EXIT));
					finish();
				}
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	public SliderLayer getSliderLayer()
	{
		return mSliderLayers;
	}
}
