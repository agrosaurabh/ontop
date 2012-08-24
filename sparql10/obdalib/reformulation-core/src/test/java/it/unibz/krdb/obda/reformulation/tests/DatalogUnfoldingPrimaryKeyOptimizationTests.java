package it.unibz.krdb.obda.reformulation.tests;

import it.unibz.krdb.obda.model.Atom;
import it.unibz.krdb.obda.model.CQIE;
import it.unibz.krdb.obda.model.DatalogProgram;
import it.unibz.krdb.obda.model.OBDADataFactory;
import it.unibz.krdb.obda.model.Term;
import it.unibz.krdb.obda.model.impl.OBDADataFactoryImpl;
import it.unibz.krdb.obda.owlrefplatform.core.unfolding.DatalogUnfolder;
import it.unibz.krdb.sql.DBMetadata;
import it.unibz.krdb.sql.TableDefinition;
import it.unibz.krdb.sql.api.Attribute;

import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class DatalogUnfoldingPrimaryKeyOptimizationTests extends TestCase {

	DBMetadata metadata;

	OBDADataFactory fac = OBDADataFactoryImpl.getInstance();

	DatalogProgram unfoldingProgram;

	@Override
	public void setUp() {
		metadata = new DBMetadata();
		TableDefinition table = new TableDefinition("TABLE");
		table.setAttribute(1, new Attribute("col1", Types.INTEGER, true, false));
		table.setAttribute(2, new Attribute("col2", Types.INTEGER, false, false));
		table.setAttribute(3, new Attribute("col3", Types.INTEGER, false, false));
		table.setAttribute(4, new Attribute("col4", Types.INTEGER, false, false));
		metadata.add(table);
		
		
		table = new TableDefinition("TABLE2");
		table.setAttribute(1, new Attribute("col1", Types.INTEGER, true, false));
		table.setAttribute(2, new Attribute("col2", Types.INTEGER, false, false));
		table.setAttribute(3, new Attribute("col3", Types.INTEGER, false, false));
		table.setAttribute(4, new Attribute("col4", Types.INTEGER, false, false));
		metadata.add(table);

		unfoldingProgram = fac.getDatalogProgram();

		Atom head = fac.getAtom(fac.getDataPropertyPredicate("name"), fac.getVariable("x"), fac.getVariable("y"));
		List<Term> bodyTerms = new LinkedList<Term>();
		bodyTerms.add(fac.getVariable("x"));
		bodyTerms.add(fac.getVariable("y"));
		bodyTerms.add(fac.getVariable("z"));
		bodyTerms.add(fac.getVariable("m"));
		Atom body = fac.getAtom(fac.getPredicate("TABLE", 4), bodyTerms);
		CQIE rule = fac.getCQIE(head, body);
		unfoldingProgram.appendRule(rule);

		head = fac.getAtom(fac.getDataPropertyPredicate("lastname"), fac.getVariable("x"), fac.getVariable("z"));
		bodyTerms = new LinkedList<Term>();
		bodyTerms.add(fac.getVariable("x"));
		bodyTerms.add(fac.getVariable("y"));
		bodyTerms.add(fac.getVariable("z"));
		bodyTerms.add(fac.getVariable("m"));
		body = fac.getAtom(fac.getPredicate("TABLE", 4), bodyTerms);
		rule = fac.getCQIE(head, body);
		unfoldingProgram.appendRule(rule);

		head = fac.getAtom(fac.getDataPropertyPredicate("id"), fac.getVariable("x"), fac.getVariable("m"));
		bodyTerms = new LinkedList<Term>();
		bodyTerms.add(fac.getVariable("x"));
		bodyTerms.add(fac.getVariable("y"));
		bodyTerms.add(fac.getVariable("z"));
		bodyTerms.add(fac.getVariable("m"));
		body = fac.getAtom(fac.getPredicate("TABLE", 4), bodyTerms);
		rule = fac.getCQIE(head, body);
		unfoldingProgram.appendRule(rule);
		
		head = fac.getAtom(fac.getDataPropertyPredicate("name"), fac.getVariable("x"), fac.getVariable("y"));
		bodyTerms = new LinkedList<Term>();
		bodyTerms.add(fac.getVariable("x"));
		bodyTerms.add(fac.getVariable("y"));
		bodyTerms.add(fac.getVariable("z"));
		bodyTerms.add(fac.getVariable("m"));
		body = fac.getAtom(fac.getPredicate("TABLE2", 4), bodyTerms);
		rule = fac.getCQIE(head, body);
		unfoldingProgram.appendRule(rule);

		head = fac.getAtom(fac.getDataPropertyPredicate("lastname"), fac.getVariable("x"), fac.getVariable("z"));
		bodyTerms = new LinkedList<Term>();
		bodyTerms.add(fac.getVariable("x"));
		bodyTerms.add(fac.getVariable("y"));
		bodyTerms.add(fac.getVariable("z"));
		bodyTerms.add(fac.getVariable("m"));
		body = fac.getAtom(fac.getPredicate("TABLE2", 4), bodyTerms);
		rule = fac.getCQIE(head, body);
		unfoldingProgram.appendRule(rule);

		head = fac.getAtom(fac.getDataPropertyPredicate("id"), fac.getVariable("x"), fac.getVariable("m"));
		bodyTerms = new LinkedList<Term>();
		bodyTerms.add(fac.getVariable("x"));
		bodyTerms.add(fac.getVariable("y"));
		bodyTerms.add(fac.getVariable("z"));
		bodyTerms.add(fac.getVariable("m"));
		body = fac.getAtom(fac.getPredicate("TABLE2", 4), bodyTerms);
		rule = fac.getCQIE(head, body);
		unfoldingProgram.appendRule(rule);
	}

	public void testRedundancyElimination() throws Exception {
		DatalogUnfolder unfolder = new DatalogUnfolder(unfoldingProgram, metadata);

		LinkedList<Term> headterms = new LinkedList<Term>();
		headterms.add(fac.getVariable("m"));
		headterms.add(fac.getVariable("n"));
		headterms.add(fac.getVariable("o"));
		headterms.add(fac.getVariable("p"));

		Atom head = fac.getAtom(fac.getPredicate("q", 4), headterms);
		List<Atom> body = new LinkedList<Atom>();
		body.add(fac.getAtom(fac.getDataPropertyPredicate("name"), fac.getVariable("m"), fac.getVariable("n")));
		body.add(fac.getAtom(fac.getDataPropertyPredicate("lastname"), fac.getVariable("m"), fac.getVariable("o")));
		body.add(fac.getAtom(fac.getDataPropertyPredicate("id"), fac.getVariable("m"), fac.getVariable("p")));
		CQIE query = fac.getCQIE(head, body);

		DatalogProgram input = fac.getDatalogProgram(query);
		DatalogProgram output = unfolder.unfold(input);
		System.out.println("input " + input);

		int atomcount = 0;
		for (CQIE result: output.getRules()) {
			for (Atom atom: result.getBody()) {
				atomcount +=1;
			}
		}
		
		System.out.println("output " + output);
		assertTrue(output.toString(), atomcount == 14);
		
		
		headterms = new LinkedList<Term>();
		headterms.add(fac.getVariable("x"));
		headterms.add(fac.getVariable("y"));
		headterms.add(fac.getVariable("m"));
		headterms.add(fac.getVariable("o"));

		head = fac.getAtom(fac.getPredicate("q", 4), headterms);
		body = new LinkedList<Atom>();
		body.add(fac.getAtom(fac.getDataPropertyPredicate("name"), fac.getVariable("s1"), fac.getVariable("x")));
		body.add(fac.getAtom(fac.getDataPropertyPredicate("name"), fac.getVariable("s2"), fac.getVariable("m")));
		body.add(fac.getAtom(fac.getDataPropertyPredicate("lastname"), fac.getVariable("s1"), fac.getVariable("y")));
		body.add(fac.getAtom(fac.getDataPropertyPredicate("lastname"), fac.getVariable("s2"), fac.getVariable("o")));
		query = fac.getCQIE(head, body);

		input = fac.getDatalogProgram(query);
		output = unfolder.unfold(input);
		System.out.println("input " + input);

		atomcount = 0;
		for (CQIE result: output.getRules()) {
			for (Atom atom: result.getBody()) {
				atomcount +=1;
			}
		}
		
		System.out.println("output " + output);
		assertTrue(output.toString(), atomcount == 48);

	}
}
