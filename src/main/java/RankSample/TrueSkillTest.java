package RankSample;

import java.util.Collection;
import java.util.Map;

import jskills.GameInfo;
import jskills.IPlayer;
import jskills.ITeam;
import jskills.Player;
import jskills.Rating;
import jskills.Team;
import jskills.trueskill.TwoPlayerTrueSkillCalculator;

/**
 * Created by k79689 on 24.01.17.
 */
public class TrueSkillTest {


    public static void main(String[]args){
        TrueSkillTest trueskilltest = new TrueSkillTest();
        trueskilltest.testCalculationImplementationTwoPlayers();
    }

    //This example shows how to update Players with new skills
    private void testCalculationImplementationTwoPlayers(){
        Player<String> bosePlayer = new Player<>("Bose");
        Player<String> askePlayer = new Player<>("Askelink");

        GameInfo gameInfo = GameInfo.getDefaultGameInfo();


        //When a player is new, use this to create First Rank (Default rating which starts at 25.0)
        Team team1 = new Team(bosePlayer, gameInfo.getDefaultRating());
        Team team2 = new Team(askePlayer, gameInfo.getDefaultRating());

        Collection<ITeam> teams = Team.concat(team1, team2);

        TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

        Map<IPlayer, Rating> boseWins = calculator.calculateNewRatings(gameInfo, teams, 1, 2);

        double newMeanAske = boseWins.get(askePlayer).getMean();
        double newDeviationAske = boseWins.get(askePlayer).getStandardDeviation();
        Rating newAskeRating = new Rating(newMeanAske,newDeviationAske);

        //Recollect new teams
        team1 = new Team(askePlayer,newAskeRating);
        teams = Team.concat(team1,team2);

        Map<IPlayer, Rating> boseWinsAgain = calculator.calculateNewRatings(gameInfo, teams, 1, 2);
        System.out.println("Bose wins, new Askelink rating: " + boseWins.toString());
        System.out.println("Bose wins again, new Askelink rating: " + boseWinsAgain.toString());

        // σ = confidence of rating
        // μ = actual rating

    }


}
