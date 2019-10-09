package pl.cyfronet.s4e.bean;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Data
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "slug"}))
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    @NaturalId
    private String slug;

    @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Group> groups;

    public void removeGroup(Long id) {
        groups.removeIf(g -> g.getId() == id);
    }
}
