package com.devis.mailserver

class RunServer {
	private static int port = 5555
	private static int sleep = 2000 //milliseconds

	public static void main(String [] args) {
		def cli = new CliBuilder(usage: 'RunServer [-h] [-p port] [-s sleep (ms)]')
		
		cli.with {
			h longOpt: 'help', 'Show usage information'
			p longOpt: 'port', args:1, argName:'port', "Port to listen on (default ${port}})"
			s longOpt: 'sleep', args:1, argName:'sleep', "Number of milliseconds to wait while polling (default ${sleep})"
		}
		
		def options = cli.parse(args)
		if (!options || options.h) {
			cli.usage()
			return
		}

		def port = (options.p) ?: port
		def sleep = (options.s) ?: sleep
		
		MailTestServer instance = new MailTestServer(port, sleep)

		def writer = new SystemOutWriter()
		writer.writeln("Starting on port: ${port}")
		instance.writer = writer

		instance.run()
	}

}


class SystemOutWriter {
	public void writeln(line) {
		System.out.println(line.toString())
	}
}
