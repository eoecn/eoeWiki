package cn.eoe.wiki.db.entity;
/**
 * 用来表示favorite的实体
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-11
 * @version 1.0.0
 */
public class FavoriteEntity extends DataBaseEntity {
	private String		title;
	private String		pageid;
	private String		url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPageid() {
		return pageid;
	}
	public void setPageid(String pageid) {
		this.pageid = pageid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
