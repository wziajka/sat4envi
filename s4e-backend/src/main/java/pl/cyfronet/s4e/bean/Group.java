package pl.cyfronet.s4e.bean;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Entity
@Data
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "slug", "institution_id"}))
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    @NaturalId
    private String slug;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToMany(mappedBy = "groups", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "app_user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"),
            uniqueConstraints = {@UniqueConstraint(
                    columnNames = {"group_id", "app_user_id"})})
    private List<AppUser> members;

    public void addMember(AppUser user) {
        members.add(user);
    }

    public void removeMember(AppUser user) {
        members.removeIf(m -> m.getId() == user.getId());
    }
}
