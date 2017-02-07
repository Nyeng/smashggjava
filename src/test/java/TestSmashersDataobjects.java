import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSmashersDataobjects {

    //http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/

    private MongoCollection<Document> collection;
    private String firstPlayer = "01";
    private String secondPlayer = "22";
    private String thirdPlayer = "33";

    private MongoDatabase database;

    @Before
    public void setup() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);

        database = mongoClient.getDatabase("mydb");
        collection = database.getCollection("Smashers");

        insertOneToCollection();
        insertSeveralToCollection();
        //Insert to mongodb will create collection if collection doesn't already exist
    }

    @After
    public void dropDatabase(){
       database.drop();
    }

    private void findAllDocumentsInCollection(){
        System.out.println("Outputting all documents created so far");
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }

    private void insertOneToCollection() {
        //Creating instances of SMashers with same default rating
        Smasher<String> smasherWinner = new Smasher<>(firstPlayer);

        smasherWinner.setMeanDeviationAndDeviationMultiplier(44, 3, 3);
        smasherWinner.setPlayerTag("Vdawg");
        smasherWinner.setEntrantId("111331");

        Document doc = new Document("id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());

        collection.insertOne(doc);
    }

    private void insertSeveralToCollection() {
        Smasher<String> smasherWinner = new Smasher<>(secondPlayer);

        smasherWinner.setMeanDeviationAndDeviationMultiplier(33, 5.0, 3.1);

        smasherWinner.setPlayerTag("Sverre");

        Document doc = new Document("id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());

        Smasher<String> smasherLoser = new Smasher<>(thirdPlayer);
        smasherLoser.setMeanDeviationAndDeviationMultiplier(5, 10, 2);

        smasherLoser.setPlayerTag("AskeLink");

        Document doc2 = new Document("id",smasherLoser.getId())
            .append("mean", smasherLoser.getMean())
            .append("deviation", smasherLoser.getDeviation())
            .append("deviationMultiplier", smasherLoser.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherLoser.getPlayerTag());

        smasherLoser.setPlayerTag("AskeLink");

        List<Document> documents = new ArrayList<>();
        documents.add(doc);
        documents.add(doc2);

        collection.insertMany(documents);
    }

    @Test
    public void getSingleDocumentThatMatchesFilter(){
        Document myDoc = collection.find(eq("id", firstPlayer)).first();
    }

    @Test
    public void iterateAndUpdatePlayerObjects(){
        //Create basis
        List<Smasher<String>> smashers = new ArrayList<>();
        String[] names = new String[]{"Aske","Sverre","Vdogg"};

        int i = 222;
        for(String name : names){
            String id = String.valueOf(i+222);
            smashers.add(new Smasher<>(name,id));
        }

        //TODO

        //Create smasher and see if it gets created if already exists


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

    @Test
    public void updateOneValueInCollection() {

        System.out.println("skriver ut eksisterende collections: ");
        findAllDocumentsInCollection();
        //inserting 3 players
        BasicDBObject searchQuery = new BasicDBObject("id", firstPlayer);

        System.out.println("Fant search query for spiller" + searchQuery);

        BasicDBObject updateFields = new BasicDBObject();

        updateFields.append("playertag", "Jonas");
        updateFields.append("mean",5.0);
        updateFields.append("deviation", 11);

        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
        collection.updateOne(searchQuery, setQuery);

        Document myDoc = collection.find(eq("playertag", "Jonas")).first();
        System.out.println(myDoc.toJson());

        System.out.println("Skriver ut alle etter å ha oppdatert jonas");
        findAllDocumentsInCollection();
    }

    @Test @Ignore //TODO: Fix
    public void updateCollectionForIdThatAlreadyExists(){

        System.out.println("Skriver ut de som er oppdatert først ");
        findAllDocumentsInCollection();

        Smasher<String> smasherLoser = new Smasher<>(thirdPlayer);
        smasherLoser.setMeanDeviationAndDeviationMultiplier(5, 10, 2);

        smasherLoser.setPlayerTag("AskeLink");

        Document doc2 = new Document("id",smasherLoser.getId())
            .append("mean", smasherLoser.getMean())
            .append("deviation", smasherLoser.getDeviation())
            .append("deviationMultiplier", smasherLoser.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherLoser.getPlayerTag());

       // collection.updateOne(doc2);


        System.out.println("skriver ut alle etter forsøk på å oppdatere ting som ikke skal legges til: Deebug");
        findAllDocumentsInCollection();
    }

    @Test
    public void updateWhenNothingExists() throws Exception {
        database.drop();





    }
}

