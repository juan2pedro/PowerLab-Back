package com.jpmt.powerlab.controllers;

import com.jpmt.powerlab.models.dto.ApiErrorResponse;
import com.jpmt.powerlab.models.dto.block.BlockRequest;
import com.jpmt.powerlab.models.dto.block.BlockResponse;
import com.jpmt.powerlab.services.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
                            schema = @Schema(implementation = BlockResponse.class)))
    })
    public ResponseEntity<List<BlockResponse>> findAllBlocks() {
        return ResponseEntity.ok(this.blockService.findAll());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get block by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlockResponse.class))),
            @ApiResponse(responseCode = "404", description = "Block not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<BlockResponse> findById(@Parameter(description = "ID del bloque") @PathVariable Long id) {
        return ResponseEntity.ok(this.blockService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create Block", description = "Crea un nuevo bloque",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Block payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlockRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Block created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlockResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<BlockResponse> save(@Valid @RequestBody BlockRequest block) {
        BlockResponse savedBlock = this.blockService.save(block);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedBlock.id())
                .toUri();
        return ResponseEntity.created(location).body(savedBlock);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update block",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Block payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlockRequest.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlockResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Block not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<BlockResponse> update(@Parameter(description = "ID del block") @PathVariable Long id,
                                                   @Valid @RequestBody BlockRequest block) {
        return ResponseEntity.ok(this.blockService.update(id, block));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Block deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Block not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID del block") @PathVariable Long id) {
        this.blockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
