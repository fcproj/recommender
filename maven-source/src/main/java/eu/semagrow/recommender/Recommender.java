package eu.semagrow.recommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jfcutils.files.read.TXTReader;
import org.jfcutils.util.DateTime;

import eu.semagrow.recommender.domain.Recommendation;
import eu.semagrow.recommender.io.RDFWriter;
import eu.semagrow.recommender.sparql.HTTPFederatedQuerier;
import eu.semagrow.recommender.sparql.HTTPIndividualQuerier;


/**
 * SemaGrow Recommender System entry point.
 * The input file is expected to be a text file with a URI for each line.
 * @author celli
 *
 */
public class Recommender {

	private final static Logger log = Logger.getLogger(Recommender.class.getName());
	private static final String sourceFilePath = Defaults.getString("sourceFilePath");
	private static final String outputFilePath = Defaults.getString("outputFilePath");
 
	/** 
	 * Entry point
	 * @param args
	 */
	public static void main(String[] args){
		String startDate = DateTime.getDateTime();	
		Recommender rec = new Recommender();
		rec.startProcess();
		String endDate = DateTime.getDateTime();
		log.info(startDate + " -- " + endDate + " ["+DateTime.dateDiffSeconds(startDate, endDate)+"s]");
	}

	/*
	 * Start the recommendation process
	 * Returns the number of recommended URIs
	 */
	protected int startProcess() {
		int result = 0;

		//check the output file path
		try {
			this.checkAndCreateFileParents(outputFilePath);
		} catch (Exception e){
			log.warning("Problems with output file path! The application will be stopped.");
			return 0;
		}

		//get URIs to be recommended 
		Set<String> sourceURIs;
		try {
			sourceURIs = this.readSourceFile(Recommender.sourceFilePath);
		} catch (Exception e){
			log.warning("Problems accessing the input file! The application will be stopped.");
			return 0;
		}

		//to display remaining computations
		int total = sourceURIs.size();
		int current = 0;
		log.info("...Remaining: "+ (total-current) +" ["+(current/total*100)+"%]");

		//for each URI, get some recommendations
		List<Recommendation> recoms = new LinkedList<Recommendation>();
		for(String uri: sourceURIs){
			try {
				//choose the specific recommender
				String type_recom = Defaults.getString("type_recommendation");
				if(type_recom.equalsIgnoreCase("federated")){
					HTTPFederatedQuerier querier = new HTTPFederatedQuerier(uri);
					querier.computeRecommendations(recoms);
				}
				else {
					HTTPIndividualQuerier querier = new HTTPIndividualQuerier(uri);
					querier.computeRecommendations(recoms);
				}

				//write a file and log
				current++;
				log.info("...Remaining: "+ (total-current));
				if(recoms.size()%1000==0 && recoms.size()>0) {
					this.writeFile(recoms);
					result+=recoms.size();
					recoms.clear();
				}

			} catch (Exception e){
				log.warning("Problems with the triplestore! URI: "+ uri+" will be discarded.");
				log.warning(e.getStackTrace().toString());
			}
		}

		//write last recommendations
		if(recoms.size()>0) {
			this.writeFile(recoms);
			result+=recoms.size();
			recoms.clear();
		}
		return result;
	}

	/*
	 * Write a set of recommendations
	 */
	private void writeFile(List<Recommendation> recoms) {
		//write the file
		try {
			RDFWriter writer = new RDFWriter();
			writer.writeRDFXML(recoms, outputFilePath+"_"+(new Date()).getTime());
		} catch (Exception e){
			log.warning("Problems writing the output!");
		}
	}

	/*
	 * Read the source URIs to be recommended
	 */
	private Set<String> readSourceFile(String filepath) throws FileNotFoundException {
		//parse the list of URIs to be recommended
		TXTReader reader = new TXTReader();
		return reader.parseTxt(filepath);
	}

	/*
	 * Check the existance of the output file path
	 */
	private void checkAndCreateFileParents(String fullpath){
		File targetFile = new File(fullpath);
		File parent = targetFile.getParentFile();
		if(!parent.exists() && !parent.mkdirs() && !parent.canWrite()){
			throw new IllegalStateException("Couldn't create/write dir: " + parent);
		}
	}

}
