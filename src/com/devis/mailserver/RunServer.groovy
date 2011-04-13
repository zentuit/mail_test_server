package com.devis.mailserver

class RunServer {
	private static String defaultPort = "5555"
	private static String defaultSleep = "2000" //milliseconds
	private static String defaultConfig = "./resources/config.groovy"

	public static void main(String [] args) {
		
		def options = determineServerParams(args)
		if (!options) { return }

		def port = (options.p) ?: defaultPort
		def sleep = (options.s) ?: defaultSleep
		
		MailTestServer instance = new MailTestServer(port, sleep)

		def writer = new SystemOutWriter()
		writer.writeln("Starting on port: ${port}")
		instance.writer = writer

		instance.run()
	}

	def determineServerParams(String [] args) {
		def cmdLineOptions = processParameters(args)
		
		def configOptions = processConfigFile(cmdLineOptions.c)
		
		def options = configOptions + cmdLineOptions
	}
	
	def static processParameters(String [] args) {
		def cli = new CliBuilder(usage: 'RunServer [-h] [-p port] [-s sleep (ms)] [-c config file')
		
		cli.with {
			h longOpt: 'help', 'Show usage information'
			p longOpt: 'port', args:1, argName:'port', "Port to listen on (default ${defaultPort}})"
			s longOpt: 'sleep', args:1, argName:'sleep', "Number of milliseconds to wait while polling (default ${defaultSleep})"
			c longOpt: 'config', args:1, argName: 'filename', "Configuration file (default ${defaultConfig})"
		}
		
		def options = cli.parse(args)
		
		if (options.h) {
			cli.usage()
			return [:]
		}

		def integerOptions = convertToInteger(options)
		return integerOptions + [config:options.c]

	} 
	
	def static convertToInteger(options) {
		def port = (options.p) ? Integer.valueOf(options.p) : null
		def sleep = (options.s) ? Integer.valueOf(options.s) : null
		return [port:port, sleep:sleep]
	}
	
	def static processConfigFile(String filename) {
		def file = new File(filename).toURL()
		def config = new ConfigSlurper().parse(file)
		
		def params = [:]
		// config file has full name, but all the processing
		// is done with the one letter options
		config.each {k,v ->
			params[k[0]] = v
		}

		return convertToInteger(params)
	}
	
}


class SystemOutWriter {
	public void writeln(line) {
		System.out.println(line.toString())
	}
}
