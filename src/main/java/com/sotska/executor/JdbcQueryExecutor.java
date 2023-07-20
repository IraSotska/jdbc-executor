package com.sotska.executor;

import com.sotska.exception.JdbcException;
import com.sotska.generator.JdbcQueryGenerator;
import com.sotska.generator.QueryGenerator;
import com.sotska.mapper.Mapper;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class JdbcQueryExecutor implements QueryExecutor {

    private static final QueryGenerator queryGenerator = new JdbcQueryGenerator();
    public static final String EXCEPTION_EXECUTING_QUERY = "Exception while executing query: ";
    private final DataSource dataSource;

    public JdbcQueryExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> executeQuery(String query, Mapper<T> mapper) {
        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(query);
             var resultSet = preparedStatement.executeQuery()) {

            return mapper.mapList(resultSet);
        } catch (SQLException e) {
            throw new JdbcException(EXCEPTION_EXECUTING_QUERY + query, e);
        }
    }

    public <T> T executeQueryForObjectWithParams(String queryTemplate, Mapper<T> mapper, Object... params) {
        return executeQueryForObjectWithParams(queryTemplate, mapper, stream(params).collect(toList()));
    }

    public <T> T executeQueryForObjectWithParams(String queryTemplate, Mapper<T> mapper, List<Object> params) {
        checkParamsCount(queryTemplate, params);

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(queryTemplate)) {

            queryGenerator.enrichParams(preparedStatement, params);

            try (var resultSet = preparedStatement.executeQuery()) {
                return mapper.map(resultSet);
            }
        } catch (SQLException e) {
            throw new JdbcException(EXCEPTION_EXECUTING_QUERY + queryTemplate, e);
        }
    }

    public void executeUpdateQueryWithParams(String queryTemplate, Object... params) {
        var listParams = stream(params).toList();
        checkParamsCount(queryTemplate, listParams);

        try (var connection = dataSource.getConnection();
             var preparedStatement = connection.prepareStatement(queryTemplate)) {

            queryGenerator.enrichParams(preparedStatement, listParams);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new JdbcException(EXCEPTION_EXECUTING_QUERY + queryTemplate, e);
        }
    }

    private void checkParamsCount(String queryTemplate, List<Object> params) {
        var count = queryTemplate.chars().filter(ch -> ch == '?').count();

        if (params.size() != count) {
            throw new JdbcException("Params count not correct. Expected: " + count + " but received " + params.size());
        }
    }
}
