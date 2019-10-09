package pl.cyfronet.s4e.service;

import com.github.slugify.Slugify;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class SlugService {
    private final Slugify slugify;

    public String slugify(String text) {
        return slugify.slugify(text);
    }
}
