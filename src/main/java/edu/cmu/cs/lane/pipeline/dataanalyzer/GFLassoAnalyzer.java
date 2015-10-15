package edu.cmu.cs.lane.pipeline.dataanalyzer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.jblas.DoubleMatrix;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.util.JSON;

import edu.cmu.cs.lane.algorithmicEngine.GeneratedNetwork;
import edu.cmu.cs.lane.brokers.ClinicalDictionary;
import edu.cmu.cs.lane.datatypes.GFLasso.Coordinate;
import edu.cmu.cs.lane.datatypes.GFLasso.SPGResult;
import edu.cmu.cs.lane.datatypes.dataset.MatrixLabels;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;
import edu.cmu.cs.lane.datatypes.dataset.SamplesDatasetList;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.datatypes.dataset.SamplesLabels;
import edu.cmu.cs.lane.datatypes.evaluation.AbstractEvaluationFunction;
import edu.cmu.cs.lane.datatypes.evaluation.EvaluationResult;
import edu.cmu.cs.lane.datatypes.model.AnalysisDetails;
import edu.cmu.cs.lane.datatypes.model.CGEModel;
import edu.cmu.cs.lane.datatypes.model.CGEModelFeatureBean;
import edu.cmu.cs.lane.datatypes.prediction.CGEPrediction;
import edu.cmu.cs.lane.matrixmath.MatrixUtils;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGFLasso;
import edu.cmu.cs.lane.settings.OptionsGeneral;

/**
 * 
 * GFLassoAnalyzer implements the GFLasso algorithm as an {@link AbstractAnalyzer} object.
 * 
 * @author zinman
 * @version 1.0
 * @since 1.0
 * 
 * @see AbstractAnalyzer 
 */
public class GFLassoAnalyzer extends AbstractAnalyzer {
	
