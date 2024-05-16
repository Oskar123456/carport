package carport;

import java.util.Random;
import java.util.Scanner;

import org.eclipse.jetty.server.Authentication.User;

import carport.config.SessionConfig;
import carport.config.ThymeleafConfig;
import carport.controllers.MainController;
import carport.entities.CustomCarport;
import carport.exceptions.DatabaseException;
import carport.persistence.ConnectionPool;
import carport.persistence.ProductMapper;
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

        boolean running = true;
        String userInput = "";
        while (running){
            System.err.println("TYPE <shedWIDTH> <shedHEIGHT> <WIDTH> <HEIGHT> TO MAKE CARPORT EG. $4000 4000 8000 8000");
            Scanner in = new Scanner(System.in);
            String needle = ".";
            userInput = in.nextLine();
            if (userInput.contains(needle))
                mcc(0,0,0,0);
            else if (userInput.equals("exit")) {
                running = false;
                in.close();
            }
            else {
                String[] uisplit = userInput.split(" ");
                if (uisplit.length > 3){
                    mcc(Integer.parseInt(uisplit[0]),
                        Integer.parseInt(uisplit[1]),
                        Integer.parseInt(uisplit[2]),
                        Integer.parseInt(uisplit[3]));
                }
            }
        }
        // Todos
        // TODO: seperate SQL files to clean mapper, one per function, specialized. Much better idea imo.
        // TODO: sql predicates move to the nested table-gluers
    }

    private static void mcc(int shedX, int shedY, int ccX, int ccY){
        CustomCarport cc = new CustomCarport();

        try {
            System.err.println(ProductMapper.SelectProductsById(connectionPool, 15));
            cc.SetStolpe(connectionPool, ProductMapper.SelectProductsById(connectionPool, 15).get(0));
            cc.SetRem(connectionPool, ProductMapper.SelectProductsById(connectionPool, 11).get(0));
            cc.SetSpaer(connectionPool, ProductMapper.SelectProductsById(connectionPool, 12).get(0));
            cc.SetStern(connectionPool, ProductMapper.SelectProductsById(connectionPool, 21).get(0));
            cc.SetTagplade(connectionPool, ProductMapper.SelectProductsById(connectionPool, 14).get(0));
        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        Random r = new Random(System.currentTimeMillis());
        int shedlX = (shedX >= 0) ? shedX : Math.abs(r.nextInt()) % 5000;
        int shedlY = (shedY >= 0) ? shedY : Math.abs(r.nextInt()) % 5000;
        ccX = (ccX >= 0) ? ccX : Math.abs(r.nextInt()) % 10000;
        ccY = (ccY >= 0) ? ccY : Math.abs(r.nextInt()) % 10000;
        cc.Make(ccX, ccY, 5000, shedX > 0 && shedY > 0, shedlX, shedlY);

        System.err.println(cc.svgDraw());

        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println(cc.toString());
        System.err.println();
        System.err.println();
        System.err.println();
        System.err.println("stolper: " + cc.GetNStolpeProd());
        System.err.println("remme: " + cc.GetNRemProd());
        System.err.println("spaer: " + cc.GetNSpaerProd());
        System.err.println("stern: " + cc.GetNSternProd());
        System.err.println("tagplader: " +  cc.GetNTagpladeProd());
        System.err.println("Parking Spots: " +  cc.GetNParkingSpots());
        System.err.println("Tagplader: " +  cc.tagplader);
    }
}
