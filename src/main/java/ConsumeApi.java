import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by k79689 on 16.01.17.
 */
public class ConsumeApi {


    public String parseGetRequestToJson(String path) throws Exception {
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

    public String getBaseApiEndpoint(){
        return "https://api.smash.gg";
    }
}
