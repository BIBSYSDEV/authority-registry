package no.bibsys.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.glassfish.jersey.server.ResourceConfig;
import com.amazonaws.serverless.proxy.internal.testutils.Timer;
import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import no.bibsys.JerseyConfig;


public class StreamLambdaHandler implements RequestStreamHandler {

    private static final ResourceConfig jerseyApplication = new JerseyConfig();

    private static final JerseyLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler =
            JerseyLambdaContainerHandler.getAwsProxyHandler(jerseyApplication);

    public StreamLambdaHandler() {
        // we enable the timer for debugging. This SHOULD NOT be enabled in production.
        Timer.enable();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
        handler.proxyStream(inputStream, outputStream, context);

        // just in case it wasn't closed by the mapper
        outputStream.close();
    }
}