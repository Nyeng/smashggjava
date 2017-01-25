package RankSample;

import java.util.Collection;

import jskills.GameInfo;
import jskills.ITeam;
import jskills.Player;
import jskills.Team;

/**
 * Created by k79689 on 25.01.17.
 */
public class SmashMatchup {

    private Team team1;
    private Team team2;

    private static GameInfo defaultGameInfo = GameInfo.getDefaultGameInfo();


    public SmashMatchup(Player player1, Player player2){
        team1 = new Team(player1, defaultGameInfo.getDefaultRating());
        team2 = new Team(player2, defaultGameInfo.getDefaultRating());
    }

    public static GameInfo returnGameinfo(){
        return defaultGameInfo;
    }

    public Collection<ITeam> returnMatchup(){
        return Team.concat(team1, team2);
    }

}
