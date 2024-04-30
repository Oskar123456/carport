package carport;

import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import carport.config.ThymeleafConfig;
import carport.controllers.CarportController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;


public class Main
{
    private static final String USER = "postgres";
    private static final String PASSWORD = "bwQc)89P";
    private static final String URL = "jdbc:postgresql://104.248.251.153:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args)
    {
        // Initializing Javalin and Jetty webserver

        Javalin jav = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);


        // Routing
        CarportController.addRoutes(jav, connectionPool);

        // Test
        try {
            System.out.printf("Testing database connection...%n\tListing item names:%n");
            CarportMapper.TestMapper(connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
