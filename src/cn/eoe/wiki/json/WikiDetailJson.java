package cn.eoe.wiki.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WikiDetailJson implements Parcelable{

	@JsonProperty("parse")
	private String parse;
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("revid")
	private String revid;
	
	@JsonProperty("displaytitle")
	private String displayTitle;
	
	@JsonProperty("text")
	private String text;
	
	public WikiDetailJson(){}
	
	public WikiDetailJson(Parcel source)
	{
		parse = source.readString();
		title = source.readString();
		revid = source.readString();
		displayTitle = source.readString();
		text = source.readString();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(parse);
		dest.writeString(title);
		dest.writeString(revid);
		dest.writeString(displayTitle);
		dest.writeString(text);
	}
	
	public String getParse() {
		return parse;
	}

	public void setParse(String parse) {
		this.parse = parse;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRevid() {
		return revid;
	}

	public void setRevid(String revid) {
		this.revid = revid;
	}

	public String getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
