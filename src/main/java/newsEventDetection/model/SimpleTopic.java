package newsEventDetection.model;

public class SimpleTopic 
{
	private int topic_id;
	private String news_title;
	private String news_time;
	private String news_summary;
	
	public SimpleTopic(int topic_id, String news_title, String news_time, String news_summary) {
		this.topic_id = topic_id;
		this.news_title = news_title;
		this.news_time = news_time;
		this.news_summary = news_summary;
	}
	
	public int getTopic_id() {
		return topic_id;
	}
	
	public String getNews_title() {
		return news_title;
	}
	
	public String getNews_time() {
		return news_time;
	}
	
	public String getNews_summary() {
		return news_summary;
	}
}
