package objectclasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Player{

    public String getGamerTag() {
        return gamerTag;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public int getPlayerId() {
        return playerId;
    }

    String gamerTag;
    String twitterHandle;
    int playerId;



    public Player(){}

    public String toString(){
    return "ToString method";
    }






}