package Trueskill;

import java.util.Collection;

import jskills.ITeam;
import jskills.Player;
import jskills.Rating;
import jskills.Team;

/**
 * Created by k79689 on 25.01.17.
 */
public class SmashMatchup {

    private Team team1;
    private Team team2;


    public SmashMatchup(Player player1, Rating ratingPlayer1, Player player2, Rating ratingPlayer2){
        team1 = new Team(player1, ratingPlayer1);
        team2 = new Team(player2, ratingPlayer2);
    }

    public Collection<ITeam> returnMatchup(){
        return Team.concat(team1, team2);
    }

}
