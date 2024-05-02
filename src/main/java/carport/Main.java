package carport;

import carport.config.SessionConfig;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import carport.config.ThymeleafConfig;
import carport.controllers.CarportController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.staticfiles.StaticFileConfig;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.function.Consumer;


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
        String userDir = System.getProperty("user.dir");
        String publicDir = userDir + "/src/main/resources/public";

        System.err.println(publicDir);

        Javalin jav = Javalin.create(config -> {
            config.staticFiles.add("/public");
            //config.staticFiles.add(publicDir, Location.EXTERNAL);
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Routing
        CarportController.addRoutes(jav, connectionPool);

        // Test
        try {
            System.err.println("user.dir = " + userDir + "\tpublicDir = " + publicDir);
            System.out.printf("Testing database connection...%n\tListing item names:%n");
            CarportMapper.TestMapper(connectionPool);
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}
