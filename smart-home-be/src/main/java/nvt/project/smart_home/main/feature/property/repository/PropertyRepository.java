package nvt.project.smart_home.main.feature.property.repository;

import nvt.project.smart_home.main.feature.property.constant.PropertyStatus;
import nvt.project.smart_home.main.feature.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<List<Property>> findByOwner_Email(String email);
    Optional<List<Property>> findByStatus(PropertyStatus status);
}
