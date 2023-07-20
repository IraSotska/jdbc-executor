package com.sotska.executor;

import com.sotska.mapper.Mapper;

import java.util.List;

public interface QueryExecutor {
    <T> List<T> executeQuery(String query, Mapper<T> mapper);

    <T> T executeQueryForObjectWithParams(String queryTemplate, Mapper<T> mapper, List<Object> params);

    <T> T executeQueryForObjectWithParams(String queryTemplate, Mapper<T> mapper, Object... params);

    void executeUpdateQueryWithParams(String queryTemplate, Object... params);
}
