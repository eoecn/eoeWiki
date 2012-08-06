package cn.eoe.wiki.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cn.eoe.wiki.view.SliderLayer.SliderListener;

public class WikiContentActivity extends SliderActivity implements OnClickListener,SliderListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

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
