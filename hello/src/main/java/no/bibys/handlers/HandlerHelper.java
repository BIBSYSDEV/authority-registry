package no.bibys.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import no.bibys.handlers.responses.GatewayResponse;
import no.bibys.utils.ApiMessageParser;
import no.bibys.utils.IOUtils;

public abstract class HandlerHelper<I, O> {


  private final Class<I> iclass;
  private final Class<O> oclass;
  private  OutputStream outputStream;
  private  Context context;
  private InputStream inputStream;




  private ApiMessageParser<I> inputParser = new ApiMessageParser<>();

  private IOUtils ioUtils = new IOUtils();
  private ObjectMapper objectMapper = new ObjectMapper();

  public HandlerHelper(Class<I> iclass,Class<O> oclass) {
    this.iclass = iclass;
    this.oclass = oclass;

  }



 public void init(InputStream inputStream, OutputStream outputStream, Context context){
    this.inputStream=inputStream;
    this.outputStream=outputStream;
    this.context=context;
 }

  public I parseInput(InputStream inputStream)
      throws IOException {
    String inputString = ioUtils.streamToString(inputStream);

    I input = inputParser.getBodyElementFromJson(inputString, iclass);

    return input;

  }

  abstract O processInput(I input);

  public void writeOutput(O o) throws IOException {
    String json = objectMapper.writeValueAsString(o);
    GatewayResponse gatewayResponse = new GatewayResponse(json);
    String responseJson = objectMapper.writeValueAsString(gatewayResponse);
    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream));
    writer.write(responseJson);
    writer.close();

  }


  protected Context getContext(){
    return this.context;
  }

}
