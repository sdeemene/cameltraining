package com.stsl.employeeservice.entity;

import com.stsl.employeeservice.entity.enums.Qualification;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "camel_employee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeEntity extends BaseEntity {

    @NotBlank
    private String authUserId;

    private String authUserName;

    @Column(unique = true)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    private Qualification qualification;

    @NotNull
    private Integer age;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    private String address;

    @NotBlank
    private String phone;

    @NotNull
    private BigDecimal currentSalary;

    @NotBlank
    private String designation;

    @NotBlank
    private String profession;

    @NotBlank
    private String lastEmployer;

    @NotNull
    private LocalDate dateOfEmployment;

    @NotNull
    private LocalDate dateCreated;

    @NotNull
    private LocalTime timeCreated;

    @NotNull
    private LocalDate dateUpdated;

    @NotNull
    private LocalTime timeUpdated;
}
