package com.devis.mailserver

import org.apache.commons.cli.Option

class RunServer {
	private static String defaultPort = "5555"
	private static String defaultSleep = "2000" //milliseconds
	private static String defaultConfig = "./resources/config.groovy"
	
	def private availableOptions = [
			[opt: "h", longOpt: 'help', description:'Show usage information'],
			[opt: "p", longOpt: 'port', args:1, argName:'port', description:"Port to listen on (default ${defaultPort}})", isInt:true],
			[opt: "s", longOpt: 'sleep', args:1, argName:'sleep', description:"Number of milliseconds to wait while polling (default ${defaultSleep})", isInt:true],
			[opt: "c", longOpt: 'config', args:1, argName: 'filename', description:"Configuration file (default ${defaultConfig})"],
			[opt: "f", longOpt: 'forward', args:1, argName: 'forward', description:"Mail port to forward, if needed", isInt:true],
		]


	public static void main(String [] args) {
		
		def server = new RunServer()
		
		def options = server.determineServerParams(args)
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
	
	def processParameters(String [] args) {
		def cli = new CliBuilder(usage: 'RunServer [options]', header: 'Options')

		availableOptions.each {
			def opt = new Option(it.opt, it.description)
			if (it.longOpt) { opt.longOpt = it.longOpt }
			if (it.args) { opt.args = it.args }
			if (it.argName) { opt.argName = it.argName }
			cli << opt
		}
		
		def options = cli.parse(args)
		
		if (options.h) {
			cli.usage()
			return [:]
		}

		def integerOptions = convertToInteger(options)
		return integerOptions + [config:options.c]

	} 
	
	def convertToInteger(options) {
		def results = [:]
		availableOptions.each {
			if (it.isInt) {
				def opt = (options[it.opt]) ? Integer.valueOf(options[it.opt]) : null
				results[it.longOpt] = opt
			}
		}
		return results
	}
	
	def processConfigFile(String filename) {
		filename = (filename) ?: defaultConfig
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
