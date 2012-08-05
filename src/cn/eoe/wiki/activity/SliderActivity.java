package cn.eoe.wiki.activity;

import android.os.Bundle;

/**
 * 所有滑块界面的基类
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class SliderActivity extends BaseActivity {
	protected MainActivity mMainActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainActivity = getWikiApplication().getMainActivity();
		if(mMainActivity==null)
		{
			throw new NullPointerException("You should start the MainActivity firstly");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public MainActivity getmMainActivity() {
		return mMainActivity;
	}

}
