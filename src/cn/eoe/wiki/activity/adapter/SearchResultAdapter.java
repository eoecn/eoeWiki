package cn.eoe.wiki.activity.adapter;

import java.util.HashMap;
import java.util.List;

import cn.eoe.wiki.R;
import cn.eoe.wiki.activity.SearchResultActivity;
import cn.eoe.wiki.activity.WikiContentActivity;
import cn.eoe.wiki.db.entity.ParamsEntity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
/**
 * seach Adapter
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @date Aug 20, 2012
 * @version 1.0.0
 *
 */
public class SearchResultAdapter extends BaseAdapter {
	
	private SearchResultActivity 			mContext;
	private List<HashMap<String, Object>> 	data;
	private LayoutInflater 			mInflater;

	public SearchResultAdapter(SearchResultActivity mContext,
			List<HashMap<String, Object>> data) {
		this.mContext = mContext;
		this.data = data;
		mInflater = LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.newsitem, null);
		TextView tvTitle = (TextView) convertView
				.findViewById(R.id.news_title);
		TextView tvContent = (TextView) convertView
				.findViewById(R.id.news_from);

		tvTitle.setText(data.get(position).get("title").toString());
		tvTitle.setPadding(15, 0, 10, 0);
		final String title = data.get(position).get("title").toString();
		String[] title_array = title.split(" ");
		StringBuffer title_search = new StringBuffer();
		for (int i = 0; i < title_array.length; i++) {
			if (i == title_array.length - 1) {
				title_search = title_search.append(title_array[i]);
			} else {
				title_search = title_search.append(title_array[i]).append(
						"_");
			}
		}

		final String url_toContent = "http://wiki.eoeandroid.com/api.php?action=parse&format=json&page="
				+ title_search;
		tvTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,
						WikiContentActivity.class);
				ParamsEntity pe = new ParamsEntity();
				pe.setFirstTitle("搜索");
				pe.setSecondTitle(title);
				pe.setUri(url_toContent);
				intent.putExtra(WikiContentActivity.WIKI_CONTENT, pe);
				mContext.getmMainActivity().showView(2, intent);
			}
		});

		String content = data.get(position).get("snippet").toString();
		content = content.replace("<span class='searchmatch'>", "");
		content = content.replace("</span>", "");
		tvContent.setText(content);
		tvContent.setPadding(30, 0, 10, 0);
		return convertView;
	}

}
