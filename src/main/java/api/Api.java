package api;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;

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
public class Api {

    private MongoCollection<Document> collection;

    private String data;
    //For printing out sorted DB
    private MongoDatabase database;



    public static void main(String[]args){

        Api api = new Api();
       // api.setupMongoDb();

        port(getHerokuAssignedPort());

        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        //port(9999);
//        String rankedsmasherunsorted = api.sortSmashersByRankDatabase();

        //get("/rank", (req, res) -> rankedsmasherunsorted);

        get("/helloworld", (req, res) -> "helo");

    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    private void setupMongoDb() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);

        database = mongoClient.getDatabase("mydb");
        collection = database.getCollection("Smashers");
    }


    private String sortSmashersByRankDatabase() {
        System.out.println("Outputting db ranks: ");
        String hei = "";

        FindIterable<Document> cursor = collection.find();
        String serialize = JSON.serialize(cursor);
        System.out.println(serialize);

        return serialize;
    }

}
