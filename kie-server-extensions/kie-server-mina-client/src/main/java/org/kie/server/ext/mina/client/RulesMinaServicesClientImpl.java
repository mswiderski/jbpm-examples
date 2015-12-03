package org.kie.server.ext.mina.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.kie.api.command.Command;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.ServiceResponse.ResponseType;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesException;


public class RulesMinaServicesClientImpl implements RulesMinaServicesClient {
    
    private String host;
    private Integer port;
    
    private Marshaller marshaller;
    
    public RulesMinaServicesClientImpl(KieServicesConfiguration configuration, ClassLoader classloader) {
        String[] serverDetails = configuration.getServerUrl().split(":");
        
        this.host = serverDetails[0];
        this.port = Integer.parseInt(serverDetails[1]);
        
        this.marshaller = MarshallerFactory.getMarshaller(configuration.getExtraJaxbClasses(), MarshallingFormat.JSON, classloader);
    }

    public ServiceResponse<String> executeCommands(String id, String payload) {
        
        try {
            String response = sendReceive(id, payload);
            if (response.startsWith("{")) {
                return new ServiceResponse<String>(ResponseType.SUCCESS, null, response);
            } else {
                return new ServiceResponse<String>(ResponseType.FAILURE, response);
            }
        } catch (Exception e) {
            throw new KieServicesException("Unable to send request to KIE Server", e);
        }
    }

    public ServiceResponse<String> executeCommands(String id, Command<?> cmd) {
        try {
            String response = sendReceive(id, marshaller.marshall(cmd));
            if (response.startsWith("{")) {
                return new ServiceResponse<String>(ResponseType.SUCCESS, null, response);
            } else {
                return new ServiceResponse<String>(ResponseType.FAILURE, response);
            }
        } catch (Exception e) {
            throw new KieServicesException("Unable to send request to KIE Server", e);
        }
    }

    protected String sendReceive(String containerId, String content) throws Exception {
        
        // content - flatten the content to be single line
        content = content.replaceAll("\\n", "");
        
        Socket minaSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        StringBuffer data = new StringBuffer();
        try {
            minaSocket = new Socket(host, port);
            out = new PrintWriter(minaSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(minaSocket.getInputStream()));
        
            // prepare and send data
            out.println(containerId + "|" + content);
            // wait for the first line
            data.append(in.readLine());
            // and then continue as long as it's available
            while (in.ready()) {
                data.append(in.readLine());
            }
            
            return data.toString();
        } finally {
            out.close();
            in.close();
            minaSocket.close();
        }
    }
}
