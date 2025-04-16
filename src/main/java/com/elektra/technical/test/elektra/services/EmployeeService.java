package com.elektra.technical.test.elektra.services;

/**
 *
 * @author JORGE
 */

import com.elektra.technical.test.elektra.models.Employee;
import com.elektra.technical.test.elektra.repositories.EmployeeRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService() throws SQLException {
        //Credenciales para probar en una BD local
        Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/bd_de_prueba",
            "jorge",
            "1234_Test"
        );
        this.repository = new EmployeeRepository(connection);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return repository.getAllEmployees();
    }

    public Employee getEmployeeById(int id) throws SQLException {
        return new Employee();
        //return repository.getEmployeeById(id);
    }

    public boolean createEmployee(Employee employee) throws SQLException {
        return true;
        //return repository.insertEmployee(employee);
    }

    public boolean updateEmployee(int id, Employee updated) throws SQLException {
        return true;
        //return repository.updateEmployee(id, updated);
    }

    public boolean deleteEmployee(int id) throws SQLException {
        return true;
        //return repository.deleteEmployee(id);
    }
}
