/*
*  MailTestServer is a standalone 'mock' SMTP server to be used during testing
*/
package com.devis.mailserver


import com.dumbster.smtp.*


class MailTestServer {

	private int port = 5555
	private int sleep = 2000 //milliseconds
	private int count = 0
    private String forwardEmail = ''
    private String forwardServer = 'localhost'
    private String forwardPort = 25
	
	def outputClosure = {
        printEmail(it)
        if (forwardEmail) { forwardEmailBody(it) }
    }
	
	def server
	def writer

	MailTestServer() {
		initialize()
	}

	MailTestServer(port, sleep, forwardEmail) {
		this.port = port
		this.sleep = sleep
        this.forwardEmail = forwardEmail
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
		while(true) {
			outputEmails()
		}
	}

	private int outputEmails() {
		Thread.sleep(sleep)
		def emails = getNewEmails()
		// TODO : make the each a closure set externally so we can have more flexibility
		emails.each(outputClosure) 
		return emails.size()
	}

	private def getNewEmails() {
		def emails = server.getEmails()
		def size = emails.size()
		// skip over emails we've already counted
		def results = emails[count..<size]
		count = size
		return results
	}	

	private def startServer() {
		server = SimpleSmtpServer.start(port)
		return server
	}
    
    private def printEmail(email) {
        writer.writeln "${email}" 
    }
    
    private def forwardEmailBody(email) {
        def ant = new AntBuilder()
        ant.mail(mailhost:forwardServer, mailport:forwardPort) {
            from(address:forwardEmail)
            to(address:forwardEmail)
            message(email)
        }
    }
    

}


