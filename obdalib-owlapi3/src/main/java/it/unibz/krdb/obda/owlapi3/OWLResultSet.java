/*
 * Copyright (C) 2009-2013, Free University of Bozen Bolzano
 * This source code is available under the terms of the Affero General Public
 * License v3.
 * 
 * Please see LICENSE.txt for full license terms, including the availability of
 * proprietary exceptions.
 */
package it.unibz.krdb.obda.owlapi3;

import it.unibz.krdb.obda.model.ResultSet;

import java.util.List;

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLPropertyAssertionObject;

public interface OWLResultSet extends ResultSet {

	// //////////////////////////////////////////////////////////////////////////////////////
	//
	// ResultSet management functions
	//
	// //////////////////////////////////////////////////////////////////////////////////////

	public int getColumCount() throws OWLException;

	public List<String> getSignature() throws OWLException;

	public int getFetchSize() throws OWLException;

	public void close() throws OWLException;

	public OWLStatement getStatement();

	public boolean nextRow() throws OWLException;

	// ////////////////////////////////////////////////////////////////////////////////////
	//
	// Main data fetching functions
	//
	// //////////////////////////////////////////////////////////////////////////////////////

	public OWLPropertyAssertionObject getOWLPropertyAssertionObject(int column) throws OWLException;

	public OWLPropertyAssertionObject getOWLPropertyAssertionObject(String column) throws OWLException;

	public OWLIndividual getOWLIndividual(int column) throws OWLException;

	public OWLIndividual getOWLIndividual(String column) throws OWLException;

	public OWLNamedIndividual getOWLNamedIndividual(int column) throws OWLException;

	public OWLNamedIndividual getOWLNamedIndividual(String column) throws OWLException;

	public OWLAnonymousIndividual getOWLAnonymousIndividual(int column) throws OWLException;

	public OWLAnonymousIndividual getOWLAnonymousIndividual(String column) throws OWLException;

	public OWLLiteral getOWLLiteral(int column) throws OWLException;

	public OWLLiteral getOWLLiteral(String column) throws OWLException;

	// //////////////////////////////////////////////////////////////////////////////////////
	//
	// The most general call
	//
	// //////////////////////////////////////////////////////////////////////////////////////

	public OWLObject getOWLObject(int column) throws OWLException;

	public OWLObject getOWLObject(String column) throws OWLException;

}
