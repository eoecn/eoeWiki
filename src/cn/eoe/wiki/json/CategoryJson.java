package cn.eoe.wiki.json;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

//只匹配有的 ignore没必要的或者不存在的
@JsonIgnoreProperties(ignoreUnknown=true)
public class CategoryJson {
	@JsonProperty("version")//key值
	private int version;//value值
	@JsonProperty("content")
	private List<CategoryChild> contents;
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public List<CategoryChild> getContents() {
		return contents;
	}
	public void setContents(List<CategoryChild> contents) {
		this.contents = contents;
	}
	
}
