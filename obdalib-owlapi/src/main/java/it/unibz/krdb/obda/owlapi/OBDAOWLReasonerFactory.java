package it.unibz.krdb.obda.owlapi;

import it.unibz.krdb.obda.model.OBDAModel;

import org.semanticweb.owl.inference.OWLReasonerFactory;

public interface OBDAOWLReasonerFactory extends OWLReasonerFactory {

	public abstract void setOBDAController(OBDAModel controller);

	public abstract void setPreferenceHolder(ReformulationPlatformPreferences preference);

}