package carservice.dao;

import carservice.common.Period;
import carservice.exceptions.DataAccessException;
import carservice.models.master.FilterParams;
import carservice.models.master.SortParams;
import di.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MasterDAOImpl extends AbstractDAOImpl<MasterDTO> implements MasterDAO {
    @Inject
    public MasterDAOImpl(Connection connection) {
        super(connection, DBTableName.MASTERS);
    }

    @Override
    protected String generateInsertSql() {
        return "INSERT INTO masters (firstname, lastname, status, orderAtWorkId) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParams(PreparedStatement stmt, MasterDTO entity) throws SQLException {
        stmt.setString(1, entity.firstname());
        stmt.setString(2, entity.lastname());
        stmt.setString(3, entity.status());
        stmt.setObject(4, entity.orderAtWorkId(), Types.INTEGER);
    }

    @Override
    protected MasterDTO createEntityWithId(int id, MasterDTO entity) {
        return new MasterDTO(id, entity.firstname(), entity.lastname(), entity.status(), entity.orderAtWorkId());
    }

    @Override
    public int update(MasterDTO entity) {
        String sql = "UPDATE masters SET firstname = ?, lastname = ?, status = ?, order_at_work_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entity.firstname());
            stmt.setString(2, entity.lastname());
            stmt.setString(3, entity.status());
            stmt.setObject(4, entity.orderAtWorkId(), Types.INTEGER);
            stmt.setInt(5, entity.id());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось обновить мастера с id = " + entity.id(), e);
        }
    }

    @Override
    public MasterDTO save(MasterDTO entity) {
        if (entity.id() == 0) {
            return insert(entity);
        }

        update(entity);
        return entity;
    }

    @Override
    protected MasterDTO mapRow(ResultSet rs) throws SQLException {
        return new MasterDTO(rs.getInt("id"), rs.getString("firstname"),
                rs.getString("lastname"), rs.getString("status"),
                rs.getInt("order_at_work_id"));
    }

    @Override
    public List<MasterDTO> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams) {
        List<MasterDTO> entityList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, firstname, lastname, status, order_at_work_id FROM masters WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filterParams != null) {
            if (filterParams.excludedIds() != null) {
                String placeholders = String.join(", ", Collections.nCopies(filterParams.excludedIds().size(), "?"));
                sql.append(" AND id NOT IN(" + placeholders + ")");
                params.addAll(filterParams.excludedIds());
            }
        }

        if (sortParams != null) {
            switch (sortParams.getSortCriteria()) {
                case FULLNAME -> sql.append(" ORDER BY lastname, firstname");
                case STATUS -> sql.append(" ORDER BY status");
            }
            switch (sortParams.getSortDirection()) {
                case ASC -> sql.append(" ASC");
                case DESC -> sql.append(" DESC");
            }
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entityList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось загрузить мастеров", e);
        }

        return entityList;
    }

    @Override
    public List<MasterDTO> findMastersFreeInPeriod(Period period) {
        List<MasterDTO> entityList = new ArrayList<>();
        String sql = """
            SELECT id, firstname, lastname, status, order_at_work_id
            FROM masters m
            WHERE NOT EXISTS (
                SELECT 1
                FROM orders o
                WHERE o.master_id = m.id
                    AND o.estimated_work_period_start < ?
                    AND o.estimated_work_period_end > ?
            )
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, period.getEnd());
            stmt.setObject(2, period.getStart());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entityList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось загрузить мастеров", e);
        }

        return entityList;
    }

    @Override
    public MasterDTO findByOrderId(int id) {
        String sql = """
            SELECT m.*
            FROM masters m
            JOIN orders o ON o.master_id = m.id
            WHERE o.id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось загрузить мастера по id заказа = " + id, e);
        }

        return null;
    }
}
