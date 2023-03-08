package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.google.common.collect.ImmutableMap;
import liquibase.Scope;
import liquibase.ext.couchbase.mapper.DocFileMapper;
import liquibase.ext.couchbase.mapper.LinesMapper;
import liquibase.ext.couchbase.mapper.ListMapper;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.provider.factory.DocumentKeyProviderFactory;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.ImportType;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A base class for import from file statements, generalize the logic of importing
 */

@Getter
public abstract class CouchbaseFromFileStatement extends CouchbaseTransactionStatement {

    private final Map<ImportType, DocFileMapper> importTypeToProcessor =
            ImmutableMap.of(ImportType.LINES, new LinesMapper()
                    , ImportType.LIST, new ListMapper());

    protected void importFromFileWith(TransactionAttemptContext transaction,
                                      File file,
                                      ClusterOperator clusterOperator,
                                      BiConsumer<TransactionAttemptContext, Map<String, Object>> importer) {
        Optional.of(file.getImportType())
                .map(importTypeToProcessor::get)
                .map(mapper -> mapper.map(file))
                .map(clusterOperator::checkDocsAndTransformToObjects)
                .ifPresent(docs -> importer.accept(transaction, docs));
    }
}
