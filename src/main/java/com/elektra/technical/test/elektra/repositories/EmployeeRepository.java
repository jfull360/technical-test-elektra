package com.elektra.technical.test.elektra.repositories;

import com.elektra.technical.test.elektra.models.Employee;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    //Se crea un nuevo objeto y se setean las propiedades correspondientes con la anotacion @builder de loombok
    //Create a new object and set corresponding properties
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return Employee.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .position(rs.getString("position"))
                .salary(rs.getDouble("salary"))
                .hireDate(rs.getDate("hire_date"))
                .department(rs.getString("department"))
                .build();
    }

    //Llamado del procedimiento para guardar el empleado
    public Optional<String> postEmployee(Employee e) {
        String procedureCall = "{call InsertEmployee(?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setString(1, e.getName());
            stmt.setString(2, e.getPosition());
            stmt.setDouble(3, e.getSalary());
            stmt.setDate(4, new java.sql.Date(e.getHireDate().getTime()));
            stmt.setString(5, e.getDepartment());
            stmt.execute(); // Ejecutar el procedimiento
            return Optional.of("Empleado insertado correctamente."); // Mensaje de éxito
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Optional.of("Error al insertar empleado: " + ex.getMessage());
        }
    }

    //Metodo para la la busqueda de empleado por id , retornado un optional en caso de contar con la informacion
    //optional nos permitira un mayor control en el controller
    public Optional<Employee> getEmployeeById(int empId) throws SQLException {
        String procedureCall = "{call GetEmployeeByID(?)}";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToEmployee(rs)) : Optional.empty();
            }
        }
    }

    public Optional<String> putEmployee(int id, Employee e) {
        String procedureCall = "{call UpdateEmployee(?, ?, ?, ?, ?, ?)}";
        String message = "No se encontró el empleado con ID: " + e.getId();
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            // Establecer los parámetros en el orden correcto
            stmt.setInt(1, id);
            stmt.setString(2, e.getName());
            stmt.setString(3, e.getPosition());
            stmt.setDouble(4, e.getSalary());
            stmt.setDate(5, new java.sql.Date(e.getHireDate().getTime()));
            stmt.setString(6, e.getDepartment());
            int rowsAffected = stmt.executeUpdate();  // Ejecutar el procedimiento
            if (rowsAffected > 0) {
                message = "Empleado actualizado correctamente.";
            }
            return Optional.of(message);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Optional.of("Error al actualizar el empleado: " + ex.getMessage());
        }
    }

    public Optional<String> deleteEmployeeById(int empId) {
        String procedureCall = "{call DeleteEmployee(?)}";
        String message = "No se encontró el empleado con ID: " + empId;
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setInt(1, empId); // Establecer el ID del empleado
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                message = "Empleado eliminado correctamente.";
            }
            return Optional.of(message);
        } catch (SQLException e) {
            return Optional.of("Error al eliminar empleado: " + e.getMessage());
        }
    }

    public Optional<String> deleteAllEmployees() {
        String procedureCall = "{call DeleteAllEmployees()}";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            int rowsAffected = stmt.executeUpdate();
            return Optional.of("Se eliminaron " + rowsAffected + " empleados.");
        } catch (SQLException e) {
            return Optional.of("Error al eliminar empleados: " + e.getMessage());
        }
    }

}
