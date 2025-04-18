package com.elektra.technical.test.elektra.services;

/**
 *
 * @author JORGE
 */
import com.elektra.technical.test.elektra.models.Employee;
import com.elektra.technical.test.elektra.repositories.EmployeeRepository;
import com.elektra.technical.test.elektra.threads.EmployeeThreadProcessor;
import com.elektra.technical.test.elektra.utils.SecretsManagerUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService() throws Exception {
        // Se obtiene la variable de entorno SECRET_ARN que previamente creamos en aws.amazon.com/lambda
        // Get SECRET_ARN environment variable created in aws.amazon.com/lambda
        String secretName = System.getenv("SECRET_ARN");

        // Se obtiene el mapa de valores del secreto desde AWS Secrets Manager
        // Obtaining the secret value map from AWS Secrets Manager
        Map<String, String> secret = SecretsManagerUtil.getDatabaseSecret(secretName, "us-east-1");

        // Se extraen las credenciales para acceder ala BD de AWS
        // Get credentials to access the AWS DB
        String host = secret.get("host");
        String username = secret.get("username");
        String password = secret.get("password");
        String dbname = secret.get("dbInstanceIdentifier");

        // URL de conexión a la base de datos
        // Database connection URL
        String url = "jdbc:mysql://" + host + ":3306/" + dbname + "?useSSL=false&allowPublicKeyRetrieval=true";

        // Se crea la conexión a la base de datos
        // Connection to the database is created
        Connection connection = DriverManager.getConnection(url, username, password);

        // Se inicializa el repositorio con la conexión
        // The repository is initialized with the connection
        this.repository = new EmployeeRepository(connection);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return repository.getAllEmployees();
    }

    public Optional<Employee> getEmployeeById(int id) throws SQLException {
        return repository.getEmployeeById(id);
    }

    public Optional<String> createEmployee(Employee employee) throws SQLException {
        return repository.postEmployee(employee);
    }

    public Optional<String> updateEmployee(int id, Employee updated) throws SQLException {
        return repository.putEmployee(id, updated);
    }

    public Optional<String> deleteEmployee(int id) throws SQLException {
        return repository.deleteEmployeeById(id);
    }

    public Optional<String> deleteAllEmployees() throws SQLException {
        return repository.deleteAllEmployees();
    }

    //Función para reutilizar el codigo si es necesario
    //Function to reuse code if its necessary
    public List<Employee> collectAllEmployeesFromFiles() throws InterruptedException {
        List<Employee> allEmployees = Collections.synchronizedList(new ArrayList<>());
        //Se coloca solo el nombre de los archivos debido a que se encuentran en raiz src/main/resources
        //Only file names are included because they are located in root src/main/resources
        String[] files = {
            "employees_data1.json",
            "employees_data2.json",
            "employees_data3.json"
        };
        ExecutorService executor = Executors.newFixedThreadPool(files.length);
        for (String file : files) {
            //Recorremos y recolectamos por medio de nuestra clase todos los empleados 
            //obtenidos del archivo JSON de forma sincrona
            //We loop through and collect all employees through our class
            //obtained from JSON file synchronously
            executor.execute(new EmployeeThreadProcessor(file, allEmployees));
        }

        //Manejamos las tareas dando un tiempo limite de 10 seg pero no se forzara su terminación
        //aun si se termina el tiempo
        //We manage tasks by giving them a 10-second time limit, but their completion will not be forced
        //even if the time runs out
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        //Se retorna el resultado 
        //Return final result
        return allEmployees;
    }

    public List<Employee> getTop10EmployeesWithBetterSalaries() throws InterruptedException {
        List<Employee> allEmployees = collectAllEmployeesFromFiles();
        // Se compara y ordena para devolver el top 10 de empleados con mejor salario
        //It´s compared and sorted to return the top 10 Employees with better salaries
        return allEmployees.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .limit(10)
                .toList();
    }
}
