package com.stsl.authservice.publisher;

import com.stsl.authservice.response.NewNotificationRequest;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:sendRegistrationData")
                .log("${body}")
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:notificationData");
    }
}
