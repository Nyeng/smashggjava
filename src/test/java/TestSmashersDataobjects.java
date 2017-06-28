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
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;

import Trueskill.Smasher;

/**
 * Created by k79689 on 03.02.17.
 */

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSmashersDataobjects {

    //http://mongodb.github.io/mongo-java-driver/3.4/driver/getting-started/quick-start/

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    private MongoCollection<Document> collection;
    private String firstPlayerId = "01"; // UUID.randomUUID().toString();
    private String secondPlayerId = "02"; // UUID.randomUUID().toString();
    private String thirdPlayerId = "03"; // UUID.randomUUID().toString();

    public MongoDatabase getDatabase() {
        return database;
    }

    private MongoDatabase database;

    private Block<Document> printBlock = document -> System.out.println(document.toJson());

    @Before
    public void setup() {
        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);

        database = mongoClient.getDatabase("testdatabase");
        collection = database.getCollection("SmashersTesters");

        collection.createIndex(Indexes.ascending("mean"));
        collection.createIndex(Indexes.text("playertag"));
        //Insert to mongodb will create collection if collection doesn't already exist
    }

    @After
    public void dropDatabase() {
        database.drop();
    }

    private void findAllDocumentsInCollection() {
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }

    public void insertOneToCollection() {
        //Creating instances of SMashers with same default rating
        Smasher<String> smasherWinner = new Smasher<>(firstPlayerId);

        smasherWinner.setMeanDeviationAndDeviationMultiplier(5, 3, 3);
        smasherWinner.setPlayerTag("Vdawg");
        smasherWinner.setEntrantId("111331");

        Document doc = new Document("_id", smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag", smasherWinner.getPlayerTag());

        collection.insertOne(doc,new InsertOneOptions().bypassDocumentValidation(false));

    }

    private void insertSeveralToCollection() {
        Smasher<String> smasherWinner;

        smasherWinner = new Smasher<>(secondPlayerId);
        smasherWinner.setMeanDeviationAndDeviationMultiplier(1.0, 5.0, 3.1);

        smasherWinner.setPlayerTag("Sverre");

        Document doc = new Document("_id", smasherWinner.getId())
            .append("mean", smasherWinner.getMean())
            .append("deviation", smasherWinner.getDeviation())
            .append("deviationMultiplier", smasherWinner.getConservativeStandardDeviationMultiplier())
            .append("playertag", smasherWinner.getPlayerTag());

        Smasher<String> smasherLoser = new Smasher<>(thirdPlayerId);
        smasherLoser.setMeanDeviationAndDeviationMultiplier(2.0, 10, 2);

        smasherLoser.setPlayerTag("AskeLink");

        Document doc2 = new Document("_id", smasherLoser.getId())
            .append("mean", smasherLoser.getMean())
            .append("deviation", smasherLoser.getDeviation())
            .append("deviationMultiplier", smasherLoser.getConservativeStandardDeviationMultiplier())
            .append("playertag", smasherLoser.getPlayerTag());

        List<Document> documents = new ArrayList<>();
        documents.add(doc);
        documents.add(doc2);

        collection.insertMany(documents);
    }

    @Test
    public void getSingleDocumentThatMatchesFilter() {
        Document myDoc = collection.find(eq("id", firstPlayerId)).first();
    }


    public void sortCollectionByMean() {

        collection.createIndex(Indexes.descending("mean"));

        collection.find()
            .sort(Sorts.descending("mean"))
            .forEach(printBlock);
    }

    @Test
    public void updateOneValueInCollection() {
        // insertOneToCollection();
        insertOneToCollection();

        Document myDoc = collection.find(eq("_id", firstPlayerId)).first();
        System.out.println(myDoc.get("mean"));

    }

    @Test
    public void updateWhenAlreadyExists() throws InterruptedException {

        BasicDBObject searchQuery = new BasicDBObject("_id", firstPlayerId);

        // insertOneToCollection();
        BasicDBObject updateFields = new BasicDBObject();

        updateFields.append("playertag", "Jonas");
        updateFields.append("mean", 5.0);
        updateFields.append("deviation", 11);

        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);
        collection.updateOne(searchQuery, setQuery, new UpdateOptions().upsert(true));

        findAllDocumentsInCollection();

    }


    @Test
    public void updateObjectWhenDuplicateId() throws Exception {
        BasicDBObject searchQuery;
        BasicDBObject updateFields;

        Smasher<String> one = new Smasher<>(firstPlayerId);
        Smasher<String> two = new Smasher<>(secondPlayerId);
        Smasher<String> three = new Smasher<>(secondPlayerId);

        one.setMeanDeviationAndDeviationMultiplier(25, 3, 3);
        two.setMeanDeviationAndDeviationMultiplier(20, 2, 3);
        three.setMeanDeviationAndDeviationMultiplier(22, 3, 3);
//
//        one.setPlayerTag("Vdogg");
//        two.setPlayerTag("Aske");
//        three.setPlayerTag("Sverre");

        //List<Smasher> smashers = new ArrrayList<>();
        List<Smasher<String>> smashers = new ArrayList<>();
        smashers.add(one);
        smashers.add(two);
        smashers.add(three);

        for (Smasher smasher : smashers) {
            searchQuery = new BasicDBObject("_id", smasher.getId());

            if(smasher.getId().equals(firstPlayerId)){
                System.out.println("found ");
            }

            updateFields = new BasicDBObject();
            updateFields.append("mean", smasher.getMean());
            updateFields.append("deviation", smasher.getDeviation());
            updateFields.append("deviationMultiplier",
            smasher.getConservativeStandardDeviationMultiplier());

//            updateFields.append("playertag", smasher.getPlayerTag());
            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);
            collection.updateOne(searchQuery, setQuery, new UpdateOptions().upsert(true));
        }



    }

}

