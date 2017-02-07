import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import RankSample.Smasher;

/**
 * Created by k79689 on 03.02.17.
 */
public class TestSmashersDataobjects {

    //http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/

    private MongoCollection<Document> collection;
    private MongoDatabase database;

    @Before
    public void setup() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);

        database = mongoClient.getDatabase("mydb");
        collection = database.getCollection("Smashers");
    }

    @Test
    public void mongoDBTest() {

        //Creating instances of SMashers with same default rating
        Smasher<String> smasherWinner = new Smasher<>("2343434", Smasher.DEFAULTRATING);
        Smasher<String> smasherLoser = new Smasher<>("232343", Smasher.DEFAULTRATING);

        smasherWinner.setMeanDeviationAndDeviationMultiplier(44, 3, 3);
        smasherWinner.setPlayerTag("Vdawg");
        smasherWinner.setEntrantId("111331");

        smasherLoser.setMeanDeviationAndDeviationMultiplier(60, 4, 4);
        smasherLoser.setEntrantId("111332");
        smasherLoser.setPlayerTag("Sverre");

        Document doc = new Document("id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());

        collection.insertOne(doc);
        System.out.println("Collection count "+ collection.count());
    }

    @Test
    public void findFirstCollection(){
        //Finds first document
        Document myDoc = collection.find().first();
        System.out.println(myDoc.toJson());
    }

    @Test
    public void findAllDocumentsInCollection(){
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }

    @Test
    public void getSingleDocumentThatMatchesFilter(){
        Document myDoc = collection.find(eq("id", "2343434")).first();
        System.out.println(myDoc.toJson());
    }

    @Test
    public void updateOneValueInCollection() {

        BasicDBObject searchQuery = new BasicDBObject("playertag", "AskeLink");
        BasicDBObject updateFields = new BasicDBObject();

        updateFields.append("playertag", "AskeLink");
        updateFields.append("mean",5.0);
        updateFields.append("deviation", 11);
        updateFields.append("id","242424");


        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
        collection.updateOne(searchQuery, setQuery);


        Document myDoc = collection.find(eq("id", "2343434")).first();
        System.out.println(myDoc.toJson());
    }

    @Test
    public void updateSeveralValuesInCollection() {
        String id = "2343434";

        Document myDoc = collection.find(eq("id", id)).first();

        myDoc.append("playertag","Bose")
        .append("deviation", 3.0)
        .append("mean", 90)
        ;

        System.out.println(myDoc.toJson());

        findAllDocumentsInCollection();
    }

    @Test
    public void iterateAndUpdatePlayerObjects(){
        //Create basis
        List<Smasher<String>> smashers = new ArrayList<>();
        String[] names = new String[]{"Aske","Sverre","Vdogg"};
        Smasher<String> smasher;

        for(String name : names){
            smashers.add(smasher = new Smasher<>(name));
        }

        //Create database scheme like this:
//        id:
//        2232,
//            mean:232,
//            deviation:232,
//            "tournaments" : [
//        {
//            "tournament-id":"house-of-smash-43", "entrant-id":"98943"
//        },
//        {
//            "tournament-id":"drommelan-23", "entrant-id":"4343"
//        }
//        ],





    }

}
