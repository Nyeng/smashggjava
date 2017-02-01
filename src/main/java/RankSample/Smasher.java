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
    private static GameInfo defaultGameInfo = GameInfo.getDefaultGameInfo();
    static final Rating DEFAULTRATING = defaultGameInfo.getDefaultRating();
    private Rating rating;
    private String entrantId;

    public Smasher(S id, Rating rating){
        super(id);
        this.id = id;
        setRating(rating);
        System.out.println("Smasher created with id"+ id);
    }

    public Smasher(S id){
        super(id);
        this.id = id;
        System.out.println("Created smasher with id "+id);
    }

    public Smasher(S id, String entrantId){
        super(id);
        this.id = id;
        this.entrantId = entrantId;
        System.out.println("Created smasher with id "+id + " and entrant id: " + entrantId);
    }

//    public S getId(){
//        return id;
//    }

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

    public Rating setDefaultRating(){
        return DEFAULTRATING;
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

    public String getEntrantId() {
        return entrantId;
    }

    public void setEntrantId(String entrantId) {
        this.entrantId = entrantId;
    }
}
