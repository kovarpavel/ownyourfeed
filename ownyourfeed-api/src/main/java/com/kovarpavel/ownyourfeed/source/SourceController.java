package com.kovarpavel.ownyourfeed.source;

import com.kovarpavel.ownyourfeed.source.dto.NewSourceDTO;
import com.kovarpavel.ownyourfeed.source.dto.SourceDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sources")
public class SourceController {

    private final SourceService sourceService;

    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping
    public List<SourceDTO> getSources() {
        return sourceService.getSources();
    }

    @PostMapping
    public SourceDTO addSource(@RequestBody NewSourceDTO newSourceDTO) {
        return sourceService.addSource(newSourceDTO);
    }

    @DeleteMapping("/{id}")
    public Long removeSource(@PathVariable Long id) {
        return sourceService.removeSource(id);
    }
}
