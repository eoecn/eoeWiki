package cn.eoe.wiki.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecentJson {
	@JsonProperty("query")
	private RecentJson query;
	@JsonProperty("query-continue")
	private RecentUpdateQueryContinusJson queryContinue;

	public RecentJson getQuery() {
		return query;
	}

	public void setQuery(RecentJson query) {
		this.query = query;
	}

	public RecentUpdateQueryContinusJson getQueryContinue() {
		return queryContinue;
	}

	public void setQueryContinue(RecentUpdateQueryContinusJson queryContinue) {
		this.queryContinue = queryContinue;
	}
	
}
