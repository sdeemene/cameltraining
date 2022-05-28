package com.stsl.attendanceservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity implements Serializable {


    public BaseEntity(){
        setUid(UUID.randomUUID().toString());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String uid;

}
