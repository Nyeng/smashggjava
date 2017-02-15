package api;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;

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
        api.setupMongoDb();


        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        port(9999);

        String lol = api.sortSmashersByRankDatabase();

        get("/hello", (req, res) -> lol);
        get("/helloworld", (req, res) -> "helo");

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

        Block<Document> printBlock = document -> hei +=(document.toJson());

        for (Block<Document> collection :  database){

        }


        collection.createIndex(Indexes.ascending("mean"));

        collection.find()
            .sort(Sorts.descending("mean"))
            .forEach(printBlock);
        return printBlock.toString();
    }

}
