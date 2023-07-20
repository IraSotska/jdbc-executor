package com.sotska.generator;

import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.*;

class JdbcQueryGeneratorTest {

    private static final JdbcQueryGenerator jdbcQueryGenerator = new JdbcQueryGenerator();

    @Test
    void shouldEnrichParams() throws SQLException {
        var preparedStatement = mock(PreparedStatement.class);

        jdbcQueryGenerator.enrichParams(preparedStatement, List.of("testString", 1.1D, true, 2L, 12));

        verify(preparedStatement).setString(1, "testString");
        verify(preparedStatement).setDouble(2, 1.1D);
        verify(preparedStatement).setBoolean(3, true);
        verify(preparedStatement).setLong(4, 2L);
        verify(preparedStatement).setInt(5, 12);

        verifyNoMoreInteractions(preparedStatement);
    }
}