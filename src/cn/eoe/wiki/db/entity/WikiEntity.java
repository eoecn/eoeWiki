package cn.eoe.wiki.db.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.text.TextUtils;
import cn.eoe.wiki.utils.L;

/**
 * 用于表示wiki的实体
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-11
 * @version 1.0.0
 */
public class WikiEntity extends DataBaseEntity {
	private String pageId;
	private String path;
	private String uri;
	private String version;
	
	private File   wikiFile;
	
	/**
	 * is the wiki file is exsit
	 * @return true->exsit, otherwise,false;
	 */
	public  boolean isWikiFileExist()
	{
		if(TextUtils.isEmpty(path))
		{
			return false;
		}
		if(wikiFile==null)
		{
			wikiFile = new File(path);
		}
		return wikiFile.exists();
	}
	/**
	 * get the wiki content
	 * @return
	 */
	public String getWikiFileContent()
	{
		if(!isWikiFileExist())
		{
			//check the file .
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(wikiFile);
			StringBuilder sb = new StringBuilder();
			byte buffer[] = new byte[4096];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, len));
			}
			return sb.toString();
		} catch (Exception e) {
			L.e("get file exception:"+path, e);
		}
		finally
		{
			if(fis !=null)
			{
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
	
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
