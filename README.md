## Recommender System for the SemaGrow Stack federation

The Recommender System is a piece of software - entirely based on JAVA - that computes meaningful combinations 
between some datasets federated by [SemaGrow](http://www.semagrow.eu/), and generates a new triplestore: the “Recommender Database”. 
The Recommender System was funded by [SemaGrow](http://www.semagrow.eu/) FP7 EU Project.

It computes meaningful combinations between two or more datasets federated by SemaGrow: 
the computation of combinations is based on the matching of AGROVOC URIs between datasets.

The file `application/defaults.properties` contains input parameters for the recommender system:
* `sourceFilePath` is the path of the input file, containing one URI for each line. The system computes recommendations for each URI available in this file  
* `outputFilePath` defines the location of the output files  
* `sparqlEndpointSG` is the [SemaGrow](http://www.semagrow.eu/) SPARQL endpoint federating target datasets of interest. It should contain at least the dataset of URIs defined in `sourceFilePath` and the output datasets (whose entities will be recommended to the client) specified by the `target_rdftype` parameter
* `max_recommendations` is the maximun number of desired recommendations for each entity
* `target_rdftype` defines the output of the recommender system: only concepts of that type will be considered recommendation for a specific URI

The Recommender System can run also by querying two individual SPARQL endpoints (for more than two endpoints, there is the need to use the federated mode). In the file `application/defaults.properties` simply configure the following prioperties:
* `type_recommendation` should be set to `individual` 
* `sparqlEndpoint1` is the SPARQL endpoint of the dataset of URIs defined in `sourceFilePath` 
* `sparqlEndpoint2` is the SPARQL endpoint of the output datasets (whose entities will be recommended to the client) specified by the `target_rdftype` parameter

The file `application/start.sh` is a bash script to run the recommender system. 
