package com.sotska;

import com.sotska.executor.JdbcQueryExecutor;
import com.sotska.executor.QueryExecutor;
import com.sotska.mapper.Mapper;

import javax.sql.DataSource;
import java.util.List;

public class JdbcTemplate {

    private final QueryExecutor queryExecutor;

    public JdbcTemplate(DataSource dataSource) {
        queryExecutor = new JdbcQueryExecutor(dataSource);
    }

    <T> List<T> query(String query, Mapper<T> mapper) {
        return queryExecutor.executeQuery(query, mapper);
    }

    <T> T queryForObject(String queryTemplate, Mapper<T> mapper, Object... params) {
        return queryExecutor.executeQueryForObjectWithParams(queryTemplate, mapper, params);
    }

    void update(String queryTemplate, Object... params) {
        queryExecutor.executeUpdateQueryWithParams(queryTemplate, params);
    }
}
