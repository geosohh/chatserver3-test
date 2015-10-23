package com.chat3.server;

import java.io.IOException;

import javax.microedition.io.ServerSocketConnection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.chat3.server.Server;

public class ServerTest {
	
	private Server server;
	private ServerSocketConnection serverSocket;

	@Before
	public void setUp() throws IOException, InterruptedException{
		server = new Server();
		serverSocket = Mockito.mock(ServerSocketConnection.class);
		Mockito.when(serverSocket.getLocalAddress()).thenReturn("mockAddress");
		Mockito.when(serverSocket.getLocalPort()).thenReturn(11111);
		
		//http://stackoverflow.com/questions/6604293/always-blocking-input-stream-for-testing
		Mockito.when(serverSocket.acceptAndOpen()).thenAnswer(new Answer<Object>() {
	        @Override
	        public Object answer(InvocationOnMock invocation) throws Throwable {
	            try {
	            Thread.sleep(10000000000L);
	            return null;
	            } catch (InterruptedException ie) {
	                throw new RuntimeException(ie);
	            }
	        }
	    });
	}
	
	@Test
	public void testIsRunning() throws InterruptedException{
		server.startListeningThread(server, serverSocket);
		Thread.sleep(1000);
		Assert.assertTrue(server.isRunning());
	}

}