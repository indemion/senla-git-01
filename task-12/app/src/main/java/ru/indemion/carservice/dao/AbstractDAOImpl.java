package ru.indemion.carservice.dao;

import ru.indemion.carservice.exceptions.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDAOImpl<T> implements DAO<T> {
    protected Connection connection;
    protected DBTableName tableName;

    public AbstractDAOImpl(Connection connection, DBTableName tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    @Override
    public T findById(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось загрузить по id = " + id + " из таблицы: " + tableName, e);
        }

        return null;
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось загрузить записи из таблицы: " + tableName, e);
        }
        return result;
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось удалить запись по id = " + id + " из таблицы: " + tableName, e);
        }
    }

    @Override
    public T insert(T entity) {
        String sql = generateInsertSql();
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParams(stmt, entity);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("Не удалось сохранить сущность в таблицу: " + tableName);
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return createEntityWithId(rs.getInt(1), entity);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось сохранить сущность в таблицу: " + tableName, e);
        }

        return null;
    }

    protected abstract String generateInsertSql();

    protected abstract void setInsertParams(PreparedStatement stmt, T entity) throws SQLException;

    protected abstract T createEntityWithId(int id, T entity);

    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
