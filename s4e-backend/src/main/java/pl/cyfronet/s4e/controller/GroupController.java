package pl.cyfronet.s4e.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.cyfronet.s4e.bean.Group;
import pl.cyfronet.s4e.controller.response.GroupResponse;
import pl.cyfronet.s4e.event.OnAddToGroupEvent;
import pl.cyfronet.s4e.event.OnRemoveFromGroupEvent;
import pl.cyfronet.s4e.ex.BadRequestException;
import pl.cyfronet.s4e.ex.GroupCreationException;
import pl.cyfronet.s4e.ex.GroupUpdateException;
import pl.cyfronet.s4e.ex.NotFoundException;
import pl.cyfronet.s4e.service.AppUserService;
import pl.cyfronet.s4e.service.GroupService;
import pl.cyfronet.s4e.service.InstitutionService;
import pl.cyfronet.s4e.service.SlugService;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static pl.cyfronet.s4e.Constants.API_PREFIX_V1;

@RestController
@RequestMapping(API_PREFIX_V1)
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final AppUserService appUserService;
    private final InstitutionService institutionService;
    private final ApplicationEventPublisher eventPublisher;
    private final SlugService slugService;

    @ApiOperation("Create a new group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "If group was created"),
            @ApiResponse(code = 400, message = "Group not created"),
            @ApiResponse(code = 404, message = "Institution not found")
    })
    @PostMapping("/institutions/{institution}/groups")
    public ResponseEntity<?> create(@RequestBody @Valid String name,
                                    @PathVariable("institution") String institutionSlug)
            throws GroupCreationException, NotFoundException {
        val institution = institutionService.getInstitution(institutionSlug)
                .orElseThrow(() -> new NotFoundException("Institution not found for id '" + institutionSlug));
        Group group = Group.builder().name(name).slug(slugService.slugify(name)).institution(institution).build();
        groupService.save(group);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Add a new member to the group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "If member was added"),
            @ApiResponse(code = 400, message = "Member not added"),
            @ApiResponse(code = 404, message = "Group or user not found")
    })
    @PostMapping("/institutions/{institution}/groups/{group}/members")
    public ResponseEntity<?> addMember(@PathVariable("group") String groupSlug,
                                       @RequestBody Long userId,
                                       @PathVariable("institution") String institutionSlug)
            throws NotFoundException, GroupUpdateException, BadRequestException {
        val group = groupService.getGroup(groupSlug)
                .orElseThrow(() -> new NotFoundException("Group not found for id '" + groupSlug));
        checkInstitution(institutionSlug, group);
        val appUser = appUserService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found for id '" + userId));
        group.addMember(appUser);
        groupService.update(group);
        eventPublisher.publishEvent(new OnAddToGroupEvent(appUser, group, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Remove a member from the group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "If member was removed"),
            @ApiResponse(code = 400, message = "Member not removed"),
            @ApiResponse(code = 404, message = "Group or user not found")
    })
    @PostMapping("/institutions/{institution}/groups/{group}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable("group") String groupSlug,
                                          @PathVariable Long userId,
                                          @PathVariable("institution") String institutionSlug)
            throws NotFoundException, GroupUpdateException, BadRequestException {
        val group = groupService.getGroup(groupSlug)
                .orElseThrow(() -> new NotFoundException("Group not found for id '" + groupSlug));
        checkInstitution(institutionSlug, group);
        val appUser = appUserService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found for id '" + groupSlug));
        group.removeMember(appUser);
        groupService.update(group);
        eventPublisher.publishEvent(new OnRemoveFromGroupEvent(appUser, group, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get a list of groups")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved list")
    })
    @GetMapping("/institutions/{institution}/groups")
    public Page<GroupResponse> getAllByInstitutionName(@PathVariable("institution") String institutionSlug) {
        Page<Group> page = groupService.getAllByInstitution(institutionSlug);
        return new PageImpl<>(
                page.stream()
                        .map(m -> GroupResponse.of(m))
                        .collect(Collectors.toList()),
                page.getPageable(),
                page.getTotalElements());
    }

    @ApiOperation("Get a group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved a group"),
            @ApiResponse(code = 400, message = "Group not retrieved"),
            @ApiResponse(code = 404, message = "Group not found")
    })
    @GetMapping("/institutions/{institution}/groups/{group}")
    public GroupResponse get(@PathVariable("group") String groupSlug,
                             @PathVariable("institution") String institutionSlug)
            throws NotFoundException, BadRequestException {
        val group = groupService.getGroup(groupSlug)
                .orElseThrow(() -> new NotFoundException("Group not found for id '" + groupSlug));
        checkInstitution(institutionSlug, group);
        return GroupResponse.of(group);
    }

    @ApiOperation("Update a group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "If group was updated"),
            @ApiResponse(code = 400, message = "Group not updated"),
            @ApiResponse(code = 404, message = "Group was not found")
    })
    @PutMapping("/institutions/{institution}/groups/{group}")
    public ResponseEntity<?> update(@RequestBody String name,
                                    @PathVariable("group") String groupSlug,
                                    @PathVariable("institution") String institutionSlug)
            throws NotFoundException, GroupUpdateException, BadRequestException {
        val group = groupService.getGroup(groupSlug)
                .orElseThrow(() -> new NotFoundException("Group not found for id '" + groupSlug));
        checkInstitution(institutionSlug, group);
        group.setName(name);
        group.setSlug(slugService.slugify(name));
        groupService.update(group);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Delete a group")
    @ApiResponses({
            @ApiResponse(code = 200, message = "If group was deleted"),
            @ApiResponse(code = 400, message = "Group was deleted")
    })
    @DeleteMapping("/institutions/{institution}/groups/{group}")
    public ResponseEntity<?> delete(@PathVariable("group") String groupSlug,
                                    @PathVariable("institution") String institutionSlug)
            throws NotFoundException, BadRequestException {
        val group = groupService.getGroup(groupSlug)
                .orElseThrow(() -> new NotFoundException("Group not found for id '" + groupSlug));
        checkInstitution(institutionSlug, group);
        val institution = institutionService.getInstitution(institutionSlug)
                .orElseThrow(() -> new NotFoundException("Institution not found for id '" + institutionSlug));
        groupService.delete(group, institution);
        return ResponseEntity.ok().build();
    }

    private void checkInstitution(String institutionSlug, Group group) throws BadRequestException {
        if (institutionSlug != group.getInstitution().getSlug()) {
            throw new BadRequestException("Institution is not correct");
        }
    }
}
