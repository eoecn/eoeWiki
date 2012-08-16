package cn.eoe.wiki.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.eoe.wiki.activity.FavoriteActivity;
import cn.eoe.wiki.activity.WikiContentActivity;

/**
 * 点击收藏夹的第一项，跳转到detail页面
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-2
 * @version 1.0.0
 */
public class FavoriteItemListener implements OnClickListener{

	private String 				mUri;
	private FavoriteActivity 	mContext;
	
	public FavoriteItemListener(String uri,FavoriteActivity context){
		mUri = uri;
		mContext = context;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent (mContext,WikiContentActivity.class);
		intent.putExtra(WikiContentActivity.WIKI_CONTENT, mUri);
		mContext.getmMainActivity().showView(2, intent);
	}

}
