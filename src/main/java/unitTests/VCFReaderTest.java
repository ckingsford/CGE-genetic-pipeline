/**
 * 
 */
package unitTests;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs.lane.brokers.load.CGELoadGeneticCenter;
import edu.cmu.cs.lane.datatypes.dataset.SamplesGeneticData;
import edu.cmu.cs.lane.pipeline.datareader.filters.AbstractDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.filters.LookUpDataFilter;
import edu.cmu.cs.lane.pipeline.datareader.geneticreader.VCFReader;
import edu.cmu.cs.lane.settings.OptionsFactory;
import edu.cmu.cs.lane.settings.OptionsGeneral;
import edu.cmu.cs.lane.settings.OptionsLoadGenetic;

/**
 * @author zinman
 *
 */
public class VCFReaderTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
    	OptionsGeneral mockOptions = new OptionsGeneral();
       	mockOptions.setLoadGeneticSource("file");
    	mockOptions.setWorkingFolder("../samples/exampleAnalyzer/working/"); 
    	
    	OptionsFactory.addOptions(mockOptions);
   
    	OptionsLoadGenetic lgOptions = new OptionsLoadGenetic();
    	lgOptions.setGeneticInputFilePattern("chr([\\d]+)-long\\.vcf");
    	OptionsFactory.addOptions(lgOptions);
    	
    	CGELoadGeneticCenter.initialize();
    	ArrayList<AbstractDataFilter> filters = new ArrayList<AbstractDataFilter>();
    	LookUpDataFilter lookupFilter = new LookUpDataFilter();
    	ArrayList<String> elements = new ArrayList<String>();
    	elements.add("1.18440200");
		lookupFilter.populateHashByPosition(elements);
    	filters.add(lookupFilter);
    	ArrayList<SamplesGeneticData> geneticData = CGELoadGeneticCenter.loadNext(filters);
    	
    	System.out.println("Patients count:" +geneticData.get(0).getSamplesCount());
    	System.out.println("Features count:" +geneticData.get(0).getFeaturesCount());

	}

}
