package nvt.project.smart_home.main.feature.power_consumption.service.interf;

import nvt.project.smart_home.main.core.influxdb.fluxResult.FluxResultDto;

import java.time.LocalDateTime;
import java.util.Collection;

public interface IPowerConsumptionService {
    Collection<FluxResultDto<Double>> getConsumedEnergyForProperty(Long propertyId, int minutesInPast);
    Collection<FluxResultDto<Double>> getConsumedEnergyForProperty(Long propertyId, LocalDateTime startDate, LocalDateTime endDate);
    Collection<FluxResultDto<Double>> getConsumedEnergyForCity(Long cityId, int minutesInPast);
    Collection<FluxResultDto<Double>> getConsumedEnergyForCity(Long cityId, LocalDateTime startDate, LocalDateTime endDate);
}
