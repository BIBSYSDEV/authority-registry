package no.bibsys.authorization;

/*
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import java.util.Map;

/**
 * AuthPolicy receives a set of allowed and denied methods and generates a valid AWS policy for the
 * API Gateway authorizer. The constructor receives the calling user principal, the AWS account ID
 * of the API owner, and an apiOptions object. The apiOptions can contain an API Gateway RestApi Id,
 * a region for the RestApi, and a stage that calls should be allowed/denied for. For example
 *
 * new AuthPolicy(principalId, AuthPolicy.PolicyDocument.getDenyAllPolicy(region, awsAccountId,
 * restApiId, stage));
 *
 * @author Jack Kohn
 */
@SuppressWarnings("PMD")
public class AuthPolicy {

    // IAM Policy Constants
    public static final String VERSION = "Version";
    public static final String STATEMENT = "Statement";
    public static final String EFFECT = "Effect";
    public static final String ACTION = "Action";
    public static final String NOT_ACTION = "NotAction";
    public static final String RESOURCE = "Resource";
    public static final String NOT_RESOURCE = "NotResource";
    public static final String CONDITION = "Condition";
    transient PolicyDocument policyDocumentObject;
    private String principalId;
    private Map<String, Object> policyDocument;
    private Map<String, String> context;

    public AuthPolicy(String principalId, PolicyDocument policyDocumentObject,
        Map<String, String> context) {
        this.principalId = principalId;
        this.policyDocumentObject = policyDocumentObject;
        this.context = context;
    }

    public AuthPolicy() {
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    public void setPolicyDocument(PolicyDocument policyDocumentObject) {
        this.policyDocumentObject = policyDocumentObject;
    }


    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }


}
