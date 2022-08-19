import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './raw-message.reducer';

export const RawMessageDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const rawMessageEntity = useAppSelector(state => state.trademanagement.rawMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rawMessageDetailsHeading">
          <Translate contentKey="tradeManagementApp.rawMessage.detail.title">RawMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.id}</dd>
          <dt>
            <span id="messageType">
              <Translate contentKey="tradeManagementApp.rawMessage.messageType">Message Type</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.messageType}</dd>
          <dt>
            <span id="executionType">
              <Translate contentKey="tradeManagementApp.rawMessage.executionType">Execution Type</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.executionType}</dd>
          <dt>
            <span id="kafkaKey">
              <Translate contentKey="tradeManagementApp.rawMessage.kafkaKey">Kafka Key</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.kafkaKey}</dd>
          <dt>
            <span id="companyId">
              <Translate contentKey="tradeManagementApp.rawMessage.companyId">Company Id</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.companyId}</dd>
          <dt>
            <span id="executionId">
              <Translate contentKey="tradeManagementApp.rawMessage.executionId">Execution Id</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.executionId}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="tradeManagementApp.rawMessage.message">Message</Translate>
            </span>
          </dt>
          <dd>{rawMessageEntity.message}</dd>
          <dt>
            <span id="creationTime">
              <Translate contentKey="tradeManagementApp.rawMessage.creationTime">Creation Time</Translate>
            </span>
          </dt>
          <dd>
            {rawMessageEntity.creationTime ? (
              <TextFormat value={rawMessageEntity.creationTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/raw-message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/raw-message/${rawMessageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RawMessageDetail;
