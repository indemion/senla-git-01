package carservice.dao;

import carservice.exceptions.DataAccessException;
import carservice.models.order.FilterParams;
import carservice.models.order.OrderStatus;
import carservice.models.order.SortParams;
import di.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderDAOImpl extends AbstractDAOImpl<OrderDTO> implements OrderDAO {
    @Inject
    public OrderDAOImpl(Connection connection) {
        super(connection, DBTableName.ORDERS);
    }

    @Override
    protected String generateInsertSql() {
        return """
            INSERT INTO orders (
                price,
                master_id,
                garage_spot_id,
                status,
                estimated_work_period_start,
                estimated_work_period_end,
                actual_work_period_start,
                actual_work_period_end,
                created_at,
                closed_at,
                canceled_at,
                deleted_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
    }

    @Override
    protected void setInsertParams(PreparedStatement stmt, OrderDTO entity) throws SQLException {
        stmt.setInt(1, entity.price());
        stmt.setInt(2, entity.masterId());
        stmt.setInt(3, entity.garageSpotId());
        stmt.setString(4, entity.status());
        stmt.setObject(5, entity.estimatedWorkPeriodStart());
        stmt.setObject(6, entity.estimatedWorkPeriodEnd());
        stmt.setObject(7, entity.actualWorkPeriodStart());
        stmt.setObject(8, entity.actualWorkPeriodEnd());
        stmt.setObject(9, entity.createdAt());
        stmt.setObject(10, entity.closedAt());
        stmt.setObject(11, entity.canceledAt());
        stmt.setObject(12, entity.deletedAt());
    }

    @Override
    protected OrderDTO createEntityWithId(int id, OrderDTO entity) {
        return new OrderDTO(id, entity.price(), entity.masterId(), entity.garageSpotId(), entity.status(),
                entity.estimatedWorkPeriodStart(), entity.estimatedWorkPeriodEnd(),
                entity.actualWorkPeriodStart(), entity.actualWorkPeriodEnd(), entity.createdAt(), entity.closedAt(),
                entity.canceledAt(), entity.deletedAt());
    }

    @Override
    protected OrderDTO mapRow(ResultSet rs) throws SQLException {
        return new OrderDTO(rs.getInt("id"), rs.getInt("price"),
                rs.getInt("master_id"), rs.getInt("garage_spot_id"),
                rs.getString("status"),
                parseLocalDateTime(rs.getString("estimated_work_period_start")),
                parseLocalDateTime(rs.getString("estimated_work_period_end")),
                parseLocalDateTime(rs.getString("actual_work_period_start")),
                parseLocalDateTime(rs.getString("actual_work_period_end")),
                parseLocalDateTime(rs.getString("created_at")),
                parseLocalDateTime(rs.getString("closed_at")),
                parseLocalDateTime(rs.getString("canceled_at")),
                parseLocalDateTime(rs.getString("deleted_at")));
    }

    private LocalDateTime parseLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return (dateTime == null) ? null : LocalDateTime.parse(dateTime, formatter);
    }

    @Override
    public int update(OrderDTO entity) {
        String sql = """
            UPDATE orders SET
                price = ?,
                master_id = ?,
                garage_spot_id = ?,
                status = ?,
                estimated_work_period_start = ?,
                estimated_work_period_end = ?,
                actual_work_period_start = ?,
                actual_work_period_end = ?,
                created_at = ?,
                closed_at = ?,
                canceled_at = ?,
                deleted_at = ?
            WHERE id = ?
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entity.price());
            stmt.setInt(2, entity.masterId());
            stmt.setInt(3, entity.garageSpotId());
            stmt.setString(4, entity.status());
            stmt.setObject(5, entity.estimatedWorkPeriodStart());
            stmt.setObject(6, entity.estimatedWorkPeriodEnd());
            stmt.setObject(7, entity.actualWorkPeriodStart());
            stmt.setObject(8, entity.actualWorkPeriodEnd());
            stmt.setObject(9, entity.createdAt());
            stmt.setObject(10, entity.closedAt());
            stmt.setObject(11, entity.canceledAt());
            stmt.setObject(12, entity.deletedAt());
            stmt.setInt(13, entity.id());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось обновить заказ с id = " + entity.id(), e);
        }
    }

    @Override
    public OrderDTO save(OrderDTO entity) {
        if (entity.id() == 0) {
            return insert(entity);
        }

        update(entity);
        return entity;
    }

    @Override
    public List<OrderDTO> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams) {
        List<OrderDTO> entityList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filterParams != null) {
            if (filterParams.getMasterId() != null) {
                sql.append(" AND master_id = ?");
                params.add(filterParams.getMasterId());
            }
            if (filterParams.getStatuses() != null) {
                String placeholders = String.join(", ",
                        Collections.nCopies(filterParams.getStatuses().size(), "?"));
                sql.append(" AND status IN (").append(placeholders).append(")");
                params.addAll(filterParams.getStatuses().stream().map(OrderStatus::toString).toList());
            }
            if (filterParams.getEstimatedWorkStartInPeriod() != null) {
                sql.append(" AND estimated_work_period_start BETWEEN ? AND ?");
                params.add(filterParams.getEstimatedWorkStartInPeriod().getStart());
                params.add(filterParams.getEstimatedWorkStartInPeriod().getEnd());
            }
        }

        if (sortParams != null) {
            switch (sortParams.getSortCriteria()) {
                case CREATED_AT -> sql.append(" ORDER BY created_at");
                case ACTUAL_WORK_PERIOD_END -> sql.append(" ORDER BY actual_work_period_end");
                case ESTIMATED_WORK_PERIOD_START -> sql.append(" ORDER BY estimated_work_period_start");
                case PRICE -> sql.append(" ORDER BY price");
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
            throw new DataAccessException("Не удалось загрузить заказы", e);
        }

        return entityList;
    }
}
