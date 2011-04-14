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
        assert !results.forward
	}
	
	@Test
	public void test_port_parameter_full_name() {
		def param = ["-port", "6677"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "6677" == results.port
		assert !results.sleep
        assert !results.forward
	}
	
	@Test
	public void test_with_no_parameters() {
		def param = [] as String[]
		
		def results = runServer.processParameters(param)
		
		assert !results.port
		assert !results.sleep
        assert !results.forward
	}
	
	@Test
	public void test_sleep_parameter() {
		def param = ["-s", "5000"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "5000" == results.sleep
		assert !results.port
        assert !results.forward
	}
    
	@Test
	public void test_sleep_parameter_full_name() {
		def param = ["-sleep", "5000"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "5000" == results.sleep
		assert !results.port
        assert !results.forward
	}
    
    
	@Test
	public void test_forward_parameter() {
		def param = ["-f", "example@example.org"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "example@example.org" == results.forward
		assert !results.port
        assert !results.sleep
	}

	@Test
	public void test_forward_parameter_full_name() {
		def param = ["-forward", "example@example.org"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "example@example.org" == results.forward
		assert !results.port
        assert !results.sleep
	}
    
	@Test
	public void test_forward_host_parameter() {
		def param = ["-fh", "localhost"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "localhost" == results.forwardHost
        assert !results.port
		assert !results.sleep
        assert !results.forward
        assert !results.forwardPort
	}
    
	@Test
	public void test_forward_host_parameter_full_name() {
		def param = ["-forwardHost", "localhost"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "localhost" == results.forwardHost
        assert !results.port
		assert !results.sleep
        assert !results.forward
        assert !results.forwardPort
	}
    
	@Test
	public void test_forward_port_parameter() {
		def param = ["-fp", "25"] as String[]
		
		def results = runServer.processParameters(param)
		
		assert "25" == results.forwardPort
        assert !results.port
		assert !results.sleep
        assert !results.forward
        assert !results.forwardHost
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
		
		def results = runServer.processConfigFile(filename)
		
		assert "7777" == results.port
		
	}
	
	@Test
	public void test_config_file_sleep_setting() {
		def filename = "./resources-test/just_sleep.groovy"
		
		def results = runServer.processConfigFile(filename)
		
		assert "3000" == results.sleep
		
	}
    
	@Test
	public void test_config_file_forward_setting() {
		def filename = "./resources-test/just_forward.groovy"
		
		def results = runServer.processConfigFile(filename)
		
		assert "example@example.org" == results.forward
		
	}
	
	@Test
	public void test_config_file_no_filename() {
		def filename = null
		
		def results = runServer.processConfigFile(filename)

		assert "4567" == results.port
		assert "1000" == results.sleep
		
	}
	
	@Test
	public void test_determine_server_params_with_mixed_cmdline_and_config() {
		def filename = "./resources-test/just_sleep.groovy"
		def params = ["-port", "6677", "-c", filename] as String[]
		
		def results = runServer.determineServerParams(params)
		
		assert 6677 == results.port
		assert 3000 == results.sleep
		
	}
	
	@Test
	public void test_determine_server_params_with_no_cmdline() {
		def params = [] as String[]
		
		def results = runServer.determineServerParams(params)
		
		assert 4567 == results.port
		assert 1000 == results.sleep
		
	}
	
	@Test
	public void test_determine_server_params_with_conflicting_cmdline_and_config() {
		def filename = "./resources-test/just_sleep.groovy"
		def params = ["-port", "6677", "-s", "9999", "-c", filename] as String[]
		
		def results = runServer.determineServerParams(params)
		
		assert 6677 == results.port
		// cmd line wins
		assert 9999 == results.sleep
		
	}
	
	
	@Test
	public void test_determine_server_params_with_no_cmdline_or_config() {
		def filename = "./resources-test/missing.groovy"
		def params = ["-c", filename] as String[]
		
		def results = runServer.determineServerParams(params)
		
		assert 5555 == results.port
		assert 2000 == results.sleep
		
	}
}
