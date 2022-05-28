package com.stsl.notificationservice.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;


@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "camel_notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationEntity extends BaseEntity {

    @NotBlank
    private String authUserId;

    @NotBlank
    private String authUserName;

    @NotBlank
    private String authUserEmail;

    @NotBlank
    private String subject;

    @Lob
    @NotBlank
    private String body;

    @Builder.Default
    private boolean read = false;

    @NotNull
    private LocalDate dateCreated;

    @NotNull
    private LocalTime timeCreated;

}
