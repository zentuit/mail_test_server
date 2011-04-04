/*
*
*/
package com.devis.mailserver


import com.dumbster.smtp.*


class MailTestServer {

	private int port = 5555
	private int sleep = 2000

	MailTestServer() {
		SimpleSmtpServer.metaClass.getEmails = { ->
			return Collections.unmodifiableList(delegate.receivedMail)
		}
	}

	public static void main(String [] args) {
		MailTestServer instance = new MailTestServer()
		instance.run()
	}

	private MailTestServer run() {
		SimpleSmtpServer server = SimpleSmtpServer.start(port)


		Runtime runtime = Runtime.getRuntime()
		Thread shutdown = new OutputSmtpServerThread(server)

		runtime.addShutdownHook(shutdown)

		def count = 0

		while(true) {
			Thread.sleep(sleep)
			def emails = server.getEmails()
			emails[count..<emails.size()].each { println it }
			count = emails.size()	
		}
		return this
	}

}



