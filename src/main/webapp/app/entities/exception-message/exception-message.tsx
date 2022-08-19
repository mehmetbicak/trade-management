import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { byteSize, Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IExceptionMessage } from 'app/shared/model/exception-message.model';
import { getEntities } from './exception-message.reducer';

export const ExceptionMessage = (props: RouteComponentProps<{ url: string }>) => {
  const dispatch = useAppDispatch();

  const exceptionMessageList = useAppSelector(state => state.trademanagement.exceptionMessage.entities);
  const loading = useAppSelector(state => state.trademanagement.exceptionMessage.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  const { match } = props;

  return (
    <div>
      <h2 id="exception-message-heading" data-cy="ExceptionMessageHeading">
        <Translate contentKey="tradeManagementApp.exceptionMessage.home.title">Exception Messages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="tradeManagementApp.exceptionMessage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/exception-message/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="tradeManagementApp.exceptionMessage.home.createLabel">Create new Exception Message</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {exceptionMessageList && exceptionMessageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.kafkaKey">Kafka Key</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.exceptionCode">Exception Code</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.exceptionMessage">Exception Message</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.retryTopic">Retry Topic</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.rawMessage">Raw Message</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.retryCount">Retry Count</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.creationTime">Creation Time</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.lastExecutionTime">Last Execution Time</Translate>
                </th>
                <th>
                  <Translate contentKey="tradeManagementApp.exceptionMessage.statusType">Status Type</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {exceptionMessageList.map((exceptionMessage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/exception-message/${exceptionMessage.id}`} color="link" size="sm">
                      {exceptionMessage.id}
                    </Button>
                  </td>
                  <td>{exceptionMessage.kafkaKey}</td>
                  <td>{exceptionMessage.exceptionCode}</td>
                  <td>{exceptionMessage.exceptionMessage}</td>
                  <td>{exceptionMessage.retryTopic}</td>
                  <td>{exceptionMessage.rawMessage}</td>
                  <td>{exceptionMessage.retryCount}</td>
                  <td>
                    {exceptionMessage.creationTime ? (
                      <TextFormat type="date" value={exceptionMessage.creationTime} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {exceptionMessage.lastExecutionTime ? (
                      <TextFormat type="date" value={exceptionMessage.lastExecutionTime} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    <Translate contentKey={`tradeManagementApp.StatusTypeEnum.${exceptionMessage.statusType}`} />
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/exception-message/${exceptionMessage.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/exception-message/${exceptionMessage.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/exception-message/${exceptionMessage.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="tradeManagementApp.exceptionMessage.home.notFound">No Exception Messages found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ExceptionMessage;
