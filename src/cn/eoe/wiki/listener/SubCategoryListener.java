package cn.eoe.wiki.listener;

import cn.eoe.wiki.activity.SubCategoryActivity;
import cn.eoe.wiki.activity.WikiContentActivity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SubCategoryListener implements OnClickListener{

	private String mUri;
	private SubCategoryActivity mContext;
	
	public SubCategoryListener(String pUri,SubCategoryActivity pContext){
		mUri = pUri;
		mContext = pContext;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent (mContext,WikiContentActivity.class);
		intent.putExtra(WikiContentActivity.WIKI_CONTENT, mUri);
		mContext.getmMainActivity().showView(2, intent);
	}

}
