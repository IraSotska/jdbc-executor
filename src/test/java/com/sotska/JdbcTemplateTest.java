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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JdbcTemplateTest {

    public static final String TEST_STRING = "testString";
    public static final double DOUBLE = 1.5D;

    @Test
    void shouldExecuteSelectQuery() throws SQLException {
        var dataSource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var preparedStatement = mock(PreparedStatement.class);
        var resultSet = mock(ResultSet.class);
        var mapper = mock(Mapper.class);
        var query = "SELECT * FROM USERS;";
        var expectedResult = List.of("REsult");

        var jdbcTemplate = new JdbcTemplate(dataSource);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(mapper.mapList(resultSet)).thenReturn(expectedResult);

        var result = jdbcTemplate.query(query, mapper);

        assertNotNull(result);
        assertEquals(expectedResult, result);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(query);
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(resultSet).close();
        verify(mapper).mapList(resultSet);

        verifyNoMoreInteractions(dataSource, connection, preparedStatement, resultSet, mapper);
    }

    @Test
    void shouldQueryForObject() throws SQLException {
        var dataSource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var preparedStatement = mock(PreparedStatement.class);
        var resultSet = mock(ResultSet.class);
        var mapper = mock(Mapper.class);
        var query = "SELECT * FROM USERS WHERE id = ? AND RATING = ? IS_ACTIVE = ?;";
        var expectedResult = "REsult";

        var jdbcTemplate = new JdbcTemplate(dataSource);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(mapper.map(resultSet)).thenReturn(expectedResult);

        var result = jdbcTemplate.queryForObject(query, mapper, TEST_STRING, DOUBLE, true);

        assertNotNull(result);
        assertEquals(expectedResult, result);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(query);
        verify(connection).close();
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).close();
        verify(preparedStatement).setString(1, TEST_STRING);
        verify(preparedStatement).setDouble(2, 1.5D);
        verify(preparedStatement).setBoolean(3, true);
        verify(resultSet).close();
        verify(mapper).map(resultSet);

        verifyNoMoreInteractions(dataSource, connection, preparedStatement, resultSet, mapper);
    }

    @Test
    void shouldUpdateRecord() throws SQLException {
        var dataSource = mock(DataSource.class);
        var connection = mock(Connection.class);
        var preparedStatement = mock(PreparedStatement.class);
        var mapper = mock(Mapper.class);
        var query = "SELECT * FROM USERS WHERE id = ? AND RATING = ? IS_ACTIVE = ?;";

        var jdbcTemplate = new JdbcTemplate(dataSource);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(query)).thenReturn(preparedStatement);

        jdbcTemplate.update(query, TEST_STRING, 1.5D, true);

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(query);
        verify(connection).close();
        verify(preparedStatement).execute();
        verify(preparedStatement).close();
        verify(preparedStatement).setString(1, TEST_STRING);
        verify(preparedStatement).setDouble(2, 1.5D);
        verify(preparedStatement).setBoolean(3, true);

        verifyNoMoreInteractions(dataSource, connection, preparedStatement, mapper);
    }

    @Test
    void shouldThrowExceptionIfUpdateRecordWithWrongParamCount() {
        var dataSource = mock(DataSource.class);
        var query = "SELECT * FROM USERS WHERE id = ? AND RATING = ? IS_ACTIVE = ?;";
        var jdbcTemplate = new JdbcTemplate(dataSource);

        var exception = assertThrows(JdbcException.class, () -> {
            jdbcTemplate.update(query, TEST_STRING, 1.5D);
        });

        assertEquals("Params count not correct. Expected: 3 but received 2", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfQueryForObjectWithWrongParamCount() {
        var dataSource = mock(DataSource.class);
        var mapper = mock(Mapper.class);
        var query = "SELECT * FROM USERS WHERE id = ? AND RATING = ? IS_ACTIVE = ?;";
        var jdbcTemplate = new JdbcTemplate(dataSource);

        var exception = assertThrows(JdbcException.class, () -> {
            jdbcTemplate.queryForObject(query, mapper, TEST_STRING, DOUBLE, true, "Param");
        });

        assertEquals("Params count not correct. Expected: 3 but received 4", exception.getMessage());
    }
}