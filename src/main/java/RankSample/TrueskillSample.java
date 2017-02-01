package RankSample;

import static RankSample.SmashMatchup.defaultGameInfo;

import java.util.Collection;
import java.util.Map;

import jskills.IPlayer;
import jskills.ITeam;
import jskills.Player;
import jskills.Rating;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

/**
 * Created by k79689 on 25.01.17.
 */
public class TrueskillSample {

    TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

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

        System.out.println("new ranks: " + winner.toString());
        System.out.println("new ranks: " + loser.toString());

        System.out.println("\n Ratings player one" + winner.getRating());
        System.out.println("Ratings player two" + loser.getRating());

    }

    public static void main(String[] args) {

        System.out.println("μ means average skill of player and σσ is a confidence of the guessed rating");
        TrueskillSample generator = new TrueskillSample();

        Smasher<String> vdogg = new Smasher<>("Vdogg", Smasher.DEFAULTRATING);
        Smasher<String> aske = new Smasher<>("Aske", Smasher.DEFAULTRATING);

        //smasherOne not used for now, update that later to use third parameter to choose winner

        for (int i = 0; i < 5; i++) {
            //looping through 10 wins for one player
            generator.updatePlayerRanks(vdogg, aske);
        }

        Smasher<String> sverre = new Smasher<>("Sverre", (Smasher.DEFAULTRATING));
        sverre.setMeanDeviationAndDeviationMultiplier(50.0, 1.0, 1.0);

        //Low ranked player gets win vs high rank player
        generator.updatePlayerRanks(sverre, vdogg);

        System.out.println(sverre.getRating());
    }

    public Collection<ITeam> generateMatchupWithNewRanks(Player playerOne, double meanPlayerOne,
        double deviationPlayerOne, Player playerTwo, double meanPlayerTwo, double deviationPlayerTwo) {

        Rating ratingPlayer1 = new Rating(meanPlayerOne, deviationPlayerOne);
        Rating ratingPlayer2 = new Rating(meanPlayerTwo, deviationPlayerTwo);

        SmashMatchup matchup = new SmashMatchup(playerOne, ratingPlayer1, playerTwo, ratingPlayer2);
        return matchup.returnMatchup();
    }

}
