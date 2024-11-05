package com.swd392.mentorbooking.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.swd392.mentorbooking.dto.push_notification.NotificationMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebasePushNotificationService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public String sendNotificationByToken(NotificationMessageDTO notificationMessageDTO) {

        Notification notification = Notification.builder()
                .setTitle(notificationMessageDTO.getTitle())
                .setBody(notificationMessageDTO.getBody())
                .setImage(notificationMessageDTO.getImage())
                .build();
        Message message = Message.builder()
                .setToken(notificationMessageDTO.getRecipientToken())
                .setNotification(notification)
                .putAllData(notificationMessageDTO.getData())
                .build();
        try {
            firebaseMessaging.send(message);
            return "Send notification successfully";
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Send notification failed";
        }
    }

}
