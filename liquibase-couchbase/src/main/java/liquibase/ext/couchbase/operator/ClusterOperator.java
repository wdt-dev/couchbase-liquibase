package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.ext.couchbase.types.Field;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class ClusterOperator {

    private final Cluster cluster;

    public BucketOperator getBucketOperator(String bucket) {
        return new BucketOperator(
                cluster.bucket(bucket)
        );
    }

    public void createPrimaryIndex(String bucket, CreatePrimaryQueryIndexOptions options) {
        cluster.queryIndexes().createPrimaryIndex(bucket, options);
    }

    public void createQueryIndex(String indexName, String bucket, List<Field> fields, CreateQueryIndexOptions options) {
        List<String> fieldList = fields.stream()
                .map(Field::getField)
                .collect(toList());
        cluster.queryIndexes().createIndex(bucket, indexName, fieldList, options);
    }

}
