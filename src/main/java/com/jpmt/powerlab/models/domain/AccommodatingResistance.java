package com.jpmt.powerlab.models.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum AccommodatingResistance {
    NONE, BANDS, CHAINS, REVERSE_BANDS, BANDS_AND_CHAINS
}
