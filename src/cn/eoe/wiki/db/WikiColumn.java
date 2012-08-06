package cn.eoe.wiki.db;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

public class WikiColumn extends DatabaseColumn {

    /**
     * This table's name
     */
    public static final String 		TABLE_NAME 		= "wiki";
    
    public static final String		NAME			="name";
    public static final String 		PATH			="path";
    
    public static final String[]	COLUMNS			= new String[]{
    	_ID,NAME,PATH,DATE_ADD,DATE_MODIFY
    };
    
    public static final Uri 		CONTENT_URI 	= Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
    private static final Map<String, String> mColumnsMap = new HashMap<String, String>();
    static {
		mColumnsMap.put(_ID, "integer primary key autoincrement not null");
		mColumnsMap.put(NAME, "text not null");
		mColumnsMap.put(PATH, "text not null");
		mColumnsMap.put(DATE_ADD, "localtime");
		mColumnsMap.put(DATE_MODIFY, "localtime");
    };
    
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public Uri getTableContent() {
		return CONTENT_URI;
	}

	@Override
	protected Map<String, String> getTableMap() {
		return mColumnsMap;
	}

}
