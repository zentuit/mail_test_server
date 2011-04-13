package com.devis.mailserver;

import static org.junit.Assert.*;
import org.junit.Test;

class RunServerTests {

	@Test
	public void test_port_parameter() {
		def param = ["-p", "6677"] as String[]
		
		def results = RunServer.processParameters(param)
		
		assert 6677 == results.port
		assert !results.sleep
	}
	
	@Test
	public void test_port_parameter_with_no_parameter() {
		def param = [] as String[]
		
		def results = RunServer.processParameters(param)
		
		assert !results.port
		assert !results.sleep
	}
	
	@Test
	public void test_sleep_parameter() {
		def param = ["-s", "5000"] as String[]
		
		def results = RunServer.processParameters(param)
		
		assert 5000 == results.sleep
		assert !results.port
	}
	
	@Test
	public void test_sleep_parameter_with_no_parameter() {
		def param = [] as String[]
		
//		def results = RunServer.processParameters(param)
		
//		assert Integer.valueOf(RunServer.sleep) == results.sleep
//		assert Integer.valueOf(RunServer.port) == results.port
	}

}
