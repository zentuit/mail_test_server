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
		
		def calledServerSetup = false
		mts.metaClass.startServer = { ->
			calledServerSetup = true
		}
		def calledHook = false
		mts.metaClass.addOutputShutdownHook = { ->
			calledHook = true
		}
		def calledPoll = false
		mts.metaClass.poll = { ->
			calledPoll = true
		}

		mts.run()
		assert calledServerSetup
		assert calledHook
		assert calledPoll
		
	}
	
	@Test
	public void test_outputEmails_returns_all_when_first_called() {
		
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return data
		}
		mts.count = 0
		
		def actual = mts.outputEmails()
		assert 3 == actual
		assert "${data[0]}${sep}${data[1]}${sep}${data[2]}${sep}" == mts.writer.toString()
		assert 3 == mts.count
	}
	
	@Test
	public void test_outputEmails_writes_correctly_based_on_count() {
		
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return data
		}
		mts.count = 2
		
		def actual = mts.outputEmails()
		assert 1 == actual
		assert "${data[2]}${sep}" == mts.writer.toString()
		assert 3 == mts.count
	}

	@Test
	public void test_outputEmails_with_no_emails() {
		
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return []
		}
		mts.count = 0
		
		def actual = mts.outputEmails()
		assert 0 == actual
		assert "" == mts.writer.toString()
		assert 0 == mts.count
	}

	@Test
	public void test_getEmails_returns_correct_slice_with_count_0() {
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return data
		}
		mts.count = 0
		
		def actual = mts.getNewEmails()
		assert data == actual

	}

	@Test
	public void test_getEmails_returns_correct_slice_with_count_1() {
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return data
		}
		mts.count = 1
		
		def actual = mts.getNewEmails()
		assert data[1..2] == actual

	}

	@Test
	public void test_getEmails_returns_correct_slice_with_emails_added() {
		def first = ["first"]
		def added = ["first", "second"]
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return first
		}
		mts.count = 0
		
		def actual = mts.getNewEmails()
		assert first == actual
		assert 1 == mts.count
		
		mts.server.getEmails = { ->
			return added
		}

		actual = mts.getNewEmails()
		assert [added[1]] == actual
		assert 2 == mts.count
	}

	@Test
	public void test_outputEmails_returns_correct_count_with_emails_added() {
		def first = ["first"]
		def added = ["first", "second"]
		mts.server = new Expando()
		mts.server.getEmails = { ->
			return first
		}
		mts.count = 0
		
		def actual = mts.outputEmails()
		assert 1 == actual
		assert 1 == mts.count
		
		mts.server.getEmails = { ->
			return added
		}

		actual = mts.outputEmails()
		assert 1 == actual
		assert 2 == mts.count
	}
}
