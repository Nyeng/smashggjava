package RankSample;

import static RankSample.SmashMatchup.returnGameinfo;

import java.util.Collection;
import java.util.Map;

import jskills.GameInfo;
import jskills.IPlayer;
import jskills.ITeam;
import jskills.Player;
import jskills.Rating;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

/**
 * Created by k79689 on 25.01.17.
 */
public class TrueskillSample {

    public static void main(String[]args){
        TrueskillSample generator = new TrueskillSample();

        Player<String> smasher1 = new Player<>("Bose");
        Player<String> smasher2 = new Player<>("Askelink");

        SmashMatchup matchup = new SmashMatchup(smasher1,smasher2);
        Collection<ITeam> match = matchup.returnMatchup();

        TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

        GameInfo defaultGameInfo = returnGameinfo();

        // first result parameter is RANK for smasher1, second is RANK for smasher2
        // 1 means rank in terms of placement, 1 = victory, 2 = loss.

        Map<IPlayer, Rating> resultsAskeWins = calculator.calculateNewRatings(defaultGameInfo, match, 2, 1);
        Map<IPlayer, Rating> results2BoseWins = calculator.calculateNewRatings(defaultGameInfo, match, 1, 2);

        System.out.println("Aske wins results" + resultsAskeWins);
        System.out.println("Bose wins results "+results2BoseWins);

//        double newMeanAske = results.get(smasher1).getMean();
//        double newDeviationAske = results.get(smasher2).getStandardDeviation();
//        Rating newAskeRating = new Rating(newMeanAske,newDeviationAske);

        //System.out.println("Aske rating: " + newAskeRating);

    }
}
