package com.stsl.attendanceservice.publisher;


import com.stsl.attendanceservice.response.BaseResponse;
import com.stsl.attendanceservice.response.NewNotificationRequest;
import com.stsl.attendanceservice.response.NewQueryRequest;
import com.stsl.attendanceservice.service.AttendanceService;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class KafkaPublisher extends RouteBuilder {

    private final AttendanceService attendanceService;

    public KafkaPublisher(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @Override
    public void configure() throws Exception {

        JacksonDataFormat jacksonDataFormat = new JacksonDataFormat();
        jacksonDataFormat.setUnmarshalType(BaseResponse.class);
        from("direct:sendTimeInData")
                .log("${body}")
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:notificationData");

        from("direct:sendTimeOutData")
                .log("${body}")
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:notificationData")
                .unmarshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .wireTap("direct:sendTappedData");


        from("direct:sendTappedData")
                .log("${body}")
                .bean(attendanceService, "checkEmployeeClockOutTime")
                .process(this::enrichData)
                .marshal().json(JsonLibrary.Jackson, NewNotificationRequest.class)
                .to("kafka:ClockOutNotificationData");


        restConfiguration()
                .component("servlet")
                .host("localhost")
                .port(8282)
                .bindingMode(RestBindingMode.auto)
                .dataFormatProperty("prettyPrint", "true");

        from("direct:sendQueryData")
//                .process(exchange -> {
//                    String token = (String) exchange.getProperty("token");
//                    exchange.getIn().setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
//                })
                .log("${body}")
                .marshal().json(JsonLibrary.Jackson, NewQueryRequest.class)
                .to("rest:post:/api/employee/query/send");



    }

    private void enrichData(Exchange exchange) {
        NewNotificationRequest request = exchange.getMessage().getBody(NewNotificationRequest.class);
        request.setAuthUserEmail("admin@stsl.com");//pick from the application to properties
        request.setWithInternal("No");
        request.setSubject("Employee Clock Out - WireTapped Attendance");
        Message message = new DefaultMessage(exchange);
        message.setBody(request);
        exchange.setMessage(message);
    }

}
