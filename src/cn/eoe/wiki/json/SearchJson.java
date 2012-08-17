package cn.eoe.wiki.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchJson {
	@JsonProperty("query")
	private SearchResultJson query;

	@JsonProperty("query-continue")
	private SearchJudgeJson query_continue;

	public SearchResultJson getQuery() {
		return query;
	}

	public SearchJudgeJson getQuery_continue() {
		return query_continue;
	}

	public void setQuery_continue(SearchJudgeJson query_continue) {
		this.query_continue = query_continue;
	}

	public void setQuery(SearchResultJson query) {
		this.query = query;
	}

}
