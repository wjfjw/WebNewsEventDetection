package newsEventDetection.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

import newsEventDetection.model.SimpleTopic;
import newsEventDetection.service.*;
import newsEventDetection.util.TimeConversion;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Controller
public class HomeController 
{
	private AlgorithmRepository algorithmRepository;
	private TopicRepository topicRepository;
	private EventRepository eventRepository;
	private NewsRepository newsRepository;
	
	@Autowired
	public HomeController(AlgorithmRepository algorithmRepository,
			TopicRepository topicRepository,
			EventRepository eventRepository,
			NewsRepository newsRepository) {
		this.algorithmRepository = algorithmRepository;
		this.topicRepository = topicRepository;
		this.eventRepository = eventRepository;
		this.newsRepository = newsRepository;
	}
	
	
	@RequestMapping(value = "/", method=GET)
	public String home(Model model) 
	{
		List<SimpleTopic> simpleTopicList = new ArrayList<SimpleTopic>();
		
		//get topic_id, news_title, news_time, news_summary
		List<JsonObject> topicList = getTopicList();
		for(JsonObject topicObject : topicList) {
			int topic_id = topicObject.getInt("topic_id");
			
			//get last event
			JsonArray topic_event_list = topicObject.getArray("topic_event_list");
			int lastEventId = topic_event_list.getInt( topic_event_list.size()-1 );
			//get first news
			int firstNewsId = eventRepository.getFirstNewsId(lastEventId);
			JsonObject newsObject = newsRepository.getNews(firstNewsId);
			
			String news_title = newsObject.getString("news_title");
			String news_time = TimeConversion.getTimeString( newsObject.getLong("news_time") );
			String news_summary = newsObject.getString("news_summary");
			simpleTopicList.add( new SimpleTopic(topic_id, news_title, news_time, news_summary) );
		}
		
		model.addAttribute("simpleTopicList", simpleTopicList);
		
		return "home";
	}
	
	private List<JsonObject> getTopicList() 
	{
		//算法参数
		//singlePass
		double single_pass_clustering_threshold = 0.5;
		int single_pass_time_window = 24;		//单位：小时
		//kmeans
		int kmeans_cluster_number = 100;
		int kmeans_time_window = 24;		//单位：小时
		//topic tracking
		double topic_tracking_threshold = 0.7;
		String algorithm_name = "single_pass";
		
		//获取topic的参数
		int algorithm_id = algorithmRepository.getAlgorithm_id(algorithm_name, single_pass_clustering_threshold, single_pass_time_window,
				kmeans_cluster_number, kmeans_time_window, topic_tracking_threshold);
		String category = "gn";
		
		List<JsonObject> topicList = topicRepository.getTopicList(algorithm_id, category);
		
		return topicList;
	}
	
}
