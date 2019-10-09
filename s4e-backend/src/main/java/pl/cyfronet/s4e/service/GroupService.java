package pl.cyfronet.s4e.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cyfronet.s4e.bean.Group;
import pl.cyfronet.s4e.bean.Institution;
import pl.cyfronet.s4e.data.repository.GroupRepository;
import pl.cyfronet.s4e.ex.GroupCreationException;
import pl.cyfronet.s4e.ex.GroupUpdateException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;

    @Transactional(rollbackFor = GroupCreationException.class)
    public Group save(Group group) throws GroupCreationException {
        try {
            return groupRepository.save(group);
        } catch (DataIntegrityViolationException e) {
            log.info("Cannot create Group with name '" + group.getName() + "'", e);
            throw new GroupCreationException(e);
        }
    }

    public Optional<Group> getGroup(String groupSlug) {
        return groupRepository.findBySlug(groupSlug);
    }

    public Page<Group> getAllByInstitution(String institutionSlug) {
        return groupRepository.findAllByInstitution_Slug(institutionSlug);
    }

    @Transactional
    public void delete(Group group, Institution institution) {
        institution.removeGroup(group.getId());
        groupRepository.deleteById(group.getId());
    }

    @Transactional(rollbackFor = GroupUpdateException.class)
    public void update(Group group) throws GroupUpdateException {
        try {
            groupRepository.save(group);
        } catch (DataIntegrityViolationException e) {
            log.info("Cannot create Group with name '" + group.getName() + "'", e);
            throw new GroupUpdateException(e);
        }
    }
}
