package cn.eoe.wiki.json;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultJson {
	@JsonProperty("search")
	private List<SearchResultChild> search;

	public List<SearchResultChild> getSearch() {
		return search;
	}

	public void setSearch(List<SearchResultChild> search) {
		this.search = search;
	}

}
