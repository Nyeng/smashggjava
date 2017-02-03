import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import RankSample.Smasher;
import RankSample.TrueSkillImplementation;

/**
 * Created by Vdawg on 17.01.17.
 */
public class ReadBracket {

    private JSONObject jsonobject;
    private HashMap<String, String> playerIdsMappedToEntrantIds;
    private List<JSONObject> winnerAndLoserIdsForEverSetPlayedAtAtournament;
    private List<Smasher<String>> smashers;

    private ConsumeApi consumeApi = new ConsumeApi();
    private TrueSkillImplementation trueskill = new TrueSkillImplementation();

    public static void main(String[] args) throws Exception {
        ReadBracket readbracket = new ReadBracket();

        //figure out contestants for tournament

        //Returns each phase group id for each bracket played for event
        List<String> phasegroupids = readbracket.returnPhaseGroupIds("house-of-smash-34", "melee-singles");
        readbracket.iterateGroups(phasegroupids);

        System.out.println("hashmap size " + readbracket.playerIdsMappedToEntrantIds.size());

        for(String value : readbracket.playerIdsMappedToEntrantIds.keySet()){
            System.out.println(value);
        }

        //Creating instances of new smashers, with default rating and player id + entrant id so far
    //    readbracket.createInstanceOfSmashersBeforeGeneratingNewRanks();

        //Update each players' rank for each match
       // readbracket.updateSmashersRanksForEachRound();
      //  readbracket.sortSmashersByRank();
    }

    private void createInstanceOfSmashersBeforeGeneratingNewRanks() {
        for (Map.Entry<String, String> players : playerIdsMappedToEntrantIds.entrySet()) {
            Smasher<String> smasher = new Smasher<>(players.getKey(), players.getValue());
            smasher.setDefaultRating();
            //createListOfSmashers(smasher);
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
                String setPlayed =
                    null;
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

    private void sortSmashersByRank() {
        // Smashers sorted:
        smashers
            .stream()
            .sorted((e2, e1) -> Double.compare(e1.getMean(),
                e2.getMean()))
            .forEach(System.out::println);
    }

    private void iterateSets(JSONArray sets) throws JSONException {
        //So far only showing how to iterate bracket, not storing the data yet. Need to figure out how to process
        // results for a rank api to know how to iterate
        winnerAndLoserIdsForEverSetPlayedAtAtournament = new ArrayList<>();

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

    private void iterateGroups(List<String> phaseGroupIds) throws Exception {

        playerIdsMappedToEntrantIds = new HashMap<>();
        smashers = new ArrayList<>();



        for (String id : phaseGroupIds) {
            String phaseGroupApiEndpoint = "/phase_group/" + id + "?expand[]=entrants&expand[]=sets";

            String getPhaseGroupJson = getJsonForRequest(phaseGroupApiEndpoint);
            jsonobject = new JSONObject(getPhaseGroupJson);

            JSONArray playerNames = jsonobject.getJSONObject("entities").getJSONArray("player");
            JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");

            iterateSets(sets);

            //(playerNames.length());

            for (int i = 0; i < playerNames.length(); i += 1) {
                String entrantId = playerNames.getJSONObject(i).get("entrantId").toString();
                String playerId = playerNames.getJSONObject(i).get("id").toString();
                String playerTag =  playerNames.getJSONObject(i).get("gamerTag" ).toString();

                if(!playerIdsMappedToEntrantIds.containsKey(playerId)){
                    playerIdsMappedToEntrantIds.put(playerId, entrantId);
                    System.out.println("adding player "+ playerTag);
                }
            }
        }


    }

    private String getJsonForRequest(String path) throws Exception {
        return consumeApi.parseGetRequestToJson(path);
    }

}
