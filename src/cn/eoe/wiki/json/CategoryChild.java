package cn.eoe.wiki.json;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CategoryChild {
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("text")
	private String text;
	
	@JsonProperty("desc")
	private String description;
	
	@JsonProperty("uri")
	private String uri;
	
	@JsonProperty("children")
	private	List<CategoryChild> children;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<CategoryChild> getChildren() {
		return children;
	}

	public void setChildren(List<CategoryChild> children) {
		this.children = children;
	}
	

}
