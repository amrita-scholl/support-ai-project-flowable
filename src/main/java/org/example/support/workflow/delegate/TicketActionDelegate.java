package org.example.support.workflow.delegate;

import org.example.support.service.TicketActionClient;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("ticketActionDelegate")
public class TicketActionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

        String action = (String) execution.getVariable("recommendedAction");
        String priority = (String) execution.getVariable("priority");

        switch (action) {
            case "ESCALATE_L2":
                // call L2 escalation API
                break;

            case "AUTO_REPLY":
                // send AI reply
                break;

            case "MANUAL_REVIEW":
                // should not reach here normally
                break;
        }

        execution.setVariable("actionExecuted", true);
    }
}
