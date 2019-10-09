package pl.cyfronet.s4e.bean;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String email;
    /// password hash
    @NotEmpty
    private String password;

    @Singular
    private Set<AppRole> roles;

    @ManyToMany(mappedBy = "members", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Group> groups;

    private boolean enabled;
}
