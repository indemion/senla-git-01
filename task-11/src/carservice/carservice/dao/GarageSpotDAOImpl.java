package carservice.dao;

import carservice.common.Period;
import carservice.exceptions.DataAccessException;
import carservice.models.garage.GarageSpotStatus;
import di.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GarageSpotDAOImpl extends AbstractDAOImpl<GarageSpotDTO> implements GarageSpotDAO {
    @Inject
    public GarageSpotDAOImpl(Connection connection) {
        super(connection, DBTableName.GARAGE_SPOTS);
    }

    @Override
    protected String generateInsertSql() {
        return "INSERT INTO garage_spots (number, status, order_at_work_id) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParams(PreparedStatement stmt, GarageSpotDTO entity) throws SQLException {
        stmt.setInt(1, entity.number());
        stmt.setString(2, entity.status());
        stmt.setObject(3, entity.orderAtWorkId(), Types.INTEGER);
    }

    @Override
    protected GarageSpotDTO createEntityWithId(int id, GarageSpotDTO entity) {
        return new GarageSpotDTO(id, entity.number(), entity.status(), entity.orderAtWorkId());
    }

    @Override
    protected GarageSpotDTO mapRow(ResultSet rs) throws SQLException {
        return new GarageSpotDTO(rs.getInt("id"), rs.getInt("number"),
                rs.getString("status"), rs.getInt("order_at_work_id"));
    }

    @Override
    public int update(GarageSpotDTO entity) {
        String sql = "UPDATE garage_spots SET number = ?, status = ?, order_at_work_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entity.number());
            stmt.setString(2, entity.status());
            stmt.setObject(3, entity.orderAtWorkId(), Types.INTEGER);
            stmt.setInt(4, entity.id());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось обновить гаражное место с id = " + entity.id(), e);
        }
    }

    @Override
    public GarageSpotDTO save(GarageSpotDTO entity) {
        if (entity.id() == 0) {
            return insert(entity);
        }

        update(entity);
        return entity;
    }

    @Override
    public GarageSpotDTO findByNumber(int number) {
        String sql = "SELECT * FROM garage_spots WHERE number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, number);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось получить гаражное место по номеру = " + number, e);
        }

        return null;
    }

    @Override
    public List<GarageSpotDTO> findFreeGarageSpotsInPeriod(Period period) {
        List<GarageSpotDTO> entityList = new ArrayList<>();
        String sql = """
            SELECT id, number, status, order_at_work_id
            FROM garage_spots gs
            WHERE NOT EXISTS (
                SELECT 1
                FROM orders o
                WHERE o.garage_spot_id = gs.id
                    AND o.estimated_work_period_start <= ?
                    AND o.estimated_work_period_end >= ?
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
            throw new DataAccessException("Не удалось загрузить гаражные места", e);
        }

        return entityList;
    }

    @Override
    public List<GarageSpotDTO> findFilteredByStatus(GarageSpotStatus status) {
        List<GarageSpotDTO> entityList = new ArrayList<>();
        String sql = """
            SELECT id, number, status, order_at_work_id
            FROM garage_spots gs
            WHERE status = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entityList.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не удалось загрузить гаражные места", e);
        }

        return entityList;
    }
}
