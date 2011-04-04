/*
*
*/


package com.devis.mailserver


class OutputSmtpServerThread extends Thread {

    def server
    
    OutputSmtpServerThread(server) { this.server = server }

    public void run() {
        server.getReceivedEmail().each { println it }
		
    }

    

}