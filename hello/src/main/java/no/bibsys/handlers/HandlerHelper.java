//package no.bibsys.handlers;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import no.bibsys.utils.IOUtils;
//import no.bibsys.handlers.responses.GatewayResponse;
//import no.bibsys.utils.ApiMessageParser;
//import org.springframework.http.HttpStatus;
//
//public abstract class HandlerHelper<I, O>  implements RequestStreamHandler {
//
//
//    private final Class<I> iclass;
//    private final Class<O> oclass;
//    private OutputStream outputStream;
//    private Context context;
//    private InputStream inputStream;
//
//
//    private ApiMessageParser<I> inputParser = new ApiMessageParser<>();
//    private IOUtils ioUtils = new IOUtils();
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    public HandlerHelper(Class<I> iclass, Class<O> oclass) {
//        this.iclass = iclass;
//        this.oclass = oclass;
//
//    }
//
//
//    public void init(InputStream inputStream, OutputStream outputStream, Context context) {
//        this.inputStream = inputStream;
//        this.outputStream = outputStream;
//        this.context = context;
//    }
//
//    public I parseInput(InputStream inputStream)
//            throws IOException {
//        String inputString = ioUtils.streamToString(inputStream);
//
//        I input = inputParser.getBodyElementFromJson(inputString, iclass);
//
//        return input;
//
//    }
//
//    public abstract O processInput(I input) throws  IOException;
//
//    public void writeOutput(O output) throws IOException {
//        String outputString = objectMapper.writeValueAsString(output);
//        GatewayResponse gatewayResponse = new GatewayResponse(outputString);
//        String responseJson = objectMapper.writeValueAsString(gatewayResponse);
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
//        writer.write(responseJson);
//        writer.close();
//
//    }
//
//
//    public void writerFailure(Throwable error) throws IOException {
//        String outputString=error.getMessage();
//        GatewayResponse gatewayResponse = new GatewayResponse(outputString,
//            GatewayResponse.defaultHeaders(), HttpStatus.INTERNAL_SERVER_ERROR.value());
//        String responseJson=objectMapper.writeValueAsString(gatewayResponse);
//        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream));
//        writer.write(responseJson);
//        writer.close();
//    }
//
//
//    protected Context getContext() {
//        return this.context;
//    }
//
//
//
//
//
//    public void handleRequest(InputStream input, OutputStream output, Context context)
//        throws IOException {
//        init(input,output,context);
//        I inputObject=parseInput(input);
//        O response= null;
//        try {
//            response = processInput(inputObject);
//            writeOutput(response);
//        } catch (IOException e) {
//            e.printStackTrace();
//            writerFailure(e);
//        }
//    }
//
//}
