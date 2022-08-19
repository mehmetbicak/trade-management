package com.broadridge.gateway.web.rest;

import com.broadridge.gateway.domain.ExceptionMessage;
import com.broadridge.gateway.repository.ExceptionMessageRepository;
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
 * REST controller for managing {@link com.broadridge.gateway.domain.ExceptionMessage}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ExceptionMessageResource {

    private final Logger log = LoggerFactory.getLogger(ExceptionMessageResource.class);

    private static final String ENTITY_NAME = "exceptionMessage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExceptionMessageRepository exceptionMessageRepository;

    public ExceptionMessageResource(ExceptionMessageRepository exceptionMessageRepository) {
        this.exceptionMessageRepository = exceptionMessageRepository;
    }

    /**
     * {@code POST  /exception-messages} : Create a new exceptionMessage.
     *
     * @param exceptionMessage the exceptionMessage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exceptionMessage, or with status {@code 400 (Bad Request)} if the exceptionMessage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/exception-messages")
    public Mono<ResponseEntity<ExceptionMessage>> createExceptionMessage(@RequestBody ExceptionMessage exceptionMessage)
        throws URISyntaxException {
        log.debug("REST request to save ExceptionMessage : {}", exceptionMessage);
        if (exceptionMessage.getId() != null) {
            throw new BadRequestAlertException("A new exceptionMessage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return exceptionMessageRepository
            .save(exceptionMessage)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/exception-messages/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /exception-messages/:id} : Updates an existing exceptionMessage.
     *
     * @param id the id of the exceptionMessage to save.
     * @param exceptionMessage the exceptionMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exceptionMessage,
     * or with status {@code 400 (Bad Request)} if the exceptionMessage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exceptionMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/exception-messages/{id}")
    public Mono<ResponseEntity<ExceptionMessage>> updateExceptionMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExceptionMessage exceptionMessage
    ) throws URISyntaxException {
        log.debug("REST request to update ExceptionMessage : {}, {}", id, exceptionMessage);
        if (exceptionMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exceptionMessage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return exceptionMessageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return exceptionMessageRepository
                    .save(exceptionMessage)
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
     * {@code PATCH  /exception-messages/:id} : Partial updates given fields of an existing exceptionMessage, field will ignore if it is null
     *
     * @param id the id of the exceptionMessage to save.
     * @param exceptionMessage the exceptionMessage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exceptionMessage,
     * or with status {@code 400 (Bad Request)} if the exceptionMessage is not valid,
     * or with status {@code 404 (Not Found)} if the exceptionMessage is not found,
     * or with status {@code 500 (Internal Server Error)} if the exceptionMessage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/exception-messages/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ExceptionMessage>> partialUpdateExceptionMessage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExceptionMessage exceptionMessage
    ) throws URISyntaxException {
        log.debug("REST request to partial update ExceptionMessage partially : {}, {}", id, exceptionMessage);
        if (exceptionMessage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exceptionMessage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return exceptionMessageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ExceptionMessage> result = exceptionMessageRepository
                    .findById(exceptionMessage.getId())
                    .map(existingExceptionMessage -> {
                        if (exceptionMessage.getKafkaKey() != null) {
                            existingExceptionMessage.setKafkaKey(exceptionMessage.getKafkaKey());
                        }
                        if (exceptionMessage.getExceptionCode() != null) {
                            existingExceptionMessage.setExceptionCode(exceptionMessage.getExceptionCode());
                        }
                        if (exceptionMessage.getExceptionMessage() != null) {
                            existingExceptionMessage.setExceptionMessage(exceptionMessage.getExceptionMessage());
                        }
                        if (exceptionMessage.getRetryTopic() != null) {
                            existingExceptionMessage.setRetryTopic(exceptionMessage.getRetryTopic());
                        }
                        if (exceptionMessage.getRawMessage() != null) {
                            existingExceptionMessage.setRawMessage(exceptionMessage.getRawMessage());
                        }
                        if (exceptionMessage.getRetryCount() != null) {
                            existingExceptionMessage.setRetryCount(exceptionMessage.getRetryCount());
                        }
                        if (exceptionMessage.getCreationTime() != null) {
                            existingExceptionMessage.setCreationTime(exceptionMessage.getCreationTime());
                        }
                        if (exceptionMessage.getLastExecutionTime() != null) {
                            existingExceptionMessage.setLastExecutionTime(exceptionMessage.getLastExecutionTime());
                        }
                        if (exceptionMessage.getStatusType() != null) {
                            existingExceptionMessage.setStatusType(exceptionMessage.getStatusType());
                        }

                        return existingExceptionMessage;
                    })
                    .flatMap(exceptionMessageRepository::save);

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
     * {@code GET  /exception-messages} : get all the exceptionMessages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exceptionMessages in body.
     */
    @GetMapping("/exception-messages")
    public Mono<List<ExceptionMessage>> getAllExceptionMessages() {
        log.debug("REST request to get all ExceptionMessages");
        return exceptionMessageRepository.findAll().collectList();
    }

    /**
     * {@code GET  /exception-messages} : get all the exceptionMessages as a stream.
     * @return the {@link Flux} of exceptionMessages.
     */
    @GetMapping(value = "/exception-messages", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ExceptionMessage> getAllExceptionMessagesAsStream() {
        log.debug("REST request to get all ExceptionMessages as a stream");
        return exceptionMessageRepository.findAll();
    }

    /**
     * {@code GET  /exception-messages/:id} : get the "id" exceptionMessage.
     *
     * @param id the id of the exceptionMessage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exceptionMessage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/exception-messages/{id}")
    public Mono<ResponseEntity<ExceptionMessage>> getExceptionMessage(@PathVariable Long id) {
        log.debug("REST request to get ExceptionMessage : {}", id);
        Mono<ExceptionMessage> exceptionMessage = exceptionMessageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(exceptionMessage);
    }

    /**
     * {@code DELETE  /exception-messages/:id} : delete the "id" exceptionMessage.
     *
     * @param id the id of the exceptionMessage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/exception-messages/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteExceptionMessage(@PathVariable Long id) {
        log.debug("REST request to delete ExceptionMessage : {}", id);
        return exceptionMessageRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
