package nvt.project.smart_home.main.feature.property.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.property.dto.request.PropertyRequestDto;
import nvt.project.smart_home.main.feature.property.dto.request.PropertyStatusRequestDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyRefResponseDto;
import nvt.project.smart_home.main.feature.property.dto.response.PropertyResponseDto;
import nvt.project.smart_home.main.feature.property.service.interf.IPropertyService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/properties")
@RestController
public class PropertyController {

    @Qualifier("PropertyService")
    private final IPropertyService propertyService;
    @PostMapping("/send-request")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<PropertyResponseDto> sendPropertyRequest(@Valid @RequestBody PropertyRequestDto property) throws IOException {
        return new ResponseEntity<>(propertyService.createRequest(property), HttpStatus.OK);
    }
    @GetMapping("/{email}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PropertyResponseDto>> getAllByOwnerEmail(@PathVariable("email") String email) throws IOException {
        return new ResponseEntity<>(propertyService.getAllByOwnerEmail(email), HttpStatus.OK);
    }
    @GetMapping("/requests")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PropertyResponseDto>> getAllRequests() throws IOException {
        return new ResponseEntity<>(propertyService.getAllRequests(), HttpStatus.OK);
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> changeStatus(@Valid @RequestBody PropertyStatusRequestDto property) throws IOException{
        propertyService.changeStatus(property);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<PropertyRefResponseDto>> getAll() {
        return ResponseEntity.ok(propertyService.getAll());
    }

}