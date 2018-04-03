package com.hedbanz.hedbanzAPI.repository.Implementation;

import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.repository.UserRepositoryFunctional;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class UserRepositoryFunctionalImpl implements UserRepositoryFunctional {
    @PersistenceContext
    EntityManager entityManager;

    public List<User> getFriends(){
        StringBuilder queryText = new StringBuilder("SELECT u FROM User u LEFT JOIN FETCH u.friends");
        Query query = entityManager.createQuery(queryText.toString());
        List<User> result = query.getResultList();
        return result;
    }
}
