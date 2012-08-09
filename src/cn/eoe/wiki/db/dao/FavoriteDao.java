package cn.eoe.wiki.db.dao;

import android.content.Context;
import cn.eoe.wiki.db.FavoriteColumn;
/**
 * FavoriteDao<br>
 * 操作Favorites相关的数据
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-8
 * @version 1.0.0
 */
public class FavoriteDao extends GeneralDao<FavoriteColumn> {

	public FavoriteDao(Context context) {
		super(new FavoriteColumn(), context);
	}

}
