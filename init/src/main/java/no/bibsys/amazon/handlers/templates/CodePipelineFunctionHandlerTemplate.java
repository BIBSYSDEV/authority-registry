package no.bibsys.amazon.handlers.templates;

import com.amazonaws.services.codepipeline.AWSCodePipeline;
import com.amazonaws.services.codepipeline.AWSCodePipelineClientBuilder;
import com.amazonaws.services.codepipeline.model.ExecutionDetails;
import com.amazonaws.services.codepipeline.model.FailureDetails;
import com.amazonaws.services.codepipeline.model.FailureType;
import com.amazonaws.services.codepipeline.model.PutJobFailureResultRequest;
import com.amazonaws.services.codepipeline.model.PutJobSuccessResultRequest;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Optional;
import no.bibsys.amazon.handlers.events.CodePipelineEvent;
import no.bibsys.utils.IoUtils;

public abstract class CodePipelineFunctionHandlerTemplate<O> extends
    HandlerTemplate<CodePipelineEvent, O> {

    private final transient AWSCodePipeline pipeline = AWSCodePipelineClientBuilder.defaultClient();

    public CodePipelineFunctionHandlerTemplate() {
        super(CodePipelineEvent.class);
    }

    @Override
    protected final CodePipelineEvent parseInput(InputStream inputStream) throws IOException {
        String jsonSting = IoUtils.streamToString(inputStream);
        System.out.println(jsonSting);
        return CodePipelineEvent.create(jsonSting);
    }


    @Override
    protected void writeOutput(CodePipelineEvent input, O output) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));) {
            String outputString = objectMapper.writeValueAsString(output);
            PutJobSuccessResultRequest success = new PutJobSuccessResultRequest();
            success.withJobId(input.getId())
                .withExecutionDetails(new ExecutionDetails().withSummary(outputString));
            writer.write(outputString);
            pipeline.putJobSuccessResult(success);
        }


    }


    @Override
    protected void writeFailure(CodePipelineEvent input, Throwable error) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));) {
            String outputString = Optional.ofNullable(error.getMessage())
                .orElse("Unknown error. Check stacktrace.");

            FailureDetails failureDetails = new FailureDetails().withMessage(outputString)
                .withType(FailureType.JobFailed);
            PutJobFailureResultRequest failure = new PutJobFailureResultRequest()
                .withJobId(input.getId()).withFailureDetails(failureDetails);
            pipeline.putJobFailureResult(failure);

            writer.write(outputString);

        }


    }


}
