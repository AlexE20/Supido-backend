package com.backend.supido.menuItem.domain.dto.response;


import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record MenuItemDTOResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String photoUrl,
        Boolean available
) {}