package com.stsl.employeeservice.entity;

import com.stsl.employeeservice.entity.enums.Status;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "camel_query")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryEntity extends BaseEntity {

    @NotBlank
    private String authUserId;

    @NotBlank
    private String authUserName;

    @NotBlank
    private String employeeId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @NotBlank
    private String reason;

    @NotBlank
    private String description;

    @NotBlank
    private String response;

    @NotNull
    private LocalDate dateCreated;

    @NotNull
    private LocalTime timeCreated;

    @NotNull
    private LocalDate dateUpdated;

    @NotNull
    private LocalTime timeUpdated;
}
