package com.talha11bu.silkroad.controller;

import com.talha11bu.silkroad.model.*;
import com.talha11bu.silkroad.services.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    SessionService sessionService;

    @PostMapping("/create")//https://<SiteName>/sessions/create
    public ResponseEntity<CreateResponse> createSession(@RequestBody CreateRequest createRequest) {

        CreateResponse response = sessionService.createSession(createRequest);
        if (response.success())
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @PostMapping("/join")//https://<SiteName>/sessions/join
    public ResponseEntity<?> joinSession(@RequestBody JoinRequest joinRequest) {
        JoinResponse response = sessionService.joinSession(joinRequest);

        if (response.success()){
            Session session = response.session();
            
            List<String> userNames = session.getUsers().stream()
                    .map(Users::getUsername) 
                    .toList();
                    
            List<String> fileNames = session.getFiles().stream()
                    .map(Files::getFileName) 
                    .toList();

            Map<String, Object> sessionDto = new HashMap<>();
            sessionDto.put("sessionId", session.getSessionId());
            sessionDto.put("password", session.getPassword());
            sessionDto.put("expiresAt", session.getExpiresAt().toString());
            sessionDto.put("users", userNames);
            sessionDto.put("files", fileNames);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("sucess", true);
            responseBody.put("Token", response.token());
            responseBody.put("session", sessionDto);
            responseBody.put("timeLeft", response.timeLeft().toString());
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ResponseBody
    @PostMapping("/rejoin") // https://<SiteName>/sessions/rejoin
    public ResponseEntity<?> rejoinSession(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        JoinResponse response = sessionService.rejoinSession(token);
        if (response.success()){
            Session session = response.session();
            
            List<String> userNames = session.getUsers().stream()
                    .map(Users::getUsername).toList();
            List<String> fileNames = session.getFiles().stream()
                    .map(Files::getFileName).toList();

            Map<String, Object> sessionDto = new HashMap<>();
            sessionDto.put("sessionId", session.getSessionId());
            sessionDto.put("password", session.getPassword());
            sessionDto.put("expiresAt", session.getExpiresAt().toString());
            sessionDto.put("users", userNames);
            sessionDto.put("files", fileNames);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("sucess", true);
            responseBody.put("Token", response.token());
            responseBody.put("session", sessionDto);
            responseBody.put("timeLeft", response.timeLeft().toString());

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/{sessionId}/upload")//https://<SiteName>/sessions/{sessionId}/upload?file={file}
    public ResponseEntity<UploadResponse> uploadFile(@PathVariable String sessionId, @RequestParam MultipartFile file){
        try {
            if (file.isEmpty())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            UploadResponse fileInfo = sessionService.uploadFile(sessionId, file);

            return new ResponseEntity<>(fileInfo, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/{sessionId}/file")//https://<site-name>/sessions/{sessionId}/file?password={password}&filename={filename}
    public ResponseEntity<Resource> downloadFile(@PathVariable String sessionId, @RequestParam String password,
            @RequestParam String filename) {
        try {
            Resource fileResource = sessionService.downloadFile(sessionId, password, filename);

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(fileResource);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{sessionId}/files/{fileName}/download-url") // https://<site-name>/sessions/{sessionId}/files/{fileName}/download-url
    public ResponseEntity<?> getFileDownloadUrl(@PathVariable String sessionId, @PathVariable String fileName,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            String downloadUrl = sessionService.getPreSignedUrlForFile(sessionId, fileName, token);
            
            if(downloadUrl == null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate download link");
            }

            return ResponseEntity.ok(Map.of("url", downloadUrl));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{sessionId}/files/zip") // https://<site-name>/sessions/{sessionId}/files/zip
    public ResponseEntity<Resource> downloadAllFilesAsZip(@PathVariable String sessionId, @RequestParam String password){
        try{
            Resource zipResource = sessionService.downloadAllFilesAsZip(sessionId, password);

            String zipFileName = String.format("session-%s-files.zip", sessionId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                    .body(zipResource);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }catch (NoSuchElementException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{sessionId}/leave")//https://<SiteName>/sessions/{sessionId}/leave
    public ResponseEntity<?>  leaveSession(@PathVariable String sessionId, @RequestParam String username){
        try{
            sessionService.removeUser(sessionId,username);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Failed to remove", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{sessionId}/file") //https://<SiteName>/sessions/{sessionId}/file?fileName={filename}
    public ResponseEntity<String> deleteFile(@PathVariable String sessionId, @RequestParam("fileName") String fileName){
        try {
            sessionService.deleteFile(sessionId, fileName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{sessionId}")//https://<SiteName>/sessions/{sessionId}
    public ResponseEntity<String> endSession(@PathVariable String sessionId, @RequestParam String username){
        try{
            sessionService.endSessionByUsers(sessionId, username);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(SecurityException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
