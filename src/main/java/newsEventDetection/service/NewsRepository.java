package newsEventDetection.service;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.x;

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
public class NewsRepository 
{
	private Bucket bucket;
	
	@Autowired
    public NewsRepository(Bucket bucket) {
        this.bucket = bucket;
    }
	
	/**
	 * 获取单篇文章
	 * @param newsId
	 * @return
	 */
	public JsonObject getNews(int newsId) 
	{
		Statement statement = select("news_time", "news_title", "news_category", "news_url",
				"news_source", "news_summary", "news_named_entity")
				.from(i(bucket.name()))
				.where( x("news_id").eq(x(newsId)) );
				
		N1qlQuery query = N1qlQuery.simple(statement);
		N1qlQueryResult result = bucket.query(query);
		List<N1qlQueryRow> resultRowList = result.allRows();
		
		N1qlQueryRow fristRow = resultRowList.get(0);
		JsonObject newsObject = fristRow.value();
		
		return newsObject;
	}
}
