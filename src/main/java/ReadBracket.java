import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import RankSample.Smasher;

/**
 * Created by k79689 on 17.01.17.
 */
public class ReadBracket extends ConsumeApi {

    private JSONObject jsonobject;
    private HashMap<String, String> playerIdsMappedToEntrantIds;
    private List<JSONObject> winnerAndLoserIdsForEverSetPlayedAtAtournament;
    private int countedSets;

    public static void main(String[]args) throws Exception {
        ReadBracket readbracket = new ReadBracket();

        List<String> phasegroupids = readbracket.returnPhaseGroupIds("house-of-smash-32", "melee-singles");
        readbracket.iterateGroups(phasegroupids);

//        String Vdogg = readbracket.playerIdsMappedToEntrantIds.get("10627");
//        System.out.println(Vdogg);

        JSONObject testObject = new JSONObject();

        int counter = 0;

        for (String player : readbracket.playerIdsMappedToEntrantIds.keySet()) {
            // create object for each smasher
            Smasher smasher = new Smasher<>(player, readbracket.playerIdsMappedToEntrantIds.get(player));


            for (JSONObject object : readbracket.winnerAndLoserIdsForEverSetPlayedAtAtournament) {
                //for EVERY set you HAVE to: update rank for each player, so that all the following matches will be correct
                //Iterate ieach set first, now only iterating one set
                counter++;
                if (object.getString("winnerId").equals(smasher.getEntrantId())){
                    System.out.println("Found matches for player "+ smasher.getId());
                    System.out.println("played this match: " + object.getString("fullRoundText"));
                }



            }
            // iterate over all sets

        }

        System.out.println("Counter" + counter);
    }


    public void iterateSets(JSONArray sets) throws JSONException {
        System.out.println("Set lengths: " + sets.length());
        System.out.println("Count sets");
        countedSets += sets.length();

        // First find old rank based on some rank stored in database. Then generate a map with every result for a player for a tournament.
        //So far only showing how to iterate bracket, not storing the data yet. Need to figure out how to process results for a rank api to know how to iterate
        winnerAndLoserIdsForEverSetPlayedAtAtournament = new ArrayList<>();

        for (int i = 0; i < sets.length(); i++) {
            JSONObject setsObjects = sets.getJSONObject(i);

            String entrant2Id = (setsObjects.getString("entrant2Id") == null) ? "N/A" : setsObjects.getString("entrant2Id");
            String entrant1id = (setsObjects.getString("entrant1Id") == null) ? "N/A" : setsObjects.getString("entrant1Id");
            String winnerId = (setsObjects.getString("winnerId") == null) ? "N/A" : setsObjects.getString("winnerId");
            String loserId;
            String setPlayed = (setsObjects.getString("fullRoundText") == null) ? "N/A" : setsObjects.getString("fullRoundText");

//            System.out.println("Set " + setPlayed);
//            System.out.println("Entrant 1: " + entrant1id);
//            System.out.println("Entrant 2 " + entrant2Id);
//            System.out.println("Winner ID: " + winnerId);
            if (winnerId.equals(entrant1id)){
                loserId = entrant2Id;
            }
            else{
                loserId = entrant1id;
            }

            if (! (winnerId.equals("null") || loserId.equals("null"))) {
                System.out.println("winner is "+winnerId +", loser is "+loserId);
                winnerAndLoserIdsForEverSetPlayedAtAtournament.add(setsObjects);
                countedSets+=1;
            }
            else{
            }
        }
    }




    public List<String> returnPhaseGroupIds(String tournamentName, String eventName) throws Exception {

        String apiPath = "/tournament/" + tournamentName + "/event/"+eventName +"?expand[]=groups";

        String json = consumeApi(apiPath);
        jsonobject = new JSONObject(json);
        JSONArray groupIds = jsonobject.getJSONObject("entities").getJSONArray("groups");

        List<String> phaseGroupIds = new ArrayList<>();

        for (int i = 0; i<groupIds.length(); i++){
            phaseGroupIds.add(groupIds.getJSONObject(i).get("id").toString());
        }
        return phaseGroupIds;

    }

    public void iterateGroups(List<String> phaseGroupIds) throws Exception {

        playerIdsMappedToEntrantIds = new HashMap<>();

        for (String id : phaseGroupIds){
            String phaseGroupApiEndpoint = "/phase_group/" +id + "?expand[]=entrants&expand[]=sets";

            String json = consumeApi(phaseGroupApiEndpoint);
            jsonobject = new JSONObject(json);

            JSONArray playerNames = jsonobject.getJSONObject("entities").getJSONArray("player");
            JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");

            iterateSets(sets);


            for(int i = 0; i<playerNames.length(); i+=1){
                String entrantId = playerNames.getJSONObject(i).get("entrantId").toString();
                String playerId =  playerNames.getJSONObject(i).get("id" ).toString();
                //String playerTag =  playerNames.getJSONObject(i).get("gamerTag" ).toString();
                String value = playerIdsMappedToEntrantIds.get(playerId);
                if (value == null){
                    playerIdsMappedToEntrantIds.put(playerId,entrantId);
                }
            }
        }

//        for (Map.Entry<String, String> entry : playerIdsMappedToEntrantIds.entrySet()) {

//            String key = entry.getKey();
//            String value = entry.getValue();
//            System.out.println("Key: "+key + ", value: "+value);
//        }

    }


    @Override
    public String consumeApi(String path) throws Exception {
        return super.consumeApi(path);
    }





}
