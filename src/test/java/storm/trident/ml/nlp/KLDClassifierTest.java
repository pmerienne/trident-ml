package storm.trident.ml.nlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import storm.trident.ml.core.TextInstance;
import storm.trident.ml.preprocessing.EnglishTokenizer;
import storm.trident.ml.testing.data.DatasetUtils;
import storm.trident.ml.testing.data.Datasets;

public class KLDClassifierTest {

	private final static String DATABASE_WIKI = "A database is an organized collection of data. The data is typically organized to model relevant aspects of reality (for example, the availability of rooms in hotels), in a way that supports processes requiring this information (for example, finding a hotel with vacancies). A general-purpose database management system (DBMS) is a software system designed to allow the definition, creation, querying, update, and administration of databases. Well-known DBMSs include MySQL, PostgreSQL, SQLite, Microsoft SQL Server, Microsoft Access, Oracle, Sybase, dBASE, FoxPro, and IBM DB2. A database is not generally portable across different DBMS, but different DBMSs can inter-operate by using standards such as SQL and ODBC or JDBC to allow a single application to work with more than one database.";
	private final static String NOSQL_WIKI = "A NoSQL database provides a mechanism for storage and retrieval of data that use looser consistency models than traditional relational databases in order to achieve horizontal scaling and higher availability. Some authors refer to them as \"Not only SQL\" to emphasize that some NoSQL systems do allow SQL-like query language to be used. NoSQL database systems are often highly optimized for retrieval and appending operations and often offer little functionality beyond record storage (e.g. key–value pairs stores). The reduced run-time flexibility compared to full SQL systems is compensated by marked gains in scalability and performance for certain data models. In short, NoSQL database management systems are useful when working with a huge quantity of data (especially big data) when the data's nature does not require a relational model. The data can be structured, but NoSQL is used when what really matters is the ability to store and retrieve great quantities of data, not the relationships between the elements. Usage examples might be to store millions of key–value in one or a few associative arrays or to store millions of data records. This organization is particularly useful for statistical or real-time analysis of growing lists of elements (such as Twitter posts or the Internet server logs from a large group of users). Other usages of this technology are related with the flexibility of the data model; a lot of applications might gain from this unstructured data model: tools like CRM, ERP, BPM, etc, could use this flexibility to store their data without performing changes on tables or creating generic columns in a database. These databases are also good to create prototypes or fast applications, because this flexibility provides a tool to develop new features very easy.";
	private final static String MYSQL_WIKI = "MySQL (pron.: /maɪ ˌɛskjuːˈɛl/ \"My S-Q-L\",[4] officially, but also called /maɪ ˈsiːkwəl/ \"My Sequel\") is (as of 2008) the world's most widely used[5][6] open source relational database management system (RDBMS)[7] that runs as a server providing multi-user access to a number of databases. It is named after co-founder Michael Widenius' daughter, My.[8] The SQL phrase stands for Structured Query Language.[4] The MySQL development project has made its source code available under the terms of the GNU General Public License, as well as under a variety of proprietary agreements. MySQL was owned and sponsored by a single for-profit firm, the Swedish company MySQL AB, now owned by Oracle Corporation.";

	private final static String FLOWER_WIKI = "A flower, sometimes known as a bloom or blossom, is the reproductive structure found in flowering plants (plants of the division Magnoliophyta, also called angiosperms). The biological function of a flower is to effect reproduction, usually by providing a mechanism for the union of sperm with eggs. Flowers may facilitate outcrossing (fusion of sperm and eggs from different individuals in a population) or allow selfing (fusion of sperm and egg from the same flower). Some flowers produce diaspores without fertilization (parthenocarpy). Flowers contain sporangia and are the site where gametophytes develop. Flowers give rise to fruit and seeds. Many flowers have evolved to be attractive to animals, so as to cause them to be vectors for the transfer of pollen.";
	private final static String LILIUM_WIKI = "Lilium (members of which are true lilies) is a genus of herbaceous flowering plants growing from bulbs, all with large prominent flowers. Lilies are a group of flowering plants which are important in culture and literature in much of the world. Most species are native to the temperate northern hemisphere, though their range extends into the northern subtropics. Many other plants have \"lily\" in their common name but are not related to true lilies.";
	private final static String ROSE_WIKI = "A rose is a woody perennial of the genus Rosa, within the family Rosaceae. There are over 100 species. They form a group of plants that can be erect shrubs, climbing or trailing with stems that are often armed with sharp prickles. Flowers vary in size and shape and are usually large and showy, in colours ranging from white through yellows and reds. Most species are native to Asia, with smaller numbers native to Europe, North America, and northwest Africa. Species, cultivars and hybrids are all widely grown for their beauty and often are fragrant. Rose plants range in size from compact, miniature roses, to climbers that can reach 7 meters in height. Different species hybridize easily, and this has been used in the development of the wide range of garden roses.";

	@Test
	public void testWithSmallWiki() {
		EnglishTokenizer tokenizer = new EnglishTokenizer();

		KLDClassifier kldClassifier = new KLDClassifier(2);
		kldClassifier.update(0, tokenizer.tokenize(NOSQL_WIKI));
		kldClassifier.update(0, tokenizer.tokenize(MYSQL_WIKI));
		kldClassifier.update(1, tokenizer.tokenize(LILIUM_WIKI));
		kldClassifier.update(1, tokenizer.tokenize(ROSE_WIKI));

		assertEquals(0, kldClassifier.classify(tokenizer.tokenize(DATABASE_WIKI)));
		assertEquals(1, kldClassifier.classify(tokenizer.tokenize(FLOWER_WIKI)));
	}

	@Test
	public void testWithReuters() {
		List<TextInstance<Integer>> training = DatasetUtils.getTrainingFolds(0, 10, Datasets.getReutersSamples());
		List<TextInstance<Integer>> eval = DatasetUtils.getEvalFold(0, 10, Datasets.getReutersSamples());

		// Use 500 max words per class to speed up test
		KLDClassifier kldClassifier = new KLDClassifier(9, 500);

		// Train
		for (TextInstance<Integer> instance : training) {
			kldClassifier.update(instance.label, instance.tokens);
		}

		// Eval
		double evalSize = 0.0;
		double errorCount = 0.0;
		for (TextInstance<Integer> instance : eval) {
			int actual = kldClassifier.classify(instance.tokens);
			if (actual != instance.label) {
				errorCount++;
			}
			evalSize++;
		}

		double error = errorCount / evalSize;
		assertTrue("Error " + error + " is to big!", error < 0.1);
		System.out.println(error);
	}

}