	private static GeneratedNetwork network = null;
	private double gamma;
	private double lambda;
	private int maxIterations;
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public String getName() {
		return "GFLassoAnalyzer";
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public AbstractAnalyzer create() {
		GFLassoAnalyzer gflasso = new GFLassoAnalyzer();
		return gflasso;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * To be used for override default load behavior. Return null to keep default
	 */
	@Override
	public ArrayList<SamplesLabels> loadLabelsOverride(ArrayList<SamplesLabels> labels) { 
		return null;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * To be used for override default load behavior. Return null to keep default
	 */
	@Override
	public ArrayList<SamplesGeneticData> loadDataOverride(ArrayList<SamplesGeneticData> data, ArrayList<AbstractDataFilter> filters) { 
		return null;
	}

	/**
	 * 
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#getAnalyzerSettings()
	 */
	private AnalysisDetails getAnalyzerSettings() {
	
		AnalysisDetails analysisDetails = new AnalysisDetails();
		analysisDetails.algorithmName = getName();
		JSONObject params = new JSONObject();
		try {
			params.put("lambda", lambda);
			params.put("gamma", gamma);
			params.put("max_iterations", maxIterations);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		analysisDetails.algParameters = params.toString();
		analysisDetails.backgroundSource = "none";
		analysisDetails.targetSource = "test data";
		analysisDetails.fileShortPostfix = String.format("%.3f", lambda)+"_"+String.format("%.3f",gamma);
		return analysisDetails;
	}

	/** 
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#initializeAnalyzers()
	 */
	@Override
	public ArrayList<AbstractAnalyzer> initializeAnalyzers(SamplesDatasetList samplesDatasetList) {
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tGenerating Network.");
	
		network = genNetwork(samplesDatasetList.get(0).getLabelData().ExtractDataAsMatrix()); //this type of analysis has only one dataset as input
		
		ArrayList<AbstractAnalyzer> analyzerInstances = new ArrayList<AbstractAnalyzer>();
		for (int i = 0; i < ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getLambdas().length; i++) {
			for (int j = 0; j < ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getGammas().length; j++) {
				GFLassoAnalyzer analyzer = new GFLassoAnalyzer();
				analyzer.lambda = ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getLambda(i); 
				analyzer.gamma = ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getGamma(j);
				analyzer.maxIterations = ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getMaxIterations();
				analyzerInstances.add(analyzer);				
			}
		}
		return analyzerInstances;
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public CGEModel analyze(SamplesDatasetList samplesDatasetList) {
		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("Running SPG Multi: Lamda: "
					+ lambda + "\tGamma: "
					+ gamma);

		SPGResult spgr = SPG_Multi(samplesDatasetList.get(0).getLabelData().ExtractDataAsMatrix(), samplesDatasetList.get(0).getGeneticData().ExtractDataAsMatrix(), //GFLasso handles only one dataset input
				gamma,lambda, network.getC(), network.getCNorm()); //TODO: check if memory use can be saved in SPG_Multi

		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose()){
			System.out.println("bm = " + spgr.getBeta().rows);
			System.out.println("bn = " + spgr.getBeta().columns);
			System.out.println("chksum = "
					+ MatrixUtils.sum(MatrixUtils.sum(spgr.getBeta(), 2)));
			System.out
					.println("chknnz = " + MatrixUtils.nnz(spgr.getBeta()));
		}
		//standardize betas
		double [][] finalBetas = new double[spgr.getBeta().rows][spgr.getBeta().columns]; //# of features * # of regressed vars
		if (((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).isStandardizeBetas()) {			
			//calculating average for Y_l where l is a variable regressed upon
			double [] Y_avg = new double[spgr.getBeta().columns]; //for each regressed variable
			double [][] X_l_k_avg = new double[spgr.getBeta().rows][spgr.getBeta().columns];
			for (int l =0; l<Y_avg.length;l++){ //iterating over all regressed variables
				Y_avg[l] = 0; 
				for (int i =0; i<samplesDatasetList.get(0).getLabelData().getSamplesCount();i++){ //iterating over all samples
					Y_avg[l]+=samplesDatasetList.get(0).getLabelData().getData(i, l);
				}
				Y_avg[l] /= (double)samplesDatasetList.get(0).getLabelData().getSamplesCount();
				
				//calculate average X_l_k where l is a regressed variable and k is a feature			
				for (int k = 0; k < spgr.getBeta().rows; ++k) { //iterating over all features
					for (int i =0; i<samplesDatasetList.get(0).getLabelData().getSamplesCount();i++){ //iterating over all samples
						X_l_k_avg[k][l]+=samplesDatasetList.get(0).getGeneticData().getData(i,k) * spgr.getBeta().get(k, l);
					}
					X_l_k_avg[k][l] /= (double)samplesDatasetList.get(0).getLabelData().getSamplesCount();
					double LS=0;
					double RS=0;
					//Sum((Y_i_l - Y_l_avg) ^2) / Sum((X_i_l_k - X_l_k_avg) ^2)
					for (int i =0; i<samplesDatasetList.get(0).getLabelData().getSamplesCount();i++){ //iterating over all samples
						LS += Math.pow(samplesDatasetList.get(0).getLabelData().getData(i, l) - Y_avg[l],2);
						RS += Math.pow((samplesDatasetList.get(0).getGeneticData().getData(i,k) * spgr.getBeta().get(k, l)) - X_l_k_avg[k][l],2);		
					}
					if (RS != 0){
						finalBetas[k][l] = spgr.getBeta().get(k,l) * Math.sqrt(LS/RS); 
					}else{
						finalBetas[k][l] = 0.0;
					}
				}				
			}
		}else{ // original finalBetas
			for (int k = 0; k < spgr.getBeta().rows; ++k) {
				for (int l = 0; l < spgr.getBeta().columns; ++l) {
					finalBetas[k][l] = spgr.getBeta().get(k,l);
				}
			}
		}
		
		//create final model
		CGEModel model = new CGEModel();
		for (int k = 0; k < finalBetas.length; ++k) {
			for (int l = 0; l < finalBetas[k].length; ++l) {
				if (finalBetas[k][l] == 0.0) { //sparse - no point of storing zeros
					continue;
				}

				CGEModelFeatureBean feature = new CGEModelFeatureBean();
				feature.id = samplesDatasetList.get(0).getGeneticData().getFeatureName(k);
				feature.type = samplesDatasetList.get(0).getGeneticData().getExtendedFeatureInfo(k);
				feature.val = finalBetas[k][l];
				feature.var = samplesDatasetList.get(0).getLabelData().getFeatureName(l);
				model.add(feature);
			}
		}

		if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
			System.out.println("\tGFLasso Completed.");
		model.setDetails(getAnalyzerSettings());
		return model;		
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 */
	@Override
	public ArrayList<CGEPrediction> applyModel(CGEModel model, SamplesDataset samplesDataset) {
		return null;
	}
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer
	 * returns the network info
	 */
	@Override
	public String getAnalysisPrototypeInfo(ArrayList<SamplesLabels> labels){
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (Coordinate edge : network.getE()) {
			sb.append(String.format("(%s,%s,%f)",  
				labels.get(0).getFeatureName(edge.getX()),
				labels.get(0).getFeatureName(edge.getY()), 
				network.getS().get(i, 0))
			);
			i++;
		}
		return sb.toString();
	}
		
	/**
	 * @see edu.cmu.cs.lane.pipeline.dataanalyzer.AbstractAnalyzer#testModel(edu.cmu.cs.lane.datatypes.model.CGEModel, java.util.ArrayList, java.util.ArrayList, edu.cmu.cs.lane.datatypes.CVindices)
	 */
	@Override
	public ArrayList<EvaluationResult> testModel(CGEModel model, SamplesDatasetList samplesDatasetList, ArrayList<AbstractEvaluationFunction>evaluateFunctions) {
		
		//goodness of fit
		//min  (V_test - F_test * B_final_(gamma,lambda) )
		
		double [] Y_avg = new double[samplesDatasetList.get(0).getLabelData().getFeaturesCount()]; //for each regressed variable
		for (int l =0; l<Y_avg.length;l++){ //iterating over all regressed variables
			Y_avg[l] = 0; 
			for (int i =0; i<samplesDatasetList.get(0).getLabelData().getSamplesCount();i++){ //iterating over all samples
				Y_avg[l]+=samplesDatasetList.get(0).getLabelData().getData(i, l);
			}
			Y_avg[l] /= (double)samplesDatasetList.get(0).getLabelData().getSamplesCount();
		}
		
		double predictorSum = 0;
		double actualSum = 0;
		double se = 0;
		for (int i =0; i<samplesDatasetList.get(0).getLabelData().getSamplesCount();i++){ //iterating over all patients
			for (int l =0; l<samplesDatasetList.get(0).getLabelData().getFeaturesCount();l++){ //iterating over regressed variables
				String variable = samplesDatasetList.get(0).getLabelData().getFeatureName(l);
				double V_l = samplesDatasetList.get(0).getLabelData().getData(i, l); //getting the feature value
				double X_l = 0; //X = F*B
				for (int k =0; k<samplesDatasetList.get(0).getGeneticData().getFeaturesCount();k++){ //iterating over genetic features
					String featureId = samplesDatasetList.get(0).getGeneticData().getFeatureName(k);
					if (model.get(featureId) != null && model.get(featureId,variable)!=null) {//if feature exist in the model
						X_l += samplesDatasetList.get(0).getGeneticData().getData(i, k) * model.get(featureId,variable).val; //x=f*b 
					}
				}
				predictorSum += Math.pow((X_l-Y_avg[l]), 2); //combining all regressed variables together
				actualSum += Math.pow((V_l-Y_avg[l]), 2); //combining all regressed variables together
				se += Math.pow(V_l-X_l, 2); //combining all regressed variables together
			}
		}
		double goodness = Math.sqrt(predictorSum/actualSum);
		double N = samplesDatasetList.get(0).getLabelData().getSamplesCount();
		double L = samplesDatasetList.get(0).getLabelData().getFeaturesCount();
		se = Math.sqrt((1/(N-2)) * (1/L) * se);
		
		ArrayList<EvaluationResult> evaluationResults = new ArrayList<EvaluationResult>();
		EvaluationResult goodnessObj = new EvaluationResult(); 
		goodnessObj.setEvaluteFunction("goodness of model");
		goodnessObj.setResult(goodness);
		evaluationResults.add(goodnessObj);
		
		EvaluationResult seObj = new EvaluationResult();
		seObj.setEvaluteFunction("standard error");
		seObj.setResult(se);
		evaluationResults.add(seObj);
		return evaluationResults;
	}
	
	/**
	 * generates the GFLasso network 
	 * @param M
	 * @return
	 */
	private GeneratedNetwork genNetwork(DoubleMatrix M) {
		int nV = M.columns;
		DoubleMatrix W;
		DoubleMatrix R = MatrixUtils.fastCorr(M);

		MatrixUtils.ltThreshold(R, 0.7, 0);
		DoubleMatrix UR = MatrixUtils.upperTriangle(R);

		if (((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getNetworkNegativeEdgeWeightTreatment().equalsIgnoreCase(
				"absolute")) {
			W = MatrixUtils.abs(UR); // W=abs(UR);
		} else {
			W = UR.muli(UR); // W=UR.^2
		}

		int[] nzUR = UR.findIndices();
		Coordinate[] E = MatrixUtils.ind2sub(nV, nzUR);

		int nE = E.length;

		double[] Ecoef = MatrixUtils.find(W);
		double[] Rtemp = MatrixUtils.find(R, nzUR);
		double[] Esign = MatrixUtils.sign(Rtemp);

		int[] C_I = new int[nE * 2];
		int[] C_J = new int[nE * 2];
		DoubleMatrix C_S = DoubleMatrix.zeros(nE, 2);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < nE; j++) {
				int k = (i * nE) + j;
				C_I[k] = j;
				if (i == 0) {
					C_J[k] = E[j].getX();
					C_S.put(j, i, Ecoef[j]);
				} else {
					C_J[k] = E[j].getY();
					C_S.put(j, i, -Ecoef[j] * Esign[j]);
				}
			}
		}

		DoubleMatrix C = MatrixUtils.sparse(C_I, C_J, C_S, nE, nV);
		Double CNorm = MatrixUtils.calculateNorm(C);
		DecimalFormat df = new DecimalFormat("#.##");
		double percent = (double) E.length / (M.columns * M.columns);
		System.out.println("\tFound " + E.length + " edges: "
				+ df.format((percent * 100)) + " %");
		//
		// df = new DecimalFormat("#.0000");
		// int i = 0;
		// for (Coordinate edge : E) {
		// System.out.print(labels.getColumnLabels()[edge.getX()] + "\t");
		// System.out.print(labels.getColumnLabels()[edge.getY()] + "\t");
		// System.out.print("CS[0]: " + df.format(C_S.get(i, 0)) + "\t");
		// System.out.print("C: " + df.format(C.get(i, 0)) + "\t");
		// System.out.println();
		// i++;
		// }

		return new GeneratedNetwork(C, CNorm, E, C_S);
	}

	private SPGResult SPG_Multi(DoubleMatrix Y, DoubleMatrix X,
			double gamma, double lambda, DoubleMatrix C, double CNorm) {
		int N = X.rows, J = X.columns, K = Y.columns;
		DoubleMatrix beta = DoubleMatrix.zeros(J, K);
		DoubleMatrix W = beta;
		double theta = 1;
		C = C.mul(gamma);
		DoubleMatrix Xtran = X.transpose();
		DoubleMatrix XX = MatrixUtils.multiply(Xtran, X);
		DoubleMatrix XY = MatrixUtils.multiply(Xtran, Y);

		double L = 0;
		if (J < 10000) {
			double mu = ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getMu();
			L = MatrixUtils.symmetricEigs(XX, 1)[0] + Math.pow(gamma, 2) * CNorm
					/ mu;
		} else {
			L = MatrixUtils.sum(MatrixUtils.elementWiseSquared(MatrixUtils.flatten(XX))) + Math.pow(gamma, 2) * CNorm
					/ ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getMu();
			XX = DoubleMatrix.zeros(XX.rows, XX.columns);
			XY = DoubleMatrix.zeros(XY.rows, XY.columns);
		}

		
		double obj[] = new double[maxIterations];
		double density[] = new double[maxIterations];
		for (int iter = 0; iter < maxIterations; iter++) {
			if (((OptionsGeneral) OptionsFactory.getOptions("general")).isVerbose())
				System.out.println("\tIteration: " + (iter + 1) + " (max " + maxIterations + ")");
			DoubleMatrix Atemp = MatrixUtils.mrdivide(MatrixUtils.multiply(C, W.transpose()), ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getMu());
			DoubleMatrix A = MatrixUtils.hardThreshold(Atemp, 1);
			DoubleMatrix tmp = null;
			DoubleMatrix grad = null;
			if (J < 2 * N && J < 10000) {
				tmp = MatrixUtils.subtract(MatrixUtils.multiply(XX, W), XY);
			} else {
				tmp = MatrixUtils.multiply(X.transpose(),
						MatrixUtils.subtract(MatrixUtils.multiply(X, W), Y));
			}
			grad = MatrixUtils.add(tmp, MatrixUtils.multiply(A.transpose(), C));

			DoubleMatrix V = MatrixUtils.subtract(W, MatrixUtils.multiply((1 / L), grad));
			DoubleMatrix beta_new = MatrixUtils.softThreshold(V, (lambda / L));
			density[iter] = MatrixUtils.getDensity(beta_new);
			double theta_new = (Math.sqrt(Math.pow(theta, 4) + 4
					* Math.pow(theta, 2)) - Math.pow(theta, 2)) / 2;

			W = MatrixUtils.add(beta_new,
					MatrixUtils.multiply(((1 - theta) / theta) * theta_new,
							MatrixUtils.subtract(beta_new, beta))); // W=beta_new+((1-theta)/theta*theta_new)*(beta_new-beta);

			double t1 = MatrixUtils.sum(MatrixUtils.sum(MatrixUtils.elementWiseSquared(MatrixUtils.subtract(Y, MatrixUtils.multiply(X, beta_new))))) / 2;
			double t2 = MatrixUtils.sum(MatrixUtils.sum(MatrixUtils.abs(MatrixUtils.multiply(C, beta_new.transpose()))));
			double t3 = lambda * MatrixUtils.sum(MatrixUtils.abs(MatrixUtils.flatten(beta_new)));

			obj[iter] = t1 + t2 + t3; // obj(iter) =
			// sum(sum((Y-X*beta_new).^2))/2 +
			// sum(sum(abs(C*beta_new')))+lambda*sum(abs(beta_new(:)));

			if (iter > 10
					&& (Math.abs(obj[iter] - obj[iter - 1])
							/ Math.abs(obj[iter - 1]) < ((OptionsGFLasso) OptionsFactory.getOptions("GFLasso")).getTolerance())) {
				break;
			}
			beta = beta_new;
			theta = theta_new;
		}

		return new SPGResult(beta);
	}
}
