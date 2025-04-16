package com.elektra.technical.test.elektra.repositories;

import com.elektra.technical.test.elektra.models.Employee;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author JORGE
 */
public class EmployeeRepository {

    private final Connection connection;

    public EmployeeRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        String procedureCall = "{call GetAllEmployees()}";

        try (CallableStatement stmt = connection.prepareCall(procedureCall); ResultSet rs = stmt.executeQuery()) {

            return resultSetToEmployeeList(rs).stream()
                    //Se ordena la lista por el nombre
                    //Sort the list by name
                    .sorted(Comparator.comparing(Employee::getName))
                    .collect(Collectors.toList());
        }
    }

    //Se agregan los resultados a la lista mediante un ciclo
    //Add results to the list using a loop
    private List<Employee> resultSetToEmployeeList(ResultSet rs) throws SQLException {
        List<Employee> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapResultSetToEmployee(rs));
        }
        return list;
    }

    //Se crea un nuevo objeto y se setean las propiedades correspondientes
    //Create a new object and set corresponding properties
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setId(rs.getInt("id"));
        e.setName(rs.getString("name"));
        e.setPosition(rs.getString("position"));
        e.setSalary(rs.getDouble("salary"));
        e.setHireDate(rs.getDate("hire_date").toLocalDate());
        e.setDepartment(rs.getString("department"));
        return e;
    }
}
