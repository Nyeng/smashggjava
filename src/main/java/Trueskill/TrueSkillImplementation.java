package Trueskill;

import java.util.Collection;
import java.util.Map;

import jskills.GameInfo;
import jskills.IPlayer;
import jskills.ITeam;
import jskills.Rating;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

/**
 * Created by k79689 on 25.01.17.
 */
public class TrueSkillImplementation {

    private GameInfo defaultGameInfo = GameInfo.getDefaultGameInfo();

    private TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

    public void updatePlayerRanks(Smasher<String> winner, Smasher<String> loser) {

        SmashMatchup playerComparison = new SmashMatchup(
            winner, winner.getRating()
            , loser, loser.getRating()
        );

        Collection<ITeam> match = playerComparison.returnMatchup();

        //figure out a way to update this game info shiit.
        Map<IPlayer, Rating> Rating = calculator.calculateNewRatings(defaultGameInfo, match, 1, 2);

        winner.setMeanDeviationAndDeviationMultiplier(Rating.get(winner).getMean(),
            Rating.get(winner).getStandardDeviation(),
            Rating.get(winner).getConservativeStandardDeviationMultiplier()
        );

        loser.setMeanDeviationAndDeviationMultiplier(Rating.get(loser).getMean(),
            Rating.get(loser).getStandardDeviation(),
            Rating.get(loser).getConservativeStandardDeviationMultiplier());
    }

}
