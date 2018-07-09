package com.hedbanz.hedbanzAPI.repository.Implementation;

import com.hedbanz.hedbanzAPI.constant.GameStatus;
import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.model.RoomFilter;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.RoomRepositoryFunctional;
import org.apache.http.util.TextUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class RoomRepositoryFunctionalImpl implements RoomRepositoryFunctional {
    private final static String FIND_ROOMS = "SELECT  r FROM Room r ";
    private final static String SEARCH_BY_NAME = "WHERE r.name LIKE ";
    private final static String SEARCH_BY_ID = "WHERE r.id = ";
    private final static String SEARCH_INACTIVE_GAMES = " r.gameStatus = :gameStatus ";
    private final static String QUERY_END = " r.currentPlayersNumber < r.maxPlayers ORDER BY r.id DESC ";
    private final static String FIND_ACTIVE_ROOMS = "SELECT r FROM Room r JOIN r.players p JOIN p.user u ";
    private final static String SEARCH_ACTIVE_ROOMS = " u.id = :userId";

    @PersistenceContext
    private EntityManager entityManager;

    public List<Room> findRoomsByFilter(RoomFilter roomFilter, int page, int size) {
        StringBuilder queryText = new StringBuilder();
        queryText.append(FIND_ROOMS);

        if(TextUtils.isEmpty(roomFilter.getRoomName())){
            if(roomFilter.getMaxPlayers() != null || roomFilter.isPrivate() != null){
                queryText.append("WHERE ");
                addFilterConditions(roomFilter, queryText);
                queryText.append(SEARCH_INACTIVE_GAMES);
                queryText.append("AND " + QUERY_END );
            }
        }else if(roomFilter.getRoomName().startsWith("#")){
            if(roomFilter.getRoomName().length() == 1)
                throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

            long roomId = Long.valueOf(roomFilter.getRoomName().substring(1, roomFilter.getRoomName().length()));
            queryText.append(SEARCH_BY_ID + roomId  + " AND ");
            addFilterConditions(roomFilter, queryText);
            queryText.append(SEARCH_INACTIVE_GAMES);
            queryText.append("AND " + QUERY_END );
        }else{
            queryText.append(SEARCH_BY_NAME + "'%" + roomFilter.getRoomName() + "%' AND ");
            addFilterConditions(roomFilter, queryText);
            queryText.append(SEARCH_INACTIVE_GAMES);
            queryText.append("AND " + QUERY_END );
        }

        Query query = entityManager.createQuery(queryText.toString()).setParameter("gameStatus", GameStatus.WAITING_FOR_PLAYERS);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public List<Room> findActiveRoomsByFilter(RoomFilter roomFilter, long userId) {
        StringBuilder queryText = new StringBuilder();
        queryText.append(FIND_ACTIVE_ROOMS);

        if(TextUtils.isEmpty(roomFilter.getRoomName())){
            if(roomFilter.getMaxPlayers() != null || roomFilter.isPrivate() != null){
                queryText.append("WHERE ");
                addFilterConditions(roomFilter, queryText);
                queryText.append(SEARCH_ACTIVE_ROOMS);
            }
        }else if(roomFilter.getRoomName().charAt(0) == '#'){
            if(roomFilter.getRoomName().length() == 1)
                throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

            long roomId = Long.valueOf(roomFilter.getRoomName().substring(1, roomFilter.getRoomName().length()));
            queryText.append(SEARCH_BY_ID + roomId  + " AND ");
            addFilterConditions(roomFilter, queryText);
            queryText.append(SEARCH_ACTIVE_ROOMS);
        }else{
            queryText.append(SEARCH_BY_NAME + "'%" + roomFilter.getRoomName() + "%' AND ");
            addFilterConditions(roomFilter, queryText);
            queryText.append(SEARCH_ACTIVE_ROOMS);
        }

        Query query = entityManager.createQuery(queryText.toString()).setParameter("userId", userId);
        return query.getResultList();
    }

    private void addFilterConditions(RoomFilter roomFilter, StringBuilder queryText){
        if(roomFilter.getMaxPlayers() != null)
            queryText.append("(r.maxPlayers BETWEEN " + roomFilter.getMinPlayers() + " AND " + roomFilter.getMaxPlayers() + ") AND ");

        if(roomFilter.isPrivate() != null) {
            if (!roomFilter.isPrivate())
                queryText.append(" r.password IS NULL AND ");
            else if (roomFilter.isPrivate())
                queryText.append(" r.password IS NOT NULL AND ");
        }

    }
}
