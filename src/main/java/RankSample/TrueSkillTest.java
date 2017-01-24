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
        //trueskillCalc.calculateNewRatings()

        TrueSkillTest trueskilltest = new TrueSkillTest();
        trueskilltest.testCalculationImplementationTwoPlayers();
    }


    public void testCalculationImplementationTwoPlayers(){
        Player<String> player1 = new Player<String>("Bose");
        Player<String> player2 = new Player<String>("Askelink");
        GameInfo gameInfo = GameInfo.getDefaultGameInfo();

        Team team1 = new Team(player1, gameInfo.getDefaultRating());
        Team team2 = new Team(player2, gameInfo.getDefaultRating());
        Collection<ITeam> teams = Team.concat(team1, team2);

        TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();

        Map<IPlayer, Rating> askeWinsPlayer = calculator.calculateNewRatings(gameInfo, teams, 2, 1);
        Map<IPlayer, Rating> boseWins = calculator.calculateNewRatings(gameInfo, teams, 1, 2);


        System.out.println(askeWinsPlayer.toString());
        System.out.println(boseWins.toString());

        // σ = confidence of rating
        // μ = actual rating

    }


}
