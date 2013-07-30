package it.unibz.krdb.obda.quest.datatypes;

import junit.framework.Test;

public class MysqlDatatypeTest extends QuestDatatypeParent {

	public MysqlDatatypeTest(String testURI, String name, String queryFileURL, String resultFileURL, 
			String owlFileURL, String obdaFileURL, String parameterFileURL) {
		super(testURI, name, queryFileURL, resultFileURL, owlFileURL, obdaFileURL, parameterFileURL);
	}

	public static Test suite() throws Exception {
		return QuestDatatypeTestUtils.suite(new Factory() {
			@Override
			public MysqlDatatypeTest createQuestDatatypeTest(String testURI, String name, String queryFileURL, 
					String resultFileURL, String owlFileURL, String obdaFileURL) {
				return new MysqlDatatypeTest(testURI, name, queryFileURL, resultFileURL, owlFileURL, obdaFileURL, "");
			}
			@Override
			public MysqlDatatypeTest createQuestDatatypeTest(String testURI, String name, String queryFileURL, 
					String resultFileURL, String owlFileURL, String obdaFileURL, String parameterFileURL) {
				return new MysqlDatatypeTest(testURI, name, queryFileURL, resultFileURL, owlFileURL, obdaFileURL, parameterFileURL);
			}
			@Override
			public String getMainManifestFile() {
				return "/testcases-datatypes/manifest-datatype-mysql.ttl";
			}
		});
	}
}