package com.hedbanz.hedbanzAPI.repository.Implementation;

import com.hedbanz.hedbanzAPI.entity.Room;
import com.hedbanz.hedbanzAPI.transfer.RoomDto;
import com.hedbanz.hedbanzAPI.transfer.RoomFilterDto;
import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.RoomRepositoryFunctional;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class RoomRepositoryFunctionalImpl implements RoomRepositoryFunctional {
    private final static String FIND_ROOMS = "SELECT  r FROM Room r ";
    private final static String SEARCH_BY_NAME = "SELECT  r FROM Room r WHERE r.name LIKE ";
    private final static String SEARCH_BY_ID = "SELECT  r FROM Room r WHERE r.id = ";
    private final static String QUERY_END = " r.currentPlayersNumber < r.maxPlayers ORDER BY r.id DESC";

    @PersistenceContext
    private EntityManager entityManager;

    public List<Room> findRoomsByFilter(RoomFilterDto roomFilterDto, int page, int size) {
        StringBuilder queryText = new StringBuilder();

        if(roomFilterDto.getRoomName() == null || roomFilterDto.getRoomName().equals("")){
            queryText.append(FIND_ROOMS);
            if(roomFilterDto.getMaxPlayers() != null || roomFilterDto.isPrivate() != null){
                queryText.append("WHERE ");
                addFilterConditions(roomFilterDto, queryText);
            }
        }else if(roomFilterDto.getRoomName().charAt(0) == '#'){
            if(roomFilterDto.getRoomName().length() == 1)
                throw ExceptionFactory.create(RoomError.INCORRECT_INPUT);

            long roomId = Long.valueOf(roomFilterDto.getRoomName().substring(1, roomFilterDto.getRoomName().length()));
            queryText.append(SEARCH_BY_ID + roomId  + " AND ");
            addFilterConditions(roomFilterDto, queryText);
        }else{
            queryText.append(SEARCH_BY_NAME + "'%" + roomFilterDto.getRoomName() + "%' AND ");
            addFilterConditions(roomFilterDto, queryText);
        }

        Query query = entityManager.createQuery(queryText.toString());
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private void addFilterConditions(RoomFilterDto roomFilterDto, StringBuilder queryText){
        if(roomFilterDto.getMaxPlayers() != null)
            queryText.append("(r.maxPlayers BETWEEN " + roomFilterDto.getMinPlayers() + " AND " + roomFilterDto.getMaxPlayers() + ") AND ");

        if(roomFilterDto.isPrivate() != null) {
            if (roomFilterDto.isPrivate() == false)
                queryText.append(" r.password IS NULL AND ");
            else if (roomFilterDto.isPrivate() == true)
                queryText.append(" r.password IS NOT NULL AND ");
        }

        queryText.append(QUERY_END);
    }
}
