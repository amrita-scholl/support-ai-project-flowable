package org.example.support.workflow.delegate;

import org.example.support.dto.ModelDecisionResponse;
import org.example.support.service.PythonModelClient;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("classifyTicketDelegate")
public class ClassifyTicketDelegate implements JavaDelegate {

    @Autowired
    private PythonModelClient pythonModelClient;

    @Override
    public void execute(DelegateExecution execution) {

        String ticket = (String) execution.getVariable("ticketDescription");
        Double confidence = (Double) execution.getVariable("confidence");

        ModelDecisionResponse ai =
                pythonModelClient.requestPriority(ticket, confidence, execution.getProcessInstanceId());

        execution.setVariable("priority", ai.getPriority());
        execution.setVariable("category", ai.getCategory());
        execution.setVariable("urgency", ai.getUrgency());
        execution.setVariable("sentiment", ai.getSentiment());
        execution.setVariable("expectedResolutionHours", ai.getExpectedResolutionHours());
        execution.setVariable("recommendedAction", ai.getRecommendedAction());
        execution.setVariable("requiresHumanReview", ai.getRequiresHumanReview());
    }
}
