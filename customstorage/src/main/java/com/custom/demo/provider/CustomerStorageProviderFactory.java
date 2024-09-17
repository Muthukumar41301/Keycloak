package com.custom.demo.provider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.HashMap;
import java.util.Map;

public class CustomerStorageProviderFactory implements UserStorageProviderFactory<CustomerStorageProvider> {

    private EntityManagerFactory entityManagerFactory;

    @Override
    public CustomerStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        try {
            CustomerStorageProvider customerStorageProvider = new CustomerStorageProvider();
            customerStorageProvider.setModel(componentModel);
            customerStorageProvider.setSession(keycloakSession);
            customerStorageProvider.setEntityManager(createEntityManager());

            return customerStorageProvider;
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    private EntityManager createEntityManager() {
        if(entityManagerFactory == null){
            createEntityManagerFactory();
        }
        return entityManagerFactory.createEntityManager();
    }

    synchronized private void createEntityManagerFactory(){
        HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
        final Map<String, Object> props = getHibernateProperties();
        entityManagerFactory = hibernatePersistenceProvider.createEntityManagerFactory("user-store", props);
    }

    private Map<String, Object> getHibernateProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.connection.url","jdbc:postgresql://localhost:5432/keycloak");
        props.put("hibernate.connection.username","postgres");
        props.put("hibernate.connection.password","Password123");
        props.put("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect");
        return props;
    }

    @Override
    public void close() {
        closeEntityManagerFactory();
    }

    private synchronized void closeEntityManagerFactory(){
        if(entityManagerFactory != null && entityManagerFactory.isOpen()){
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    @Override
    public String getId() {
        return "Custom-user-storage";
    }
}
