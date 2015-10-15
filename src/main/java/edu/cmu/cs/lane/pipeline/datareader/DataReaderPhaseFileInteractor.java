package edu.cmu.cs.lane.pipeline.datareader;


public interface DataReaderPhaseFileInteractor<T> {
	public String getName();

	public T create();
}
