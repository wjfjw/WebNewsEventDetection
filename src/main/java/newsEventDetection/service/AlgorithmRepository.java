package newsEventDetection.service;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
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
import com.couchbase.client.java.query.dsl.Expression;

@Service
public class AlgorithmRepository 
{
	private Bucket bucket;
	
	@Autowired
    public AlgorithmRepository(Bucket bucket) {
        this.bucket = bucket;
    }
	
	
	/**
	 * 获取指定算法及参数的id
	 * @param algorithm_name
	 * @return
	 */
	public int getAlgorithm_id(String algorithm_name,
			double single_pass_clustering_threshold, int single_pass_time_window,
			int kmeans_cluster_number, int kmeans_time_window,
			double topic_tracking_threshold)
	{
		Expression expression = x("algorithm_name").eq(s(algorithm_name));
		if(algorithm_name.equals("single_pass")) {
			expression = expression.and( x("algorithm_parameters.similarity_threshold").eq( x(single_pass_clustering_threshold) ) )
					.and( x("algorithm_parameters.time_window").eq( x(single_pass_time_window) ) )
					.and( x("algorithm_parameters.topic_tracking_threshold").eq( x(topic_tracking_threshold) ) );
		}else if(algorithm_name.equals("kmeans")) {
			expression = expression.and( x("algorithm_parameters.cluster_number").eq( x(kmeans_cluster_number) ) )
					.and( x("algorithm_parameters.time_window").eq( x(kmeans_time_window) ) )
					.and( x("algorithm_parameters.topic_tracking_threshold").eq( x(topic_tracking_threshold) ) );
		}
		
		//根据算法参数查询algorithm_id
		Statement statement = select("algorithm_id")
				.from(i(bucket.name()))
				.where(expression);

		N1qlQuery query = N1qlQuery.simple(statement);
		N1qlQueryResult result = bucket.query(query);
		List<N1qlQueryRow> resultRowList = result.allRows();
		
		int algorithm_id = -1;
		if(!resultRowList.isEmpty() && resultRowList.get(0).value().containsKey("algorithm_id")) {
			JsonObject object = resultRowList.get(0).value();
			algorithm_id = object.getInt("algorithm_id");
		}else {
			System.out.println("\n*********************************");
			System.out.println("对应的算法不存在！");
			System.out.println("*********************************\n");
		}
		return algorithm_id;
	}
	
}
