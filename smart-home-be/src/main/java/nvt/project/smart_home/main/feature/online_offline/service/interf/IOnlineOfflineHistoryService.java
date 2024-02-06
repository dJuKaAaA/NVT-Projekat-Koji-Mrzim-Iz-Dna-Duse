package nvt.project.smart_home.main.feature.online_offline.service.interf;

import nvt.project.smart_home.main.feature.online_offline.web_dto.request.OnlineOfflineWebRequest;
import nvt.project.smart_home.main.feature.online_offline.web_dto.response.OnlineOfflineWebResponse;

import java.util.List;

public interface IOnlineOfflineHistoryService {
    List<OnlineOfflineWebResponse> getGraphData(long deviceId, OnlineOfflineWebRequest request);
}
