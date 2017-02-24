import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Base64;

import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Before;

import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;

import spark.Spark;

/**
 * Created by k79689 on 22.02.17.
 */
public class TestSpark {

    private static TestSmashersDataobjects mongodbOperations = new TestSmashersDataobjects();

    public static void main(String[] args) throws FileNotFoundException {
        ReadBracket readBracket = new ReadBracket();
        readBracket.mongosetup2();



//        //mongo ds157839.mlab.com:57839/heroku_7btb6zs3 -u heroku_7btb6zs3 -p bvh12rab31k58n8ijraufist0@ds157839
//
//       // mongodb://heroku_7btb6zs3:bvh12rab31k58n8ijraufist0@ds157839.mlab.com:57839/heroku_7btb6zs3
//
//        /newpw: testpassordheroku1
        // user mongodb: heroku_7btb6zs3
//
        get("/rank", (req, res) -> readBracket.sortSmashers());
//
        after((req, res) -> {
            res.type("application/json");
        });

        before("/post", (request, response) -> {
            Boolean authenticated = false;
            String auth = request.headers("Authorization");
            if(auth != null && auth.startsWith("Basic")) {
                String b64Credentials = auth.substring("Basic".length()).trim();
                String credentials = new String(Base64.getDecoder().decode(b64Credentials));
                System.out.println(credentials);
                if(credentials.equals("admin:admin")) authenticated = true;
            }
            if(!authenticated) {
                response.header("WWW-Authenticate", "Basic realm=\"Restricted\"");
                halt(401, "You are not authorized to make this request. Contact Vdogg, and we'll see what we can do about it.");
                response.status(401);
            }
        });

        post("/post", (request, response) -> {
            // Create something
            String tournamentName = request.queryParams("eventid");
            readBracket.generateRank(tournamentName);
            return "Updated ranks with tournament with id: " + tournamentName;
        });
    }



    private String sortSmashersByRankDatabase() throws FileNotFoundException {

        System.out.println("Outputting db ranks: ");
        String hei = "";

        FindIterable<Document> cursor = mongodbOperations.getCollection().find();
        String serialize = JSON.serialize(cursor);
        System.out.println(serialize);

        try (PrintWriter out = new PrintWriter("smashers.json")) {
            out.println(serialize);
        }

        return serialize;
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    @Before
    public void postTournamentResults() {
        Main.getHerokuAssignedPort();
        mongodbOperations.setup();

    }

    public void submitTournament(String tournamentId) throws Exception {

        System.out.println("Oppdaterer turnering for turnering med id " + tournamentId);

        mongodbOperations.updateObjectWhenDuplicateId();

    }
}
