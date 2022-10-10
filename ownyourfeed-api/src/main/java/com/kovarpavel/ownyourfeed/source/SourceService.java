package com.kovarpavel.ownyourfeed.source;

import com.kovarpavel.ownyourfeed.source.dto.NewSourceDTO;
import com.kovarpavel.ownyourfeed.source.dto.SourceDTO;

import java.util.List;

public interface SourceService {
    List<SourceDTO> getSources();
    SourceDTO addSource(final NewSourceDTO newSourceDTO);
    Long removeSource(final Long id);
}
