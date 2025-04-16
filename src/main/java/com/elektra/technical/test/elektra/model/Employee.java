package com.elektra.technical.test.elektra.model;

/**
 *
 * @author JORGE
 */
import java.time.LocalDate;
import lombok.Data; // Se agrego la librería lombok para reducir código
//Added lombok library to reduce code

@Data
public class Employee {
    private int id;
    private String name;
    private String position;
    private double salary;
    private LocalDate hireDate;
    private String department;
}