package com.devis.mailserver;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

class RunServerTests {

	def runServer

	@Before
	public void setUp() throws Exception {
		runServer = new RunServer()
	}

	@Test
	public void test_port_parameter() {
		def param = ["-p", "6677"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "6677" == results.port
		assert !results.sleep
	}
	
	@Test
	public void test_port_parameter_full_name() {
		def param = ["-port", "6677"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "6677" == results.port
		assert !results.sleep
	}
	
	@Test
	public void test_port_parameter_with_no_parameter() {
		def param = [] as String[]
		
		def results = runServer.processParameters(param)
		
		assert !results.port
		assert !results.sleep
	}
	
	@Test
	public void test_sleep_parameter() {
		def param = ["-s", "5000"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "5000" == results.sleep
		assert !results.port
	}
	
	@Test
	public void test_config_parameter() {
		def path = "./my/path/myconfig.groovy"
		def param = ["-c", path] as String[]
		
		def results = runServer.processParameters(param)
		
		assert path == results.config
	}
	
	@Test
	public void test_config_parameter_full_name() {
		def path = "./my/path/myconfig.groovy"
		def param = ["-config", path] as String[]
		
		def results = runServer.processParameters(param)
		
		assert path == results.config
	}
	
	@Test
	public void test_config_file_port_setting() {
		def filename = "./resources-test/just_port.groovy"
		
		println "test_config_file_port_setting"
		def results = runServer.processConfigFile(filename)
		
		assert "7777" == results.port
		
	}
	
	@Test
	public void test_config_file_sleep_setting() {
		def filename = "./resources-test/just_sleep.groovy"
		
		println "test_config_file_sleep_setting"
		def results = runServer.processConfigFile(filename)
		
		assert "3000" == results.sleep
		
	}
	
	@Test
	public void test_config_file_no_filename() {
		def filename = null
		
		def results = runServer.processConfigFile(filename)

		assert [:] == results
		
	}
	
	@Test
	public void test_determine_server_params_with_mixed_cmdline_and_config() {
		def filename = "./resources-test/just_sleep.groovy"
		def params = ["-port", "6677", "-c", filename] as String[]
		
		println "test_determine_server_params_with_mixed_cmdline_and_config"
		def results = runServer.determineServerParams(params)
		
		assert 6677 == results.port
		assert 3000 == results.sleep
		
	}
	
	@Test
	public void test_determine_server_params_with_no_cmdline() {
		def params = [] as String[]
		
		println "test_determine_server_params_with_mixed_cmdline_and_config"
		def results = runServer.determineServerParams(params)
		
		assert 5555 == results.port
		assert 2000 == results.sleep
		
	}
	
	
}
