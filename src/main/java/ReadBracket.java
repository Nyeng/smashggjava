import static com.mongodb.client.model.Filters.eq;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.util.JSON;

import Trueskill.Smasher;
import Trueskill.TrueSkillImplementation;

/**
 * Created by Vdawg on 17.01.17.
 */
public class ReadBracket {

    private JSONObject jsonobject;
    private List<JSONObject> winnerAndLoserIdsForEverSetPlayedAtAtournament;
    private List<Smasher<String>> smashers;
    private ConsumeApi consumeApi = new ConsumeApi();


    private TrueSkillImplementation trueskill = new TrueSkillImplementation();


    private MongoCollection<Document> collection;

    //For printing out sorted DB
    private Block<Document> printBlock = document -> System.out.println(document.toJson());
    private MongoDatabase database;

    public static void main(String[] args) throws Exception {
        ReadBracket readbracket = new ReadBracket();
        readbracket.setupMongoWithAuth();

    }

    public String sortSmashers() throws FileNotFoundException {
        System.out.println("Outputting db ranks: ");
        FindIterable<Document> cursor = collection.find();
        String serialize = JSON.serialize(cursor);
        System.out.println(serialize);
        return serialize;
    }

    public void generateRank(String eventName) throws Exception {
        //Returns each phase group id for each bracket played for event
        List<String> phasegroupids = returnPhaseGroupIds(eventName, "melee-singles");
        getAllPlayedSetsForTournament(phasegroupids);
        createSmasherObjectsForEntrants(phasegroupids);

        //Update smashers (list) with mean, deviation etc from db if exists
        updateSmasherObjectsWithMeanFromDb();

        //Update each players' rank for each match to each Smasher-object
        System.out.println("updateing for each round");
        updateSmashersRanksForEachRound();

        System.out.println("updating in db: (tryuing)");
        //Update each database instance of smasher with ranks updated for Smasher's objects
        updateSmasherRanksInDatabase();

        sortSmashersByRankDatabase();

    }

    private void dropDb() {
        database.drop();
    }

    public void sortSmashersByRankDatabase() {
        System.out.println("Outputting db ranks: ");

        collection.createIndex(Indexes.ascending("mean"));

        collection.find()
            .sort(Sorts.descending("mean"))
            .forEach(printBlock);

    }

