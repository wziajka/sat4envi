package pl.cyfronet.s4e.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import pl.cyfronet.s4e.bean.Group;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<Group, Long> {
    Page<Group> findAllByInstitution_Slug(String slug);
    Optional<Group> findBySlug(String slug);
}
