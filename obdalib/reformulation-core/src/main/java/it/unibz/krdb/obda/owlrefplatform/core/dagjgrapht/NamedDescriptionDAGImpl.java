package it.unibz.krdb.obda.owlrefplatform.core.dagjgrapht;

import it.unibz.krdb.obda.ontology.Description;
import it.unibz.krdb.obda.ontology.OClass;
import it.unibz.krdb.obda.ontology.Property;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

/** Build a DAG with only a named description*/

public class NamedDescriptionDAGImpl implements NamedDescriptionDAG {

	private Set<OClass> namedClasses;
	private Set<Property> property;
	
	private Map<Description, Set<Description>> equivalencesMap;

	private Map<Description, Description> replacements;
	DAGImpl namedDag;

	//use class TBoxReasoneNamed to find descendants and ancestors of a node
	public NamedDescriptionDAGImpl(DAGImpl d) {
		System.out.println(d);
		//take classes and roles from the DAG
		namedDag=(DAGImpl) d.clone();
		namedClasses= namedDag.getClasses();
		property = namedDag.getRoles();
		equivalencesMap= namedDag.getMapEquivalences();
		replacements = namedDag.getReplacements();
				
		for( Description vertex: d.vertexSet()){
			
				//if the node in in the list keep it
		if(namedClasses.contains(vertex) | property.contains(vertex)){
			
			continue;
		}
		System.out.println("v: "+vertex);
		System.out.println("R: "+replacements);
		System.out.println(replacements.get(vertex) != null);
		//if it's not representative delete
		if(replacements.get(vertex) != null){
			namedDag.removeVertex(vertex);
			
			System.out.println("vertex:" +vertex);

			Description reference = replacements.get(vertex);
			
			//delete from replacements
			replacements.remove(vertex);
			
			//delete from equivalencesMap
			Set<Description> equivalences =equivalencesMap.get(reference);
			equivalences.remove(vertex);
			equivalencesMap.put(reference, equivalences);
			for(Description e : equivalences){
				equivalencesMap.put(e, equivalences);
			}
			
		}
		//if the node is not in the list and it's representative delete
		else{
			
	
			//delete from equivalencesMap
			Set<Description> equivalences =equivalencesMap.get(vertex);
			if(equivalences!=null){
			equivalences.remove(vertex);
			
			//change the representative node
			Iterator<Description> e=equivalences.iterator();
			Description reference=  e.next();
			replacements.remove(reference);
			equivalencesMap.put(reference, equivalences);
			
			while(e.hasNext()){
				Description node =e.next();
				replacements.put( node, reference);
				equivalencesMap.put(node, equivalences);
			}
			/*
			 * Re-pointing all links to and from the eliminated node to the
			 * representative node
			 */

			Set<DefaultEdge> edges = new HashSet<DefaultEdge>(namedDag.incomingEdgesOf(vertex));
			for (DefaultEdge incEdge : edges) {
				Description source = namedDag.getEdgeSource(incEdge);
				namedDag.removeAllEdges(source, vertex);
				
				if (source.equals(reference))
					continue;
				
				namedDag.addEdge(source, reference);
			}

			edges = new HashSet<DefaultEdge>(namedDag.outgoingEdgesOf(vertex));
			for (DefaultEdge outEdge : edges) {
				Description target = namedDag.getEdgeTarget(outEdge);
				namedDag.removeAllEdges(vertex, target);
				
				if (target.equals(reference))
					continue;
				namedDag.addEdge(reference, target);
			}
			
			namedDag.removeVertex(vertex);

			}
			else{
			//add edge between the first of his ancestor that it's still present and it's child
			
			Set<DefaultEdge> edges = new HashSet<DefaultEdge>(namedDag.incomingEdgesOf(vertex));
			for (DefaultEdge incEdge : edges) {
				Description source = namedDag.getEdgeSource(incEdge);
				namedDag.removeAllEdges(source, vertex);
				
				edges = new HashSet<DefaultEdge>(namedDag.outgoingEdgesOf(vertex));
				for (DefaultEdge outEdge : edges) {
					Description target = namedDag.getEdgeTarget(outEdge);
					namedDag.removeAllEdges(vertex, target);
					
		
					namedDag.addEdge(source, target);
				}
				
				
			}

			
			
			namedDag.removeVertex(vertex);
			System.out.println(vertex+ " "+namedDag);
			}
			
			

			}
			}
			
		
		
		
		
		namedDag.setMapEquivalences(equivalencesMap);
		namedDag.setReplacements(replacements);
		namedDag.setIsaDAG(true);
		
	}
	
	@Override
	public DAGImpl getDAG() {
		return namedDag;
	}

}