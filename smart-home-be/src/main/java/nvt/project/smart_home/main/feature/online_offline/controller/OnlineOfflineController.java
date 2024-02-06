package nvt.project.smart_home.main.feature.online_offline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nvt.project.smart_home.main.feature.online_offline.service.interf.IOnlineOfflineHistoryService;
import nvt.project.smart_home.main.feature.online_offline.web_dto.request.OnlineOfflineWebRequest;
import nvt.project.smart_home.main.feature.online_offline.web_dto.response.OnlineOfflineWebResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping("/api/offline-online")
@RestController
public class OnlineOfflineController {

    private final IOnlineOfflineHistoryService historyService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/graph-data")
    public Collection<OnlineOfflineWebResponse> getGraphData(@PathVariable("id") long id, @RequestBody @Valid OnlineOfflineWebRequest request) {
        return historyService.getGraphData(id, request);
    }
}
