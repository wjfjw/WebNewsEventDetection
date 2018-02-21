package newsEventDetection.service;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Sort;

@Service
public class EventRepository 
{
	private Bucket bucket;
	
	@Autowired
    public EventRepository(Bucket bucket) {
        this.bucket = bucket;
    }
	
	/**
	 * 获取事件的第一篇新闻的id
	 * @param eventId
	 * @return
	 */
	public int getFirstNewsId(int eventId) 
	{
		List<JsonObject> eventList = getEventList( JsonArray.create().add(eventId) );
		
		JsonArray event_news_array = eventList.get(0).getArray("event_news_list");
		List<Integer> event_news_list = new ArrayList<Integer>();
		for(Object object : event_news_array) {
			int event_id = Integer.parseInt( object.toString() );
			event_news_list.add(event_id);
		}
		event_news_list.sort( (Integer a, Integer b) -> {
			return a-b;
		});
		
		int firstNewsId = event_news_list.get(0);
		
		return firstNewsId;
	}
	
	/**
	 * 获取事件列表
	 * @param event_id_list
	 * @return
	 */
	public List<JsonObject> getEventList(JsonArray event_id_list) 
	{
		Statement statement = select("*")
				.from(i(bucket.name()))
				.where( x("event_id").in(event_id_list) )
				.orderBy(Sort.desc("event_id"));
				
		N1qlQuery query = N1qlQuery.simple(statement);
		N1qlQueryResult result = bucket.query(query);
		List<N1qlQueryRow> resultRowList = result.allRows();
		
		List<JsonObject> eventList = new ArrayList<JsonObject>();

		for(N1qlQueryRow row : resultRowList) {
			JsonObject eventObject = row.value();
			eventObject = eventObject.getObject(bucket.name());
			eventList.add(eventObject);
		}
		
		return eventList;
	}
	
}
