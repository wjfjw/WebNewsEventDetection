package newsEventDetection.model;

public class SimpleEvent 
{
	private String news_title;
	private String event_time;
	
	public SimpleEvent(String news_title, String event_time) {
		this.news_title = news_title;
		this.event_time = event_time;
	}
	
	public String getNews_title() {
		return news_title;
	}
	
	public String getEvent_time() {
		return event_time;
	}
}
