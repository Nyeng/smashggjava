package RankSample;


import jskills.GameInfo;
import jskills.Player;
import jskills.Rating;

/**
 * Created by k79689 on 26.01.17.
 */
public class Smasher<S> extends Player {

    private S id;
    private double mean = 0.0;
    private double conservativeStandardDeviationMultiplier = 0.0;
    private double deviation = 0.0;
    public static GameInfo defaultGameInfo = GameInfo.getDefaultGameInfo();
    public static final Rating DEFAULTRATING = defaultGameInfo.getDefaultRating();
    public Rating rating;

    public Smasher(S id, Rating rating){
        super(id);
        this.id = id;
        setRating(rating);
        System.out.println("Smasher created with id"+ id);
    }

    public double getConservativeStandardDeviationMultiplier() {
        return conservativeStandardDeviationMultiplier;
    }


    public double getMean() {
        return mean;
    }

    public double getDeviation() {
        return deviation;
    }

    public void setRating(Rating rating){
        this.rating = rating;
    }

    public Rating getRating(){
        // new SmashMatchup(smasherVdogg,defaultGameInfo.getDefaultRating(),smasherAske,defaultGameInfo.getDefaultRating());
        if( (getMean() == 0.0 || getDeviation() == 0)  ){
            return DEFAULTRATING;
        }
        else if (getConservativeStandardDeviationMultiplier() == 0.0) {
            System.out.println("returning mean and deviation");
            return new Rating(getMean(), getDeviation());
        }
        else{
            System.out.println("returning conservative stuff " );
            return new Rating(getMean(),getDeviation(),getConservativeStandardDeviationMultiplier());
        }
    }

    @Override
    public String toString() {
        return "Smasher: "+ id +" { "+
            "mean=" + mean +
            ", deviation=" + deviation +
            '}';
    }

    public void setMeanDeviationAndDeviationMultiplier(double mean, double deviation,double conservativeStandardDeviationMultiplier){
        this.mean = mean;
        this.deviation = deviation;
        this.conservativeStandardDeviationMultiplier = conservativeStandardDeviationMultiplier;
    }


}
