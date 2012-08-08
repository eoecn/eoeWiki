package cn.eoe.wiki.json;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CategoryJson {
	@JsonProperty("version")
	private int version;
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
