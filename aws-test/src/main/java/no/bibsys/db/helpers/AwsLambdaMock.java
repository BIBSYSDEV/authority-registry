package no.bibsys.db.helpers;

import org.mockito.Mockito;

import com.amazonaws.services.lambda.AWSLambda;

public class AwsLambdaMock  {

    public static AWSLambda build() {
        
        AWSLambda awsLambdaMock = Mockito.mock(AWSLambda.class);
        return awsLambdaMock;
    }
    
}
