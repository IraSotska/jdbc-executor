package com.sotska.mapper;

import com.sotska.exception.JdbcException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Mapper<T> {

    T map(ResultSet resultSet);

    default List<T> mapList(ResultSet resultSet) {
        var result = new ArrayList<T>();
        while (true) {
            try {
                if (!resultSet.next()) break;
                result.add(map(resultSet));
            } catch (SQLException exception) {
                throw new JdbcException("", exception);
            }
        }
        return result;
    }
}
