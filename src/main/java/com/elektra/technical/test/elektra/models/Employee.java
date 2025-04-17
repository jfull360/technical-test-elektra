package com.elektra.technical.test.elektra.models;

/**
 *
 * @author JORGE
 */
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.*; // Se agrego la librería lombok para reducir código
//Added lombok library to reduce code

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    private int id;
    private String name;
    private String position;
    private double salary;
    //Se indica a la libreria Jackson convertir de string a java date (Opcion más rapida)
    //ademas de mapear la variable
    //Jackson library is instructed to convert from string to Java date (Faster option) 
    //in addition to mapping the variable
    @JsonProperty("hire_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date hireDate;
    private String department;
}
