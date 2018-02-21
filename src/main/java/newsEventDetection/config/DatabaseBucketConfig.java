package newsEventDetection.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

@Configuration
public class DatabaseBucketConfig 
{
    @Value("${storage.bucketName}")
    private String bucketName;

    @Value("${storage.bucketPassword}")
    private String bucketPassword;
    
    @Value("${spring.couchbase.bootstrap-hosts}")
    private String clusterHostName;
    
//    @Bean
//    public Cluster couchbaseCluster() {
//    	return CouchbaseCluster.create(clusterHostName);
//    }
//    
//    @Autowired
//    private Cluster cluster;

    @Bean
    public Bucket couchbaseBucket() {
    	CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
                .connectTimeout(10000) //10000ms = 10s, default is 5s
                .build();

    	
    	Cluster cluster = CouchbaseCluster.create(env, clusterHostName);
        return cluster.openBucket(bucketName, bucketPassword);
    }
}

