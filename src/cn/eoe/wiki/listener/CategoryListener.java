package cn.eoe.wiki.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cn.eoe.wiki.activity.CategorysActivity;
import cn.eoe.wiki.activity.MainActivity;
import cn.eoe.wiki.activity.SubCategorysActivity;
import cn.eoe.wiki.json.CategoryChild;

/**
 * listener for the category
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class CategoryListener implements OnClickListener {

	private CategorysActivity 	context;
	private CategoryChild 		category;
	private String				parentName;
	
	public CategoryListener(CategorysActivity context,CategoryChild category,String parentName)
	{
		this.context = context;
		this.category = category;
		this.parentName = parentName;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent (context,SubCategorysActivity.class);
		intent.putExtra(SubCategorysActivity.KEY_CATEGORY, category);
		intent.putExtra(SubCategorysActivity.KEY_PARENT_TITLE, parentName);
		context.getmMainActivity().showView(1, intent);
	}
	
}
