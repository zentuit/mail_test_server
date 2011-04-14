package com.devis.mailserver

import org.apache.commons.cli.Option

class RunServer {
	private static int defaultPort = 5555
	private static int defaultSleep = 2000 //milliseconds
    private static String defaultForwardEmail = ''
    private static String defaultForwardHost = "localhost"
    private static String defaultForwardPort = 25
	private static String defaultConfig = "./resources/config.groovy"
	
	def private availableOptions = [
			[opt: "h", longOpt: 'help', description:'Show usage information'],
			[opt: "p", longOpt: 'port', args:1, argName:'port', 
				description:"Port to listen on (default ${defaultPort}})", default:defaultPort, isInt:true],
			[opt: "s", longOpt: 'sleep', args:1, argName:'sleep', 
				description:"Number of milliseconds to wait while polling (default ${defaultSleep})", default:defaultSleep, isInt:true],
			[opt: "f", longOpt: 'forward', args:1, argName: 'forward', 
				description:"If provided, email address to forward a copy of all incoming mail (default is no mail sent)", default:defaultForwardEmail],
			[opt: "fh", longOpt: 'forwardHost', args:1, argName: 'forwardHost', 
				description:"Mail server to use to forward email (default is ${defaultForwardHost})", default:defaultForwardHost],
			[opt: "fp", longOpt: 'forwardPort', args:1, argName: 'forwardPort', 
				description:"Mail server port to use to forward email (default is ${defaultForwardPort})", default:defaultForwardPort],
            [opt: "c", longOpt: 'config', args:1, argName: 'filename', 
				description:"Configuration file (default ${defaultConfig})", default:defaultConfig],
		]

	def static optionResults = [:]

	public static void main(String [] args) {
		
		def server = new RunServer()
		
		optionResults = server.determineServerParams(args)
		if (!optionResults) { return }

		def port = (optionResults.p) ?: defaultPort
		def sleep = (optionResults.s) ?: defaultSleep
        def forwardEmail = (optionResults.f) ?: defaultForwardEmail
        def forwardHost = (optionResults.fh) ?: defaultForwardHost
        def forwardPort = (optionResults.fp) ?: defaultForwardPort
		
		MailTestServer instance = new MailTestServer(port, sleep, forwardEmail, forwardHost, forwardPort)

		def writer = new SystemOutWriter()
		writer.writeln("Starting on port: ${port}")
		instance.writer = writer

		instance.run()
	}

	def determineServerParams(String [] args) {
		def cmdLineOptions = processParameters(args)
		
		def configOptions = processConfigFile(cmdLineOptions.config)
		
		optionResults = combineOptions(cmdLineOptions, configOptions)
		return convertToInteger(optionResults)
	}
	
	def private combineOptions(primary, secondary) {
		def results = [:]
		availableOptions.each {
			def value = (primary[it.longOpt]) ?: secondary[it.longOpt]
			if (value) {
				results[it.longOpt] = value
			}
		}
		return results
	}
	
	def private processParameters(String [] args) {
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

		return convertToLongName(options)

	} 
	
	def convertToLongName(options) {
		def results = [:]
		availableOptions.each {
			if (options[it.opt]) {
				results[it.longOpt] = options[it.opt]
			}
		}
		return results
	}
	
	def convertToInteger(options) {
		availableOptions.each {
			def value = options[it.longOpt]
			if (it.isInt && value) {
				options[it.longOpt] = Integer.valueOf(value)
			}
		}
		return options
	}
	
	def processConfigFile(filename) {
		filename = (filename) ?: defaultConfig
		
		def results = [:]
		try {
			def file = new File(filename).toURL()
			def config = new ConfigSlurper().parse(file)
			
			// config file has full name, but all the processing
			// is done with the one letter options
			config.each {k,v ->
				// we need to have all the values as strings because
				// the cmd line values are strings; so batch 
				// converting to integer is easier
				results[k] = v.toString()
			}

		} catch (java.io.FileNotFoundException ex) {
			//TODO log error here
			//TODO this availableOptions.each is repeated in multiple places
			// set to defaults
			availableOptions.each {
				def defaultValue = it["default"]
				if (defaultValue) {
					results[it.longOpt] = defaultValue
				}
			}
		}
		return results
	}
	
}


class SystemOutWriter {
	public void writeln(line) {
		System.out.println(line.toString())
	}
}
