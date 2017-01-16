import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import objectclasses.Player;

/**
 * Created by k79689 on 16.01.17.
 */
public class Apisample {

    private ObjectMapper mapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, true);
        return mapper;
    }


    public static void main(String[]args) throws Exception {
        Apisample sample = new Apisample();

        String json =  sample.readPlayer();
        System.out.println(json);
    }


    public String consumeApi(String path) throws Exception {
        String responseBody;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            org.apache.http.client.methods.HttpGet
                httpget = new org.apache.http.client.methods.HttpGet(getBaseApiEndpoint() + path);

            // Create a custom response handler
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new org.apache.http.client.ClientProtocolException(
                        "Unexpected response status: " + status + "for path: " + path +
                            "\n whole path:" + getBaseApiEndpoint() + path);
                }
            };
            responseBody = httpclient.execute(httpget, responseHandler);
        }
        return responseBody;
    }


    public String readPlayer() throws Exception {
        String playerPath = "/player/10627";
        String json = consumeApi(playerPath);


        ObjectMapper mapper = mapper();

        Player player = mapper.readValue(json, Player.class);

        System.out.println(player.getGamerTag());

        return "";

    }

    public String getBaseApiEndpoint(){
        return "https://api.smash.gg";
    }
}
