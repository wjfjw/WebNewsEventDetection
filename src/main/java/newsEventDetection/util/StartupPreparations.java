package newsEventDetection.util;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.couchbase.client.java.query.Index.PRIMARY_NAME;
import static com.couchbase.client.java.query.Index.createIndex;
import static com.couchbase.client.java.query.Index.createPrimaryIndex;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

@Component
public class StartupPreparations implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupPreparations.class);

    private Bucket bucket;

    @Autowired
    public StartupPreparations(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ensureIndexes();
    }


    private void ensureIndexes() throws Exception {
        LOGGER.info("Ensuring all Indexes are created.");

        N1qlQueryResult indexResult = bucket.query(
            N1qlQuery.simple(select("indexes.*").from("system:indexes").where(i("keyspace_id").eq(s(bucket.name()))))
        );

        List<String> indexesToCreate = new ArrayList<String>();
        indexesToCreate.addAll(Arrays.asList(
            "def_algorithm_parameters",
            "def_event_id", "def_event_start_time", 
            "def_topic_id", "def_topic_algorithm", "def_topic_category", 
            "def_news_time", "def_news_id", "def_news_category"
        ));

        boolean hasPrimary = false;
        List<String> foundIndexes = new ArrayList<String>();
        for (N1qlQueryRow indexRow : indexResult) {
            String name = indexRow.value().getString("name");
            Boolean isPrimary = indexRow.value().getBoolean("is_primary");
            if (name.equals(PRIMARY_NAME) || isPrimary == Boolean.TRUE) {
                hasPrimary = true;
            } else {
                foundIndexes.add(name);
            }
        }
        indexesToCreate.removeAll(foundIndexes);

        if (!hasPrimary) {
            Statement query = createPrimaryIndex().on(bucket.name()).withDefer();
            LOGGER.info("Executing index query: {}", query);
            N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
            if (result.finalSuccess()) {
                LOGGER.info("Successfully created primary index.");
            } else {
                LOGGER.warn("Could not create primary index: {}", result.errors());
            }
        }

        for (String name : indexesToCreate) {
            Statement query = createIndex(name).on(bucket.name(), x(name.replace("def_", ""))).withDefer();
            LOGGER.info("Executing index query: {}", query);
            N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
            if (result.finalSuccess()) {
                LOGGER.info("Successfully created index with name {}.", name);
            } else {
                LOGGER.warn("Could not create index {}: {}", name, result.errors());
            }
        }


        List<String> indexesToBuild = new ArrayList<String>(indexesToCreate.size()+1);
        indexesToBuild.addAll(indexesToCreate);
        if (!hasPrimary) {
            indexesToBuild.add(PRIMARY_NAME);
        }

        if (indexesToBuild.isEmpty()) {
            LOGGER.info("All indexes are already in place, nothing to build");
            return;
        }

        LOGGER.info("Waiting 5 seconds before building the indexes.");
        Thread.sleep(5000);

        StringBuilder indexes = new StringBuilder();
        boolean first = true;
        for (String name : indexesToBuild) {
            if (first) {
                first = false;
            } else {
                indexes.append(",");
            }
            indexes.append(name);
        }

        String query = "BUILD INDEX ON `" + bucket.name() + "` (" + indexes.toString() + ")";
        LOGGER.info("Executing index query: {}", query);
        N1qlQueryResult result = bucket.query(N1qlQuery.simple(query));
        if (result.finalSuccess()) {
            LOGGER.info("Successfully executed build index query.");
        } else {
            LOGGER.warn("Could not execute build index query {}.", result.errors());
        }
    }

}
