package com.jpmt.powerlab.controllers;

import com.jpmt.powerlab.models.domain.Block;
import com.jpmt.powerlab.models.dto.exercise.ExerciseResponse;
import com.jpmt.powerlab.services.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/blocks")
@Tag(name = "Blocks", description = "APIs para gestionar bloques")
public class BlockController {

    private final BlockService blockService;

    public BlockController(BlockService blockService) {
        this.blockService = blockService;
    }

    @GetMapping()
    @Operation(summary = "List blocks", description = "Devuelve la lista completa de bloques")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExerciseResponse.class)))
    })
    public ResponseEntity<List<Block>> findAllBlocks() {
        return ResponseEntity.ok(this.blockService.findAll());
    }
}
