package newsEventDetection.model;

import java.util.List;
import java.util.Map;

public class SimpleNews 
{
	private String news_time;
	private String news_title;
	private String news_url;
	private String news_source;
	private Map<String, List<String>> news_named_entity;
	
	public SimpleNews(String news_time, String news_title, String news_url, 
			String news_source, Map<String, List<String>> news_named_entity) 
	{
		this.news_time = news_time;
		this.news_title = news_title;
		this.news_url = news_url;
		this.news_source = news_source;
		this.news_named_entity = news_named_entity;
	}
	
	public String getNews_time(){
		return news_time;
	}
	
	public String getNews_title(){
		return news_title;
	}
	
	public String getNews_url(){
		return news_url;
	}
	
	public String getNews_source(){
		return news_source;
	}
	
	public Map<String, List<String>> getNews_named_entity(){
		return news_named_entity;
	}
	
}