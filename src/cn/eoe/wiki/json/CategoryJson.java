package cn.eoe.wiki.json;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CategoryJson {
	@JsonProperty("date")
	private String date;
	@JsonProperty("content")
	private List<CategoryChild> contents;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<CategoryChild> getContents() {
		return contents;
	}
	public void setContents(List<CategoryChild> contents) {
		this.contents = contents;
	}
	
}
