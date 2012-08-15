package cn.eoe.wiki.activity.adapter;

import java.util.List;

import cn.eoe.wiki.R;
import cn.eoe.wiki.activity.BaseActivity;
import cn.eoe.wiki.db.entity.FavoriteEntity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
/**
 * 收藏夹的适配器
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @date Aug 15, 2012
 * @version 1.0.0
 *
 */
public class FavoriteAdapter extends BaseAdapter{
	private BaseActivity			context;
	private List<FavoriteEntity> 	favorites;
	private LayoutInflater 			inflater;
	
	public FavoriteAdapter(BaseActivity context,List<FavoriteEntity> favorites)
	{
		this.context = context;
		this.favorites = favorites;
		inflater = LayoutInflater.from(context);
	}
	/**
	 * set the favorites. and notify the data set
	 * @param favorites
	 */
	public void setFavorites(List<FavoriteEntity> favorites)
	{
		this.favorites = favorites;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return favorites==null?0:favorites.size();
	}

	@Override
	public Object getItem(int position) {
		return favorites==null?null:favorites.get(position);
	}

	@Override
	public long getItemId(int position) {
		return favorites==null?0:favorites.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		{
			convertView = inflater.inflate(R.layout.category_item, null);
		}
		return null;
	}

}
