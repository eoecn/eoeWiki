package cn.eoe.wiki.db.dao;

import android.content.Context;
import android.database.Cursor;
import cn.eoe.wiki.db.WikiColumn;
import cn.eoe.wiki.db.entity.WikiEntity;
/**
 * WikiDao<br>
 * 操作wiki相关的数据
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-7
 * @version 1.0.0
 */
public class WikiDao extends GeneralDao<WikiColumn> {

	public WikiDao(Context context) {
		super(new WikiColumn(), context);
	}

	/**
	 * get the wiki by the page id 
	 * @param pageid
	 * @return
	 */
	public WikiEntity getWikiByPageId(String pageid)
	{
		WikiEntity entity = new WikiEntity();
		Cursor cursor = queryByParameter(WikiColumn.PAGEID, pageid);
		if(cursor!=null && cursor.moveToFirst())
		{
			entity.setId(cursor.getLong(cursor.getColumnIndex(WikiColumn._ID)));
			entity.setAddDate(cursor.getString(cursor.getColumnIndex(WikiColumn.DATE_ADD)));
			entity.setModifyDate(cursor.getString(cursor.getColumnIndex(WikiColumn.DATE_MODIFY)));
			entity.setPageId(cursor.getString(cursor.getColumnIndex(WikiColumn.PAGEID)));
			entity.setPath(cursor.getString(cursor.getColumnIndex(WikiColumn.PATH)));
			entity.setUri(cursor.getString(cursor.getColumnIndex(WikiColumn.URI)));
		}
		return entity;
	}
}
