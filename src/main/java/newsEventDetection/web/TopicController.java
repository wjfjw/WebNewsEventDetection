package newsEventDetection.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

import newsEventDetection.model.SimpleEvent;
import newsEventDetection.model.SimpleNews;
import newsEventDetection.service.AlgorithmRepository;
import newsEventDetection.service.EventRepository;
import newsEventDetection.service.NewsRepository;
import newsEventDetection.service.TopicRepository;
import newsEventDetection.util.TimeConversion;

@Controller
public class TopicController 
{
	private TopicRepository topicRepository;
	private EventRepository eventRepository;
	private NewsRepository newsRepository;
	
	@Autowired
	public TopicController(TopicRepository topicRepository,
			EventRepository eventRepository,
			NewsRepository newsRepository) {
		this.topicRepository = topicRepository;
		this.eventRepository = eventRepository;
		this.newsRepository = newsRepository;
	}
	
	@RequestMapping(value = "/{topicId}", method=GET)
	public String topic( @PathVariable("topicId") int topicId, Model model ) 
	{
		JsonObject topicObject = topicRepository.getTopic(topicId);
		JsonArray topic_event_list = topicObject.getArray("topic_event_list");
		List<JsonObject> eventObjectList = eventRepository.getEventList(topic_event_list);
		
		List<SimpleEvent> simpleEventList = new ArrayList<SimpleEvent>();
		for(JsonObject eventObject : eventObjectList) {
			//获取事件的标题
			int firstNewsId = eventRepository.getFirstNewsId( eventObject.getInt("event_id") );
			JsonObject newsObject = newsRepository.getNews(firstNewsId);
			String news_title = newsObject.getString("news_title");
			
			//获取事件的时间
			long time = eventObject.getLong("event_start_time");
			String event_time = TimeConversion.getTimeString(time);
					
			simpleEventList.add( new SimpleEvent(news_title, event_time) );
		}
		
		//加入model
		model.addAttribute("simpleEventList", simpleEventList);
		
		//******************************************************//
		
		//获取话题的最后一个事件
		JsonObject lastEventObject = eventObjectList.get( eventObjectList.size()-1 );
		//获取新闻列表
		JsonArray event_news_list = lastEventObject.getArray("event_news_list");
		List<JsonObject> newsObjectList = newsRepository.getNewsList(event_news_list);
		
		List<SimpleNews> simpleNewsList = new ArrayList<SimpleNews>();
		for(JsonObject newsObject : newsObjectList) {
			//获取新闻的时间
			long time = newsObject.getLong("news_time");
			String news_time = TimeConversion.getTimeString(time);
			//新闻的标题
			String news_title = newsObject.getString("news_title");
			//新闻的url
			String news_url = newsObject.getString("news_url");
			//新闻的来源
			String news_source = newsObject.getString("news_source");
			//新闻的命名实体
			JsonObject named_entity_object = newsObject.getObject("news_named_entity");
			
			simpleNewsList.add( new SimpleNews(
					news_time, news_title, news_url, news_source, news_named_entity) );
		}
		
		//加入model
		model.addAttribute("simpleNewsList", simpleNewsList);
		
		//******************************************************//
		
		//获取最后一个事件的第一篇新闻
		int lastEventId = lastEventObject.getInt("event_id");
		int firstNewsId = eventRepository.getFirstNewsId(lastEventId);
		JsonObject newsObject = newsRepository.getNews(firstNewsId);
		
		//获取事件的title和summary
		String event_title = newsObject.getString("news_title");
		String event_summary = newsObject.getString("news_summary");
		
		//加入model
		model.addAttribute("event_title", event_title);
		model.addAttribute("event_summary", event_summary);
		
		return "topic";
	}
}
