package com.github.araujoronald.infra.adapters.repositories.postgres;

import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.domain.model.Attendant;
import com.github.araujoronald.infra.adapters.repositories.postgres.config.ConnectionFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendantRepositoryPostgres implements AttendantRepository {

    private static final Logger LOGGER = Logger.getLogger(AttendantRepositoryPostgres.class.getName());
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");
    private final DataSource dataSource;

    public AttendantRepositoryPostgres(){
        this.dataSource = ConnectionFactory.getDataSource();
    }

    public AttendantRepositoryPostgres(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Attendant save(Attendant attendant) {
        String sql = "INSERT INTO attendants (id, name, email) VALUES (?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, email = EXCLUDED.email";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, attendant.id());
            pstmt.setString(2, attendant.name());
            pstmt.setString(3, attendant.email());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.attendant.save.error"), e);
            throw new RuntimeException(MESSAGES.getString("repository.attendant.save.error"), e);
        }
        return attendant;
    }

    @Override
    public Optional<Attendant> find(UUID id) {
        String sql = "SELECT id, name, email FROM attendants WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAttendant(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.attendant.find.id.error"), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Attendant> findByEmail(String email) {
        String sql = "SELECT id, name, email FROM attendants WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToAttendant(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.attendant.find.email.error"), e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Attendant> findAll() {
        List<Attendant> attendants = new ArrayList<>();
        String sql = "SELECT id, name, email FROM attendants";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                attendants.add(mapRowToAttendant(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.attendant.findall.error"), e);
        }
        return attendants;
    }

    private Attendant mapRowToAttendant(ResultSet rs) throws SQLException {
        return new Attendant(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("email")
        );
    }
}