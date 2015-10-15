/**
 * 
 */
package edu.cmu.cs.lane.pipeline.datapreprocessor.patient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import edu.cmu.cs.lane.datatypes.dataset.SamplesDataset;

/**
 * @author zinman
 *
 */
public class SimpleStartificationDatasetFilter extends AbstractDatasetFilter {

	private double similarityCutoff;
	private double sampleCutoff;
	
	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.patient.AbstractDatasetFilter#getName()
	 */
	@Override
	public String getName() {
		return "stratificationFilter";
	}

	/**
	 * @see edu.cmu.cs.lane.pipeline.datapreprocessor.patient.AbstractDatasetFilter#filter(edu.cmu.cs.lane.datatypes.dataset.SamplesDataset)
	 */
	@Override
	public SamplesDataset filter(SamplesDataset dataset) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> findPatientsToRemove(String fileName) {

		float[] missingDataPerPatient = null;
		int[] patientSimilarity = null;
		int numberOfPatients = 0;
		int SNPsCount = 0;
		List<String> patientsToRemove = new LinkedList<String>();
		BufferedReader bfr = null;
		try {
			bfr = new BufferedReader(new FileReader(fileName));
			String str;
			String[] tokens;

			Random rand = new Random();
			float randVal;

			String patientsS = bfr.readLine(); // reading header line
			String[] patients = patientsS.split("\\s+");
			numberOfPatients = patients.length - 1;
			missingDataPerPatient = new float[numberOfPatients];

			// need to check all possible pairwises
			patientSimilarity = new int[numberOfPatients
					* (numberOfPatients - 1) / 2];

			int sampledSNPsCount = 0;
			while ((str = bfr.readLine()) != null) {
				SNPsCount++;
				tokens = str.split("\\s+");
				randVal = rand.nextFloat();
				if (randVal < sampleCutoff) {
					sampledSNPsCount++;
				}
				int sim_index = 0;

				for (int patient = 1; patient < tokens.length; patient++) {
					// first column is SNP identifier
					// trying to identify patients with too much missing data
					if (tokens[patient].equals("3")) { // missing data
						missingDataPerPatient[patient - 1]++;
					}
					// finding sibling patients to be removed - (no point to
					// compare a missing data
					if (randVal < sampleCutoff) {
						// expensive operation so trying to reduce it -
						// another option is to not check all options for a SNP
						// but against a random selection

						for (int patient_j = (patient + 1); patient_j < tokens.length; patient_j++) {
							if (!tokens[patient].equals("3")
									&& !tokens[patient_j].equals("3")) {
								if (tokens[patient].equals(tokens[patient_j])) {
									patientSimilarity[sim_index]++;
								}
							}
							sim_index++;
						}
					}
				}
			}
			int sim_index = 0;
			for (int i = 0; i < numberOfPatients; i++) {
				for (int j = i + 1; j < numberOfPatients; j++) {
					if ((float) patientSimilarity[sim_index] / sampledSNPsCount >= similarityCutoff) {
						patientsToRemove.add(patients[j + 1]);
					}
					sim_index++;
				}
			}
			
		} catch (IOException e) {
			
		} finally {
			if (bfr != null) {
				try {
					bfr.close();
					bfr = null;
				} catch (IOException e) {
					
				}
			}
		}

		return patientsToRemove;
	}

	public void setSimilarityCutoff(double simCutoff) {
		this.similarityCutoff = simCutoff;
	}
	
	public void setSampleCutoff(double samCutoff) {
		sampleCutoff = samCutoff;
	}

}
