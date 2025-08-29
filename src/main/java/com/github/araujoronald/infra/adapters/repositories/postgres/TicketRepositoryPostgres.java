package com.github.araujoronald.infra.adapters.repositories.postgres;

import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.domain.model.*;
import com.github.araujoronald.infra.adapters.repositories.postgres.config.ConnectionFactory;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketRepositoryPostgres implements TicketRepository {

    private static final Logger LOGGER = Logger.getLogger(TicketRepositoryPostgres.class.getName());
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");
    private final DataSource dataSource;

    public TicketRepositoryPostgres() {
        this.dataSource = ConnectionFactory.getDataSource();
    }

    public TicketRepositoryPostgres(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Ticket save(Ticket ticket) {
        String sql = "INSERT INTO tickets (id, status, priority, created, start, \"end\", description, cancellation_reason, customer_id, attendant_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "status = EXCLUDED.status, priority = EXCLUDED.priority, start = EXCLUDED.start, \"end\" = EXCLUDED.\"end\", " +
                     "description = EXCLUDED.description, cancellation_reason = EXCLUDED.cancellation_reason, attendant_id = EXCLUDED.attendant_id";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, ticket.getId());
            pstmt.setString(2, ticket.getStatus().name());
            pstmt.setInt(3, ticket.getPriority());
            pstmt.setTimestamp(4, new Timestamp(ticket.getCreated().getTime()));
            pstmt.setTimestamp(5, ticket.getStart() != null ? new Timestamp(ticket.getStart().getTime()) : null);
            pstmt.setTimestamp(6, ticket.getEnd() != null ? new Timestamp(ticket.getEnd().getTime()) : null);
            pstmt.setString(7, ticket.getDescription());
            pstmt.setString(8, ticket.getCancellationReason());
            pstmt.setObject(9, ticket.getCustomer().id());
            pstmt.setObject(10, ticket.getAttendant().id());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.ticket.save.error"), e);
            throw new RuntimeException(MESSAGES.getString("repository.ticket.save.error"), e);
        }
        return ticket;
    }

    @Override
    public Optional<Ticket> find(UUID id) {
        String sql = getSelectTicketQuery() + " WHERE t.id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToTicket(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.ticket.find.id.error"), e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = getSelectTicketQuery();
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) tickets.add(mapRowToTicket(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.ticket.findall.error"), e);
        }
        return tickets;
    }

    private String getSelectTicketQuery() {
        return "SELECT t.id, t.status, t.priority, t.created, t.start, t.\"end\", t.description, t.cancellation_reason, " +
               "c.id as c_id, c.name as c_name, c.email as c_email, c.phone as c_phone, c.qualifier as c_qualifier, " +
               "a.id as a_id, a.name as a_name, a.email as a_email " +
               "FROM tickets t " +
               "JOIN customers c ON t.customer_id = c.id " +
               "JOIN attendants a ON t.attendant_id = a.id";
    }

    private Ticket mapRowToTicket(ResultSet rs) throws SQLException {
        Customer customer = new Customer(rs.getObject("c_id", UUID.class), rs.getString("c_name"), rs.getString("c_email"), rs.getString("c_phone"), CustomerQualifier.valueOf(rs.getString("c_qualifier")));
        Attendant attendant = new Attendant(rs.getObject("a_id", UUID.class), rs.getString("a_name"), rs.getString("a_email"));

        try {
            // As the Ticket constructor is private, we must use reflection to instantiate it.
            // This is a workaround due to the domain model design.
            Constructor<Ticket> constructor = Ticket.class.getDeclaredConstructor(UUID.class, TicketStatus.class, Integer.class, Date.class, Customer.class, Attendant.class);
            constructor.setAccessible(true);
            Ticket ticket = constructor.newInstance(rs.getObject("id", UUID.class), TicketStatus.valueOf(rs.getString("status")), rs.getInt("priority"), new Date(rs.getTimestamp("created").getTime()), customer, attendant);

            // Set non-constructor fields via reflection as well.
            setField(ticket, "start", rs.getTimestamp("start"));
            setField(ticket, "end", rs.getTimestamp("end"));
            setField(ticket, "description", rs.getString("description"));
            setField(ticket, "cancellationReason", rs.getString("cancellation_reason"));

            return ticket;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate Ticket via reflection", e);
        }
    }

    private void setField(Ticket ticket, String fieldName, Object value) throws ReflectiveOperationException {
        if (value != null) {
            Field field = Ticket.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (value instanceof Timestamp && field.getType().equals(Date.class)) {
                field.set(ticket, new Date(((Timestamp) value).getTime()));
            } else {
                field.set(ticket, value);
            }
        }
    }
}