package RankSample;

import java.util.List;

/**
 * Created by k79689 on 02.02.17.
 */
public class SmashersList {

    private List<Smasher<String>> smashers;

    public SmashersList(List<Smasher<String>> smashers) {
        this.smashers = smashers;
    }

    public void addSmasher(List<Smasher<String>> smashers, Smasher smasher){
        smashers.add(smasher);
    }


    private void sortSmashersByRank(List<Smasher<String>> smashers){
        smashers
            .stream()
            .sorted((e2, e1) -> Double.compare(e1.getMean(),
                e2.getMean()))
            .forEach(System.out::println);
    }

    public List<Smasher<String>> getSmashers() {
        return smashers;
    }
}
