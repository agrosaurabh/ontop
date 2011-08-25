package it.unibz.krdb.obda.owlrefplatform.core.abox;

import it.unibz.krdb.obda.gui.swing.utils.OBDAProgressListener;
import it.unibz.krdb.obda.model.DataSource;
import it.unibz.krdb.obda.model.OBDADataFactory;
import it.unibz.krdb.obda.model.OBDAMappingAxiom;
import it.unibz.krdb.obda.model.Predicate;
import it.unibz.krdb.obda.model.Predicate.COL_TYPE;
import it.unibz.krdb.obda.model.PredicateAtom;
import it.unibz.krdb.obda.model.Query;
import it.unibz.krdb.obda.model.impl.OBDADataFactoryImpl;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.ABoxAssertion;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.Assertion;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.AttributeABoxAssertion;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.ConceptABoxAssertion;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.Ontology;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.RoleABoxAssertion;
import it.unibz.krdb.obda.owlrefplatform.exception.PunningException;
import it.unibz.krdb.sql.JDBCConnectionManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.reasoner.IllegalParameterException;

public class RDBMSDirectDataRepositoryManager implements RDBMSDataRepositoryManager, OBDAProgressListener {

	private Connection				conn						= null;
	// private APIController apic = null;
	private List<ABoxDumpListener>	listener					= null;
	private DataSource				db							= null;
	// private int indexcounter = 1;
	//
	private boolean					isCanceled					= false;
	private Statement				statement					= null;

	private final Logger			log							= LoggerFactory.getLogger(RDBMSDirectDataRepositoryManager.class);

	private Map<Predicate, String>	predicatetableMap			= new HashMap<Predicate, String>();

	private Map<String, Predicate>	uriPredicateMap				= new HashMap<String, Predicate>();

	private Properties				config						= null;

	private final OBDADataFactory	obdaFactory					= OBDADataFactoryImpl.getInstance();

	final String					strtablemetada				= "QuestMetadataDirectMapping";

	final String					strcreate_table_class		= "CREATE TABLE %s (term0 VARCHAR)";

	final String					strcreate_table_property	= "CREATE TABLE %s (term0 VARCHAR, term1 VARCHAR)";

	final String					strcreate_index_class		= "CREATE INDEX idx%s ON %s (term0)";

	final String					strcreate_index_property_1	= "CREATE INDEX idx1%s ON %s (term0, term1)";

	final String					strcreate_index_property_2	= "CREATE INDEX idx2%s ON %s (term1, term0)";

	final String					strcreate_meta_table		= "CREATE TABLE "
																		+ strtablemetada
																		+ " (uri VARCHAR NOT NULL, type VARCHAR NOT NULL, tablename VARCHAR NOT NULL)";

	final String					strinsert_meta_table		= "INSERT INTO " + strtablemetada + " VALUES (%s, %s, %s)";

	final String					strinsert_table_class		= "INSERT INTO %s VALUES (%s)";

	final String					strinsert_table_property	= "INSERT INTO %s VALUES (%s, %s)";

	final String					strselect_table_class		= "SELECT term0 FROM %s";

	final String					strselect_table_property	= "SELECT term0, term1 FROM %s";

	final String					strdrop_table_class			= "DROP TABLE %s";

	final String					strdrop_meta_table			= "DROP TABLE " + strtablemetada + "";

	final String					stranalyze					= "ANALYZE";

	final String					strselect_meta_table		= "SELECT uri, type, tablename FROM " + strtablemetada + "";

	private Set<Predicate>			vocabulary;

	public RDBMSDirectDataRepositoryManager(DataSource ds) throws SQLException, PunningException {
		this(ds, null);
	}

	public RDBMSDirectDataRepositoryManager(DataSource ds, Set<Predicate> vocabulary) throws SQLException, PunningException {
		this();
		try {
			if (vocabulary != null) {
				setVocabulary(vocabulary);
			}
			setDatabase(ds);
		} catch (ClassNotFoundException e) {
			RuntimeException ex = new RuntimeException(e);
			e.fillInStackTrace();
			throw ex;
		}
	}

	public RDBMSDirectDataRepositoryManager() {
		listener = new Vector<ABoxDumpListener>();
	}

	/**
	 * Adds the given the listener
	 * 
	 * @param l
	 *            the listener
	 */
	public void addListener(ABoxDumpListener l) {
		listener.add(l);
	}

