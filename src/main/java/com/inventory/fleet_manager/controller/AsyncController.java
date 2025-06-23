package com.inventory.fleet_manager.controller;

import com.inventory.fleet_manager.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AsyncController {

    @Autowired
    private AsyncService asyncService;

    @GetMapping("/start-async-task")
    public String startAsyncTask() {
        asyncService.performAsyncTask("SampleTask");
        return "Async task started!";
    }
}