package org.example.support.workflow.delegate;

import org.example.support.dto.ModelDecisionResponse;
import org.example.support.service.PythonModelClient;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("classifyTicketDelegate")
public class ClassifyTicketDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(ClassifyTicketDelegate.class);

    @Autowired
    private PythonModelClient pythonModelClient;

    @Override
    public void execute(DelegateExecution execution) {

        // 1️⃣ Read input variables
        String ticket = (String) execution.getVariable("ticketDescription");
        Double confidence = (Double) execution.getVariable("confidence");

        // Defensive default
        if (confidence == null) {
            confidence = 0.0;
        }

        // 2️⃣ Call Python AI service
        ModelDecisionResponse ai =
                pythonModelClient.requestPriority(
                        ticket,
                        confidence,
                        execution.getProcessInstanceId()
                );

        // 3️⃣ SAFETY: validate AI response
        if (ai == null) {
            throw new IllegalStateException("AI service returned null response");
        }

        // 4️⃣ Set ALL variables used by BPMN
        execution.setVariable("confidence", ai.getConfidence());
        execution.setVariable("priority", ai.getPriority());
        execution.setVariable("category", ai.getCategory());
        execution.setVariable("urgency", ai.getUrgency());
        execution.setVariable("sentiment", ai.getSentiment());
        execution.setVariable("expectedResolutionHours", ai.getExpectedResolutionHours());
        execution.setVariable("recommendedAction", ai.getRecommendedAction());
        execution.setVariable("requiresHumanReview", ai.getRequiresHumanReview());

        // 🔥 THIS WAS MISSING (CRITICAL)
        execution.setVariable("escalationLevel", ai.getEscalationLevel());

        // 5️⃣ Defensive defaults (avoid gateway crashes)
        if (ai.getEscalationLevel() == null) {
            execution.setVariable("escalationLevel", "L2");
        }

        // 6️⃣ Debug logging (remove later if you want)
        log.info(
                "AI Decision | pid={} | confidence={} | requiresHumanReview={} | escalationLevel={} | priority={}",
                execution.getProcessInstanceId(),
                execution.getVariable("confidence"),
                execution.getVariable("requiresHumanReview"),
                execution.getVariable("escalationLevel"),
                execution.getVariable("priority")
        );
    }
}
