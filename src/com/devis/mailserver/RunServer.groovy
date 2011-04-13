package com.devis.mailserver

class RunServer {
	private static String defaultPort = "5555"
	private static String defaultSleep = "2000" //milliseconds

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
		def cli = new CliBuilder(usage: 'RunServer [-h] [-p port] [-s sleep (ms)]')
		
		cli.with {
			h longOpt: 'help', 'Show usage information'
			p longOpt: 'port', args:1, argName:'port', "Port to listen on (default ${defaultPort}})"
			s longOpt: 'sleep', args:1, argName:'sleep', "Number of milliseconds to wait while polling (default ${defaultSleep})"
		}
		
		def options = cli.parse(args)
		
		println "Options: ${options}"
		
		if (options.h) {
			cli.usage()
			return [:]
		}

		def port = (!options.p) ? null: Integer.valueOf(options.p)
		def sleep = (!options.s) ? null: Integer.valueOf(options.s)
		return [port:port, sleep:sleep]

	} 
}


class SystemOutWriter {
	public void writeln(line) {
		System.out.println(line.toString())
	}
}
