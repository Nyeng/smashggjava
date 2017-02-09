import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;

import RankSample.Smasher;

/**
 * Created by k79689 on 03.02.17.
 */

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSmashersDataobjects {

    //http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/

    private MongoCollection<Document> collection;
    private String firstPlayerId = "01";
    private MongoDatabase database;

    Block<Document> printBlock = document -> System.out.println(document.toJson());

    @Before
    public void setup() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);

        database = mongoClient.getDatabase("testdatabase");
        collection = database.getCollection("SmashersTesters");

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
        Smasher<String> smasherWinner = new Smasher<>(firstPlayerId);

        smasherWinner.setMeanDeviationAndDeviationMultiplier(5, 3, 3);
        smasherWinner.setPlayerTag("Vdawg");
        smasherWinner.setEntrantId("111331");

        Document doc = new Document("_id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());

        collection.insertOne(doc);

        collection.createIndex(Indexes.ascending("mean"));
        collection.createIndex(Indexes.text("playertag"));

    }

    private void insertSeveralToCollection() {
        String secondPlayer = "22";
        Smasher<String> smasherWinner = new Smasher<>(secondPlayer);
        smasherWinner.setMeanDeviationAndDeviationMultiplier(1.0, 5.0, 3.1);

        smasherWinner.setPlayerTag("Sverre");

        Document doc = new Document("_id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());

        String thirdPlayer = "33";
        Smasher<String> smasherLoser = new Smasher<>(thirdPlayer);
        smasherLoser.setMeanDeviationAndDeviationMultiplier(2.0, 10, 2);

        smasherLoser.setPlayerTag("AskeLink");

        Document doc2 = new Document("_id",smasherLoser.getId())
            .append("mean", smasherLoser.getMean())
            .append("deviation", smasherLoser.getDeviation())
            .append("deviationMultiplier", smasherLoser.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherLoser.getPlayerTag());

        List<Document> documents = new ArrayList<>();
        documents.add(doc);
        documents.add(doc2);

        collection.insertMany(documents);
    }

    @Test
    public void getSingleDocumentThatMatchesFilter(){
        Document myDoc = collection.find(eq("id", firstPlayerId)).first();
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
    }

    @Test
    public void sortCollectionByMean() {

        collection.createIndex(Indexes.descending("mean"));

        findAllDocumentsInCollection();

        collection.find()
            .sort(Sorts.ascending("mean"))
            .forEach(printBlock);




    }

    @Test
    public void updateOneValueInCollection() {
        dropDatabase();
        // insertOneToCollection();
        BasicDBObject searchQuery = new BasicDBObject("_id", firstPlayerId);
        insertOneToCollection();

        Document myDoc = collection.find(eq("_id", firstPlayerId)).first();
        System.out.println(myDoc.get("mean"));


//        System.out.println("Skriver ut alle etter å ha inserta en i collection: ");
//        findAllDocumentsInCollection();
//
//        BasicDBObject updateFields = new BasicDBObject();
//
//        updateFields.append("playertag", "Jonas");
//        updateFields.append("mean",5.0);
//        updateFields.append("deviation", 11);
//
//        BasicDBObject setQuery = new BasicDBObject();
//        setQuery.append("$set", updateFields);
//        collection.updateOne(searchQuery, setQuery);
//
//        System.out.println("Skriver ut alle etter å ha oppdatert collection med nytt tag-name");
//        findAllDocumentsInCollection();
    }

    @Test
    public void updateWhenAlreadyExists() throws InterruptedException {

        System.out.println("Wiper eksisterende kolleksjoner først og prøver å skrive ut ");
        database.drop();

//
//        UpdateOptions options = new UpdateOptions();
//        options.upsert(true);

        BasicDBObject searchQuery = new BasicDBObject("_id", firstPlayerId);

       // insertOneToCollection();
        BasicDBObject updateFields = new BasicDBObject();

        updateFields.append("playertag", "Jonas");
        updateFields.append("mean",5.0);
        updateFields.append("deviation", 11);

        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
        collection.updateOne(searchQuery, setQuery,new UpdateOptions().upsert(true));


        findAllDocumentsInCollection();

    }



    @Test
    public void updateObjectWhenDuplicateId() throws Exception {
        insertOneToCollection();

        database.drop();

        String id = "01";
        Smasher<String> smasherWinner = new Smasher<>(id);

        smasherWinner.setMeanDeviationAndDeviationMultiplier(5, 3, 3);
        smasherWinner.setPlayerTag("Vdawg");
        smasherWinner.setEntrantId("111331");

        Document doc = new Document("_id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());

        collection.insertOne(doc);


        String idTwo = "02";
        Smasher<String> newPLayer = new Smasher<>(idTwo);

        newPLayer.setMeanDeviationAndDeviationMultiplier(5, 3, 3);
        newPLayer.setPlayerTag("Vdawg");
        newPLayer.setEntrantId("111331");

        Document doc2 = new Document("_id",smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag",smasherWinner.getPlayerTag());


        // search for duplicates
        Document documentPossiblyExist = collection.find(eq("playertag", smasherWinner.getPlayerTag())).first();
        String playerTagExistingPlayer = (String) documentPossiblyExist.get("playertag");

        System.out.println(newPLayer.getPlayerTag());

        //TODO Create logic to handle duplicate tags

        if (newPLayer.getPlayerTag().equals(playerTagExistingPlayer)){
            System.out.println("Found duplicate values, now overwrite last smasher for new collection ");

        }


    }
}

