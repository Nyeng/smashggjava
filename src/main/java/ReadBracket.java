import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by k79689 on 17.01.17.
 */
public class ReadBracket extends ConsumeApi {

    private JSONObject jsonobject;
    private HashMap<String, String> playerIdsMappedToEntrantIds;


    public static void main(String[]args) throws Exception {
        ReadBracket readbracket = new ReadBracket();

        List<String> phasegroupids = readbracket.returnPhaseGroupIds();
        readbracket.iterateGroups(phasegroupids);

        //NOt using this one for now, but might come in use laterreadbracket.iteratePhases();
    }


    public void iterateSets(JSONArray sets) throws JSONException {

        for (int i = 0; i < sets.length(); i++) {
            JSONObject setsObjects = sets.getJSONObject(i);

            String entrant2Id = (setsObjects.getString("entrant2Id") == null) ? "N/A" : setsObjects.getString("entrant2Id");
            String entrant1id = (setsObjects.getString("entrant1Id") == null) ? "N/A" : setsObjects.getString("entrant1Id");
            String winnerId = (setsObjects.getString("winnerId") == null) ? "N/A" : setsObjects.getString("winnerId");

            String setPlayed = (setsObjects.getString("fullRoundText") == null) ? "N/A" : setsObjects.getString("fullRoundText");

            System.out.println("Set " + setPlayed);
            System.out.println("Entrant 1: " + entrant1id);
            System.out.println("Entrant 2 " + entrant2Id);
            System.out.println("Winner ID: " + winnerId);

            System.out.println("\n");
        }
    }

    //TODO: not using this one but might get handy later
    public void iteratePhases() throws Exception {
        String apiPath = "/tournament/house-of-smash-38/event/melee-singles?expand[]=phase"; // returns 3 phases
        String json = consumeApi(apiPath);

        jsonobject = new JSONObject(json);
        JSONArray phases = jsonobject.getJSONObject("entities").getJSONArray("phase");

        List<String> phaseIds = new ArrayList<>();

        for(int i=0; i<phases.length();i++){
            phaseIds.add(phases.getJSONObject(i).get("id").toString());
        }

        List<String> groupIds = new ArrayList<>();

        for(String phaseId : phaseIds){
            System.out.println("\n Phase number" + phaseId);
            json = consumeApi("/phase/" +phaseId + "?expand[]=groups");
            jsonobject = new JSONObject(json);
            JSONArray groups = jsonobject.getJSONObject("entities").getJSONArray("groups");

            for (int i=0;i<groups.length();i++){
                System.out.println("Group id: "+groups.getJSONObject(i).get("id"));
            }
        }
        //             String jsonPhase = consumeApi(/phase/101553?expand[]=groups);


    }

    public List<String> returnPhaseGroupIds() throws Exception {
        String apiPath = "/tournament/house-of-smash-38/event/melee-singles?expand[]=groups";
        //Fetch groups from above url endpoint ^

        // Return all phases: https://api.smash.gg//tournament/house-of-smash-38/event/melee-singles?expand[]=phase
        // Amateur bracket, pools, pro bracket
        // ids; 101553, 101554, 101555
        //https://api.smash.gg/phase/101555


        String json = consumeApi(apiPath);
        jsonobject = new JSONObject(json);
        JSONArray groupIds = jsonobject.getJSONObject("entities").getJSONArray("groups");

        List<String> phaseGroupIds = new ArrayList<>();

        for (int i = 0; i<groupIds.length(); i++){
            phaseGroupIds.add(groupIds.getJSONObject(i).get("id").toString());
            System.out.println(groupIds.getJSONObject(i).get("id"));

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
