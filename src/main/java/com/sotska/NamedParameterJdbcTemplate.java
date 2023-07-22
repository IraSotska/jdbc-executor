package com.sotska;

import com.sotska.exception.JdbcException;
import com.sotska.executor.JdbcQueryExecutor;
import com.sotska.executor.QueryExecutor;
import com.sotska.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class NamedParameterJdbcTemplate {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PARAM_START_DETERMINER = ":";
    private static final Pattern FOUND_PARAM_PATTERN = Pattern.compile(PARAM_START_DETERMINER + "(\\w+)");
    private final QueryExecutor queryExecutor;

    public NamedParameterJdbcTemplate(DataSource dataSource) {
        queryExecutor = new JdbcQueryExecutor(dataSource);
    }

    <T> T queryForObject(String queryTemplate, Mapper<T> mapper, Map<String, Object> params) {
        log.debug("Requested to query for object: " + queryTemplate + " with params: " + params);

        var paramNames = extractParamNamesFromTemplate(queryTemplate);
        for (var name : paramNames) {
            queryTemplate = queryTemplate.replace(name, "?");
        }

        var sortedParams = paramNames.stream()
                .map(param -> param.substring(PARAM_START_DETERMINER.length()))
                .map(param -> {
                    if (params.containsKey(param)) {
                        return params.get(param);
                    }
                    throw new JdbcException("All parameters needed. Parameter: " + param + " not found.");
                }).collect(toList());

        return queryExecutor.executeQueryForObjectWithParams(queryTemplate, mapper, sortedParams);
    }

    private List<String> extractParamNamesFromTemplate(String queryTemplate) {
        List<String> paramNames = new ArrayList<>();
        var matcher = FOUND_PARAM_PATTERN.matcher(queryTemplate);
        while (matcher.find()) {
            var foundedParam = matcher.group();
            paramNames.add(foundedParam);
        }
        return paramNames;
    }
}
