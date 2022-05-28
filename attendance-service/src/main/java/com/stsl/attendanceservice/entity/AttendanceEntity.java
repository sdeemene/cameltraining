package com.stsl.attendanceservice.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "camel_attendance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttendanceEntity extends BaseEntity{

    @NotBlank
    private String authUserId;

    @NotNull
    private LocalTime timeIn;

    @NotBlank
    private String dayOfWeek;

    @NotNull
    private LocalTime timeOut;

    @NotNull
    private Long totalHours;

    @NotNull
    private LocalDate dateCreated;

    @NotNull
    private LocalTime timeCreated;

    @Builder.Default
    private Long totalDaysPresent = 0L;
}
