import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by k79689 on 17.01.17.
 */
public class ReadBracket extends ConsumeApi {


    public static void main(String[]args) throws Exception {
        ReadBracket readbracket = new ReadBracket();
        String json = readbracket.consumeApi("/phase_group/208986?expand[]=sets");

        JSONArray sets = readbracket.returnSetsObjects();
        readbracket.iterateSets(sets);
    }


    public JSONArray returnSetsObjects() throws Exception {
        String json = consumeApi("/phase_group/208986?expand[]=sets");

        JSONObject jsonobject = new JSONObject(json);
        JSONArray sets = jsonobject.getJSONObject("entities").getJSONArray("sets");

        return sets;

    }

    public void iterateSets(JSONArray sets) throws JSONException {
        long startTime = System.currentTimeMillis();


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

        long endTime = System.currentTimeMillis();

        System.out.println("End time - start time: " + (endTime-startTime));
    }



    @Override
    public String consumeApi(String path) throws Exception {
        return super.consumeApi(path);
    }





}
