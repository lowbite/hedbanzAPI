package com.hedbanz.hedbanzAPI.service.Implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedbanz.hedbanzAPI.model.FcmPush;
import com.hedbanz.hedbanzAPI.interceptor.HeaderRequestInterceptor;
import com.hedbanz.hedbanzAPI.error.FcmError;
import com.hedbanz.hedbanzAPI.exception.ExceptionFactory;
import com.hedbanz.hedbanzAPI.service.FcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class FcmServiceImpl implements FcmService {
    private final Logger log = LoggerFactory.getLogger("FcmService");

    @Value("${fcm.server.key}")
    private String FIREBASE_SERVER_KEY;
    @Value("${fcm.server.url}")
    private String FIREBASE_API_URL;

    public void sendPushNotification(FcmPush push) {
        HttpEntity<FcmPush> entity = new HttpEntity<>(push);

        CompletableFuture<String> pushNotification = sendRequest(entity);
        CompletableFuture.allOf(pushNotification).join();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String firebaseResponse = pushNotification.get();
            JsonNode responseObj = objectMapper.readTree(firebaseResponse);
            int success = responseObj.get("success").asInt();

            if(success == 0){
                JsonNode resultNode = responseObj.get("results").get(0);
                log.error("FCMPush result error: " + resultNode.get("error").asText());
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw ExceptionFactory.create(FcmError.CANT_SEND_MESSAGE_NOTIFICATION);
        }
    }


    private CompletableFuture<String> sendRequest(HttpEntity<FcmPush> entity){
        RestTemplate restTemplate = new RestTemplate();
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        String fcmResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);
        return CompletableFuture.completedFuture(fcmResponse);
    }
}
