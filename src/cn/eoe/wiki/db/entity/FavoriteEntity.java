package cn.eoe.wiki.db.entity;
/**
 * 用来表示favorite的实体
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-11
 * @version 1.0.0
 */
public class FavoriteEntity extends DataBaseEntity {
	private String 	wikiId;
	private int 	status;
	private WikiEntity	wiki;
	public String getWikiId() {
		return wikiId;
	}
	public void setWikiId(String wikiId) {
		this.wikiId = wikiId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public WikiEntity getWiki() {
		return wiki;
	}
	public void setWiki(WikiEntity wiki) {
		this.wiki = wiki;
	}
	
	
}
