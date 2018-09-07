package com.hedbanz.hedbanzAPI.model;

import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.entity.Player;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.error.InputError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import org.apache.http.util.TextUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class AdminRoomFilterSpecification implements Specification<Room> {
    private RoomFilter filter;

    public AdminRoomFilterSpecification(RoomFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Room> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (!TextUtils.isEmpty(filter.getRoomName())) {
            if (filter.getRoomName().startsWith("#")) {
                if (filter.getRoomName().length() == 1)
                    throw ExceptionFactory.create(InputError.INCORRECT_FILTER);

                long roomId = Long.valueOf(filter.getRoomName().substring(1, filter.getRoomName().length()));
                predicates.add(cb.equal(root.get("id"), roomId));
            } else {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getRoomName().toLowerCase() + "%"));
            }
        }

        if(filter.getMaxPlayers() != null){
            if(filter.getMaxPlayers() > 8 || filter.getMaxPlayers() < 0)
                throw ExceptionFactory.create(InputError.INCORRECT_FILTER);

            predicates.add(cb.lessThanOrEqualTo(root.get("maxPlayers"), filter.getMaxPlayers()));
        }

        if(filter.getMinPlayers() != null){
            if(filter.getMaxPlayers() > 8 || filter.getMaxPlayers() < 0)
                throw ExceptionFactory.create(InputError.INCORRECT_FILTER);

            predicates.add(cb.greaterThanOrEqualTo(root.get("maxPlayers"), filter.getMinPlayers()));
        }

        if(filter.isPrivate() != null) {
            if (!filter.isPrivate())
                predicates.add(cb.isNotNull(root.get("isPrivate")));
            else if (filter.isPrivate())
                predicates.add(cb.isNull(root.get("isPrivate")));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
