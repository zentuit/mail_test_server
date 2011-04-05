package com.devis.mailserver;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

class MailTestServerTests {

	def sep = System.getProperty("line.separator")
	def writer
	def data
	MailTestServer mts
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		StringWriter.metaClass.writeln = { line ->
			delegate.write("${line}${sep}")
		}
		
		writer = new StringWriter()

		data = ["this is first email", "this is second email", "and 3rd"]

		mts = new MailTestServer()
		mts.sleep = 0
		mts.writer = writer

	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void testRun() {
		def mts = new MailTestServer()
	
		fail("still have infinite loop")	
//		mts.run()
		
	}
	
	@Test
	public void test_outputEmails_returns_all_when_first_called() {
		
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return data
		}
		
		def actual = mts.outputEmails(0)
		assert 3 == actual
		assert "${data[0]}${sep}${data[1]}${sep}${data[2]}${sep}" == mts.writer.toString()
	}
	
	@Test
	public void test_outputEmails_writes_correctly_based_on_count() {
		
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return data
		}
		
		def actual = mts.outputEmails(2)
		assert 3 == actual
		assert "${data[2]}${sep}" == mts.writer.toString()
	}

}
