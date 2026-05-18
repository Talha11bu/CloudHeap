package com.talha11bu.silkroad.controller;

import com.talha11bu.silkroad.model.*;
import com.talha11bu.silkroad.services.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Session Management")
    @Operation(
            summary = "Create a new session",
            description = "Creates a new file-sharing session with a password and configurable expiration duration. " +
                    "Returns session credentials including a JWT token for the creator."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Session created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "sessionId": "SKR-7X9K2M",
                                      "userName": "alice",
                                      "password": "mySecret123",
                                      "token": "eyJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uSWQiOiJTS1ItN1g5SzJNIiwidXNlcm5hbWUiOiJhbGljZSJ9.abc123",
                                      "duration": "PT1H"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request — missing or malformed fields", content = @Content)
    })
    @PostMapping("/create")//https://<SiteName>/sessions/create
    public ResponseEntity<CreateResponse> createSession(@RequestBody CreateRequest createRequest) {

        CreateResponse response = sessionService.createSession(createRequest);
        if (response.success())
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Tag(name = "Session Management")
    @Operation(
            summary = "Join an existing session",
            description = "Joins an active session using a session ID and password. " +
                    "Returns a JWT token for the user along with the full session state including current users and files."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully joined the session",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "Token": "eyJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uSWQiOiJTS1ItN1g5SzJNIiwidXNlcm5hbWUiOiJib2IifQ.xyz789",
                                      "session": {
                                        "sessionId": "SKR-7X9K2M",
                                        "password": "mySecret123",
                                        "expiresAt": "2025-06-15T14:30:00Z",
                                        "users": ["alice", "bob"],
                                        "files": ["report.pdf", "photo.png"]
                                      },
                                      "timeLeft": "PT45M12S"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Session not found or wrong password", content = @Content)
    })
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

    @Tag(name = "Session Management")
    @Operation(
            summary = "Rejoin a session using JWT token",
            description = "Allows a user to rejoin a session they previously joined by providing their JWT token " +
                    "in the Authorization header. Useful for reconnecting after page refresh or connection drop.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully rejoined the session",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "Token": "eyJhbGciOiJIUzI1NiJ9.eyJzZXNzaW9uSWQiOiJTS1ItN1g5SzJNIiwidXNlcm5hbWUiOiJhbGljZSJ9.renewed456",
                                      "session": {
                                        "sessionId": "SKR-7X9K2M",
                                        "password": "mySecret123",
                                        "expiresAt": "2025-06-15T14:30:00Z",
                                        "users": ["alice", "bob", "charlie"],
                                        "files": ["report.pdf", "photo.png", "notes.txt"]
                                      },
                                      "timeLeft": "PT30M5S"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Session expired or token invalid", content = @Content)
    })
    @ResponseBody
    @PostMapping("/rejoin") // https://<SiteName>/sessions/rejoin
    public ResponseEntity<?> rejoinSession(
            @Parameter(description = "Bearer JWT token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...", required = true)
            @RequestHeader("Authorization") String auth) {
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

    @Tag(name = "File Operations")
    @Operation(
            summary = "Get a pre-signed upload URL for a file",
            description = "Generates a temporary pre-signed URL for uploading a specific file to R2 storage directly from the client. " +
                    "Requires JWT Bearer token authentication.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pre-signed URL generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "url": "https://da215c1edfa10adf656562d3d8f482ef.r2.cloudflarestorage.com/silkroad-r2/...",
                                      "fileKey": "SKR-7X9K2M/uuid-photo.png"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header", content = @Content),
            @ApiResponse(responseCode = "403", description = "User not authorized for this session", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to generate upload link", content = @Content)
    })
    @GetMapping("/{sessionId}/upload-url")
    public ResponseEntity<?> getUploadUrl(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Name of the file", example = "photo.png", required = true)
            @RequestParam String fileName,
            @Parameter(description = "Content type of the file", example = "image/png", required = true)
            @RequestParam String contentType,
            @Parameter(description = "Bearer JWT token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            Map<String, String> uploadInfo = sessionService.getPreSignedUploadUrl(sessionId, fileName, contentType, token);
            return ResponseEntity.ok(uploadInfo);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate upload link");
        }
    }

    @Tag(name = "File Operations")
    @Operation(
            summary = "Confirm file upload to a session",
            description = "Confirms that a file has been successfully uploaded to R2. " +
                    "Saves the file metadata and notifies other users in the session. Requires JWT Bearer token authentication.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File upload confirmed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UploadResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "filename": "quarterly-report.pdf",
                                      "fileType": "",
                                      "fileSize": 2458624
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header", content = @Content),
            @ApiResponse(responseCode = "403", description = "User not authorized for this session", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping(value = "/{sessionId}/upload-complete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadComplete(
            @Parameter(description = "ID of the session to upload to", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Upload completion details", required = true)
            @RequestBody UploadCompleteRequest request,
            @Parameter(description = "Bearer JWT token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader){
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            UploadResponse fileInfo = sessionService.confirmFileUpload(sessionId, request.fileName(), request.fileKey(), request.fileSize(), token);
            return new ResponseEntity<>(fileInfo, HttpStatus.OK);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Tag(name = "File Operations")
    @Operation(
            summary = "Download a single file",
            description = "Downloads a specific file from a session. Requires the session password for authentication. " +
                    "The file is returned as a binary stream with appropriate content-disposition headers."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "File downloaded successfully",
                    content = @Content(mediaType = "application/octet-stream")
            ),
            @ApiResponse(responseCode = "401", description = "Invalid session password", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session or file not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{sessionId}/file")//https://<site-name>/sessions/{sessionId}/file?password={password}&filename={filename}
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Session password", example = "mySecret123", required = true)
            @RequestParam String password,
            @Parameter(description = "Name of the file to download", example = "quarterly-report.pdf", required = true)
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

    @Tag(name = "File Operations")
    @Operation(
            summary = "Get a pre-signed download URL for a file",
            description = "Generates a temporary pre-signed URL for downloading a specific file from R2 storage. " +
                    "Requires JWT Bearer token authentication. The URL is valid for a limited time.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pre-signed URL generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "url": "https://da215c1edfa10adf656562d3d8f482ef.r2.cloudflarestorage.com/silkroad-r2/SKR-7X9K2M/photo.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Expires=3600&..."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header", content = @Content),
            @ApiResponse(responseCode = "403", description = "User not authorized for this session", content = @Content),
            @ApiResponse(responseCode = "404", description = "Session or file not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Failed to generate download link", content = @Content)
    })
    @GetMapping("/{sessionId}/files/{fileName}/download-url") // https://<site-name>/sessions/{sessionId}/files/{fileName}/download-url
    public ResponseEntity<?> getFileDownloadUrl(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Name of the file", example = "photo.png", required = true)
            @PathVariable String fileName,
            @Parameter(description = "Bearer JWT token", example = "Bearer eyJhbGciOiJIUzI1NiJ9...", required = true)
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

    @Tag(name = "File Operations")
    @Operation(
            summary = "Download all session files as a ZIP",
            description = "Downloads all files from a session bundled into a single ZIP archive. " +
                    "Requires the session password. The ZIP file is named `session-{sessionId}-files.zip`."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "ZIP file downloaded successfully",
                    content = @Content(mediaType = "application/zip")
            ),
            @ApiResponse(responseCode = "204", description = "No files found in the session", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid session password", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{sessionId}/files/zip") // https://<site-name>/sessions/{sessionId}/files/zip
    public ResponseEntity<Resource> downloadAllFilesAsZip(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Session password", example = "mySecret123", required = true)
            @RequestParam String password){
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

    @Tag(name = "Session Management")
    @Operation(
            summary = "Leave a session",
            description = "Removes a user from the specified session. Other users in the session will be " +
                    "notified via WebSocket that the user has left."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully left the session"),
            @ApiResponse(responseCode = "500", description = "Failed to remove user from session", content = @Content)
    })
    @DeleteMapping("/{sessionId}/leave")//https://<SiteName>/sessions/{sessionId}/leave
    public ResponseEntity<?>  leaveSession(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Username of the user leaving", example = "bob", required = true)
            @RequestParam String username){
        try{
            sessionService.removeUser(sessionId,username);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>("Failed to remove", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Tag(name = "File Operations")
    @Operation(
            summary = "Delete a file from a session",
            description = "Deletes a specific file from the session. The file is removed from both the database " +
                    "and R2 storage. Other users are notified via WebSocket."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found in the session", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{sessionId}/file") //https://<SiteName>/sessions/{sessionId}/file?fileName={filename}
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Name of the file to delete", example = "quarterly-report.pdf", required = true)
            @RequestParam("fileName") String fileName){
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

    @Tag(name = "Session Management")
    @Operation(
            summary = "End a session",
            description = "Permanently ends a session and deletes all associated files from R2 storage. " +
                    "Only the session creator can end the session. All connected users will be notified via WebSocket."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Session ended successfully"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to end this session", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{sessionId}")//https://<SiteName>/sessions/{sessionId}
    public ResponseEntity<String> endSession(
            @Parameter(description = "Session ID", example = "SKR-7X9K2M", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Username of the session creator", example = "alice", required = true)
            @RequestParam String username){
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
