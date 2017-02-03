import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

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

    private MongoClientURI connectionString;
    private MongoClient mongoClient;
    private MongoDatabase database;
    MongoCollection<Document> collection;

    @Before
    public void setup() {
        connectionString = new MongoClientURI("mongodb://localhost:27017");
        mongoClient = new MongoClient(connectionString);

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
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        } finally {
            cursor.close();
        }
    }

    @Test
    public void getSingleDocumentThatMatchesFilter(){
        Document myDoc = collection.find(eq("id", "2343434")).first();
        System.out.println(myDoc.toJson());

    }

}
