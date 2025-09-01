package com.github.araujoronald.infra.api.rest.springboot.config;

import com.github.araujoronald.application.ports.AttendantRepository;
import com.github.araujoronald.application.ports.CustomerRepository;
import com.github.araujoronald.application.ports.TicketRepository;
import com.github.araujoronald.infra.adapters.repositories.postgres.AttendantRepositoryPostgres;
import com.github.araujoronald.infra.adapters.repositories.postgres.CustomerRepositoryPostgres;
import com.github.araujoronald.infra.adapters.repositories.postgres.TicketRepositoryPostgres;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.sql.DataSource;
import java.util.Locale;

@Configuration
public class BeanConfiguration {

    public LocaleResolver localeResolver(){
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    @Bean
    public CustomerRepository customerRepository(DataSource dataSource) {
        // Assumindo que a implementação CustomerRepositoryPostgres existe
        return new CustomerRepositoryPostgres(dataSource);
    }

    @Bean
    public AttendantRepository attendantRepository(DataSource dataSource) {
        return new AttendantRepositoryPostgres(dataSource);
    }

    @Bean
    public TicketRepository ticketRepository(DataSource dataSource) {
        // Assumindo que a implementação TicketRepositoryPostgres existe
        return new TicketRepositoryPostgres(dataSource);
    }
}