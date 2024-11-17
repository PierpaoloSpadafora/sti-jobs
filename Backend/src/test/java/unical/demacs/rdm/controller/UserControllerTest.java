package unical.demacs.rdm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.entities.User;
import unical.demacs.rdm.persistence.service.implementation.UserServiceImpl;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDTO userDTO;
    private User user;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ID = "123";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        userDTO = new UserDTO();
        userDTO.setId(TEST_ID);
        userDTO.setEmail(TEST_EMAIL);

        user = User.buildUser()
                .id(TEST_ID)
                .email(TEST_EMAIL)
                .build();
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(TEST_EMAIL)).thenReturn(user);
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/user/by-email/" + TEST_EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void testGetUserByEmail_UserNotFound() throws Exception {
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/user/by-email/" + TEST_EMAIL))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(TEST_ID)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), eq(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/user/by-id/" + TEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void testGetUserById_UserNotFound() throws Exception {
        when(userService.getUserById(TEST_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/user/by-id/" + TEST_ID))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserById() throws Exception {
        doNothing().when(userService).deleteUserById(TEST_ID);

        mockMvc.perform(delete("/api/v1/user/by-id/" + TEST_ID))
                .andExpect(status().isNoContent());
    }
}