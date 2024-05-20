package carport;

import carport.config.SessionConfig;
import carport.config.ThymeleafConfig;
import carport.controllers.MainController;
import carport.controllers.OrderController;
import carport.entities.Product;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
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
            config.jetty
                    .modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);
        // Routing
        MainController.addRoutes(jav, connectionPool);
        OrderController.addRoutes(jav, connectionPool);
        // Other setup
        // Test
        System.err.printf(
                "DebugInfo::%nEnv_Vars:%n\tdeployed: %s%n\tjdbc_user: %s%n\tjdbc_pw: %s%n\tjdbc_conn_str: %s%n\tjdbc_db: %s%n\ttest: %s%n",
                System.getenv("DEPLOYED"),
                System.getenv("JDBC_USER"),
                System.getenv("JDBC_PASSWORD"),
                System.getenv("JDBC_CONNECTION_STRING"),
                System.getenv("JDBC_DB"),
                System.getenv("test"));
        // Todos
    }
}
