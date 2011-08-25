package it.unibz.krdb.obda.owlrefplatform.core.abox.tests;

import it.unibz.krdb.obda.model.DataSource;
import it.unibz.krdb.obda.model.OBDADataFactory;
import it.unibz.krdb.obda.model.OBDAModel;
import it.unibz.krdb.obda.model.Predicate;
import it.unibz.krdb.obda.model.Predicate.COL_TYPE;
import it.unibz.krdb.obda.model.impl.OBDADataFactoryImpl;
import it.unibz.krdb.obda.model.impl.RDBMSourceParameterConstants;
import it.unibz.krdb.obda.owlrefplatform.core.abox.RDBMSDirectDataRepositoryManager;
import it.unibz.krdb.obda.owlrefplatform.core.abox.VirtualABoxMaterializer;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.ABoxAssertion;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.imp.DLLiterAttributeABoxAssertionImpl;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.imp.DLLiterConceptABoxAssertionImpl;
import it.unibz.krdb.obda.owlrefplatform.core.ontology.imp.DLLiterRoleABoxAssertionImpl;
import it.unibz.krdb.obda.owlrefplatform.core.translator.OWLAPI2ABoxIterator;
import it.unibz.krdb.obda.owlrefplatform.core.translator.OWLAPI2VocabularyExtractor;
import it.unibz.krdb.sql.JDBCConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDBMSDirectDataRepositoryManagerTest extends TestCase {

	OBDADataFactory	fac	= OBDADataFactoryImpl.getInstance();

	Logger			log	= LoggerFactory.getLogger(RDBMSDirectDataRepositoryManagerTest.class);

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDBCreationFromString() throws Exception {
		String owlfile = "src/test/resources/test/ontologies/translation/onto2.owl";

		// Loading the OWL file
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromPhysicalURI((new File(owlfile)).toURI());

		OWLAPI2VocabularyExtractor ext = new OWLAPI2VocabularyExtractor();
		Set<Predicate> preds = ext.getVocabulary(ontology);

		String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:aboxdump";
		String username = "sa";
		String password = "";

		DataSource source = fac.getDataSource(URI.create("http://www.obda.org/ABOXDUMP1"));
		source.setParameter(RDBMSourceParameterConstants.DATABASE_DRIVER, driver);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_PASSWORD, password);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_URL, url);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_USERNAME, username);
		source.setParameter(RDBMSourceParameterConstants.IS_IN_MEMORY, "true");
		source.setParameter(RDBMSourceParameterConstants.USE_DATASOURCE_FOR_ABOXDUMP, "true");

		RDBMSDirectDataRepositoryManager dbman = new RDBMSDirectDataRepositoryManager(source);

		dbman.setVocabulary(preds);

		Connection conn = JDBCConnectionManager.getJDBCConnectionManager().getConnection(source);
		Statement st = conn.createStatement();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		dbman.getTablesDDL(out);
		st.executeUpdate(out.toString());
		System.out.println(out.toString());
		out.reset();

		dbman.getMetadataSQLInserts(out);
		st.executeUpdate(out.toString());
		System.out.println(out.toString());
		out.reset();
		
		OWLAPI2ABoxIterator ait = new OWLAPI2ABoxIterator(ontology);
		dbman.getSQLInserts(ait, out);
		st.executeUpdate(out.toString());
		System.out.println(out.toString());
		out.reset();

		dbman.getIndexDDL(out);
		st.executeUpdate(out.toString());
		System.out.println(out.toString());
		out.reset();

		conn.commit();

		OBDAModel model = fac.getOBDAModel();
		model.addSource(source);
		model.addMappings(source.getSourceID(), dbman.getMappings());

		VirtualABoxMaterializer materializer = new VirtualABoxMaterializer(model);

		List<ABoxAssertion> list = materializer.getAssertionList();

		System.out.println("###########################");

		int count = 0;
		for (ABoxAssertion ass : list) {
			System.out.println(ass.toString());
			count += 1;
		}
		assertTrue("count: " + count, count == 9);

		System.out.println("###########################");

		count = materializer.getTripleCount();
		assertTrue("count: " + count, count == 9);

		// dbman.getDropDDL(out);

		conn.close();
	}

	public void testDBCreation() throws Exception {
		String owlfile = "src/test/resources/test/ontologies/translation/onto2.owl";

		// Loading the OWL file
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromPhysicalURI((new File(owlfile)).toURI());

		OWLAPI2VocabularyExtractor ext = new OWLAPI2VocabularyExtractor();
		Set<Predicate> preds = ext.getVocabulary(ontology);

		String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:aboxdump22";
		String username = "sa";
		String password = "";

		DataSource source = fac.getDataSource(URI.create("http://www.obda.org/ABOXDUMP22"));
		source.setParameter(RDBMSourceParameterConstants.DATABASE_DRIVER, driver);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_PASSWORD, password);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_URL, url);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_USERNAME, username);
		source.setParameter(RDBMSourceParameterConstants.IS_IN_MEMORY, "true");
		source.setParameter(RDBMSourceParameterConstants.USE_DATASOURCE_FOR_ABOXDUMP, "true");

		RDBMSDirectDataRepositoryManager dbman = new RDBMSDirectDataRepositoryManager(source,preds);

		

		Connection conn = JDBCConnectionManager.getJDBCConnectionManager().getConnection(source);
		Statement st = conn.createStatement();

		dbman.createDBSchema(false);
		OWLAPI2ABoxIterator ait = new OWLAPI2ABoxIterator(ontology);
		dbman.insertMetadata();
		dbman.insertData(ait);
		dbman.createIndexes();
		conn.commit();

		OBDAModel model = fac.getOBDAModel();
		model.addSource(source);
		model.addMappings(source.getSourceID(), dbman.getMappings());

		VirtualABoxMaterializer materializer = new VirtualABoxMaterializer(model);

		List<ABoxAssertion> list = materializer.getAssertionList();

		System.out.println("###########################");

		int count = 0;
		for (ABoxAssertion ass : list) {
			System.out.println(ass.toString());
			count += 1;
		}
		assertTrue("count: " + count, count == 9);

		System.out.println("###########################");

		count = materializer.getTripleCount();
		assertTrue("count: " + count, count == 9);

		conn.close();
	}

	public void testRestore() throws Exception {
		String owlfile = "src/test/resources/test/ontologies/translation/onto2.owl";

		// Loading the OWL file
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromPhysicalURI((new File(owlfile)).toURI());

		OWLAPI2VocabularyExtractor ext = new OWLAPI2VocabularyExtractor();
		Set<Predicate> preds = ext.getVocabulary(ontology);

		String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:aboxdump1000";
		String username = "sa";
		String password = "";

		DataSource source = fac.getDataSource(URI.create("http://www.obda.org/ABOXDUMP1000"));
		source.setParameter(RDBMSourceParameterConstants.DATABASE_DRIVER, driver);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_PASSWORD, password);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_URL, url);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_USERNAME, username);
		source.setParameter(RDBMSourceParameterConstants.IS_IN_MEMORY, "true");
		source.setParameter(RDBMSourceParameterConstants.USE_DATASOURCE_FOR_ABOXDUMP, "true");

		RDBMSDirectDataRepositoryManager dbman = new RDBMSDirectDataRepositoryManager(source,preds);
		

		Connection conn = JDBCConnectionManager.getJDBCConnectionManager().getConnection(source);
		Statement st = conn.createStatement();

		dbman.createDBSchema(false);
		OWLAPI2ABoxIterator ait = new OWLAPI2ABoxIterator(ontology);
		dbman.insertMetadata();
		dbman.insertData(ait);
		dbman.createIndexes();
		conn.commit();

		/*
		 * Reseting the manager
		 */
		dbman = new RDBMSDirectDataRepositoryManager(source);
		assertTrue(dbman.checkMetadata());
		dbman.loadMetadata();

		OBDAModel model = fac.getOBDAModel();
		model.addSource(source);

		model.addMappings(source.getSourceID(), dbman.getMappings());

		VirtualABoxMaterializer materializer = new VirtualABoxMaterializer(model);

		List<ABoxAssertion> list = materializer.getAssertionList();

		System.out.println("###########################");

		int count = 0;
		for (ABoxAssertion ass : list) {
			System.out.println(ass.toString());
			count += 1;
		}
		assertTrue("count: " + count, count == 9);

		System.out.println("###########################");

		count = materializer.getTripleCount();
		assertTrue("count: " + count, count == 9);

		conn.close();
	}

	public void testDBCreationBIGDATA() throws Exception {
		String owlfile = "src/test/resources/test/ontologies/translation/onto2.owl";

		// Loading the OWL file
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromPhysicalURI((new File(owlfile)).toURI());

		OWLAPI2VocabularyExtractor ext = new OWLAPI2VocabularyExtractor();
		Set<Predicate> preds = ext.getVocabulary(ontology);

		String driver = "org.h2.Driver";
		String url = "jdbc:h2:mem:aboxdump66";
		String username = "sa";
		String password = "";

		DataSource source = fac.getDataSource(URI.create("http://www.obda.org/ABOXDUMP4"));
		source.setParameter(RDBMSourceParameterConstants.DATABASE_DRIVER, driver);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_PASSWORD, password);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_URL, url);
		source.setParameter(RDBMSourceParameterConstants.DATABASE_USERNAME, username);
		source.setParameter(RDBMSourceParameterConstants.IS_IN_MEMORY, "true");
		source.setParameter(RDBMSourceParameterConstants.USE_DATASOURCE_FOR_ABOXDUMP, "true");

		RDBMSDirectDataRepositoryManager dbman = new RDBMSDirectDataRepositoryManager(source);

		dbman.setVocabulary(preds);

		Connection conn = JDBCConnectionManager.getJDBCConnectionManager().getConnection(source);
		Statement st = conn.createStatement();

		log.debug("Creating schema and loading data...");

		dbman.createDBSchema(false);
		ABoxAssertionGeneratorIterator ait = new ABoxAssertionGeneratorIterator(100000, preds);
		dbman.insertMetadata();
		dbman.insertData(ait);
		dbman.createIndexes();

		conn.commit();

		log.debug("Executing tests...");

		OBDAModel model = fac.getOBDAModel();
		model.addSource(source);
		model.addMappings(source.getSourceID(), dbman.getMappings());

		VirtualABoxMaterializer materializer = new VirtualABoxMaterializer(model);

		List<ABoxAssertion> list = materializer.getAssertionList();

		// System.out.println("###########################");

		int count = 0;
		for (ABoxAssertion ass : list) {
			// System.out.println(ass.toString());
			count += 1;
		}
		assertTrue("count: " + count, count == 100000);

		// System.out.println("###########################");

		count = materializer.getTripleCount();
		assertTrue("count: " + count, count == 100000);

		conn.close();
	}

	public class ABoxAssertionGeneratorIterator implements Iterator<ABoxAssertion> {

		final int				MAX_ASSERTIONS;
		int						currentassertion	= 0;
		final OBDADataFactory	fac					= OBDADataFactoryImpl.getInstance();
		List<Predicate>			vocab				= new LinkedList<Predicate>();
		final int				size;

		final Random			rand;

		public ABoxAssertionGeneratorIterator(int numberofassertions, Collection<Predicate> vocabulary) {
			MAX_ASSERTIONS = numberofassertions;

			for (Predicate pred : vocabulary) {
				vocab.add(pred);
			}
			size = vocabulary.size();
			rand = new Random();

		}

		@Override
		public boolean hasNext() {
			if (currentassertion < MAX_ASSERTIONS)
				return true;
			return false;
		}

		@Override
		public ABoxAssertion next() {
			if (currentassertion >= MAX_ASSERTIONS)
				throw new NoSuchElementException();

			currentassertion += 1;
			int pos = rand.nextInt(size);
			Predicate pred = vocab.get(pos);
			ABoxAssertion assertion = null;

			if (pred.getArity() == 1) {
				assertion = new DLLiterConceptABoxAssertionImpl(pred, fac.getURIConstant(URI.create("1")));
			} else if (pred.getType(1) == COL_TYPE.OBJECT) {
				assertion = new DLLiterRoleABoxAssertionImpl(pred, fac.getURIConstant(URI.create("1")), fac.getURIConstant(URI.create("2")));
			} else {
				assertion = new DLLiterAttributeABoxAssertionImpl(pred, fac.getURIConstant(URI.create("1")), fac.getValueConstant("x"));
			}
			return assertion;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

}