package services;

import objects.WordVector;
import org.apache.commons.math4.linear.*;

import java.util.ArrayList;

public class PCAReduction {

    /*
        For a data matrix A
        W= A^t * A
        X = [e1,e2]
        T = AX
     */
    private final Array2DRowRealMatrix A;

    public PCAReduction(ArrayList<WordVector> wordMatrix){
        int numVectors = wordMatrix.size();
        int numDimensions = wordMatrix.get(0).size();

        double[][] Avalues = new double[numVectors][numDimensions];
        double[] means = new double[numDimensions];

        for(int i=0; i < numVectors; i++) {
            for (int j = 0; j < numDimensions; j++) {
                double val = wordMatrix.get(i).getWordList().get(j).getTF_IDF();
                Avalues[i][j] = val;
                means[j] += (val / numVectors);
            }
        }

        for(int i = 0; i < numDimensions; i++){
            double currentMean = means[i];
            for(int j =0; j < numVectors; j++)
                Avalues[j][i] -= currentMean;
        }

        this.A = new Array2DRowRealMatrix(Avalues);

    }

    public double[][] eigenvalueDecomp(){
        RealMatrix covar = A.transpose().multiply(A);
        EigenDecomposition spectrum = new EigenDecomposition(covar); //Take largest 2 eigenvectors to map to 2D space

        double[][] eVectors = new double[2][];
        for(int i=0; i < eVectors.length; i++)
            eVectors[i] = spectrum.getEigenvector(i).toArray();
        RealMatrix x = new Array2DRowRealMatrix(eVectors).transpose();
        RealMatrix spectrum1 = this.A.multiply(x);
        double[][] coords = new double[spectrum1.getRowDimension()][];
        for(int i = 0; i < spectrum1.getRowDimension(); i++)
            coords[i] = spectrum1.getRow(i);

        return coords;
    }

    public double[][] SVD(){
        SingularValueDecomposition svd = new SingularValueDecomposition(this.A);
        RealMatrix sigma = svd.getS();
        RealMatrix u = svd.getU();
        RealMatrix SVD = sigma.multiply(u);

        double[][] coords = new double[SVD.getColumnDimension()][];
        for(int i = 0; i < SVD.getRowDimension(); i++){
            double[] vals = new double[2];
            System.arraycopy(SVD.getRow(i),0,vals,0,2);
            coords[i] = vals;
        }

        return coords;
    }

}
