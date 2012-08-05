package cn.eoe.wiki.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.eoe.wiki.R;

public class CategorysActivity extends SliderActivity {
	private Button 		nextBtn;
	private TextView	displayLabel;

	private TextView	displayTimeLabel;
	private int 		index 	= -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorys);
		nextBtn = (Button)findViewById(R.id.button1);
		displayLabel = (TextView)findViewById(R.id.textView1);
		displayTimeLabel = (TextView)findViewById(R.id.textView2);
		index = getIntent().getIntExtra("index", -1);
		System.out.println("DisplayActivity:index:"+index);
		displayLabel.setText("Current view is:"+index);
		displayTimeLabel.setText(System.currentTimeMillis()+"");
		nextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int openIndex = index+1;
				if(openIndex>=getmMainActivity().getSliderLayer().getLayersCount()) 
					return;
				Intent intent = new Intent (mContext,CategorysActivity.class);
				getmMainActivity().showView(openIndex,intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
