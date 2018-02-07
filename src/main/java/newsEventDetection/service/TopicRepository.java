package newsEventDetection.service;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.Expression.s;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;

@Service
public class TopicRepository 
{
	private Bucket bucket;
	
	@Autowired
    public TopicRepository(Bucket bucket) {
        this.bucket = bucket;
    }
	
	/**
	 * 获取单个话题
	 * @param topicId
	 * @return
	 */
	public JsonObject getTopic(int topicId) 
	{
		Statement statement = select("*")
				.from(i(bucket.name()))
				.where( x("topic_id").eq(x(topicId)) );
				
		N1qlQuery query = N1qlQuery.simple(statement);
		N1qlQueryResult result = bucket.query(query);
		List<N1qlQueryRow> resultRowList = result.allRows();
		N1qlQueryRow fristRow = resultRowList.get(0);
		JsonObject topicObject = fristRow.value();
		
		return topicObject;
	}
	
	/**
	 * 获取指定算法参数的话题列表
	 * @param algorithm_id
	 * @param category
	 * @return
	 */
	public List<JsonObject> getTopicList(int algorithm_id, String category) 
	{
		Statement statement = select("*")
				.from(i(bucket.name()))
				.where( x("topic_algorithm").eq(x(algorithm_id)).and( x("topic_category").eq(s(category)) ) )
				.limit(10);
				
		N1qlQuery query = N1qlQuery.simple(statement);
		N1qlQueryResult result = bucket.query(query);
		List<N1qlQueryRow> resultRowList = result.allRows();
		
		List<JsonObject> topicList = new ArrayList<JsonObject>();

		for(N1qlQueryRow row : resultRowList) {
			topicList.add(row.value());
		}
		
		return topicList;
	}
}
