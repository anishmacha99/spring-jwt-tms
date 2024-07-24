package com.andela.tms.controller;

import com.andela.tms.BaseTest;
import com.andela.tms.models.entity.Role;
import com.andela.tms.models.entity.Task;
import com.andela.tms.models.entity.User;
import com.andela.tms.payload.response.SigninResponse;
import com.andela.tms.repository.RoleRepository;
import com.andela.tms.repository.UserRepository;
import com.andela.tms.security.WebSecurityConfig;
import com.andela.tms.security.jwt.AuthEntryPointJwt;
import com.andela.tms.security.jwt.JwtUtils;
import com.andela.tms.security.services.AuthUserService;
import com.andela.tms.security.services.UserDetailsImpl;
import com.andela.tms.service.tasks.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({WebSecurityConfig.class, AuthUserService.class,
        AuthEntryPointJwt.class, JwtUtils.class})
public class AuthControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RoleRepository roleRepository;


    @Test
    public void signup_ValidReturnOk() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "testuser",
                            "password": "password",
                            "roles": ["ROLE_USER"]
                            }
                         """
                )).andExpect(status().isOk());
    }

    @Test
    public void signin_ValidReturnAndValidateToken() throws Exception {
        Authentication mockedAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockedAuthentication);
        User mockUser = createMockUser();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : mockUser.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        when(mockedAuthentication.getPrincipal()).thenReturn(new UserDetailsImpl(1L, mockUser.getUsername(), mockUser.getPassword(), authorities));

        String response = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                            {
                                "username": "%s",
                                "password": "%s"
                            }
                        """,  mockUser.getUsername(), mockUser.getPassword())
                )).andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readValue(response, SigninResponse.class).getAccessToken();

        mockMvc.perform(post("/api/auth/validateToken") // Replace "/api/protected/endpoint" with the actual endpoint that requires authorization
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}