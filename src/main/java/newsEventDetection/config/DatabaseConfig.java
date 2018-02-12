package newsEventDetection.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

@Configuration
public class DatabaseConfig 
{
    @Value("${storage.bucketName}")
    private String bucketName;

    @Value("${storage.bucketPassword}")
    private String bucketPassword;
    
    @Autowired
    private Cluster couchbaseCluster;

    @Bean
    public Bucket couchbaseBucket() {
        return couchbaseCluster.openBucket(bucketName, bucketPassword);
    }
}

