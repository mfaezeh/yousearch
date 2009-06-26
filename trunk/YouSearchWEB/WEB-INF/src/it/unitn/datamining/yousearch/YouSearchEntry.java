package it.unitn.datamining.yousearch;

public class YouSearchEntry {

	private String videoId;
	private String title;
	private String tags;

	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		//System.out.println(tags);
		this.tags = tags;
	}
	public YouSearchEntry(String id, String title, String taglist) {
		this.tags = taglist;
		this.videoId = id;
		this.title = title;
	}
	public YouSearchEntry() {

	}	

}
