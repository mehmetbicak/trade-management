package com.broadridge.gateway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.broadridge.gateway.IntegrationTest;
import com.broadridge.gateway.domain.RawMessage;
import com.broadridge.gateway.domain.enumeration.ExecutionTypeEnum;
import com.broadridge.gateway.domain.enumeration.MessageTypeEnum;
import com.broadridge.gateway.repository.EntityManager;
import com.broadridge.gateway.repository.RawMessageRepository;
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
 * Integration tests for the {@link RawMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RawMessageResourceIT {

    private static final MessageTypeEnum DEFAULT_MESSAGE_TYPE = MessageTypeEnum.FIX;
    private static final MessageTypeEnum UPDATED_MESSAGE_TYPE = MessageTypeEnum.SWIFT;

    private static final ExecutionTypeEnum DEFAULT_EXECUTION_TYPE = ExecutionTypeEnum.ADD;
    private static final ExecutionTypeEnum UPDATED_EXECUTION_TYPE = ExecutionTypeEnum.AMEND;

    private static final String DEFAULT_KAFKA_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KAFKA_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY_ID = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EXECUTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXECUTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATION_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/raw-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RawMessageRepository rawMessageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RawMessage rawMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMessage createEntity(EntityManager em) {
        RawMessage rawMessage = new RawMessage()
            .messageType(DEFAULT_MESSAGE_TYPE)
            .executionType(DEFAULT_EXECUTION_TYPE)
            .kafkaKey(DEFAULT_KAFKA_KEY)
            .companyId(DEFAULT_COMPANY_ID)
            .executionId(DEFAULT_EXECUTION_ID)
            .message(DEFAULT_MESSAGE)
            .creationTime(DEFAULT_CREATION_TIME);
        return rawMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMessage createUpdatedEntity(EntityManager em) {
        RawMessage rawMessage = new RawMessage()
            .messageType(UPDATED_MESSAGE_TYPE)
            .executionType(UPDATED_EXECUTION_TYPE)
            .kafkaKey(UPDATED_KAFKA_KEY)
            .companyId(UPDATED_COMPANY_ID)
            .executionId(UPDATED_EXECUTION_ID)
            .message(UPDATED_MESSAGE)
            .creationTime(UPDATED_CREATION_TIME);
        return rawMessage;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RawMessage.class).block();
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
        rawMessage = createEntity(em);
    }

    @Test
    void createRawMessage() throws Exception {
        int databaseSizeBeforeCreate = rawMessageRepository.findAll().collectList().block().size();
        // Create the RawMessage
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeCreate + 1);
        RawMessage testRawMessage = rawMessageList.get(rawMessageList.size() - 1);
        assertThat(testRawMessage.getMessageType()).isEqualTo(DEFAULT_MESSAGE_TYPE);
        assertThat(testRawMessage.getExecutionType()).isEqualTo(DEFAULT_EXECUTION_TYPE);
        assertThat(testRawMessage.getKafkaKey()).isEqualTo(DEFAULT_KAFKA_KEY);
        assertThat(testRawMessage.getCompanyId()).isEqualTo(DEFAULT_COMPANY_ID);
        assertThat(testRawMessage.getExecutionId()).isEqualTo(DEFAULT_EXECUTION_ID);
        assertThat(testRawMessage.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testRawMessage.getCreationTime()).isEqualTo(DEFAULT_CREATION_TIME);
    }

    @Test
    void createRawMessageWithExistingId() throws Exception {
        // Create the RawMessage with an existing ID
        rawMessage.setId(1L);

        int databaseSizeBeforeCreate = rawMessageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRawMessagesAsStream() {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        List<RawMessage> rawMessageList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RawMessage.class)
            .getResponseBody()
            .filter(rawMessage::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(rawMessageList).isNotNull();
        assertThat(rawMessageList).hasSize(1);
        RawMessage testRawMessage = rawMessageList.get(0);
        assertThat(testRawMessage.getMessageType()).isEqualTo(DEFAULT_MESSAGE_TYPE);
        assertThat(testRawMessage.getExecutionType()).isEqualTo(DEFAULT_EXECUTION_TYPE);
        assertThat(testRawMessage.getKafkaKey()).isEqualTo(DEFAULT_KAFKA_KEY);
        assertThat(testRawMessage.getCompanyId()).isEqualTo(DEFAULT_COMPANY_ID);
        assertThat(testRawMessage.getExecutionId()).isEqualTo(DEFAULT_EXECUTION_ID);
        assertThat(testRawMessage.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testRawMessage.getCreationTime()).isEqualTo(DEFAULT_CREATION_TIME);
    }

    @Test
    void getAllRawMessages() {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        // Get all the rawMessageList
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
            .value(hasItem(rawMessage.getId().intValue()))
            .jsonPath("$.[*].messageType")
            .value(hasItem(DEFAULT_MESSAGE_TYPE.toString()))
            .jsonPath("$.[*].executionType")
            .value(hasItem(DEFAULT_EXECUTION_TYPE.toString()))
            .jsonPath("$.[*].kafkaKey")
            .value(hasItem(DEFAULT_KAFKA_KEY))
            .jsonPath("$.[*].companyId")
            .value(hasItem(DEFAULT_COMPANY_ID))
            .jsonPath("$.[*].executionId")
            .value(hasItem(DEFAULT_EXECUTION_ID))
            .jsonPath("$.[*].message")
            .value(hasItem(DEFAULT_MESSAGE.toString()))
            .jsonPath("$.[*].creationTime")
            .value(hasItem(DEFAULT_CREATION_TIME.toString()));
    }

    @Test
    void getRawMessage() {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        // Get the rawMessage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rawMessage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rawMessage.getId().intValue()))
            .jsonPath("$.messageType")
            .value(is(DEFAULT_MESSAGE_TYPE.toString()))
            .jsonPath("$.executionType")
            .value(is(DEFAULT_EXECUTION_TYPE.toString()))
            .jsonPath("$.kafkaKey")
            .value(is(DEFAULT_KAFKA_KEY))
            .jsonPath("$.companyId")
            .value(is(DEFAULT_COMPANY_ID))
            .jsonPath("$.executionId")
            .value(is(DEFAULT_EXECUTION_ID))
            .jsonPath("$.message")
            .value(is(DEFAULT_MESSAGE.toString()))
            .jsonPath("$.creationTime")
            .value(is(DEFAULT_CREATION_TIME.toString()));
    }

    @Test
    void getNonExistingRawMessage() {
        // Get the rawMessage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRawMessage() throws Exception {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();

        // Update the rawMessage
        RawMessage updatedRawMessage = rawMessageRepository.findById(rawMessage.getId()).block();
        updatedRawMessage
            .messageType(UPDATED_MESSAGE_TYPE)
            .executionType(UPDATED_EXECUTION_TYPE)
            .kafkaKey(UPDATED_KAFKA_KEY)
            .companyId(UPDATED_COMPANY_ID)
            .executionId(UPDATED_EXECUTION_ID)
            .message(UPDATED_MESSAGE)
            .creationTime(UPDATED_CREATION_TIME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRawMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRawMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
        RawMessage testRawMessage = rawMessageList.get(rawMessageList.size() - 1);
        assertThat(testRawMessage.getMessageType()).isEqualTo(UPDATED_MESSAGE_TYPE);
        assertThat(testRawMessage.getExecutionType()).isEqualTo(UPDATED_EXECUTION_TYPE);
        assertThat(testRawMessage.getKafkaKey()).isEqualTo(UPDATED_KAFKA_KEY);
        assertThat(testRawMessage.getCompanyId()).isEqualTo(UPDATED_COMPANY_ID);
        assertThat(testRawMessage.getExecutionId()).isEqualTo(UPDATED_EXECUTION_ID);
        assertThat(testRawMessage.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testRawMessage.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
    }

    @Test
    void putNonExistingRawMessage() throws Exception {
        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();
        rawMessage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rawMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRawMessage() throws Exception {
        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();
        rawMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRawMessage() throws Exception {
        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();
        rawMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRawMessageWithPatch() throws Exception {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();

        // Update the rawMessage using partial update
        RawMessage partialUpdatedRawMessage = new RawMessage();
        partialUpdatedRawMessage.setId(rawMessage.getId());

        partialUpdatedRawMessage
            .messageType(UPDATED_MESSAGE_TYPE)
            .kafkaKey(UPDATED_KAFKA_KEY)
            .companyId(UPDATED_COMPANY_ID)
            .creationTime(UPDATED_CREATION_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRawMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRawMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
        RawMessage testRawMessage = rawMessageList.get(rawMessageList.size() - 1);
        assertThat(testRawMessage.getMessageType()).isEqualTo(UPDATED_MESSAGE_TYPE);
        assertThat(testRawMessage.getExecutionType()).isEqualTo(DEFAULT_EXECUTION_TYPE);
        assertThat(testRawMessage.getKafkaKey()).isEqualTo(UPDATED_KAFKA_KEY);
        assertThat(testRawMessage.getCompanyId()).isEqualTo(UPDATED_COMPANY_ID);
        assertThat(testRawMessage.getExecutionId()).isEqualTo(DEFAULT_EXECUTION_ID);
        assertThat(testRawMessage.getMessage()).isEqualTo(DEFAULT_MESSAGE);
        assertThat(testRawMessage.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
    }

    @Test
    void fullUpdateRawMessageWithPatch() throws Exception {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();

        // Update the rawMessage using partial update
        RawMessage partialUpdatedRawMessage = new RawMessage();
        partialUpdatedRawMessage.setId(rawMessage.getId());

        partialUpdatedRawMessage
            .messageType(UPDATED_MESSAGE_TYPE)
            .executionType(UPDATED_EXECUTION_TYPE)
            .kafkaKey(UPDATED_KAFKA_KEY)
            .companyId(UPDATED_COMPANY_ID)
            .executionId(UPDATED_EXECUTION_ID)
            .message(UPDATED_MESSAGE)
            .creationTime(UPDATED_CREATION_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRawMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRawMessage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
        RawMessage testRawMessage = rawMessageList.get(rawMessageList.size() - 1);
        assertThat(testRawMessage.getMessageType()).isEqualTo(UPDATED_MESSAGE_TYPE);
        assertThat(testRawMessage.getExecutionType()).isEqualTo(UPDATED_EXECUTION_TYPE);
        assertThat(testRawMessage.getKafkaKey()).isEqualTo(UPDATED_KAFKA_KEY);
        assertThat(testRawMessage.getCompanyId()).isEqualTo(UPDATED_COMPANY_ID);
        assertThat(testRawMessage.getExecutionId()).isEqualTo(UPDATED_EXECUTION_ID);
        assertThat(testRawMessage.getMessage()).isEqualTo(UPDATED_MESSAGE);
        assertThat(testRawMessage.getCreationTime()).isEqualTo(UPDATED_CREATION_TIME);
    }

    @Test
    void patchNonExistingRawMessage() throws Exception {
        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();
        rawMessage.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rawMessage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRawMessage() throws Exception {
        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();
        rawMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRawMessage() throws Exception {
        int databaseSizeBeforeUpdate = rawMessageRepository.findAll().collectList().block().size();
        rawMessage.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rawMessage))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RawMessage in the database
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRawMessage() {
        // Initialize the database
        rawMessageRepository.save(rawMessage).block();

        int databaseSizeBeforeDelete = rawMessageRepository.findAll().collectList().block().size();

        // Delete the rawMessage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rawMessage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RawMessage> rawMessageList = rawMessageRepository.findAll().collectList().block();
        assertThat(rawMessageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