    public void updateSmasherObjectsWithMeanFromDb() {
        System.out.println("trying to update with mean from db");
        for (Smasher smasher : smashers) {
            Document myDoc = collection.find(eq("_id", smasher.getId())).first();
            double mean = 0;
            double deviation = 0;
            double conservativestandarddeviationmultiplier = 0;

            if (!(myDoc == null)) {
                try {
                    mean = (double) myDoc.get("mean");
                    deviation = (double) myDoc.get("deviation");
                    conservativestandarddeviationmultiplier =
                        (double) myDoc.get("conservativestandarddeviationmultiplier");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                smasher
                    .setMeanDeviationAndDeviationMultiplier(mean, deviation, conservativestandarddeviationmultiplier);
            }
        }
    }

    public void updateSmasherRanksInDatabase() {
        //for each smasher create new documents which you'll insert using insert aLl in mongodb
        BasicDBObject searchQuery;
        BasicDBObject updateFields;
        //TODO: make database field values FiNAL so they cant get wrong

        for (Smasher smasher : smashers) {
            // if playertag exists
            searchQuery = new BasicDBObject("_id", smasher.getId());
            updateFields = new BasicDBObject();
            updateFields.append("mean", smasher.getMean());
            updateFields.append("deviation", smasher.getDeviation());
            updateFields.append("conservativestandarddeviationmultiplier",
                smasher.getConservativeStandardDeviationMultiplier());
            updateFields.append("playertag", smasher.getPlayerTag());
            BasicDBObject setQuery = new BasicDBObject();
            setQuery.append("$set", updateFields);
            collection.updateOne(searchQuery, setQuery, new UpdateOptions().upsert(true));
        }
    }


    public void setupMongoWithAuth(){

        MongoCredential prodAuth = MongoCredential.createCredential("username","database","asd".toCharArray());
        MongoCredential testAuth = MongoCredential.createPlainCredential("pankaj", "test", "pankaj123".toCharArray());

        List<MongoCredential> auths = new ArrayList<>();

        auths.add(prodAuth);
        auths.add(testAuth);

        ServerAddress serverAddress = new ServerAddress("localhost", 57839);
        MongoClient mongo = new MongoClient(serverAddress, auths);
        mongo.listDatabaseNames();
    }


    public void setupMongoDb(String hostName, String databaseName, String collectionName) {
        MongoClientOptions.Builder options = MongoClientOptions.builder();
        options.socketKeepAlive(true);
        options.connectTimeout(10000);
        options.socketTimeout(10000);

        MongoClientURI uri = new MongoClientURI(
            hostName,
            new MongoClientOptions.Builder(options.build()));

        MongoClient mongoClient = new MongoClient(uri);

        database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
    }

    private void createInstanceOfSmashersBeforeGeneratingNewRanks(String entrantId, String playerId, String playerTag) {
        if (!smashers.contains(playerId)) {
            Smasher<String> smasher = new Smasher<>(playerId, entrantId);
            smasher.setPlayerTag(playerTag);
            //Unless rank exists in db
            smasher.setDefaultRating();
            smashers.add(smasher);
        }
    }

    private void updateSmashersRanksForEachRound() {

        for (JSONObject bracketRound : winnerAndLoserIdsForEverSetPlayedAtAtournament) {

            Smasher<String> loser = null;
            Smasher<String> winner = null;

            for (Smasher<String> smasher : smashers) {

                String winnerId = null;
                String entrant2Id = null;

                try {
                    winnerId = bracketRound.getString("winnerId");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    entrant2Id =
                        (bracketRound.getString("entrant2Id") == null) ? "N/A" : bracketRound.getString("entrant2Id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String entrant1id = null;
                try {
                    entrant1id =
                        (bracketRound.getString("entrant1Id") == null) ? "N/A" : bracketRound.getString("entrant1Id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String loserId;
                String setPlayed = null;
                try {
                    setPlayed =
                        (bracketRound.getString("fullRoundText") == null) ? "N/A" : bracketRound.getString
                            ("fullRoundText");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (winnerId.equals(entrant1id)) {
                    loserId = entrant2Id;
                } else {
                    loserId = entrant1id;
                }

                if (smasher.getEntrantId().contains(winnerId)) {
                    winner = smasher;
                } else if (smasher.getEntrantId().contains(loserId)) {
                    loser = smasher;
                }
            }
            if (winner != null && loser != null) {
                trueskill.updatePlayerRanks(winner, loser);
            }
        }
    }

    private void getAllWinnersAndLosersForEachSet(JSONArray sets) throws JSONException {
        for (int i = 0; i < sets.length(); i++) {
            JSONObject setsObjects = sets.getJSONObject(i);

            try {
                String entrant2Id =
                    (setsObjects.getString("entrant2Id") == null) ? "N/A" : setsObjects.getString("entrant2Id");
                String entrant1id =
                    (setsObjects.getString("entrant1Id") == null) ? "N/A" : setsObjects.getString("entrant1Id");
                String winnerId =
                    (setsObjects.getString("winnerId") == null) ? "N/A" : setsObjects.getString("winnerId");
                String loserId;

                if (winnerId.equals(entrant1id)) {
                    loserId = entrant2Id;
                } else {
                    loserId = entrant1id;
                }

                if (!(winnerId.equals("null") || loserId.equals("null"))) {
                    winnerAndLoserIdsForEverSetPlayedAtAtournament.add(setsObjects);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> returnPhaseGroupIds(String tournamentName, String eventName) throws Exception {

        String apiPath = "/tournament/" + tournamentName + "/event/" + eventName + "?expand[]=groups";

        String json = getJsonForRequest(apiPath);
        jsonobject = new JSONObject(json);
        JSONArray groupIds = jsonobject.getJSONObject("entities").getJSONArray("groups");

        List<String> phaseGroupIds = new ArrayList<>();

        for (int i = 0; i < groupIds.length(); i++) {
            phaseGroupIds.add(groupIds.getJSONObject(i).get("id").toString());
        }
        return phaseGroupIds;

    }

    private void getAllPlayedSetsForTournament(List<String> phaseGroupIds) throws Exception {
        winnerAndLoserIdsForEverSetPlayedAtAtournament = new ArrayList<>();

        for (String id : phaseGroupIds) {
            String phaseGroupApiEndpoint = "/phase_group/" + id + "?expand[]=sets";

            try {
                String getPhaseGroupJson = getJsonForRequest(phaseGroupApiEndpoint);
                jsonobject = new JSONObject(getPhaseGroupJson);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("couldn't find phase group for phaseGroupApiEndpoint " + phaseGroupApiEndpoint);
            }

            try {
                JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");
                getAllWinnersAndLosersForEachSet(sets);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createSmasherObjectsForEntrants(List<String> phaseGroupIds) throws Exception {

        smashers = new ArrayList<>();
        HashMap<String, String> playerIdsMappedToEntrantIds = new HashMap<>();

        for (String id : phaseGroupIds) {
            String phaseGroupApiEndpoint = "/phase_group/" + id + "?expand[]=entrants";

            try {
                String getPhaseGroupJson = getJsonForRequest(phaseGroupApiEndpoint);
                jsonobject = new JSONObject(getPhaseGroupJson);
            } catch (Exception e) {
                System.out.println("wasnt able to get phasegroup json for endpoint " );
                e.printStackTrace();
            }

            JSONArray playerNames = null;
            try {
                playerNames = jsonobject.getJSONObject("entities").getJSONArray("player");
            } catch (JSONException e) {
                System.out.println("unable to find jsonobject.getJSONObject, printing jsonobject:: " + jsonobject);
                e.printStackTrace();
            }

            for (int i = 0; i < playerNames.length(); i += 1) {
                String entrantId = null;
                String playerId = null;
                String playerTag = null;
                try {
                    entrantId = playerNames.getJSONObject(i).get("entrantId").toString();
                    playerId = playerNames.getJSONObject(i).get("id").toString();
                    playerTag = playerNames.getJSONObject(i).get("gamerTag").toString();
                } catch (JSONException e) {
                    System.out.println("Had issues fetching either entrantid, playerid or playertag from json");
                    e.printStackTrace();
                }

                if (!playerIdsMappedToEntrantIds.containsKey(playerId)) {
                    if (!smashers.contains(entrantId)) {
                        createInstanceOfSmashersBeforeGeneratingNewRanks(entrantId, playerId, playerTag);
                    }
                    playerIdsMappedToEntrantIds.put(playerId, entrantId);
                }
            }
        }
    }

    private String getJsonForRequest(String path) throws Exception {
        return consumeApi.returnJsonForGetRequest(path);
    }

    public void dropDatabase() {
    database.drop();
    }
}
