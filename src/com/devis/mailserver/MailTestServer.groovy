/*
*
*/
package com.devis.mailserver


import com.dumbster.smtp.*


class MailTestServer {

	private int port = 5555
	private int sleep = 2000
	private int count = 0
	
	def server
	def writer

	MailTestServer() {
		SimpleSmtpServer.metaClass.getEmails = { ->
			return Collections.unmodifiableList(delegate.receivedMail)
		}

		writer = new OutputStreamWriter(System.out)
		def sep = System.getProperty("line.separator")
		writer.metaClass.writeln = { line ->
			delegate.write("${line}${sep}")
		}
		writer.writeln("Starting")
	}

	public static void main(String [] args) {
		MailTestServer instance = new MailTestServer()
		instance.run()
	}

	private MailTestServer run() {
		startServer()
		addOutputShutdownHook()
		poll()
		return this
	}

	private addOutputShutdownHook() {
		Runtime runtime = Runtime.getRuntime()
		Thread shutdown = new OutputSmtpServerThread(server, writer)
		runtime.addShutdownHook(shutdown)
	}

	private poll() {
		// keep track of the emails we've received already
		// so we can skip over them
		while(true) {
			count = outputEmails()
		}
	}

	private int outputEmails() {
		Thread.sleep(sleep)
		def emails = getEmails()
		emails.each { writer.writeln "${it}" }
		return emails.size()
	}

	private def getEmails() {
		def emails = server.getEmails()
		// skip over emails we've output
		def results = emails[count..<emails.size()]
		count = emails.size()
		return results
	}	

		private def startServer() {
		server = SimpleSmtpServer.start(port)
		return server
	}

}



