package com.stsl.employeeservice.publisher;

import com.stsl.employeeservice.response.NewNotificationRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.support.DefaultMessage;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //create timer endpoint

        from("direct:sendNewNotification")
                .log("${body}")
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:notificationData");


        from("direct:sendQueryResponse")
                .log("${body}")
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:notificationData")
                .unmarshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .wireTap("direct:sendTappedData");


        from("direct:sendTappedData")
                .log("${body}")
                .process(this::enrichData)
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:notificationData");

    }


    private void enrichData(Exchange exchange) {
        NewNotificationRequest request = exchange.getMessage().getBody(NewNotificationRequest.class);
        request.setAuthUserEmail("admin@stsl.com");//pick from the application to properties
        request.setWithInternal("No");
        Message message = new DefaultMessage(exchange);
        message.setBody(request);
        exchange.setMessage(message);
    }
}
