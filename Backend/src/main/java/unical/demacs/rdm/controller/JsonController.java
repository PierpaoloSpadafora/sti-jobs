package unical.demacs.rdm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unical.demacs.rdm.config.exception.MachineException;
import unical.demacs.rdm.persistence.dto.JsonDTO;
import unical.demacs.rdm.persistence.service.implementation.JsonServiceImpl;

@RestController
@RequestMapping(value = "/api/v1/json", produces = "application/json")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "json-controller", description = "Operations related to JSON import and export.")
public class JsonController {

    private final JsonServiceImpl jsonServiceImpl;

    @Operation(summary = "Import data from JSON file", description = "Upload a JSON file to import data into the system.",
            tags = {"json-controller"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Data imported successfully.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file format or content.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Server error. Please try again later.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/import")
    public ResponseEntity<String> importJsonFile(@RequestParam("file") MultipartFile file) {
        try {
            JsonDTO jsonDTO = jsonServiceImpl.parseJsonFile(file);
            jsonServiceImpl.processImport(jsonDTO);
            return ResponseEntity.ok("Data imported successfully!");
        } catch (Exception e) {
            throw new MachineException("Failed to import data: " + e.getMessage());
        }
    }

}
