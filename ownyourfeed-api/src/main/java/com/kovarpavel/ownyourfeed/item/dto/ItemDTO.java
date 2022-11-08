package com.kovarpavel.ownyourfeed.item.dto;

import java.time.LocalDateTime;

public record ItemDTO(String title, String link, String description, String author, LocalDateTime date, String guid) {
    
}
