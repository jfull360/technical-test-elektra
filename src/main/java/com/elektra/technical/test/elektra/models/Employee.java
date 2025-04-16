package com.elektra.technical.test.elektra.models;

/**
 *
 * @author JORGE
 */
import java.util.Date;
import lombok.Builder;
import lombok.Data; // Se agrego la librería lombok para reducir código
//Added lombok library to reduce code

@Data
@Builder
public class Employee {
    private int id;
    private String name;
    private String position;
    private double salary;
    private Date hireDate;
    private String department;
}