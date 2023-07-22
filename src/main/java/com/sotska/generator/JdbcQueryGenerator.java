package com.sotska.generator;

import com.sotska.exception.JdbcException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

public class JdbcQueryGenerator implements QueryGenerator {

    @Override
    public void enrichParams(PreparedStatement preparedStatement, List<Object> params) {

        try {
            for (int index = 1; index <= params.size(); index++) {

                var param = params.get(index - 1);

                if (param instanceof Boolean) {
                    preparedStatement.setBoolean(index, parseBoolean(valueOf(param)));
                } else if (param instanceof String) {
                    preparedStatement.setString(index, valueOf(param));
                } else if (param instanceof Double) {
                    preparedStatement.setDouble(index, parseDouble(valueOf(param)));
                } else if (param instanceof Long) {
                    preparedStatement.setLong(index, parseLong(valueOf(param)));
                } else if (param instanceof Integer) {
                    preparedStatement.setInt(index, parseInt(valueOf(param)));
                } else {
                    preparedStatement.setObject(index, param);
                }
            }
        } catch (SQLException exception) {
            throw new JdbcException("Exception while setting params to query: ", exception);
        }
    }
}
