package nvt.project.smart_home.main.core.controller;

import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.core.dto.CityDto;
import nvt.project.smart_home.main.core.service.interf.ICityService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/cities")
@RestController
public class CityController {
    @Qualifier("CityService")
    private final ICityService cityService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping ("")
    public ResponseEntity<List<CityDto>> getAll() {
        return new ResponseEntity<>(cityService.getAll(), HttpStatus.OK);
    }
}
