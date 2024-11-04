package unical.demacs.rdm.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unical.demacs.rdm.persistence.dto.UserDTO;
import unical.demacs.rdm.persistence.service.implementation.UserServiceImpl;

@RestController
@RequestMapping(value = "/api/v1/user", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "user-controller", description = "Operations related to user management, include login.")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final ModelMapper modelMapper;


    @Operation(summary = "Create user by email", description = "Create a user using their email address.",
            tags = {"user-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(modelMapper.map(userServiceImpl.createUser(userDTO.getEmail()), UserDTO.class));
    }

    @Operation(summary = "Get user by email", description = "Retrieve a user using their email address.",
            tags = {"user-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path="/by-email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(modelMapper.map(userServiceImpl.getUserByEmail(email), UserDTO.class));
    }

    @Operation(summary = "Get user by id", description = "Retrieve a user using their id.",
            tags = {"user-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path="/by-id/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(modelMapper.map(userServiceImpl.getUserById(id), UserDTO.class));
    }

    // delete user

    @Operation(summary = "Delete user by id", description = "Delete a user using their id.",
            tags = {"user-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully."),
            @ApiResponse(responseCode = "404", description = "User not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @DeleteMapping(path="/by-id/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") String id) {
        userServiceImpl.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }


}
