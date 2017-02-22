import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
//        mongodbOperations.setup();
//        mongodbOperations.getDatabase().drop();
        ReadBracket readBracket = new ReadBracket();

        TestSpark spark = new TestSpark();

        post("/post", (request, response) -> {
            // Create something
            String parameter1 = request.queryParams("eventid");

            readBracket.generateRank(parameter1);
//            spark.submitTournament(parameter1);

            return "Updated ranks with tournament with id: " +parameter1;
        });

//        spark.sortSmashersByRankDatabase();

        get("/rank", (req, res) -> readBracket.sortSmashers());


        after((req, res) -> {
            res.type("application/json");
        });

    }

    private String sortSmashersByRankDatabase() throws FileNotFoundException {


        System.out.println("Outputting db ranks: ");
        String hei = "";

        FindIterable<Document> cursor = mongodbOperations.getCollection().find();
        String serialize = JSON.serialize(cursor);
        System.out.println(serialize);

        try(  PrintWriter out = new PrintWriter( "smashers.json" )  ){
            out.println( serialize );
        }

        return serialize;
    }


    @AfterClass
    public static void afterClass() {
        Spark.stop();
        mongodbOperations.dropDatabase();
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
