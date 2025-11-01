package com.talha11bu.cloudheap.controller;

import com.talha11bu.cloudheap.model.CreateRequest;
import com.talha11bu.cloudheap.model.CreateResponse;
import com.talha11bu.cloudheap.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @PostMapping("join-session")
    public ResponseEntity<CreateResponse> createSession(@RequestBody CreateRequest createRequest){
        CreateResponse response = sessionService.createSession(createRequest);
        if(response.success())
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
