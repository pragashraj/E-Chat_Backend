package com.echat.chat.utils;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;

public class GenericIdGenerator implements IdentifierGenerator, Configurable {
    private String prefix;
    private String digits;
    private String initialId;

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
        digits = properties.getProperty("digits");
        initialId = properties.getProperty("initial_id");
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
        String query = String.format("select %s from %s",
                sharedSessionContractImplementor.getEntityPersister(o.getClass().getName(), o).getIdentifierPropertyName(),
                o.getClass().getSimpleName());

        Stream ids = sharedSessionContractImplementor.createQuery(query).stream();

        Long max = ids.map(id -> String.valueOf(id).replace(prefix, ""))
                .mapToLong(i -> Long.parseLong(String.valueOf(i)))
                .max()
                .orElse(Long.parseLong(initialId));

        String nextId = String.format("%0" + digits + "d", (max + 1));

        return prefix + nextId;
    }
}
