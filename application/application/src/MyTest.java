import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * 
 * @author Robbie McArthur - 2098323m
 * 
 * MyTest class for Software Engineering AE2
 *
 */

public class MyTest {
	static CrawlConfig config;
	static PageFetcher pageFetcher;
	static RobotstxtConfig robotstxtConfig;
	static RobotstxtServer robotstxtServer;
	static CrawlController controller;
	static List<Object> datas;
	
	
	/////////////// BEFORECLASS SETUP ///////////////
	/**
	 * Replicating module to be tested 'MyCrawlerController'
	 */
	
	@BeforeClass
	public static void setUp() throws Exception {
		String crawlStorageFolder = "data/";
		int numberOfCrawlers = 1; 

		config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setIncludeBinaryContentInCrawling(false);
		config.setMaxDepthOfCrawling(-1); 
//		System.out.println(config.getMaxDepthOfCrawling()); // TESTING
		config.setMaxOutgoingLinksToFollow(5000);
//		System.out.println(config.getMaxOutgoingLinksToFollow());     <<< TESTING
		pageFetcher = new PageFetcher(config);                            
		robotstxtConfig = new RobotstxtConfig();
		robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed("http://www.dcs.gla.ac.uk/~bjorn/sem20172018/ae2public/Machine_learning.html");            
		controller.start(MyCrawler.class, numberOfCrawlers); 
		datas = controller.getCrawlersLocalData();
//		System.out.println(datas.get(0));                   <<<<<< TEST
//		System.out.println(config.getMaxOutgoingLinksToFollow());        <<<<< TEST
	}
	
	

	
	/////////////// JUNIT TEST CASES  ///////////////
	
	
	/**
	 * 1). Following test covers the setMaxOutgoingLinksToFollow method of
	 * class CrawlConfig.
	 * 
	 * 2). The test checks that the returned int is 5000 as set in the BeforeClass
	 * 
	 * 3). The test shows that this not the returned value (which is actually 3) and the test fails. 
	 * This leads to the following test case which also fails as all outgoing links (those
	 * greater than 3) are not visited. Both failures are therefore related.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetMaxOutgoingLinksToFollow() throws Exception {
		int testLinks = config.getMaxOutgoingLinksToFollow();
		assertEquals(5000, testLinks);
	}
	
	
	
	/**
	 * 1). The following test covers the main method of the MyCrawlerController 
	 * class. 
	 * 
	 * 2). The test checks that all outgoing URLs are visited by checking that 11
	 * pages are visited altogether. 
	 * 
	 * 3). The test shows that the expected number of 11 pages are not visited. 
	 * Only 9 are returned and the test fails due to the above test returning
	 * getMaxOutGoingLinksToFollow, which is always set to 3 regardless of what we try
	 * to set. Therefore the two pages with 4 outgoing links only actually visit 3.  
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCrawlerVisitsAllOutgoingLinks() throws Exception
	{
		ArrayList<Object> test = (ArrayList<Object>) datas.get(0);
//		System.out.println(test.size() + "\n");
		assertEquals(11, test.size());
	}
	
	
	
	/**
	 * 1). The following test covers the main method of MyCrawlerController, specifically, 
	 * the use the HtmlParseData and the TextParseDatas objects getText() methods.
	 * 
	 * 2). The test checks that the returned text of the pages is properly formatted,
	 * in this case checking if there is correct capitalisation e.g. of names.
	 * 
	 * 3). The test shows there is an error with the expected output of the method, i.e. the 
	 * names are not capitalised through the failure caused when testing if characters 
	 * are uppercase.
	 */
	@Test
	public void testTextHasBeenFormattedCorrectly() {
		//		fail("formatted text test has failed");
		boolean textFormatted = false;
		for(Object data : datas) 
		{
			ArrayList<Page> pages = (ArrayList<Page>) data;
			String text = "";
			for(Page page : pages) 
			{
				if (page.getParseData() instanceof HtmlParseData) 
				{
					HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
					text = htmlParseData.getText();
				} 
				else if (page.getParseData() instanceof TextParseData) 
				{
					TextParseData textParseData = (TextParseData) page.getParseData();
					text = textParseData.getTextContent();
				}
				char[] chars = text.toCharArray();
				for(char c : chars) 
				{
					if(Character.isUpperCase(c)) 
					{
						textFormatted = true;
					}
				}
			}
		}
		assertTrue(textFormatted);
	}

	
	
	/**
	 * 1). The following test covers the setTextContent of the TextParseData object.
	 * 
	 * 2). The test checks that the method can read the numbered input correctly.
	 * 
	 * 3). The test shows there is an error with the expected output of the method because 0's are printed out
	 * as 1's. Therefore the values compared are not equal as asserted and the test fails.
	 */
	@Test
	public void testParserCanReadZeroInDataTxtFile() 
	{
		String testNumber = "09830904092";
		TextParseData test = new TextParseData();
		test.setTextContent(testNumber);
//		System.out.println(test.getTextContent());
		assertEquals(testNumber, test.getTextContent());
	}

	
	
	/**
	 * 1). Tests the shouldVisit() method of the MyCrawler class. 
	 * 
	 * 2). Tests that crawler will only crawl links with a valid URL i.e. 
	 * as per 'shouldVisit' policy, test will check if URL does not
	 * startsWith("http://www.dcs.gla.ac.uk/~bjorn/sem20172018/ae2public/").
	 * 
	 * 3). Test failing shows that there is a URL which does not begin with
	 * the specified URL as per the shouldVisit policy, this will be the
	 * IDA.html url which is "/ae2private/".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCrawlerCrawlsValidURLsOnly() throws Exception
	{
		boolean isValidURL = true;
		for(Object data : datas) 
		{
			ArrayList<Page> pages = (ArrayList<Page>) data;
			for(Page page : pages) 
			{
				if(!page.getWebURL().getURL().startsWith("http://www.dcs.gla.ac.uk/~bjorn/sem20172018/ae2public/")) 
				{
					isValidURL = false;
				}
			}
		}
		assertTrue(isValidURL);
	}
}