	/**
	 * Removes the given the listener
	 * 
	 * @param l
	 *            the listener
	 */
	public void removeListener(ABoxDumpListener l) {
		listener.remove(l);
	}

	@Override
	public void actionCanceled() {

		try {
			isCanceled = true;
			statement.cancel();
			statement.close();
		} catch (SQLException e) {
			log.warn(e.getMessage());
		}
	}

	@Override
	public void setConfig(Properties config) {
		this.config = config;
	}

	@Override
	public void setDatabase(DataSource db) throws SQLException, ClassNotFoundException {
		this.db = db;
		conn = JDBCConnectionManager.getJDBCConnectionManager().getConnection(db);
	}

	@Override
	public void setTBox(Ontology ontology) {
		// Nothing to do here

	}

	@Override
	public String getType() {
		return TYPE_DIRECT;
	}

	@Override
	public void getTablesDDL(OutputStream outstream) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outstream));

		out.append(strcreate_meta_table);
		out.append(";\n");

		for (Predicate predicate : predicatetableMap.keySet()) {
			if (predicate.getArity() == 1) {
				out.append(String.format(strcreate_table_class, predicatetableMap.get(predicate)));
				out.append(";\n");
			} else if (predicate.getArity() == 2) {
				out.append(String.format(strcreate_table_property, predicatetableMap.get(predicate)));
				out.append(";\n");
			} else {
				throw new RuntimeException("Unsupported predicate: " + predicate);
			}
		}

		out.flush();

	}

	@Override
	public void getIndexDDL(OutputStream outstream) throws IOException {

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outstream));

		for (Predicate predicate : predicatetableMap.keySet()) {
			if (predicate.getArity() == 1) {
				out.append(String.format(strcreate_index_class, predicatetableMap.get(predicate), predicatetableMap.get(predicate)));
				out.append(";\n");
			} else if (predicate.getArity() == 2) {
				out.append(String.format(strcreate_index_property_1, predicatetableMap.get(predicate), predicatetableMap.get(predicate)));
				out.append(";\n");
				out.append(String.format(strcreate_index_property_2, predicatetableMap.get(predicate), predicatetableMap.get(predicate)));
				out.append(";\n");
			} else {
				throw new RuntimeException("Unsupported predicate: " + predicate);
			}
		}

		out.flush();

	}

	@Override
	public void getDropDDL(OutputStream outstream) throws IOException {

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outstream));

		for (Predicate predicate : predicatetableMap.keySet()) {
			out.append(String.format(strdrop_table_class, predicatetableMap.get(predicate)));
			out.append(";\n");
		}
		out.append(strdrop_meta_table);
		out.append(";\n");
		out.flush();

	}

	@Override
	public void getMetadataSQLInserts(OutputStream outstream) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outstream));

		/*
		 * Generating the inserts for the metadata table
		 */
		for (Predicate predicate : predicatetableMap.keySet()) {
			if (predicate.getArity() == 1) {
				out.append(String.format(strinsert_meta_table, getQuotedString(predicate.getName()), getQuotedString("CONCEPT"),
						getQuotedString(predicatetableMap.get(predicate))));
				out.append(";\n");
			} else if (predicate.getType(1) == COL_TYPE.OBJECT) {
				out.append(String.format(strinsert_meta_table, getQuotedString(predicate.getName()), getQuotedString("OBJECTPROPERTY"),
						getQuotedString(predicatetableMap.get(predicate))));
				out.append(";\n");
			} else if (predicate.getType(1) == COL_TYPE.LITERAL) {
				out.append(String.format(strinsert_meta_table, getQuotedString(predicate.getName()), getQuotedString("DATAPROPERTY"),
						getQuotedString(predicatetableMap.get(predicate))));
				out.append(";\n");
			} else {
				throw new RuntimeException("Unsupported predicate: " + predicate);
			}
		}

		out.flush();
	}

	@Override
	public void getSQLInserts(Iterator<ABoxAssertion> data, OutputStream outstream) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outstream));

		/*
		 * Generating the inserts for the ABox data
		 */

		while (data.hasNext()) {
			Assertion assertion = data.next();
			if (assertion instanceof ConceptABoxAssertion) {
				ConceptABoxAssertion cassertion = (ConceptABoxAssertion) assertion;
				out.append(String.format(strinsert_table_class, predicatetableMap.get(cassertion.getConcept()), getQuotedString(cassertion
						.getObject().getURI())));
				out.append(";\n");
			} else if (assertion instanceof RoleABoxAssertion) {
				RoleABoxAssertion rassertion = (RoleABoxAssertion) assertion;
				out.append(String.format(strinsert_table_property, predicatetableMap.get(rassertion.getRole()), getQuotedString(rassertion
						.getFirstObject().getURI()), getQuotedString(rassertion.getSecondObject().getURI())));
				out.append(";\n");
			} else if (assertion instanceof AttributeABoxAssertion) {
				AttributeABoxAssertion rassertion = (AttributeABoxAssertion) assertion;
				out.append(String.format(strinsert_table_property, predicatetableMap.get(rassertion.getAttribute()),
						getQuotedString(rassertion.getObject().getURI()), getQuotedString(rassertion.getValue().getValue())));
				out.append(";\n");
			}
		}

		out.flush();
	}

	@Override
	public void getCSVInserts(Iterator<ABoxAssertion> data, OutputStream out) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDBSchema(boolean dropExisting) throws SQLException {

		if (dropExisting) {
			try {
				dropDBSchema();
			} catch (SQLException e) {
				log.debug(e.getMessage(), e);
			}
		}

		conn.setAutoCommit(false);
		Statement st = conn.createStatement();

		st.addBatch(strcreate_meta_table);

		for (Predicate predicate : predicatetableMap.keySet()) {
			if (predicate.getArity() == 1) {
				st.addBatch(String.format(strcreate_table_class, predicatetableMap.get(predicate)));

			} else if (predicate.getArity() == 2) {
				st.addBatch(String.format(strcreate_table_property, predicatetableMap.get(predicate)));
			} else {
				throw new RuntimeException("Unsupported predicate: " + predicate);
			}
		}

		st.executeBatch();
		st.close();
		conn.commit();

	}

	@Override
	public void createIndexes() throws SQLException {
		Statement st = conn.createStatement();
		conn.setAutoCommit(false);

		for (Predicate predicate : predicatetableMap.keySet()) {
			if (predicate.getArity() == 1) {
				st.addBatch(String.format(strcreate_index_class, predicatetableMap.get(predicate), predicatetableMap.get(predicate)));

			} else if (predicate.getArity() == 2) {
				st.addBatch(String.format(strcreate_index_property_1, predicatetableMap.get(predicate), predicatetableMap.get(predicate)));
				st.addBatch(String.format(strcreate_index_property_2, predicatetableMap.get(predicate), predicatetableMap.get(predicate)));
			} else {
				throw new RuntimeException("Unsupported predicate: " + predicate);
			}
		}

		st.executeBatch();
		st.close();
		conn.commit();

	}

	@Override
	public void dropDBSchema() throws SQLException {

		log.debug("Droping existing tables");
		Map<Predicate, String> tempPredicatetableMap = new HashMap<Predicate, String>();
		Map<String, Predicate> tempUriPredicateMap = new HashMap<String, Predicate>();

		this.loadMetadataFromDB(tempPredicatetableMap, tempUriPredicateMap);

		Statement st = conn.createStatement();
		
		conn.setAutoCommit(false);

		for (Predicate predicate : tempPredicatetableMap.keySet()) {
			st.addBatch(String.format(strdrop_table_class, tempPredicatetableMap.get(predicate)));
			log.debug("Droping: {}", tempPredicatetableMap.get(predicate));
			
		}
		log.debug("Droping: {}", strdrop_meta_table);
		
		st.addBatch(strdrop_meta_table);

		st.executeBatch();
		st.clearBatch();
		st.close();
		conn.commit();

	}

	@Override
	public void insertMetadata() throws SQLException {
		Statement st = conn.createStatement();
		conn.setAutoCommit(false);
		/*
		 * Generating the inserts for the metadata table
		 */
		for (Predicate predicate : predicatetableMap.keySet()) {
			if (predicate.getArity() == 1) {
				st.addBatch(String.format(strinsert_meta_table, getQuotedString(predicate.getName()), getQuotedString("CONCEPT"),
						getQuotedString(predicatetableMap.get(predicate))));

			} else if (predicate.getType(1) == COL_TYPE.OBJECT) {
				st.addBatch(String.format(strinsert_meta_table, getQuotedString(predicate.getName()), getQuotedString("OBJECTPROPERTY"),
						getQuotedString(predicatetableMap.get(predicate))));
			} else if (predicate.getType(1) == COL_TYPE.LITERAL) {
				st.addBatch(String.format(strinsert_meta_table, getQuotedString(predicate.getName()), getQuotedString("DATAPROPERTY"),
						getQuotedString(predicatetableMap.get(predicate))));
			} else {
				throw new RuntimeException("Unsupported predicate: " + predicate);
			}
		}
		st.executeBatch();
		st.close();
		conn.commit();
	}

	@Override
	public void insertData(Iterator<ABoxAssertion> data) throws SQLException {
		Statement st = conn.createStatement();
		conn.setAutoCommit(false);
		/*
		 * Generating the inserts for the ABox data
		 */

		int batchCount = 0;
		while (data.hasNext()) {
			Assertion assertion = data.next();
			batchCount += 1;
			if (assertion instanceof ConceptABoxAssertion) {
				ConceptABoxAssertion cassertion = (ConceptABoxAssertion) assertion;
				st.addBatch(String.format(strinsert_table_class, predicatetableMap.get(cassertion.getConcept()), getQuotedString(cassertion
						.getObject().getURI())));
			} else if (assertion instanceof RoleABoxAssertion) {
				RoleABoxAssertion rassertion = (RoleABoxAssertion) assertion;
				st.addBatch(String.format(strinsert_table_property, predicatetableMap.get(rassertion.getRole()), getQuotedString(rassertion
						.getFirstObject().getURI()), getQuotedString(rassertion.getSecondObject().getURI())));

			} else if (assertion instanceof AttributeABoxAssertion) {
				AttributeABoxAssertion rassertion = (AttributeABoxAssertion) assertion;
				st.addBatch(String.format(strinsert_table_property, predicatetableMap.get(rassertion.getAttribute()),
						getQuotedString(rassertion.getObject().getURI()), getQuotedString(rassertion.getValue().getValue())));
			}

			if (batchCount == 50000) {
				st.executeBatch();
				st.clearBatch();
			}
		}

		st.executeBatch();
		st.close();
		conn.commit();
	}

	@Override
	public Collection<Assertion> getCBox() {
		return new LinkedList<Assertion>();
	}

	@Override
	public Collection<OBDAMappingAxiom> getMappings() {

		List<OBDAMappingAxiom> mappings = new LinkedList<OBDAMappingAxiom>();
		int mappingcounter = 0;

		Predicate unaryq = obdaFactory.getPredicate(URI.create("q"), 1);
		Predicate binaryq = obdaFactory.getPredicate(URI.create("q"), 2);

		for (Predicate pred : predicatetableMap.keySet()) {

			mappingcounter = mappingcounter + 1;
			OBDAMappingAxiom map = null;

			if (pred.getArity() == 1) {
				PredicateAtom head = obdaFactory.getAtom(unaryq, obdaFactory.getVariable("term0"));
				PredicateAtom body = obdaFactory.getAtom(pred, obdaFactory.getVariable("term0"));
				Query target = obdaFactory.getCQIE(head, body);
				String sqlquery = String.format(strselect_table_class, predicatetableMap.get(pred));
				map = obdaFactory.getRDBMSMappingAxiom(sqlquery, target);
			} else if (pred.getArity() == 2) {
				PredicateAtom head = obdaFactory.getAtom(binaryq, obdaFactory.getVariable("term0"), obdaFactory.getVariable("term1"));
				PredicateAtom body = obdaFactory.getAtom(pred, obdaFactory.getVariable("term0"), obdaFactory.getVariable("term1"));
				Query target = obdaFactory.getCQIE(head, body);
				String sqlquery = String.format(strselect_table_property, predicatetableMap.get(pred));
				map = obdaFactory.getRDBMSMappingAxiom(sqlquery, target);
			} else {
				throw new RuntimeException("Unsupported predicate: " + pred);
			}

			mappings.add(map);
		}
		return mappings;
	}

	@Override
	public void collectStatistics() throws SQLException {

		Statement sqlst = conn.createStatement();
		sqlst.executeUpdate(stranalyze);

		try {
			sqlst.close();
		} catch (Exception e) {
		}

		try {
			conn.commit();
		} catch (Exception e) {

		}
	}

	public void setVocabulary(Set<Predicate> vocabulary) throws PunningException {
		this.vocabulary = vocabulary;
		predicatetableMap.clear();
		int classcounter = 0;
		int propertycounter = 0;

		/*
		 * Initializing the table map. The table map will be used in all other
		 * operations.
		 */
		for (Predicate predicate : vocabulary) {

			Predicate existingPredicate = uriPredicateMap.get(predicate.getName().toString());
			if (existingPredicate != null && existingPredicate.equals(predicate)) {
				/* The predicate has already been processed */
				continue;
			} else if (existingPredicate != null && !existingPredicate.equals(predicate)) {
				/*
				 * A predicate with the same URI has been already processed, but
				 * it has different arity or types, i.e., the URI has been
				 * PUNNED.
				 */
				throw new PunningException(predicate, existingPredicate);
			}

			if (predicate.getArity() == 1) {
				predicatetableMap.put(predicate, "tclass" + classcounter);
				classcounter += 1;
			} else if (predicate.getArity() == 2) {
				predicatetableMap.put(predicate, "tproperty" + propertycounter);
				propertycounter += 1;
			} else {
				throw new RuntimeException("Unsupported arity. Offending predicate: " + predicate);
			}
			uriPredicateMap.put(predicate.getName().toString(), predicate);
		}
	}

	@Override
	public void loadMetadata() throws SQLException {
		this.predicatetableMap.clear();
		this.uriPredicateMap.clear();
		/*
		 * Reconstructing a predicate-table mapping
		 */
		this.loadMetadataFromDB(predicatetableMap, uriPredicateMap);
	}

	@Override
	public boolean checkMetadata() throws SQLException {

		/*
		 * Fetching the metadata from the DB
		 */
		Map<Predicate, String> dbPredicateTableMap = new HashMap<Predicate, String>();
		Map<String, Predicate> dbUriPredicateMap = new HashMap<String, Predicate>();

		loadMetadataFromDB(dbPredicateTableMap, dbUriPredicateMap);

		/*
		 * Comparing with the current vocabulary
		 */

		Set<String> uris = uriPredicateMap.keySet();
		for (String uri : uris) {
			Predicate dbPredicate = dbUriPredicateMap.get(uri);
			Predicate localPredicate = uriPredicateMap.get(uri);
			if (!dbPredicate.equals(localPredicate))
				return false;
		}

		return true;
	}

	private void loadMetadataFromDB(Map<Predicate, String> predicateTableMap, Map<String, Predicate> uriPredicateMap) throws SQLException {
		Statement sqlst = conn.createStatement();
		ResultSet result = sqlst.executeQuery(strselect_meta_table);
		log.debug("Restoring metadata from DB");
		try {
			while (result.next()) {
				String predicatename = result.getString("uri");
				String type = result.getString("type");
				String tablename = result.getString("tablename");

				Predicate predicate = null;

				if (type.equals("CONCEPT")) {
					predicate = obdaFactory.getPredicate(URI.create(predicatename), 1);
				} else if (type.equals("OBJECTPROPERTY")) {
					predicate = obdaFactory.getPredicate(URI.create(predicatename), 2, new COL_TYPE[] { COL_TYPE.OBJECT, COL_TYPE.OBJECT });
				} else if (type.equals("DATAPROPERTY")) {
					predicate = obdaFactory
							.getPredicate(URI.create(predicatename), 2, new COL_TYPE[] { COL_TYPE.OBJECT, COL_TYPE.LITERAL });
				} else {
					IllegalParameterException ex = new IllegalParameterException("URI type: " + type);
					ex.fillInStackTrace();
					throw ex;
				}
				log.debug("Predicate: {} Table: {}", predicate.toString(), tablename);
				predicateTableMap.put(predicate, tablename);
				uriPredicateMap.put(predicatename, predicate);
			}
			log.debug("Done restoring metadata");
		} catch (SQLException e) {
			throw e;
		} finally {
			try {
				result.close();
			} catch (Exception e) {
			}
			try {
				sqlst.close();
			} catch (Exception e) {
			}
		}
	}

	/*
	 * Utilities
	 */

	private String getQuotedString(String str) {
		StringBuffer bf = new StringBuffer();
		bf.append("'");
		bf.append(str);
		bf.append("'");
		return bf.toString();
	}

	private String getQuotedString(URI str) {
		StringBuffer bf = new StringBuffer();
		bf.append("'");
		bf.append(str.toString());
		bf.append("'");
		return bf.toString();
	}

}