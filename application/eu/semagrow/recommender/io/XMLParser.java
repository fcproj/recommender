package eu.semagrow.recommender.io;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML parser
 * @author celli
 *
 */
public class XMLParser {

	private final static Logger log = Logger.getLogger(XMLParser.class.getName());

	/**
	 * Parse an XML with <uri> elements and stores the content in a Set
	 * @param xmlStr the input XML
	 * @param termsURIs the output Set
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseURI(String xmlStr, Set<String> termsURIs) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		if(termsURIs!=null && xmlStr!=null){
			SAXParserFactory  spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(false);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlStr)), new URISaxParser(termsURIs));
		}
		else {
			log.warning("Set of terms URIs was not initialized");
		}
	}

	/**
	 * Parse an XML with <uri> elements and stores the content in a Map, counting the occurrences
	 * @param xmlStr
	 * @param termsURIsOccurr
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseURI(String xmlStr, Map<String, Integer> termsURIsOccurr) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		if(termsURIsOccurr!=null && xmlStr!=null){
			//parse XML
			List<String> listUris = new LinkedList<String>();
			SAXParserFactory  spf = SAXParserFactory.newInstance();
			spf.setValidating(false);
			spf.setNamespaceAware(false);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(xmlStr)), new URISaxParser(listUris));

			//count occurrences
			for (String uri: listUris) {
				if(termsURIsOccurr.containsKey(uri)) {
					int value = termsURIsOccurr.get(uri);
					value = value+1;
					termsURIsOccurr.put(uri, value);
				}
				else
					termsURIsOccurr.put(uri, 1);
			}
		}
		else {
			log.warning("Set of terms URIs was not initialized");
		}
	}

}