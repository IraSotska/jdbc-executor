package com.sotska.generator;

import java.sql.PreparedStatement;
import java.util.List;

public interface QueryGenerator {

    void enrichParams(PreparedStatement preparedStatement, List<Object> params);
}
