package org.example.support.controller;

import org.example.support.dto.ModelDecisionResponse;
import org.example.support.service.PythonModelClient;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.support.dto.HumanReviewRequest;
import org.example.support.dto.TicketRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private PythonModelClient pythonModelClient; // <-- injected service

    @PostMapping
    public ResponseEntity<Map<String, String>> createTicket(@RequestBody TicketRequest request) {
        Map<String, Object> vars = new HashMap<>();
        // keep confidence as before
        vars.put("confidence", request.getConfidence() != null ? request.getConfidence() : 0.0);
        // store ticket text under a descriptive key
        vars.put("ticketDescription", request.getTicket());

        var processInstance = runtimeService.startProcessInstanceByKey("supportAutomation", vars);
        String processInstanceId = processInstance.getId();

        // call Python model to get priority based on ticket text + confidence + processInstanceId
        ModelDecisionResponse modelResp = null;
        try {
            modelResp = pythonModelClient.requestPriority(request.getTicket(), request.getConfidence(), processInstanceId);
        } catch (Exception ex) {
            logger.warn("Python model call failed for process {}: {}", processInstanceId, ex.getMessage());
        }

        if (modelResp != null && modelResp.getPriority() != null) {
            // push priority back into runtime variables so the process can use it
            runtimeService.setVariable(processInstanceId, "priority", modelResp.getPriority());
        }

        Map<String, String> response = new HashMap<>();
        response.put("processInstanceId", processInstanceId);
        if (modelResp != null && modelResp.getPriority() != null) {
            response.put("priority", modelResp.getPriority());
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTicket(@PathVariable("id") String processInstanceId) {
        var activeInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        Map<String, Object> out = new HashMap<>();
        boolean finished = (activeInstance == null);

        if (!finished) {
            Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
            if (vars == null || vars.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            out.putAll(vars);
        } else {
            List<HistoricVariableInstance> historicVars = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list();

            if (historicVars == null || historicVars.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            for (HistoricVariableInstance hvi : historicVars) {
                out.put(hvi.getVariableName(), hvi.getValue());
            }
        }

        out.put("finished", finished);
        return ResponseEntity.ok(out);
    }

    // Debug endpoint - list active tasks for the process instance
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Map<String, Object>>> listTasks(@PathVariable("id") String processInstanceId) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .list();

        List<Map<String, Object>> out = tasks.stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("name", t.getName());
            m.put("taskDefinitionKey", t.getTaskDefinitionKey());
            m.put("assignee", t.getAssignee());
            return m;
        }).collect(Collectors.toList());

        if (out.isEmpty()) {
            // check if process finished
            boolean finished = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() == null;
            if (finished) {
                return ResponseEntity.status(HttpStatus.GONE).body(out); // process finished, no active tasks
            }
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(out);
    }

    @PostMapping("/{id}/human-review")
    public ResponseEntity<Map<String, Object>> submitHumanReview(
            @PathVariable("id") String processInstanceId,
            @RequestBody(required = false) HumanReviewRequest request) {

        Map<String, Object> resp = new HashMap<>();
        resp.put("processInstanceId", processInstanceId);

        if (request == null) {
            resp.put("success", false);
            resp.put("message", "Request body is required (approved + optional notes).");
            resp.put("finished", runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() == null);
            return ResponseEntity.badRequest().body(resp);
        }

        // Try to find the human review task
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey("humanReview")
                .singleResult();

        if (task == null) {
            task = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .taskName("Human Review")
                    .singleResult();
        }

        if (task == null) {
            task = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .singleResult();
        }

        boolean finished = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult() == null;

        if (task == null) {
            resp.put("success", false);
            resp.put("finished", finished);
            if (finished) {
                resp.put("message", "Process already finished; no active human task.");
                return ResponseEntity.status(HttpStatus.GONE).body(resp);
            } else {
                resp.put("message", "No active human task found for this process instance.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
            }
        }

        // complete the task with supplied variables
        Map<String, Object> vars = new HashMap<>();
        vars.put("humanApproved", request.isApproved());
        if (request.getNotes() != null) {
            vars.put("reviewNotes", request.getNotes());
        }

        logger.info("Completing task {} ({}), vars={}", task.getId(), task.getTaskDefinitionKey(), vars);
        taskService.complete(task.getId(), vars);

        // re-check process state and collect variables for response
        finished = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult() == null;

        Map<String, Object> returnedVars = new HashMap<>();
        if (!finished) {
            Map<String, Object> runtimeVars = runtimeService.getVariables(processInstanceId);
            if (runtimeVars != null) returnedVars.putAll(runtimeVars);
        } else {
            List<HistoricVariableInstance> historicVars = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            if (historicVars != null) {
                for (HistoricVariableInstance hvi : historicVars) {
                    returnedVars.put(hvi.getVariableName(), hvi.getValue());
                }
            }
        }

        resp.put("success", true);
        resp.put("message", "Human review submitted");

        resp.put("taskId", task.getId());
        resp.put("finished", finished);
        resp.put("variables", returnedVars);

        return ResponseEntity.ok(resp);
    }
}
