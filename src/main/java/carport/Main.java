package carport;

import java.util.Random;

import carport.config.SessionConfig;
import carport.config.ThymeleafConfig;
import carport.controllers.MainController;
import carport.entities.CustomCarport;
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
        MainController.addRoutes(jav, connectionPool);
        // Other setup

        // Test
        System.err.printf("DebugInfo::%nEnv_Vars:%n\tdeployed: %s%n\tjdbc_user: %s%n\tjdbc_pw: %s%n\tjdbc_conn_str: %s%n\tjdbc_db: %s%n\ttest: %s%n",
                          System.getenv("DEPLOYED"),
                          System.getenv("JDBC_USER"),
                          System.getenv("JDBC_PASSWORD"),
                          System.getenv("JDBC_CONNECTION_STRING_STARTCODE"),
                          System.getenv("JDBC_DB"),
                          System.getenv("test"));

        CustomCarport cc = new CustomCarport();
        int slX = 100; int slY = 100; int rlX = 50; int rlY = 50; int splX = 40; int splY = 40;
        Random r = new Random(System.currentTimeMillis());
        int shedlX = Math.abs(r.nextInt()) % 5000;
        int shedlY = Math.abs(r.nextInt()) % 5000;
        cc.LenX = Math.abs(r.nextInt()) % 10000;
        cc.LenY = Math.abs(r.nextInt()) % 10000;
        System.err.printf("shedlX: %d, shedlY: %d, LenX: %d, LenY: %d%n%n", shedlX, shedlY, cc.LenX, cc.LenY);
        cc.Make(slX, slY, rlX, rlY, splX, splX, shedlX, shedlY);

        System.err.println(cc.svgDraw());
        // Todos
        // TODO: seperate SQL files to clean mapper, one per function, specialized. Much better idea imo.
        // TODO: sql predicates move to the nested table-gluers
    }
}
