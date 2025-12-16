// java
package org.example.support.workflow.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GenerateReplyDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(GenerateReplyDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        Object ticketObj = execution.getVariable("ticket");
        Object confidenceObj = execution.getVariable("confidence");

        String ticket = ticketObj != null ? ticketObj.toString() : null;
        Double confidence = null;

        if (confidenceObj instanceof Number) {
            confidence = ((Number) confidenceObj).doubleValue();
        } else if (confidenceObj instanceof String) {
            try {
                confidence = Double.parseDouble((String) confidenceObj);
            } catch (NumberFormatException ignored) { /* leave confidence as null */ }
        }

        logger.info("GenerateReplyDelegate running - ticket: {}, confidence: {}", ticket, confidence);

        String reply;
        if (ticket == null || ticket.isBlank()) {
            reply = "No ticket content provided.";
        } else if (confidence != null && confidence >= 0.8) {
            reply = "Automated reply: We have identified the issue and provided steps to resolve it.";
        } else {
            reply = "Please escalate to human support for further review.";
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("reply", reply);
        // only put non-null values to avoid NPEs when using Map.of elsewhere
        if (ticket != null) vars.put("ticket", ticket);
        if (confidence != null) vars.put("confidence", confidence);

        execution.setVariables(vars);
        logger.info("GenerateReplyDelegate set variables: {}", vars);
    }
}
