package com.broadridge.gateway.repository;

import com.broadridge.gateway.domain.RawMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RawMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RawMessageRepository extends ReactiveCrudRepository<RawMessage, Long>, RawMessageRepositoryInternal {
    @Override
    <S extends RawMessage> Mono<S> save(S entity);

    @Override
    Flux<RawMessage> findAll();

    @Override
    Mono<RawMessage> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RawMessageRepositoryInternal {
    <S extends RawMessage> Mono<S> save(S entity);

    Flux<RawMessage> findAllBy(Pageable pageable);

    Flux<RawMessage> findAll();

    Mono<RawMessage> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RawMessage> findAllBy(Pageable pageable, Criteria criteria);

}
