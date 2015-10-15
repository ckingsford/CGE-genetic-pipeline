/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cmu.cs.lane.datareaders.VCFReader;

/**
 * @author
 *
 */
public class VcfreaderTest {

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
	public void testGetName() {
		VCFReader r = new VCFReader();
		assertEquals("VCFReader", r.getName());
	}

	@Test
	public void test() {
		VCFReader r = new VCFReader();
		assertEquals("VCFReader", r.getName());
	}

}
