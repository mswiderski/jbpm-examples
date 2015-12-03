package org.kie.server.ext.mina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Ignore;
import org.junit.Test;

/**
 * make sure your KIE Server has deployed container named 'demo' that is 
 * built from https://github.com/mswiderski/bpm-projects/tree/master/kie-server-demo
 */
@Ignore("Requires running server")
public class MinaKieServerTest {
    
    private static final String MINA_HOST = System.getProperty("org.kie.server.drools-mina.ext.port", "localhost");
    private static final int MINA_PORT = Integer.parseInt(System.getProperty("org.kie.server.drools-mina.ext.port", "9123"));

    @Test
    public void testSimpleKieServiceInteractionOverMina() throws Exception {
        
        String containerId = "demo";
        String jsonContent = "{\"lookup\":\"defaultKieSession\",\"commands\":[{\"fire-all-rules\":\"\"}]}";
        
        Socket minaSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            minaSocket = new Socket(MINA_HOST, MINA_PORT);
            out = new PrintWriter(minaSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(minaSocket.getInputStream()));
        } catch (IOException e) {
            return;
        }

        out.println(containerId + "|" + jsonContent);
        // wait for the first line
        System.out.println(in.readLine());
        // and then continue as long as it's available
        while (in.ready()) {
            System.out.println(in.readLine());
        }
        
        out.close();
        in.close();
        minaSocket.close();

    }
}
