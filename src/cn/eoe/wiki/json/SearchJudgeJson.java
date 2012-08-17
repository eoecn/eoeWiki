package cn.eoe.wiki.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchJudgeJson {
	@JsonProperty("search")
	private SearchJudgeChild search;

	public SearchJudgeChild getSearch() {
		return search;
	}

	public void setSearch(SearchJudgeChild search) {
		this.search = search;
	}

}
