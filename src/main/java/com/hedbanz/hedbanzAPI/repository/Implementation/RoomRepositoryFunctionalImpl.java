package com.hedbanz.hedbanzAPI.repository.Implementation;

import com.hedbanz.hedbanzAPI.entity.error.CustomError;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO;
import com.hedbanz.hedbanzAPI.entity.DTO.RoomFilterDTO;
import com.hedbanz.hedbanzAPI.entity.error.RoomError;
import com.hedbanz.hedbanzAPI.exception.RoomException;
import com.hedbanz.hedbanzAPI.repository.RoomRepositoryFunctional;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class RoomRepositoryFunctionalImpl implements RoomRepositoryFunctional {
    private final static String FIND_ROOMS = "SELECT new com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO(r.id, r.name, r.maxPlayers, r.currentPlayersNumber, r.isPrivate) FROM Room r ";
    private final static String SEARCH_BY_NAME = "SELECT new com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO(r.id, r.name, r.maxPlayers, r.currentPlayersNumber, r.isPrivate) FROM Room r WHERE r.name LIKE ";
    private final static String SEARCH_BY_ID = "SELECT new com.hedbanz.hedbanzAPI.entity.DTO.RoomDTO(r.id, r.name, r.maxPlayers, r.currentPlayersNumber, r.isPrivate) FROM Room r WHERE r.id = ";
    private final static String QUERY_END = " r.currentPlayersNumber < r.maxPlayers ORDER BY r.id DESC";

    @PersistenceContext
    private EntityManager entityManager;

    public List<RoomDTO> findRoomsByFilter(RoomFilterDTO roomFilterDTO, int page, int size) {
        StringBuilder queryText = new StringBuilder();

        if(roomFilterDTO.getRoomName() == null || roomFilterDTO.getRoomName().equals("")){
            queryText.append(FIND_ROOMS);
            if(roomFilterDTO.getMaxPlayers() != null || roomFilterDTO.isPrivate() != null){
                queryText.append("WHERE ");
                addFilterConditions(roomFilterDTO, queryText);
            }
        }else if(roomFilterDTO.getRoomName().charAt(0) == '#'){
            if(roomFilterDTO.getRoomName().length() == 1)
                throw new RoomException(new CustomError(RoomError.INCORRECT_INPUT.getErrorCode(), RoomError.INCORRECT_INPUT.getErrorMessage()));

            long roomId = Long.valueOf(roomFilterDTO.getRoomName().substring(1, roomFilterDTO.getRoomName().length()));
            queryText.append(SEARCH_BY_ID + roomId  + " AND ");
            addFilterConditions(roomFilterDTO, queryText);
        }else{
            queryText.append(SEARCH_BY_NAME + "'%" + roomFilterDTO.getRoomName() + "%' AND ");
            addFilterConditions(roomFilterDTO, queryText);
        }

        Query query = entityManager.createQuery(queryText.toString());
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private void addFilterConditions(RoomFilterDTO roomFilterDTO, StringBuilder queryText){
        if(roomFilterDTO.getMaxPlayers() != null)
            queryText.append("(r.maxPlayers BETWEEN " + roomFilterDTO.getMinPlayers() + " AND " + roomFilterDTO.getMaxPlayers() + ") AND ");

        if(roomFilterDTO.isPrivate() != null) {
            if (roomFilterDTO.isPrivate() == false)
                queryText.append(" r.password IS NULL AND ");
            else if (roomFilterDTO.isPrivate() == true)
                queryText.append(" r.password IS NOT NULL AND ");
        }

        queryText.append(QUERY_END);
    }
}
