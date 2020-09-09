package objects;

public class Parameters {

    private final String creation;
    private final String distance;
    private final String PCA;
    private final int count;

    public Parameters(String creation, String distance, String PCA, int count) {
        this.creation = creation;
        this.distance = distance;
        this.PCA = PCA;
        this.count = count;
    }

    public String getCreation() {
        return creation;
    }

    public String getDistance() {
        return distance;
    }

    public String getPCA() {
        return PCA;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString(){
        return String.format("%s %s %s %d\n", PCA, distance, creation, count);
    }
}
