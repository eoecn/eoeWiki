package cn.eoe.wiki.listener;

import cn.eoe.wiki.activity.SliderActivity;
import cn.eoe.wiki.json.CategoryChild;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * listener for the category
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class CategoryListener implements OnClickListener {

	SliderActivity context;
	private CategoryChild category;
	
	public CategoryListener(SliderActivity context,CategoryChild category)
	{
		this.context = context;
		this.category = category;
	}

	@Override
	public void onClick(View v) {
		new AlertDialog.Builder(context)
		.setTitle("Tip")
		.setMessage(category.getUri())
		.setNegativeButton("Cancel", null)
		.show();
	}
	
}
