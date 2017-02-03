import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import RankSample.Smasher;
import RankSample.TrueSkillImplementation;

/**
 * Created by k79689 on 02.02.17.
 */
public class TestSmasherSkills {

    private TrueSkillImplementation trueSkill;

    @Before
    public void setup() {
        trueSkill = new TrueSkillImplementation();
        System.out.println("μ means average skill of player and σσ is a confidence of the guessed rating");

    }

    @Test
    public void measureRanksOnOneWin() {
        //Creating instances of SMashers with same default rating
        Smasher<String> smasherWinner = new Smasher<>("Vdogg", Smasher.DEFAULTRATING);
        Smasher<String> smasherLoser = new Smasher<>("Aske", Smasher.DEFAULTRATING);

        for (int i = 0; i < 2; i++) {
            //looping through i wins for one player

            trueSkill.updatePlayerRanks(smasherWinner, smasherLoser);
        }

        assertThat("Winner should have higher rank ", smasherWinner.getMean() > smasherLoser.getMean());
    }

    @Test
    public void updateSkillsOnExistingRank() {
        //Two Smashers already have ranks, now
        double meanWinner = 50.0;
        double DeviationMultiplierWinner = 5.0;
        double deviationWinner = 5.0;

        double meanLoser = 20.0;
        double deviationMultiplierLoser = 5;
        double deviationLoser = 4;

        Smasher<String> winner = new Smasher<>("Vdawg");
        winner.setMeanDeviationAndDeviationMultiplier(meanWinner, deviationWinner, DeviationMultiplierWinner);

        Smasher<String> loser = new Smasher<>("Zorc");
        loser.setMeanDeviationAndDeviationMultiplier(meanLoser, deviationLoser, deviationMultiplierLoser);

        System.out.println(winner.getRating());

        assertThat(
            "Winner should be created with highest rank: " + winner.getRating() + " losers rank: " + loser.getRating(),
            winner.getMean() > loser.getMean());
    }

}
