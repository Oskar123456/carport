package carport;

import carport.config.SessionConfig;
import carport.config.ThymeleafConfig;
import carport.controllers.CarportController;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "bwQc)89P";
    private static final String URL = "jdbc:postgresql://104.248.251.153:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public static void main(String[] args) {
        // Initializing Javalin and Jetty webserver

        Javalin jav = Javalin.create(config -> {
                // TODO: figure out hotloading files instead of static
                config.staticFiles.add("/public");
                config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
                config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            }).start(7070);

        // Routing
        CarportController.addRoutes(jav, connectionPool);
        // Other setup
        CarportMapper.Init();

        // Test
        System.err.printf("DebugInfo::%nEnv_Vars:%n\t%s, %s, %s, %s, %s%n",
                        System.getenv("DEPLOYED"),
                        System.getenv("JDBC_USER"),
                        System.getenv("JDBC_PASSWORD"),
                        System.getenv("JDBC_CONNECTION_STRING_STARTCODE"),
                        System.getenv("JDBC_DB"));
        // Todos
        // TODO: implement search (by name and category)
        // TODO: add images that we can now serve to product display
    }
}
