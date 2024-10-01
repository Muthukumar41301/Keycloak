package com.portal.customstorage.provider;

import com.portal.customstorage.model.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class CustomerStorageProvider implements UserStorageProvider, UserRegistrationProvider, UserQueryProvider, UserLookupProvider {

    private ComponentModel componentModel;

    private KeycloakSession keycloakSession;

    @Setter
    private EntityManager entityManager;

    public void setModel(ComponentModel componentModel) {
        this.componentModel = componentModel;
    }

    public void setSession(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public void close() {

    }

   /* @Override
    public Stream<UserModel> getUsersStream(RealmModel realmModel, Integer firstResult, Integer maxResults) {
        TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c", Customer.class);

        if (firstResult != null && maxResults != null) {
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
        }

        Stream<Customer> customerStream = query.getResultStream();
        return customerStream.map(customer -> new CustomerAdapter(keycloakSession, realmModel, componentModel, customer));
    }*/


    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realmModel, Map<String, String> map, Integer integer, Integer integer1) {
        String searchParam = map.getOrDefault("keycloak.session.realm.users.query.search",
                map.getOrDefault("email",map.getOrDefault("username","")));


        final TypedQuery<Customer> query = entityManager.createQuery("select c from Customer c where c.email like '%"+searchParam+"%'", Customer.class);

        Stream<Customer> customerStream = query.getResultStream();
        return customerStream.map(customer -> new CustomerAdapter(keycloakSession,realmModel, componentModel,customer));
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realmModel, GroupModel groupModel, Integer integer, Integer integer1) {
        return null;
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realmModel, String s, String s1) {
        return null;
    }

    @Override
    public UserModel addUser(RealmModel realmModel, String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (getUserByUsername(realmModel, userName) != null) {
            throw new IllegalArgumentException("User already exists: " + userName);
        }
        Customer newCustomer = new Customer();
        newCustomer.setUserName(userName);
        newCustomer.setEmail("default@example.com");
        newCustomer.setActive(1);
         try {
            entityManager.getTransaction().begin();
            entityManager.persist(newCustomer);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to add user: " + userName, e);
        }
         return new CustomerAdapter(keycloakSession, realmModel, componentModel, newCustomer);
    }

    @Override
    public boolean removeUser(RealmModel realmModel, UserModel userModel) {
        long persistenceId = Long.parseLong(StorageId.externalId(userModel.getId()));
        Customer customer = entityManager.find(Customer.class, persistenceId);
        if (customer != null) {
            entityManager.remove(customer);
            return true;
        }
        return false;
    }

    @Override
    public UserModel getUserById(RealmModel realmModel, String id) {

        long persistenceId = Long.parseLong(StorageId.externalId(id));
        final TypedQuery<Customer> query = entityManager.createQuery("select c from Customer c where c.id = "+persistenceId, Customer.class);

        List<Customer> customerList = query.getResultList();
        if(!customerList.isEmpty()){
            return new CustomerAdapter(keycloakSession,realmModel, componentModel,customerList.get(0));
        }
        return null;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realmModel, String userName) {
        final TypedQuery<Customer> query = entityManager.createQuery("select c from Customer c where c.userName ='"+userName+"'", Customer.class);

        List<Customer> customerList = query.getResultList();

        if(!customerList.isEmpty()){
            return new CustomerAdapter(keycloakSession,realmModel, componentModel,customerList.get(0));
        }
        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realmModel, String email) {
        final TypedQuery<Customer> query = entityManager.createQuery("select c from Customer c where c.email ='"+email+"'", Customer.class);

        List<Customer> customerList = query.getResultList();
        if(!customerList.isEmpty()){
            return new CustomerAdapter(keycloakSession,realmModel, componentModel,customerList.get(0));
        }
        return null;
    }
}
