package com.stsl.employeeservice.response;

import com.stsl.employeeservice.entity.enums.Qualification;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class NewEmployeeRequest {

    private Qualification currentQualification;

    private LocalDate dateOfBirth;

    private String address;

    private String phone;

    private BigDecimal currentSalary;

    private String designation;

    private String profession;

    private String lastEmployer;

    private LocalDate dateOfEmployment;

}
