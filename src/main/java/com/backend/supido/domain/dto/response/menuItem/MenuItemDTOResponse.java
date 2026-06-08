package com.backend.supido.domain.dto.response.menuItem;


import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record MenuItemDTOResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        String photoUrl,
        Boolean available
) {}