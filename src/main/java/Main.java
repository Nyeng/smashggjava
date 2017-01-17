/**
 * Created by k79689 on 17.01.17.
 */
public class Main {

    public ConsumeApi consumeApi = new ConsumeApi();

    public Main(){

    }

    public String readPlayer() throws Exception {
        String playerPath = "/player/10627";
        String json = consumeApi.consumeApi(playerPath);

        return json;
    }
}
