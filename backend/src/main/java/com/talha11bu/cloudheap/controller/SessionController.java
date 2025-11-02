package com.talha11bu.cloudheap.controller;

import com.talha11bu.cloudheap.model.CreateRequest;
import com.talha11bu.cloudheap.model.CreateResponse;
import com.talha11bu.cloudheap.model.JoinRequest;
import com.talha11bu.cloudheap.model.JoinResponse;
import com.talha11bu.cloudheap.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @PostMapping("/create")
    public ResponseEntity<CreateResponse> createSession(@RequestBody CreateRequest createRequest){
        CreateResponse response = sessionService.createSession(createRequest);
        if(response.success())
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/join")
    public ResponseEntity<JoinResponse> joinSession(@RequestBody JoinRequest joinRequest){
        JoinResponse response = sessionService.joinSession(joinRequest);
        if(response.success())
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        else if(response.message().contains("Invalid Password"))
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        else if(response.message().contains("Session Expired"))
            return new ResponseEntity<>(response, HttpStatus.GONE);
        else
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
