package no.bibsys.web.exception;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEvent.Type;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionLogger implements ApplicationEventListener, RequestEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionLogger.class);

    @Override
    public void onEvent(final ApplicationEvent applicationEvent) {
    }

    @Override
    public void onEvent(RequestEvent paramRequestEvent) {
        if (paramRequestEvent.getType() == Type.ON_EXCEPTION) {
            logger.error("", paramRequestEvent.getException());
        }
    }

    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        return this;
    }
}
