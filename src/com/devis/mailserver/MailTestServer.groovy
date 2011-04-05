/*
*
*/
package com.devis.mailserver


import com.dumbster.smtp.*


class MailTestServer {

	private int port = 5555
	private int sleep = 2000 //milliseconds
	private int count = 0
	
	def server
	def writer

	MailTestServer() {
		initialize()
	}

	MailTestServer(port, sleep) {
		this.port = port
		this.sleep = sleep
		initialize()
	}

	private def initialize() {
		SimpleSmtpServer.metaClass.getEmails = { ->
			return Collections.unmodifiableList(delegate.receivedMail)
		}

	}
	
	public MailTestServer run() {
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
			outputEmails()
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


