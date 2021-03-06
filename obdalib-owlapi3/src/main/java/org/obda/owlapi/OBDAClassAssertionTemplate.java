/*
 * Copyright (C) 2009-2013, Free University of Bozen Bolzano
 * This source code is available under the terms of the Affero General Public
 * License v3.
 * 
 * Please see LICENSE.txt for full license terms, including the availability of
 * proprietary exceptions.
 */
package org.obda.owlapi;

import org.semanticweb.owlapi.model.OWLClass;

public interface OBDAClassAssertionTemplate extends OBDAAssertionTemplate

{
	public OWLClass getEntity();

}
