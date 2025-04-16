package com.elektra.technical.test.elektra.controllers;

import com.elektra.technical.test.elektra.models.Employee;
import com.elektra.technical.test.elektra.services.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return buildResponse(200, employees);
    }

    public Map<String, Object> getEmployeeById(int id) throws Exception {
        Optional<Employee> emp = service.getEmployeeById(id);
        return buildResponse(emp.isPresent() ? 200 : 404, emp.get());
    }

    public Map<String, Object> createEmployee(String body) throws Exception {
        Employee employee = objectMapper.readValue(body, Employee.class);
        Optional<String> success = service.createEmployee(employee);
        return buildResponse(success.isPresent() ? 201 : 400, success.get());
    }

    public Map<String, Object> updateEmployee(int id, String body) throws Exception {
        Employee employee = objectMapper.readValue(body, Employee.class);
        Optional<String> success = service.updateEmployee(id, employee);
        return buildResponse(success.isPresent() ? 200 : 400, success.get());
    }

    public Map<String, Object> deleteEmployee(int id) throws Exception {
        Optional<String> success = service.deleteEmployee(id);
        return buildResponse(success.isPresent() ? 200 : 404, success.get());
    }
    
    public Map<String, Object> deleteAllEmployees() throws Exception {
        Optional<String> success = service.deleteAllEmployees();
        return buildResponse(success.isPresent() ? 200 : 404, success.get());
    }

    public Map<String, Object> getTop10EmployeesWithBetterSalaries() throws Exception {
        List<Employee> top10 = service.getTop10EmployeesWithBetterSalaries();
        return buildResponse(200, top10);
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
