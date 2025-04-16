package com.elektra.technical.test.elektra;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.elektra.technical.test.elektra.controllers.EmployeeController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main implements RequestStreamHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmployeeController controller = new EmployeeController();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        //Se obtiene el JSON recibido desde API Gateway y se convierte en un Map para 
        //poder manejarlo utilizando herramientas de la libreria jackson
        //JSON received from API Gateway is obtained and converted into a Map so 
        //that it can be managed using tools from the Jackson library
        Map<String, Object> lambdaEvent = objectMapper.readValue(input, Map.class);
        Map<String, Object> response = new HashMap<>();

        String httpMethod = (String) lambdaEvent.get("httpMethod");
        String path = (String) lambdaEvent.get("path");

        context.getLogger().log("Método: " + httpMethod + " - Path: " + path);

        try {
            //Se separa la logica dependiendo del tipo de metodo http solicitado
            //The logic is separated depending on the type of http method requested
            switch (httpMethod) {
                case "GET":
                    if (path.matches("/employees/\\d+")) {
                        //Si la peticion contiene un parametro (ID), se obtiene 
                        //para poder llevar a cabo la busqueda del empleado
                        //If request contains a parameter (ID), it is obtained
                        //to be able to carry out the employee search
                        int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                        response = controller.getEmployeeById(id);
                    } else if (path.equals("/employees")) {
                        //Si no se obtiene un parametro se enviaran todos los empleados
                        //If a parameter is not obtained, all employees will be sent.
                        response = controller.getAllEmployees();
                    } else if (path.equals("/employees/salary/top")) {
                        //Si no se obtiene un parametro se enviaran todos los empleados
                        //If a parameter is not obtained, all employees will be sent.
                        response = controller.getAllEmployees();
                    }
                    break;

                case "POST":
                    if (path.equals("/employees")) {
                        //Se obtiene el objeto del body con las propiedades necesarias
                        //para poder crear un nuevo empleado
                        //Get the body object with necessary properties to create a new employee
                        String body = (String) lambdaEvent.get("body");
                        response = controller.createEmployee(body);
                    }
                    break;

                case "PUT":
                    //Si se obtiene un parametro (ID) ademas de ser una peticion PUT 
                    //Se llevara a cabo la actualizacion del empleado tomando en cuenta las propiedades
                    //obtenidas del body
                    // If an ID parameter is provided along with a PUT request,
                    // the employee update will be executed using the properties
                    // obtained from the request body.
                    if (path.matches("/employees/\\d+")) {
                        int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                        String body = (String) lambdaEvent.get("body");
                        response = controller.updateEmployee(id, body);
                    }
                    break;

                case "DELETE":
                    //Si se obtiene un parametro (ID) ademas de ser una peticion DELETE
                    //Se llevara a cabo un borrado logico
                    // If an ID parameter is supplied with the DELETE request,
                    // a logical deletion will be executed.
                    if (path.matches("/employees/\\d+")) {
                        int id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                        response = controller.deleteEmployee(id);
                    }
                    else if (path.matches("/all-employees")) {
                        response = controller.deleteAllEmployees();
                    }
                    break;

                default:
                    //Si el metodo solicitado no entra en ningun caso se envia una respuesta para 
                    //hacer de conocimiento el por que no se llevara a cabo la petición
                    // If the requested method doesn't match any case,
                    // a response will be returned to explain why the request can't be processed.
                    response.put("statusCode", 405);
                    response.put("body", "{\"message\":\"Método no permitido (Disallowed method)\"}");
            }
        } catch (Exception e) {
            //Si ocurre un error en alguna transaccion se captura para poder tener siempre una respuesta
            //y evitar que la aplicacion sufra una caida
            e.printStackTrace();
            response.put("statusCode", 500);
            response.put("body", "{\"error\":\"Lo sentimos, error interno del servidor (Sorry, internal server error)\"}");
        }

        objectMapper.writeValue(output, response);
    }
}
