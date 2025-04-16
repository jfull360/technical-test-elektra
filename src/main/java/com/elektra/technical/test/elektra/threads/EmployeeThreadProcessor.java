package com.elektra.technical.test.elektra.threads;

/**
 *
 * @author JORGE
 */
import com.elektra.technical.test.elektra.models.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class EmployeeThreadProcessor implements Runnable {

    private final String fileName;
    private final List<Employee> employeeList;

    public EmployeeThreadProcessor(String fileName, List<Employee> list) {
        //Constructor para setear valores
        //Constructor to set values
        this.fileName = fileName;
        this.employeeList = list;
    }

    @Override
    public void run() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
            Employee[] employees = mapper.readValue(input, Employee[].class);
            //Synchronize access to employeeList and add all employees obtained from JSON file."
            //Sincronizar el acceso a employeeList y agregar todos los empleados obtenidos del archivo JSON.
            synchronized (employeeList) {
                Collections.addAll(employeeList, employees);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
