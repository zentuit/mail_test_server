/*
*
*/
package com.devis.mailserver


import com.dumbster.smtp.*


class MailTestServer {

	private int port = 5555
	private int sleep = 2000
	
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
	}

	public static void main(String [] args) {
		MailTestServer instance = new MailTestServer()
		instance.run()
	}

	private MailTestServer run() {
		SimpleSmtpServer server = startServer()


		Runtime runtime = Runtime.getRuntime()
		Thread shutdown = new OutputSmtpServerThread(server, writer)

		runtime.addShutdownHook(shutdown)

		def count = 0

		while(true) {
			count = outputEmails(count)
		}
		return this
	}

	private int outputEmails(int count) {
		Thread.sleep(sleep)
		def emails = server.getEmails()
		emails[count..<emails.size()].each { writer.writeln "${it}" }
		count = emails.size()
		return count
	}
	
	private def startServer() {
		server = SimpleSmtpServer.start(port)
		return server
	}

}



