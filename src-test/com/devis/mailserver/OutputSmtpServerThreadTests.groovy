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
		
		def osst = new OutputSmtpServerThread(server)
		
		osst.run()
		
		fail ("need to refactor to make it testable")
		
	}

}
