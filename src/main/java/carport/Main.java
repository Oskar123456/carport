package carport;

import java.util.List;

import carport.config.SessionConfig;
import carport.config.ThymeleafConfig;
import carport.controllers.CarportController;
import carport.entities.ProductCategory;
import carport.exceptions.DatabaseException;
import carport.persistence.CarportMapper;
import carport.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://db:5432/%s?currentSchema=public";
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
        try {
            List<ProductCategory> cats = CarportMapper.SelectAllCategories(connectionPool);
            for (ProductCategory c : cats){
                System.err.printf("%s::%s%n", c.Name, c.CommonSpecs.toString());
            }
        } catch (DatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Todos
        // TODO: add wrappe around image-gets for frontend, so that there is always some placeholder to serve if none found
    }
}
