package com.stsl.notificationservice.consumer;


import com.stsl.notificationservice.response.NewNotificationRequest;
import com.stsl.notificationservice.service.NotificationService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer extends RouteBuilder {

    private final NotificationService notificationService;

    public KafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void configure() throws Exception {

        from("kafka:notificationData")
                .log("${body}")
                .unmarshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .bean(notificationService, "createNewNotification")
                .to("log:received-msg-from-kafka");

        from("kafka:ClockOutNotificationData")
                .log("${body}")
                .unmarshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .bean(notificationService, "createNewNotification")
                .to("log:received-msg-from-kafka");
    }


}
