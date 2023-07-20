package com.sotska.mapper;

import java.sql.ResultSet;
import java.util.List;

public interface Mapper<T> {

    T map(ResultSet resultSet);

    List<T> mapList(ResultSet resultSet);
}
