import static RankSample.SmashMatchup.returnGameinfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import RankSample.SmashMatchup;
import jskills.GameInfo;
import jskills.IPlayer;
import jskills.ITeam;
import jskills.Player;
import jskills.Rating;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

/**
 * Created by k79689 on 17.01.17.
 */
public class ReadBracket extends ConsumeApi {

    private JSONObject jsonobject;
    private HashMap<String, String> playerIdsMappedToEntrantIds;


    public static void main(String[]args) throws Exception {
        ReadBracket readbracket = new ReadBracket();

        List<String> phasegroupids = readbracket.returnPhaseGroupIds("house-of-smash-32","melee-singles");
        readbracket.iterateGroups(phasegroupids);
    }


    public void iterateSets(JSONArray sets) throws JSONException {

        HashMap<String, List<String>> playerResults = new HashMap<>();
        List<String> listResults = new ArrayList<>();

        // First find old rank based on some rank stored in database. Then generate a map with every result for a player for a tournament.


        //So far only showing how to iterate bracket, not storing the data yet. Need to figure out how to process results for a rank api to know how to iterate

        for (int i = 0; i < sets.length(); i++) {
            JSONObject setsObjects = sets.getJSONObject(i);

            String entrant2Id = (setsObjects.getString("entrant2Id") == null) ? "N/A" : setsObjects.getString("entrant2Id");
            String entrant1id = (setsObjects.getString("entrant1Id") == null) ? "N/A" : setsObjects.getString("entrant1Id");
            String winnerId = (setsObjects.getString("winnerId") == null) ? "N/A" : setsObjects.getString("winnerId");

            Player smasher1 = new Player<>(entrant1id);
            Player smasher2 = new Player<>(entrant2Id);

            SmashMatchup smashmatchup = new SmashMatchup(smasher1,smasher2);
            Collection<ITeam> players  = smashmatchup.returnMatchup();
            TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

            GameInfo defaultGameInfo = returnGameinfo();


            int resultPlayer1,resultPlayer2;

            if (winnerId.equals(entrant1id)){
                System.out.println("winner is " + entrant1id +", loser is "+entrant2Id);
                resultPlayer1 = 1;
                resultPlayer2 = 2;
            }
            else{
                System.out.println("winner is "+entrant2Id +", loser is "+entrant2Id);
                resultPlayer1 = 2;
                resultPlayer2 = 1;
            }



            // first result parameter is RANK for smasher1, second is RANK for smasher2
            Map<IPlayer, Rating> setResult = calculator.calculateNewRatings(defaultGameInfo, players, resultPlayer1, resultPlayer2);

            double newMeanSmasher1 = setResult.get(smasher1).getMean();
            double newMeanSmasher2 = setResult.get(smasher2).getStandardDeviation();

            Rating newPlayerRating = new Rating(newMeanSmasher1,newMeanSmasher2);

            System.out.println("new Rating for players: " + newPlayerRating);

            String setPlayed = (setsObjects.getString("fullRoundText") == null) ? "N/A" : setsObjects.getString("fullRoundText");

            System.out.println("Set " + setPlayed);
            System.out.println("Entrant 1: " + entrant1id);
            System.out.println("Entrant 2 " + entrant2Id);
            System.out.println("Winner ID: " + winnerId);

            System.out.println("\n");

            break;


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
            //System.out.println(groupIds.getJSONObject(i).get("id"));

        }
        return phaseGroupIds;
    }

    public void iterateGroups(List<String> phaseGroupIds) throws Exception {

        playerIdsMappedToEntrantIds = new HashMap<>();

        for (String id : phaseGroupIds){
            String apiEndpoint = "/phase_group/" +id + "?expand[]=entrants&expand[]=sets";

            String json = consumeApi(apiEndpoint);
            jsonobject = new JSONObject(json);

            JSONArray playerNames = jsonobject.getJSONObject("entities").getJSONArray("player");
            System.out.println(playerNames.length());

            JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");

            iterateSets(sets);

            for(int i = 0; i<playerNames.length(); i+=1){
                String entrantId = playerNames.getJSONObject(i).get("entrantId").toString();
                String playerId =  playerNames.getJSONObject(i).get("id" ).toString();
                String playerTag =  playerNames.getJSONObject(i).get("gamerTag" ).toString();

                String value = playerIdsMappedToEntrantIds.get(playerId);
                if (value == null){
                    playerIdsMappedToEntrantIds.put(playerId,"Entrant id " +entrantId + ", Player tag: " +playerTag);
                }
            }
        }

        for (Map.Entry<String, String> entry : playerIdsMappedToEntrantIds.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Key: "+key + ", value: "+value);
        }
    }


    @Override
    public String consumeApi(String path) throws Exception {
        return super.consumeApi(path);
    }





}
