/*
*
*/


package com.devis.mailserver


class OutputSmtpServerThread extends Thread {

    def server
	def writer
    
    OutputSmtpServerThread(server, writer) { 
		this.server = server
		this.writer = writer 
	}

    public void run() {
		server.getReceivedEmail().each { writer.writeln "${it}" }
    }

    

}