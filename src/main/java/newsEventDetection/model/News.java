package newsEventDetection.model;

import java.util.List;
import java.util.Map;

public class News 
{
	private int news_id;
	private long news_time;
	private String news_title;
	private String news_category;
	private String news_url;
	private String news_source;
	private String news_content;
	private String news_summary;
	private Map<String, List<String>> news_named_entity;
	
	public News(int news_id, long news_time, String news_title, String news_category, String news_url, 
			String news_source, String news_content, String news_summary, Map<String, List<String>> news_named_entity) 
	{
		this.news_id = news_id;
		this.news_time = news_time;
		this.news_title = news_title;
		this.news_category = news_category;
		this.news_url = news_url;
		this.news_source = news_source;
		this.news_content = news_content;
		this.news_summary = news_summary;
		this.news_named_entity = news_named_entity;
	}
	
	public int getNews_id() {
		return news_id;
	}
	
	public long getNews_time(){
		return news_time;
	}
	
	public String getNews_title(){
		return news_title;
	}
	
	public String getNews_category(){
		return news_category;
	}
	
	public String getNews_url(){
		return news_url;
	}
	
	public String getNews_source(){
		return news_source;
	}
	
	public String getNews_content(){
		return news_content;
	}
	
	public String getNews_summary(){
		return news_summary;
	}
	
	public Map<String, List<String>> getNews_named_entity(){
		return news_named_entity;
	}
	
	
}