package com.sotska;

import com.sotska.exception.JdbcException;
import com.sotska.mapper.Mapper;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NamedParameterJdbcTemplateTest {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void shouldQueryForObject() throws SQLException {
        var query = "SELECT * FROM USERS WHERE id = :id AND RATING = :rating IS_ACTIVE = :isActive AND NAME = :name;";
        var readyQuery = "SELECT * FROM USERS WHERE id = ? AND RATING = ? IS_ACTIVE = ? AND NAME = ?;";
        var expectedResult = List.of("REsult");
        var name = "Vicktor";
        var isActive = true;
        var id = 1358L;
        var rating = 4.6D;

        var dataSource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var resultSet = mock(ResultSet.class);
        var preparedStatement = mock(PreparedStatement.class);
        var mapper = mock(Mapper.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(readyQuery)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(mapper.map(resultSet)).thenReturn(expectedResult);

        Map<String, Object> params =
                Map.of("name", name, "isActive", isActive, "id", id, "rating", rating);

        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        var result = namedParameterJdbcTemplate.queryForObject(query, mapper, params);

        assertNotNull(result);
        assertEquals(expectedResult, result);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(readyQuery);
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setLong(1, id);
        verify(preparedStatement).setDouble(2, rating);
        verify(preparedStatement).setBoolean(3, isActive);
        verify(preparedStatement).setString(4, name);
        verify(preparedStatement).close();
        verify(resultSet).close();
        verify(mapper).map(resultSet);

        verifyNoMoreInteractions(dataSource, connection, preparedStatement, resultSet, mapper);
    }

    @Test
    void shouldThrowExceptionIfUpdateRecordWithWrongParamCount() {
        var query = "SELECT * FROM USERS WHERE id = :id AND RATING = :rating IS_ACTIVE = :isActive AND NAME = :name;";
        Map<String, Object> params = Map.of("name", "Vicktor", "isActive", true, "rating", 4.6D);

        var dataSource = mock(DataSource.class);
        var mapper = mock(Mapper.class);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        var exception = assertThrows(JdbcException.class, () -> {
            namedParameterJdbcTemplate.queryForObject(query, mapper, params);
        });

        assertEquals("All parameters needed. Parameter: id not found.", exception.getMessage());
    }
}