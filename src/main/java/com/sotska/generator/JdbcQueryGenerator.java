package com.sotska.generator;

import com.sotska.exception.JdbcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JdbcQueryGenerator implements QueryGenerator {

    @Override
    public void enrichParams(PreparedStatement preparedStatement, List<Object> params) {

        try {
            for (int paramIndex = 1; paramIndex <= params.size(); paramIndex++) {

                var param = params.get(paramIndex - 1);

                if (param instanceof Boolean) {
                    preparedStatement.setBoolean(paramIndex, Boolean.parseBoolean(param.toString()));
                } else if (param instanceof String) {
                    preparedStatement.setString(paramIndex, param.toString());
                } else if (param instanceof Double) {
                    preparedStatement.setDouble(paramIndex, Double.parseDouble(param.toString()));
                } else if (param instanceof Long) {
                    preparedStatement.setLong(paramIndex, Long.parseLong(param.toString()));
                } else if (param instanceof Integer) {
                    preparedStatement.setInt(paramIndex, Integer.parseInt(param.toString()));
                } else {
                    preparedStatement.setObject(paramIndex, param);
                }
            }
        } catch (SQLException exception) {
            throw new JdbcException("Exception while setting params to query: ", exception);
        }
    }
}
