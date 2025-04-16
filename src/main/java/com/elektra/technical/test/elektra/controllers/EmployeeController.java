package com.elektra.technical.test.elektra.controllers;

import com.elektra.technical.test.elektra.models.Employee;
import com.elektra.technical.test.elektra.services.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author JORGE
 */
public class EmployeeController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmployeeService service;

    public EmployeeController() {
        EmployeeService tempService;
        try { //Manejamos las posibles excepciones (We handle possible exceptions)
            tempService = new EmployeeService();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Se presento un error al inicializar el service (An error occurred while initializing the service)", e);
        }
        this.service = tempService;
    }

    public Map<String, Object> getAllEmployees() throws Exception {
        List<Employee> employees = service.getAllEmployees();
        //
        return buildResponse(200, employees);
    }

    public Map<String, Object> getEmployeeById(int id) throws Exception {
        Employee emp = service.getEmployeeById(id);
        return buildResponse(emp != null ? 200 : 404, emp);
    }

    public Map<String, Object> createEmployee(String body) throws Exception {
        Employee employee = objectMapper.readValue(body, Employee.class);
        boolean success = service.createEmployee(employee);
        return buildResponse(success ? 201 : 400, null);
    }

    public Map<String, Object> updateEmployee(int id, String body) throws Exception {
        Employee employee = objectMapper.readValue(body, Employee.class);
        boolean success = service.updateEmployee(id, employee);
        return buildResponse(success ? 200 : 400, null);
    }

    public Map<String, Object> deleteEmployee(int id) throws Exception {
        boolean success = service.deleteEmployee(id);
        return buildResponse(success ? 200 : 404, null);
    }

    private Map<String, Object> buildResponse(int statusCode, Object body) throws JsonProcessingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", headers);
        response.put("body", body != null ? objectMapper.writeValueAsString(body) : "{}");
        return response;
    }
}

