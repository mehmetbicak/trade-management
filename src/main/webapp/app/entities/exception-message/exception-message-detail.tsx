import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './exception-message.reducer';

export const ExceptionMessageDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const exceptionMessageEntity = useAppSelector(state => state.trademanagement.exceptionMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="exceptionMessageDetailsHeading">
          <Translate contentKey="tradeManagementApp.exceptionMessage.detail.title">ExceptionMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.id}</dd>
          <dt>
            <span id="kafkaKey">
              <Translate contentKey="tradeManagementApp.exceptionMessage.kafkaKey">Kafka Key</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.kafkaKey}</dd>
          <dt>
            <span id="exceptionCode">
              <Translate contentKey="tradeManagementApp.exceptionMessage.exceptionCode">Exception Code</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.exceptionCode}</dd>
          <dt>
            <span id="exceptionMessage">
              <Translate contentKey="tradeManagementApp.exceptionMessage.exceptionMessage">Exception Message</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.exceptionMessage}</dd>
          <dt>
            <span id="retryTopic">
              <Translate contentKey="tradeManagementApp.exceptionMessage.retryTopic">Retry Topic</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.retryTopic}</dd>
          <dt>
            <span id="rawMessage">
              <Translate contentKey="tradeManagementApp.exceptionMessage.rawMessage">Raw Message</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.rawMessage}</dd>
          <dt>
            <span id="retryCount">
              <Translate contentKey="tradeManagementApp.exceptionMessage.retryCount">Retry Count</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.retryCount}</dd>
          <dt>
            <span id="creationTime">
              <Translate contentKey="tradeManagementApp.exceptionMessage.creationTime">Creation Time</Translate>
            </span>
          </dt>
          <dd>
            {exceptionMessageEntity.creationTime ? (
              <TextFormat value={exceptionMessageEntity.creationTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastExecutionTime">
              <Translate contentKey="tradeManagementApp.exceptionMessage.lastExecutionTime">Last Execution Time</Translate>
            </span>
          </dt>
          <dd>
            {exceptionMessageEntity.lastExecutionTime ? (
              <TextFormat value={exceptionMessageEntity.lastExecutionTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="statusType">
              <Translate contentKey="tradeManagementApp.exceptionMessage.statusType">Status Type</Translate>
            </span>
          </dt>
          <dd>{exceptionMessageEntity.statusType}</dd>
        </dl>
        <Button tag={Link} to="/exception-message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/exception-message/${exceptionMessageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ExceptionMessageDetail;
