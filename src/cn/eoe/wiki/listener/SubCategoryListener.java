package cn.eoe.wiki.listener;

import cn.eoe.wiki.activity.SubCategorysActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SubCategoryListener implements OnClickListener{

	private String mUri;
	private SubCategorysActivity mContext;
	
	public SubCategoryListener(String pUri,SubCategorysActivity pContext){
		mUri = pUri;
		mContext = pContext;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println(mUri);
		Toast.makeText(mContext, mUri, 3000).show();
	}

}
