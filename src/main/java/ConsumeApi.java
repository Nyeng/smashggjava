import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by k79689 on 16.01.17.
 */
public class ConsumeApi {


    public static void main(String[] args){
//
//        processing api with url https://api.smash.gg//phase_group/313999?expand[]=sets
//        processing api with url https://api.smash.gg//phase_group/314000?expand[]=sets
//        processing api with url https://api.smash.gg//phase_group/314001?expand[]=sets

        ConsumeApi api = new ConsumeApi();

        api.returnJsonForGetRequest("phase_group/313999?expand[]=sets");
        api.returnJsonForGetRequest("phase_group/314000?expand[]=sets");
        api.returnJsonForGetRequest("phase_group/314001?expand[]=sets");

    }

    public String getBaseApiEndpoint(){
        return "https://api.smash.gg";
    }

    public String returnJsonForGetRequest(String path) {
        String uri = getBaseApiEndpoint() + "/" + path;
        String responseBody = null;

        System.out.println("processing api with url " + uri);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);

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
        try {
            responseBody = httpclient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBody;

    }

}
