package com.broadridge.gateway.web.rest;

import com.broadridge.gateway.domain.RawMessage;
import com.broadridge.gateway.repository.RawMessageRepository;
import com.broadridge.gateway.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.broadridge.gateway.domain.RawMessage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RawMessageResource {

    private final Logger log = LoggerFactory.getLogger(RawMessageResource.class);

    private static final String ENTITY_NAME = "rawMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RawMessageRepository rawMessageRepository;

    public RawMessageResource(RawMessageRepository rawMessageRepository) {
        this.rawMessageRepository = rawMessageRepository;
    }

    /**
     * {@code POST  /raw-messages} : Create a new rawMessage.
     *
     * @param rawMessage the rawMessage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rawMessage, or with status {@code 400 (Bad Request)} if the rawMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/raw-messages")
    public Mono<ResponseEntity<RawMessage>> createRawMessage(@RequestBody RawMessage rawMessage) throws URISyntaxException {
        log.debug("REST request to save RawMessage : {}", rawMessage);
        if (rawMessage.getId() != null) {
            throw new BadRequestAlertException("A new rawMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rawMessageRepository
            .save(rawMessage)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/raw-messages/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /raw-messages/:id} : Updates an existing rawMessage.
     *
     * @param id the id of the rawMessage to save.
     * @param rawMessage the rawMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMessage,
     * or with status {@code 400 (Bad Request)} if the rawMessage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rawMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/raw-messages/{id}")
    public Mono<ResponseEntity<RawMessage>> updateRawMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RawMessage rawMessage
    ) throws URISyntaxException {
        log.debug("REST request to update RawMessage : {}, {}", id, rawMessage);
        if (rawMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMessage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rawMessageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return rawMessageRepository
                    .save(rawMessage)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /raw-messages/:id} : Partial updates given fields of an existing rawMessage, field will ignore if it is null
     *
     * @param id the id of the rawMessage to save.
     * @param rawMessage the rawMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMessage,
     * or with status {@code 400 (Bad Request)} if the rawMessage is not valid,
     * or with status {@code 404 (Not Found)} if the rawMessage is not found,
     * or with status {@code 500 (Internal Server Error)} if the rawMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/raw-messages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RawMessage>> partialUpdateRawMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RawMessage rawMessage
    ) throws URISyntaxException {
        log.debug("REST request to partial update RawMessage partially : {}, {}", id, rawMessage);
        if (rawMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMessage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rawMessageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RawMessage> result = rawMessageRepository
                    .findById(rawMessage.getId())
                    .map(existingRawMessage -> {
                        if (rawMessage.getMessageType() != null) {
                            existingRawMessage.setMessageType(rawMessage.getMessageType());
                        }
                        if (rawMessage.getExecutionType() != null) {
                            existingRawMessage.setExecutionType(rawMessage.getExecutionType());
                        }
                        if (rawMessage.getKafkaKey() != null) {
                            existingRawMessage.setKafkaKey(rawMessage.getKafkaKey());
                        }
                        if (rawMessage.getCompanyId() != null) {
                            existingRawMessage.setCompanyId(rawMessage.getCompanyId());
                        }
                        if (rawMessage.getExecutionId() != null) {
                            existingRawMessage.setExecutionId(rawMessage.getExecutionId());
                        }
                        if (rawMessage.getMessage() != null) {
                            existingRawMessage.setMessage(rawMessage.getMessage());
                        }
                        if (rawMessage.getCreationTime() != null) {
                            existingRawMessage.setCreationTime(rawMessage.getCreationTime());
                        }

                        return existingRawMessage;
                    })
                    .flatMap(rawMessageRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /raw-messages} : get all the rawMessages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rawMessages in body.
     */
    @GetMapping("/raw-messages")
    public Mono<List<RawMessage>> getAllRawMessages() {
        log.debug("REST request to get all RawMessages");
        return rawMessageRepository.findAll().collectList();
    }

    /**
     * {@code GET  /raw-messages} : get all the rawMessages as a stream.
     * @return the {@link Flux} of rawMessages.
     */
    @GetMapping(value = "/raw-messages", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RawMessage> getAllRawMessagesAsStream() {
        log.debug("REST request to get all RawMessages as a stream");
        return rawMessageRepository.findAll();
    }

    /**
     * {@code GET  /raw-messages/:id} : get the "id" rawMessage.
     *
     * @param id the id of the rawMessage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rawMessage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/raw-messages/{id}")
    public Mono<ResponseEntity<RawMessage>> getRawMessage(@PathVariable Long id) {
        log.debug("REST request to get RawMessage : {}", id);
        Mono<RawMessage> rawMessage = rawMessageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(rawMessage);
    }

    /**
     * {@code DELETE  /raw-messages/:id} : delete the "id" rawMessage.
     *
     * @param id the id of the rawMessage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/raw-messages/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRawMessage(@PathVariable Long id) {
        log.debug("REST request to delete RawMessage : {}", id);
        return rawMessageRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
