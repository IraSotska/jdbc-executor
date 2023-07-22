package com.sotska;

import com.sotska.executor.JdbcQueryExecutor;
import com.sotska.executor.QueryExecutor;
import com.sotska.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

public class JdbcTemplate {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final QueryExecutor queryExecutor;

    public JdbcTemplate(DataSource dataSource) {
        queryExecutor = new JdbcQueryExecutor(dataSource);
    }

    <T> List<T> query(String query, Mapper<T> mapper) {
        log.debug("Requested to query: " + query);
        return queryExecutor.executeQuery(query, mapper);
    }

    <T> T queryForObject(String queryTemplate, Mapper<T> mapper, Object... params) {
        log.debug("Requested to query for object: " + queryTemplate + " with params: " + Arrays.toString(params));
        return queryExecutor.executeQueryForObjectWithParams(queryTemplate, mapper, params);
    }

    void update(String queryTemplate, Object... params) {
        log.debug("Requested to update query: " + queryTemplate + " with params: " + Arrays.toString(params));
        queryExecutor.executeUpdateQueryWithParams(queryTemplate, params);
    }
}
