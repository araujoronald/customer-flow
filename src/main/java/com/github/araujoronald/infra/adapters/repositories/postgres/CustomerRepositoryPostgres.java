package com.github.araujoronald.infra.adapters.repositories.postgres;

import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.domain.model.Customer;
import com.github.araujoronald.domain.model.CustomerQualifier;
import com.github.araujoronald.infra.adapters.repositories.postgres.config.ConnectionFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerRepositoryPostgres implements CustomerRepository {

    private static final Logger LOGGER = Logger.getLogger(CustomerRepositoryPostgres.class.getName());
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");
    private final DataSource dataSource;

    public CustomerRepositoryPostgres() {
        this.dataSource = ConnectionFactory.getDataSource();
    }

    public CustomerRepositoryPostgres(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Customer save(Customer customer) {
        String sql = "INSERT INTO customers (id, name, email, phone, qualifier) VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, email = EXCLUDED.email, phone = EXCLUDED.phone, qualifier = EXCLUDED.qualifier";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, customer.id());
            pstmt.setString(2, customer.name());
            pstmt.setString(3, customer.email());
            pstmt.setString(4, customer.phone());
            pstmt.setString(5, customer.qualifier().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.customer.save.error"), e);
            throw new RuntimeException(MESSAGES.getString("repository.customer.save.error"), e);
        }
        return customer;
    }

    @Override
    public Optional<Customer> find(UUID id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.customer.find.id.error"), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.customer.find.email.error"), e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) customers.add(mapRowToCustomer(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, MESSAGES.getString("repository.customer.findall.error"), e);
        }
        return customers;
    }

    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                CustomerQualifier.valueOf(rs.getString("qualifier"))
        );
    }
}