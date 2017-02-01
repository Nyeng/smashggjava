import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import RankSample.Smasher;
import RankSample.TrueskillSample;

/**
 * Created by k79689 on 17.01.17.
 */
public class ReadBracket extends ConsumeApi {

    private JSONObject jsonobject;
    private HashMap<String, String> playerIdsMappedToEntrantIds;
    private List<JSONObject> winnerAndLoserIdsForEverSetPlayedAtAtournament;
    private int countedSets;
    private List<Smasher<String>> smashers;
    private Smasher smasher;

    public static void main(String[]args) throws Exception {
        ReadBracket readbracket = new ReadBracket();
        TrueskillSample trueskill = new TrueskillSample();

        List<String> phasegroupids = readbracket.returnPhaseGroupIds("house-of-smash-33", "melee-singles");
        readbracket.iterateGroups(phasegroupids);

        for (Map.Entry<String, String> players : readbracket.playerIdsMappedToEntrantIds.entrySet()) {
            Smasher<String> smasher = new Smasher<>(players.getKey(),players.getValue());
            smasher.setDefaultRating();
            readbracket.smashers.add(smasher);
        }

        for (JSONObject bracketRound : readbracket.winnerAndLoserIdsForEverSetPlayedAtAtournament) {

            Smasher<String> loser = null;
            Smasher<String> winner = null;
            // System.out.println("winner id " +winnerId);
            for (Smasher<String> smasher : readbracket.smashers) {
                String winnerId = bracketRound.getString("winnerId");
                String entrant2Id = (bracketRound.getString("entrant2Id") == null) ? "N/A" : bracketRound.getString("entrant2Id");
                String entrant1id = (bracketRound.getString("entrant1Id") == null) ? "N/A" : bracketRound.getString("entrant1Id");
                String loserId;
                String setPlayed =
                    (bracketRound.getString("fullRoundText") == null) ? "N/A" : bracketRound.getString("fullRoundText");

                if (winnerId.equals(entrant1id)) {
                    loserId = entrant2Id;
                } else {
                    loserId = entrant1id;
                }

                if (smasher.getEntrantId().contains(winnerId)) {
                    winner = smasher;
                    System.out.println("Bracket round " + setPlayed);
                }
                else if (smasher.getEntrantId().contains(loserId))
                {
                    loser = smasher;
                }
            }
            if(winner!= null && loser !=null){
                System.out.println("Loser id of player: " + loser.getId());
                System.out.println("winner id of player: " + winner.getId());
                trueskill.updatePlayerRanks(winner,loser);
            }
        }

        // Smashers sorted:
        readbracket.smashers
            .stream()
            .sorted((e2, e1) -> Double.compare(e1.getMean(),
                e2.getMean()))
            .forEach(System.out::println);

    }


    public void iterateSets(JSONArray sets) throws JSONException {
        // First find old rank based on some rank stored in database. Then generate a map with every result for a player for a tournament.
        //So far only showing how to iterate bracket, not storing the data yet. Need to figure out how to process results for a rank api to know how to iterate
        winnerAndLoserIdsForEverSetPlayedAtAtournament = new ArrayList<>();

        for (int i = 0; i < sets.length(); i++) {
            JSONObject setsObjects = sets.getJSONObject(i);

            String entrant2Id = (setsObjects.getString("entrant2Id") == null) ? "N/A" : setsObjects.getString("entrant2Id");
            String entrant1id = (setsObjects.getString("entrant1Id") == null) ? "N/A" : setsObjects.getString("entrant1Id");
            String winnerId = (setsObjects.getString("winnerId") == null) ? "N/A" : setsObjects.getString("winnerId");
            String loserId;

            if (winnerId.equals(entrant1id)){
                loserId = entrant2Id;
            }
            else{
                loserId = entrant1id;
            }

            if (!(winnerId.equals("null") || loserId.equals("null"))) {
              //  System.out.println("winner is "+winnerId +", loser is "+loserId);
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

        for (String id : phaseGroupIds){
            String phaseGroupApiEndpoint = "/phase_group/" +id + "?expand[]=entrants&expand[]=sets";
            System.out.println(phaseGroupApiEndpoint);


            String json = consumeApi(phaseGroupApiEndpoint);
            jsonobject = new JSONObject(json);

            JSONArray playerNames = jsonobject.getJSONObject("entities").getJSONArray("player");
            JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");

            iterateSets(sets);

            smashers = new ArrayList<>(playerNames.length());
            playerIdsMappedToEntrantIds = new HashMap<>(playerNames.length());

            for(int i = 0; i<playerNames.length(); i+=1){
                String entrantId = playerNames.getJSONObject(i).get("entrantId").toString();
                String playerId =  playerNames.getJSONObject(i).get("id" ).toString();
                //String playerTag =  playerNames.getJSONObject(i).get("gamerTag" ).toString();
                String value = playerIdsMappedToEntrantIds.get(playerId);
                if (value == null){
                    //adds same player twice, but keeps them unique cus HashMap
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
