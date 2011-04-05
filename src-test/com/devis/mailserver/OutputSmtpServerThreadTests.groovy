package com.devis.mailserver;

import static org.junit.Assert.*;

import org.junit.Test;

class OutputSmtpServerThreadTests {

	@Test
	public void testRun() {
		def server = new Expando() 
		server.getReceivedEmail = {
			return ["a", "b", "c"]
		}

		def sep = System.getProperty("line.separator")
		StringWriter.metaClass.writeln = { line ->
			delegate.write("${line}${sep}")
		}
		
		Writer writer = new StringWriter()

		def osst = new OutputSmtpServerThread(server, writer)
		
		osst.run()
		
		
		assert writer.toString() == "a${sep}b${sep}c${sep}"
		
	}

}
