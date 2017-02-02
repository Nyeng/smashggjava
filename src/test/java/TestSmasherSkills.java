import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import RankSample.Smasher;
import RankSample.TrueSkillImplementation;

/**
 * Created by k79689 on 02.02.17.
 */
public class TestSmasherSkills {


    @Test
    public void measureRanksOnOneWin() {

        System.out.println("μ means average skill of player and σσ is a confidence of the guessed rating");
        TrueSkillImplementation trueSkill = new TrueSkillImplementation();

        Smasher<String> smasherWinner = new Smasher<>("Vdogg", Smasher.DEFAULTRATING);
        Smasher<String> smasherLoser = new Smasher<>("Aske", Smasher.DEFAULTRATING);

        for (int i = 0; i < 2; i++) {
            //looping through i wins for one player
            trueSkill.updatePlayerRanks(smasherWinner, smasherLoser);
        }

        assertThat("Winner should have higher rank ",smasherWinner.getMean() > smasherLoser.getMean());
    }

    @Test
    public void updateSkillsOnExistingRank(){
        //Two Smashers already have ranks, now


    }


}
