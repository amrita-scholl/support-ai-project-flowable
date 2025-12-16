package org.example.support.workflow.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassifyTicketDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(ClassifyTicketDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        Object ticket = execution.getVariable("ticket");
        Object confidence = execution.getVariable("confidence");

        logger.info("ClassifyTicketDelegate running - ticket: {}, confidence: {}", ticket, confidence);

        // example: set a derived variable
        if (confidence instanceof Number) {
            double c = ((Number) confidence).doubleValue();
            execution.setVariable("highConfidence", c >= 0.8);
        }
    }
}
