import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

/**
 * Created by k79689 on 14.02.17.
 */
public class Main {

    private MongoCollection<Document> collection;

    private String data;
    //For printing out sorted DB
    private MongoDatabase database;
    public static ReadBracket readBracket = new ReadBracket();


    public static void main(String[]args) throws IOException {

        Main main= new Main();
        readBracket.setupMongoDb();

        main.getSmashersFromFile();

        port(getHerokuAssignedPort());

        get("/rank", (req, res) -> readBracket.sortSmashers());
//
        after((req, res) -> {
            res.type("application/json");
        });

        before("/post", (request, response) -> {
            Boolean authenticated = false;
            String auth = request.headers("Authorization");
            if(auth != null && auth.startsWith("Basic")) {
                String b64Credentials = auth.substring("Basic".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(b64Credentials));
                System.out.println(credentials);
                if(credentials.equals("admin:admin")) authenticated = true;
            }
            if(!authenticated) {
                response.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
                halt(401, "You are not authorized to make this request. Contact Vdogg, and we'll see what we can do about it.");
                response.status(401);
            }
        });

        post("/post", (request, response) -> {
            // Create something
            String tournamentName = request.queryParams("eventid");
            readBracket.generateRank(tournamentName);
            return "Updated ranks with tournament with id: " + tournamentName;
        });

        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        //port(9999);
       // String rankedsmasherunsorted = main.sortSmashersByRankDatabase();

        //get("/rank", (req, res) -> rankedsmasherunsorted);

        post("/post", (request, response) -> {
            // Create something

            return "";
        });

        after((req, res) -> {
            res.type("application/json");
        });

        //Heroku pw: Smashnorgeheroku13

        port(getHerokuAssignedPort());

        get("/rank", (req, res) -> main.sortSmashersByRankDatabase());

        get("/", (req, res) -> "Melee rank incoming");
        get("/hello", (req, res) -> "Hello Heroku World");

    }

    public static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            System.out.println("SPARK PORT: " +Integer.parseInt(processBuilder.environment().get("PORT")));
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    private void setupMongoDb() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");

        //prod mongodb://<dbuser>:<dbpassword>@ds157839.mlab.com:57839/heroku_7btb6zs3

        String prodConnectionUri = "mongodb://heroku_7btb6zs3:bvh12rab31k58n8ijraufist0@ds157839.mlab.com:57839/heroku_7btb6zs3";

        MongoClient mongoClient = new MongoClient(prodConnectionUri);

        database = mongoClient.getDatabase("mydb");
        collection = database.getCollection("Smashers");
    }

    private String getSmashersFromFile() throws IOException {
        String everything;

        BufferedReader br = new BufferedReader(new FileReader("smashers.json"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }

        String lol = JSON.serialize(everything);


       return lol;
    }

    private String sortSmashersByRankDatabase() throws FileNotFoundException {
        System.out.println("Outputting db ranks: ");
        String hei = "";

        FindIterable<Document> cursor = collection.find();
        String serialize = JSON.serialize(cursor);
        System.out.println(serialize);

        try(  PrintWriter out = new PrintWriter( "smashers.json" )  ){
            out.println( serialize );
        }

        return serialize;
    }

}
