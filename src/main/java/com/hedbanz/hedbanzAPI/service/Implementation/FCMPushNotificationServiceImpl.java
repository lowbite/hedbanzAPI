package com.hedbanz.hedbanzAPI.service.Implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedbanz.hedbanzAPI.entity.HeaderRequestInterceptor;
import com.hedbanz.hedbanzAPI.entity.User;
import com.hedbanz.hedbanzAPI.entity.error.UserError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.repository.CRUDUserRepository;
import com.hedbanz.hedbanzAPI.service.FCMPushNotificationService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class FCMPushNotificationServiceImpl implements FCMPushNotificationService {
    @Autowired
    private CRUDUserRepository CRUDUserRepository;

    private static final String FIREBASE_SERVER_KEY = "AAAAPr_PYbs:APA91bEYs2o7Dtz_jkU_chkoFOo-vgPXESHnG5SWtSN8TJwgTKwEexq1vxpR7mbEPvDbg3T2siL7ZKFIw-8Tb1htwG84X_ZR2B3o5Glnt4WKpXY6eCkkEkwEq8VjT-uy-AGYKlk2iKge";
    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private final static String DEVICE_TOKEN = "eMjAPLuU6kk:APA91bGizcnyutmfQ1YI7jI9ZPaZzENOTQQbmu1DkMvtYvoJAZf9zNm3YwbRtuQBoLCj8griGtSRGy4VbnjcVNETHqYHpd8lAjgjT0nmaR-DsVcm-50LNirnjHCZ9AqUqlArNuPD3Lhl";

    public void sendFriendshipRequest(long userId, long friendId){
        User friend = CRUDUserRepository.findOne(friendId);
        User userDTO = CRUDUserRepository.findOne(userId);

        JSONObject body = new JSONObject();
        body.put("to", friend.getToken());

        JSONObject notification = new JSONObject();
        notification.put("title", "New friendship request");
        notification.put("body", "User " + userDTO.getLogin() + " would like to add to his friend list");

        body.put("notification", notification);

        HttpEntity<String> entity = new HttpEntity<>(body.toString());

        CompletableFuture<String> pushNotification = sendRequest(entity);
        CompletableFuture.allOf(pushNotification).join();

        try {
            String firebaseResponse = pushNotification.get();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseObj = objectMapper.readTree(firebaseResponse);
            int success = responseObj.get("success").asInt();

            if(success == 0){
                throw ExceptionFactory.create(UserError.CANT_SEND_FRIENDSHIP_REQUEST);
            }
            if(!userDTO.addFriend(friend)){
                throw ExceptionFactory.create(UserError.ALREADY_FRIENDS);
            }
            CRUDUserRepository.save(userDTO);

        } catch (InterruptedException | ExecutionException | IOException e) {
            throw ExceptionFactory.create(UserError.CANT_SEND_FRIENDSHIP_REQUEST);
        }
    }


    public void acceptFriendRequest(long userId, long friendId){
        User friend = CRUDUserRepository.findOne(friendId);
        User userDTO = CRUDUserRepository.findOne(userId);

        if(!userDTO.addFriend(friend)){
            throw ExceptionFactory.create(UserError.ALREADY_FRIENDS);
        }
        CRUDUserRepository.save(userDTO);
    }


    private CompletableFuture<String> sendRequest(HttpEntity<String> entity){
        RestTemplate restTemplate = new RestTemplate();
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);
        return CompletableFuture.completedFuture(firebaseResponse);
    }
}
