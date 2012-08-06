package cn.eoe.wiki.json;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CategoryJson {
	@JsonProperty("version")
	private String version;
	@JsonProperty("content")
	private List<CategoryChild> contents;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<CategoryChild> getContents() {
		return contents;
	}
	public void setContents(List<CategoryChild> contents) {
		this.contents = contents;
	}
	
}
