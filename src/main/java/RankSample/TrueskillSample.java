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


    public void updatePlayerRanks(Smasher<String> smasherOne, Smasher<String> smasherTwo, Smasher<String> winner){

        SmashMatchup playerComparison = new SmashMatchup(
            smasherOne,smasherOne.getRating()
            ,smasherTwo,smasherTwo.getRating()
        );

        Collection<ITeam> match = playerComparison.returnMatchup();

        //figure out a way to update this game info shiit.
        Map<IPlayer, Rating> Rating = calculator.calculateNewRatings(defaultGameInfo, match, 2, 1);

        smasherOne.setMeanDeviationAndDeviationMultiplier(Rating.get(smasherOne).getMean(),
            Rating.get(smasherOne).getStandardDeviation(),
            Rating.get(smasherOne).getConservativeStandardDeviationMultiplier()
        );

        smasherTwo.setMeanDeviationAndDeviationMultiplier(Rating.get(smasherTwo).getMean(),
            Rating.get(smasherTwo).getStandardDeviation(),
            Rating.get(smasherTwo).getConservativeStandardDeviationMultiplier());

        System.out.println("new ranks: " + smasherOne.toString());
        System.out.println("new ranks: " + smasherTwo.toString());

        System.out.println("\n Ratings player one" + smasherOne.getRating());
        System.out.println("Ratings player two" + smasherTwo.getRating());

    }


    public static void main(String[]args) {

        System.out.println("μ means average skill of player and σσ is a confidence of the guessed rating");

        TrueskillSample generator = new TrueskillSample();

        Smasher<String> vdogg = new Smasher<>("Vdogg",Smasher.DEFAULTRATING);
        Smasher<String> aske = new Smasher<>("Aske",Smasher.DEFAULTRATING);

        //smasherOne not used for now, update that later to use third parameter to choose winner

        for (int i = 0; i<5; i++){
            //looping through 10 wins for one player
            generator.updatePlayerRanks(vdogg, aske, vdogg);
        }

        Smasher<String> sverre = new Smasher<>("Sverre",(new Rating(50,4.5,4.0)));
        System.out.println(sverre.getRating());
    }

    public Collection<ITeam> generateMatchupWithNewRanks(Player playerOne, double meanPlayerOne, double deviationPlayerOne, Player playerTwo, double meanPlayerTwo, double deviationPlayerTwo){

        Rating ratingPlayer1 =  new Rating(meanPlayerOne,deviationPlayerOne);
        Rating ratingPlayer2 = new Rating(meanPlayerTwo,deviationPlayerTwo);

        SmashMatchup matchup = new SmashMatchup(playerOne,ratingPlayer1,playerTwo,ratingPlayer2);
        return matchup.returnMatchup();
    }


}
