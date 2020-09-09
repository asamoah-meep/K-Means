package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import objects.Parameters;
import services.FileManager;
import services.WordAnalysis;

import java.io.IOException;


public class Controller {

    @FXML private TextField Cluster_Count;
    @FXML private ToggleGroup Cluster_Creation;
    @FXML private ToggleGroup Distance_Measure;
    @FXML private ToggleGroup PCA_Analysis;

    @FXML protected void Submit(ActionEvent actionEvent) {

        String CC = Cluster_Count.textProperty().getValue();
        if(invalidClusterCount(CC)){
            System.out.println("Invalid!!!");
            return;
        }
        int ClusterCount = Integer.parseInt(CC);
        if(ClusterCount < 2 || ClusterCount > 10){
            System.out.println("Invalid!!");
            return;
        }


        String PCA = PCA_Analysis.getSelectedToggle().getUserData().toString();
        String Distance = Distance_Measure.getSelectedToggle().getUserData().toString();
        String ClusterCreation = Cluster_Creation.getSelectedToggle().getUserData().toString();

        Parameters p = new Parameters(ClusterCreation,Distance,PCA,ClusterCount);

        try{
            FileManager.init();
            WordAnalysis.init(FileManager.getWordMatrix(), FileManager.getAllDocs(), p);
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    @FXML
    public void initialize(){
        Cluster_Count.textProperty().addListener( (Observable, oldVal, newVal)-> {
            if (invalidClusterCount(newVal)){
                System.out.println("Invalid!");
                return;
            }
            int v = Integer.parseInt(newVal);
            if(v < 2 || v > 10)
                System.out.println("Invalid!");
        });

    }

    private boolean invalidClusterCount(String s){
        if(s.isEmpty())
            return true;
        try{
            Integer.parseInt(s);
            return false;
        }catch (NumberFormatException e){
            return true;
        }
    }

}
