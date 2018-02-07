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
		List<SimpleEvent> simpleEventList = new ArrayList<SimpleEvent>();
		
		JsonObject topicObject = topicRepository.getTopic(topicId);
		JsonArray topic_event_list = topicObject.getArray("topic_event_list");
		List<JsonObject> eventObjectList = eventRepository.getEventList(topic_event_list);
		
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
		
		
		return "topic";
	}
}
