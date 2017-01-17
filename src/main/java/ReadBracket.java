import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by k79689 on 17.01.17.
 */
public class ReadBracket extends ConsumeApi {

    private JSONObject jsonobject;

    public static void main(String[]args) throws Exception {
        ReadBracket readbracket = new ReadBracket();
//        String json = readbracket.consumeApi("/phase_group/208986?expand[]=sets");
//
//        JSONArray sets = readbracket.returnSetsObjects();
//        readbracket.iterateSets(sets);

        List<String> phasegroupids = readbracket.returnPhaseGroupIds();
        readbracket.returnPhaseGroups(phasegroupids);


    }

    public JSONArray returnSetsObjects() throws Exception {
        String json = consumeApi("/phase_group/208986?expand[]=sets");

        JSONObject jsonobject = new JSONObject(json);
        JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");

        return sets;
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


    public void mapParticipantIdToPlayerId(){
        // https://api.smash.gg//tournament/house-of-smash-38/event/melee-singles?expand[]=groups&expand[]=entrants



    }


    public List<String> returnPhaseGroupIds() throws Exception {
        String apiPath = "/tournament/house-of-smash-38/event/melee-singles?expand[]=groups";
        //Fetch groups from above url endpoint ^

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

    public void returnPhaseGroups(List<String> phaseGroupIds) throws Exception {
        for (String id : phaseGroupIds){
            System.out.println("Each id is "+ id);

            String apiEndpoint = "/phase_group/" +id + "?expand[]=sets&expand=entrants";

            System.out.println("Current API Endpoint: "+ apiEndpoint);

            String json = consumeApi(apiEndpoint);
            jsonobject = new JSONObject(json);

            JSONArray playerNames = jsonobject.getJSONObject("entities").getJSONArray("player");


            for(int i = 0; i<playerNames.length(); i+=1){
                System.out.println( "entrant id: " +playerNames.getJSONObject(i).get("entrantId"));
                System.out.println("player id: " +playerNames.getJSONObject(i).get("id" ));
                System.out.println("\n");
                if (i >  4){
                    break;
                }
            }





            break;
        }

        String apiEndpoint = "/phase_group/305690";



    }

    public void steps(){



        //readBracket from tourney URL:
        //base url: tournament/house-of-smash-38/events/melee-singles/brackets

        // https://api.smash.gg/tournament/house-of-smash-38/event/melee-singles?expand[]=groups

        // use these IDs to fetch different phase groups for that tournament


//        Shortly explained:
//        Use tournament slug to find:
//        - event id, which you use to find groups, which in turn you use to find:
//        phase groups: https://api.smash.gg/tournament/groups/phase_group/phaseGroupId
//
//        Then expand the phase group with entrants and sets to iterate wins/losses:
//        https://api.smash.gg//phase_group/208986?expand[]=sets&expand[]=entrants
    }



    @Override
    public String consumeApi(String path) throws Exception {
        return super.consumeApi(path);
    }





}
