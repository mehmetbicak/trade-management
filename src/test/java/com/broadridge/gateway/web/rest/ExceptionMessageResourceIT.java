package com.broadridge.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.broadridge.gateway.IntegrationTest;
import com.broadridge.gateway.domain.ExceptionMessage;
import com.broadridge.gateway.domain.enumeration.StatusTypeEnum;
import com.broadridge.gateway.repository.EntityManager;
import com.broadridge.gateway.repository.ExceptionMessageRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ExceptionMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ExceptionMessageResourceIT {

    private static final String DEFAULT_KAFKA_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KAFKA_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_EXCEPTION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_EXCEPTION_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_EXCEPTION_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_EXCEPTION_MESSAGE = "BBBBBBBBBB";

    private static final String DEFAULT_RETRY_TOPIC = "AAAAAAAAAA";
    private static final String UPDATED_RETRY_TOPIC = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_RAW_MESSAGE = "BBBBBBBBBB";

    private static final Integer DEFAULT_RETRY_COUNT = 1;
    private static final Integer UPDATED_RETRY_COUNT = 2;

    private static final Instant DEFAULT_CREATION_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_EXECUTION_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_EXECUTION_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatusTypeEnum DEFAULT_STATUS_TYPE = StatusTypeEnum.NON_RETRYABLE;
    private static final StatusTypeEnum UPDATED_STATUS_TYPE = StatusTypeEnum.RETRYABLE;

    private static final String ENTITY_API_URL = "/api/exception-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExceptionMessageRepository exceptionMessageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ExceptionMessage exceptionMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExceptionMessage createEntity(EntityManager em) {
        ExceptionMessage exceptionMessage = new ExceptionMessage()
            .kafkaKey(DEFAULT_KAFKA_KEY)
            .exceptionCode(DEFAULT_EXCEPTION_CODE)
            .exceptionMessage(DEFAULT_EXCEPTION_MESSAGE)
            .retryTopic(DEFAULT_RETRY_TOPIC)
            .rawMessage(DEFAULT_RAW_MESSAGE)
            .retryCount(DEFAULT_RETRY_COUNT)
            .creationTime(DEFAULT_CREATION_TIME)
            .lastExecutionTime(DEFAULT_LAST_EXECUTION_TIME)
            .statusType(DEFAULT_STATUS_TYPE);
        return exceptionMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExceptionMessage createUpdatedEntity(EntityManager em) {
        ExceptionMessage exceptionMessage = new ExceptionMessage()
            .kafkaKey(UPDATED_KAFKA_KEY)
            .exceptionCode(UPDATED_EXCEPTION_CODE)
            .exceptionMessage(UPDATED_EXCEPTION_MESSAGE)
            .retryTopic(UPDATED_RETRY_TOPIC)
            .rawMessage(UPDATED_RAW_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .creationTime(UPDATED_CREATION_TIME)
            .lastExecutionTime(UPDATED_LAST_EXECUTION_TIME)
            .statusType(UPDATED_STATUS_TYPE);
        return exceptionMessage;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ExceptionMessage.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        exceptionMessage = createEntity(em);
    }

    @Test
    void createExceptionMessage() throws Exception {
        int databaseSizeBeforeCreate = exceptionMessageRepository.findAll().collectList().block().size();
        // Create the ExceptionMessage
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeCreate + 1);
        ExceptionMessage testExceptionMessage = exceptionMessageList.get(exceptionMessageList.size() - 1);
        assertThat(testExceptionMessage.getKafkaKey()).isEqualTo(DEFAULT_KAFKA_KEY);
        assertThat(testExceptionMessage.getExceptionCode()).isEqualTo(DEFAULT_EXCEPTION_CODE);
        assertThat(testExceptionMessage.getExceptionMessage()).isEqualTo(DEFAULT_EXCEPTION_MESSAGE);
        assertThat(testExceptionMessage.getRetryTopic()).isEqualTo(DEFAULT_RETRY_TOPIC);
        assertThat(testExceptionMessage.getRawMessage()).isEqualTo(DEFAULT_RAW_MESSAGE);
        assertThat(testExceptionMessage.getRetryCount()).isEqualTo(DEFAULT_RETRY_COUNT);
        assertThat(testExceptionMessage.getCreationTime()).isEqualTo(DEFAULT_CREATION_TIME);
        assertThat(testExceptionMessage.getLastExecutionTime()).isEqualTo(DEFAULT_LAST_EXECUTION_TIME);
        assertThat(testExceptionMessage.getStatusType()).isEqualTo(DEFAULT_STATUS_TYPE);
    }

    @Test
    void createExceptionMessageWithExistingId() throws Exception {
        // Create the ExceptionMessage with an existing ID
        exceptionMessage.setId(1L);

        int databaseSizeBeforeCreate = exceptionMessageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllExceptionMessagesAsStream() {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        List<ExceptionMessage> exceptionMessageList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ExceptionMessage.class)
            .getResponseBody()
            .filter(exceptionMessage::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(exceptionMessageList).isNotNull();
        assertThat(exceptionMessageList).hasSize(1);
        ExceptionMessage testExceptionMessage = exceptionMessageList.get(0);
        assertThat(testExceptionMessage.getKafkaKey()).isEqualTo(DEFAULT_KAFKA_KEY);
        assertThat(testExceptionMessage.getExceptionCode()).isEqualTo(DEFAULT_EXCEPTION_CODE);
        assertThat(testExceptionMessage.getExceptionMessage()).isEqualTo(DEFAULT_EXCEPTION_MESSAGE);
        assertThat(testExceptionMessage.getRetryTopic()).isEqualTo(DEFAULT_RETRY_TOPIC);
        assertThat(testExceptionMessage.getRawMessage()).isEqualTo(DEFAULT_RAW_MESSAGE);
        assertThat(testExceptionMessage.getRetryCount()).isEqualTo(DEFAULT_RETRY_COUNT);
        assertThat(testExceptionMessage.getCreationTime()).isEqualTo(DEFAULT_CREATION_TIME);
        assertThat(testExceptionMessage.getLastExecutionTime()).isEqualTo(DEFAULT_LAST_EXECUTION_TIME);
        assertThat(testExceptionMessage.getStatusType()).isEqualTo(DEFAULT_STATUS_TYPE);
    }

    @Test
    void getAllExceptionMessages() {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        // Get all the exceptionMessageList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(exceptionMessage.getId().intValue()))
            .jsonPath("$.[*].kafkaKey")
            .value(hasItem(DEFAULT_KAFKA_KEY))
            .jsonPath("$.[*].exceptionCode")
            .value(hasItem(DEFAULT_EXCEPTION_CODE))
            .jsonPath("$.[*].exceptionMessage")
            .value(hasItem(DEFAULT_EXCEPTION_MESSAGE))
            .jsonPath("$.[*].retryTopic")
            .value(hasItem(DEFAULT_RETRY_TOPIC))
            .jsonPath("$.[*].rawMessage")
            .value(hasItem(DEFAULT_RAW_MESSAGE.toString()))
            .jsonPath("$.[*].retryCount")
            .value(hasItem(DEFAULT_RETRY_COUNT))
            .jsonPath("$.[*].creationTime")
            .value(hasItem(DEFAULT_CREATION_TIME.toString()))
            .jsonPath("$.[*].lastExecutionTime")
            .value(hasItem(DEFAULT_LAST_EXECUTION_TIME.toString()))
            .jsonPath("$.[*].statusType")
            .value(hasItem(DEFAULT_STATUS_TYPE.toString()));
    }

    @Test
    void getExceptionMessage() {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        // Get the exceptionMessage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, exceptionMessage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(exceptionMessage.getId().intValue()))
            .jsonPath("$.kafkaKey")
            .value(is(DEFAULT_KAFKA_KEY))
            .jsonPath("$.exceptionCode")
            .value(is(DEFAULT_EXCEPTION_CODE))
            .jsonPath("$.exceptionMessage")
            .value(is(DEFAULT_EXCEPTION_MESSAGE))
            .jsonPath("$.retryTopic")
            .value(is(DEFAULT_RETRY_TOPIC))
            .jsonPath("$.rawMessage")
            .value(is(DEFAULT_RAW_MESSAGE.toString()))
            .jsonPath("$.retryCount")
            .value(is(DEFAULT_RETRY_COUNT))
            .jsonPath("$.creationTime")
            .value(is(DEFAULT_CREATION_TIME.toString()))
            .jsonPath("$.lastExecutionTime")
            .value(is(DEFAULT_LAST_EXECUTION_TIME.toString()))
            .jsonPath("$.statusType")
            .value(is(DEFAULT_STATUS_TYPE.toString()));
    }

    @Test
    void getNonExistingExceptionMessage() {
        // Get the exceptionMessage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewExceptionMessage() throws Exception {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();

        // Update the exceptionMessage
        ExceptionMessage updatedExceptionMessage = exceptionMessageRepository.findById(exceptionMessage.getId()).block();
        updatedExceptionMessage
            .kafkaKey(UPDATED_KAFKA_KEY)
            .exceptionCode(UPDATED_EXCEPTION_CODE)
            .exceptionMessage(UPDATED_EXCEPTION_MESSAGE)
            .retryTopic(UPDATED_RETRY_TOPIC)
            .rawMessage(UPDATED_RAW_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .creationTime(UPDATED_CREATION_TIME)
            .lastExecutionTime(UPDATED_LAST_EXECUTION_TIME)
            .statusType(UPDATED_STATUS_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedExceptionMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedExceptionMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
        ExceptionMessage testExceptionMessage = exceptionMessageList.get(exceptionMessageList.size() - 1);
        assertThat(testExceptionMessage.getKafkaKey()).isEqualTo(UPDATED_KAFKA_KEY);
        assertThat(testExceptionMessage.getExceptionCode()).isEqualTo(UPDATED_EXCEPTION_CODE);
        assertThat(testExceptionMessage.getExceptionMessage()).isEqualTo(UPDATED_EXCEPTION_MESSAGE);
        assertThat(testExceptionMessage.getRetryTopic()).isEqualTo(UPDATED_RETRY_TOPIC);
        assertThat(testExceptionMessage.getRawMessage()).isEqualTo(UPDATED_RAW_MESSAGE);
        assertThat(testExceptionMessage.getRetryCount()).isEqualTo(UPDATED_RETRY_COUNT);
        assertThat(testExceptionMessage.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
        assertThat(testExceptionMessage.getLastExecutionTime()).isEqualTo(UPDATED_LAST_EXECUTION_TIME);
        assertThat(testExceptionMessage.getStatusType()).isEqualTo(UPDATED_STATUS_TYPE);
    }

    @Test
    void putNonExistingExceptionMessage() throws Exception {
        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();
        exceptionMessage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, exceptionMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExceptionMessage() throws Exception {
        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();
        exceptionMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExceptionMessage() throws Exception {
        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();
        exceptionMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExceptionMessageWithPatch() throws Exception {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();

        // Update the exceptionMessage using partial update
        ExceptionMessage partialUpdatedExceptionMessage = new ExceptionMessage();
        partialUpdatedExceptionMessage.setId(exceptionMessage.getId());

        partialUpdatedExceptionMessage
            .exceptionCode(UPDATED_EXCEPTION_CODE)
            .exceptionMessage(UPDATED_EXCEPTION_MESSAGE)
            .retryTopic(UPDATED_RETRY_TOPIC)
            .retryCount(UPDATED_RETRY_COUNT)
            .creationTime(UPDATED_CREATION_TIME)
            .lastExecutionTime(UPDATED_LAST_EXECUTION_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExceptionMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExceptionMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
        ExceptionMessage testExceptionMessage = exceptionMessageList.get(exceptionMessageList.size() - 1);
        assertThat(testExceptionMessage.getKafkaKey()).isEqualTo(DEFAULT_KAFKA_KEY);
        assertThat(testExceptionMessage.getExceptionCode()).isEqualTo(UPDATED_EXCEPTION_CODE);
        assertThat(testExceptionMessage.getExceptionMessage()).isEqualTo(UPDATED_EXCEPTION_MESSAGE);
        assertThat(testExceptionMessage.getRetryTopic()).isEqualTo(UPDATED_RETRY_TOPIC);
        assertThat(testExceptionMessage.getRawMessage()).isEqualTo(DEFAULT_RAW_MESSAGE);
        assertThat(testExceptionMessage.getRetryCount()).isEqualTo(UPDATED_RETRY_COUNT);
        assertThat(testExceptionMessage.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
        assertThat(testExceptionMessage.getLastExecutionTime()).isEqualTo(UPDATED_LAST_EXECUTION_TIME);
        assertThat(testExceptionMessage.getStatusType()).isEqualTo(DEFAULT_STATUS_TYPE);
    }

    @Test
    void fullUpdateExceptionMessageWithPatch() throws Exception {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();

        // Update the exceptionMessage using partial update
        ExceptionMessage partialUpdatedExceptionMessage = new ExceptionMessage();
        partialUpdatedExceptionMessage.setId(exceptionMessage.getId());

        partialUpdatedExceptionMessage
            .kafkaKey(UPDATED_KAFKA_KEY)
            .exceptionCode(UPDATED_EXCEPTION_CODE)
            .exceptionMessage(UPDATED_EXCEPTION_MESSAGE)
            .retryTopic(UPDATED_RETRY_TOPIC)
            .rawMessage(UPDATED_RAW_MESSAGE)
            .retryCount(UPDATED_RETRY_COUNT)
            .creationTime(UPDATED_CREATION_TIME)
            .lastExecutionTime(UPDATED_LAST_EXECUTION_TIME)
            .statusType(UPDATED_STATUS_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExceptionMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExceptionMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
        ExceptionMessage testExceptionMessage = exceptionMessageList.get(exceptionMessageList.size() - 1);
        assertThat(testExceptionMessage.getKafkaKey()).isEqualTo(UPDATED_KAFKA_KEY);
        assertThat(testExceptionMessage.getExceptionCode()).isEqualTo(UPDATED_EXCEPTION_CODE);
        assertThat(testExceptionMessage.getExceptionMessage()).isEqualTo(UPDATED_EXCEPTION_MESSAGE);
        assertThat(testExceptionMessage.getRetryTopic()).isEqualTo(UPDATED_RETRY_TOPIC);
        assertThat(testExceptionMessage.getRawMessage()).isEqualTo(UPDATED_RAW_MESSAGE);
        assertThat(testExceptionMessage.getRetryCount()).isEqualTo(UPDATED_RETRY_COUNT);
        assertThat(testExceptionMessage.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
        assertThat(testExceptionMessage.getLastExecutionTime()).isEqualTo(UPDATED_LAST_EXECUTION_TIME);
        assertThat(testExceptionMessage.getStatusType()).isEqualTo(UPDATED_STATUS_TYPE);
    }

    @Test
    void patchNonExistingExceptionMessage() throws Exception {
        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();
        exceptionMessage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, exceptionMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExceptionMessage() throws Exception {
        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();
        exceptionMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExceptionMessage() throws Exception {
        int databaseSizeBeforeUpdate = exceptionMessageRepository.findAll().collectList().block().size();
        exceptionMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(exceptionMessage))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ExceptionMessage in the database
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExceptionMessage() {
        // Initialize the database
        exceptionMessageRepository.save(exceptionMessage).block();

        int databaseSizeBeforeDelete = exceptionMessageRepository.findAll().collectList().block().size();

        // Delete the exceptionMessage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, exceptionMessage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ExceptionMessage> exceptionMessageList = exceptionMessageRepository.findAll().collectList().block();
        assertThat(exceptionMessageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
