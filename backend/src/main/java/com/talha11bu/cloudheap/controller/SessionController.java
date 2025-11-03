package com.talha11bu.cloudheap.controller;

import com.talha11bu.cloudheap.model.*;
import com.talha11bu.cloudheap.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/{sessionId}/upload")
    public ResponseEntity<UploadResponse> uploadFile(@PathVariable String sessionId, @RequestParam MultipartFile file){
        try {
            if (file.isEmpty())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            UploadResponse fileInfo = sessionService.uploadFile(sessionId, file);

            return new ResponseEntity<>(fileInfo, HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }
}
