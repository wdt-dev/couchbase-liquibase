package liquibase.ext.couchbase.types.subdoc;

import com.couchbase.client.java.kv.MutateInSpec;

import java.util.List;
import java.util.function.BiFunction;

import liquibase.SingletonObject;

/**
 * Factory for {@link MutateInSpec} from xml enumeration <br> Association between xml enumeration to MutateInSpec
 */
public enum MutateInType implements SingletonObject {

    INSERT(MutateInSpec::insert),
    ARRAY_PREPEND((path, value) -> MutateInSpec.arrayPrepend(path, (List<Object>) value)),
    ARRAY_APPEND((path, value) -> MutateInSpec.arrayAppend(path, (List<Object>) value)),
    ARRAY_CREATE((path, value) -> MutateInSpec.arrayAppend(path, (List<Object>) value).createPath()),

    ARRAY_INSERT((path, value) -> MutateInSpec.arrayInsert(path, (List<Object>) value)),
    ARRAY_INSERT_UNIQUE(MutateInSpec::arrayAddUnique),
    INCREMENT((path, value) -> MutateInSpec.increment(path, (Long) value)),
    DECREMENT((path, value) -> MutateInSpec.decrement(path, (Long) value)),
    UPSERT(MutateInSpec::upsert),
    REPLACE(MutateInSpec::replace),
    REMOVE((path, value) -> MutateInSpec.remove(path));

    private final BiFunction<String, Object, MutateInSpec> factoryMethod;

    MutateInType(BiFunction<String, Object, MutateInSpec> consumer) {
        this.factoryMethod = consumer;
    }

    public MutateInSpec toMutateInSpec(String path, Object value) {
        return factoryMethod.apply(path, value);
    }



}
