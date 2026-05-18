package com.talha11bu.silkroad;

/*import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

//import com.talha11bu.silkroad.model.Session;
import com.talha11bu.silkroad.repo.SessionRepo;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unused") // Fields used by Spring context; test methods are commented out pending implementation
class SessionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private S3Presigner s3Presigner;

	@Autowired
	private SessionRepo sessionRepository;

	/*
	 * @BeforeEach void setup() { Session session = new Session();
	 * session.setId("active-session"); session.setPassword("secure123");
	 * session.setExpiresAt(LocalDateTime.now().plusHours(1));
	 * sessionRepository.save(session); }
	 * @Test void testJoinSessionSuccess() throws Exception { String joinBody =
	 * "{\"name\": \"Alice\", \"password\": \"secure123\"}";
	 * mockMvc.perform(post("/sessions/active-session/join").contentType(MediaType.
	 * APPLICATION_JSON).content(joinBody))
	 * .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists())
	 * .andExpect(jsonPath("$.displayName").value("Alice")); }
	 * @Test void testJoinSessionWrongPassword() throws Exception { String joinBody
	 * = "{\"name\": \"Alice\", \"password\": \"wrong-pass\"}";
	 * mockMvc.perform(post("/sessions/active-session/join").contentType(MediaType.
	 * APPLICATION_JSON).content(joinBody)) .andExpect(status().isForbidden()); }
	 * @Test void testGetDownloadUrl() { PresignedGetObjectRequest
	 * mockPresignedRequest = Mockito.mock(PresignedGetObjectRequest.class); try {
	 * URL mockUrl = new URL("http://localhost:8080/mock-download");
	 * Mockito.when(mockPresignedRequest.url()).thenReturn(mockUrl); } catch
	 * (Exception e) { }
	 * Mockito.when(s3Presigner.presignGetObject(Mockito.any(GetObjectPresignRequest
	 * .class))) .thenReturn(mockPresignedRequest); String url =
	 * r2Service.generatePreSignedDownloadUrl("test-key");
	 * assertTrue(url.contains("mock-download")); }
	 */
}