import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IExceptionMessage } from 'app/shared/model/exception-message.model';
import { StatusTypeEnum } from 'app/shared/model/enumerations/status-type-enum.model';
import { getEntity, updateEntity, createEntity, reset } from './exception-message.reducer';

export const ExceptionMessageUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const exceptionMessageEntity = useAppSelector(state => state.trademanagement.exceptionMessage.entity);
  const loading = useAppSelector(state => state.trademanagement.exceptionMessage.loading);
  const updating = useAppSelector(state => state.trademanagement.exceptionMessage.updating);
  const updateSuccess = useAppSelector(state => state.trademanagement.exceptionMessage.updateSuccess);
  const statusTypeEnumValues = Object.keys(StatusTypeEnum);
  const handleClose = () => {
    props.history.push('/exception-message');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.creationTime = convertDateTimeToServer(values.creationTime);
    values.lastExecutionTime = convertDateTimeToServer(values.lastExecutionTime);

    const entity = {
      ...exceptionMessageEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          creationTime: displayDefaultDateTime(),
          lastExecutionTime: displayDefaultDateTime(),
        }
      : {
          statusType: 'NON_RETRYABLE',
          ...exceptionMessageEntity,
          creationTime: convertDateTimeFromServer(exceptionMessageEntity.creationTime),
          lastExecutionTime: convertDateTimeFromServer(exceptionMessageEntity.lastExecutionTime),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tradeManagementApp.exceptionMessage.home.createOrEditLabel" data-cy="ExceptionMessageCreateUpdateHeading">
            <Translate contentKey="tradeManagementApp.exceptionMessage.home.createOrEditLabel">Create or edit a ExceptionMessage</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="exception-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.kafkaKey')}
                id="exception-message-kafkaKey"
                name="kafkaKey"
                data-cy="kafkaKey"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.exceptionCode')}
                id="exception-message-exceptionCode"
                name="exceptionCode"
                data-cy="exceptionCode"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.exceptionMessage')}
                id="exception-message-exceptionMessage"
                name="exceptionMessage"
                data-cy="exceptionMessage"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.retryTopic')}
                id="exception-message-retryTopic"
                name="retryTopic"
                data-cy="retryTopic"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.rawMessage')}
                id="exception-message-rawMessage"
                name="rawMessage"
                data-cy="rawMessage"
                type="textarea"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.retryCount')}
                id="exception-message-retryCount"
                name="retryCount"
                data-cy="retryCount"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.creationTime')}
                id="exception-message-creationTime"
                name="creationTime"
                data-cy="creationTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.lastExecutionTime')}
                id="exception-message-lastExecutionTime"
                name="lastExecutionTime"
                data-cy="lastExecutionTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tradeManagementApp.exceptionMessage.statusType')}
                id="exception-message-statusType"
                name="statusType"
                data-cy="statusType"
                type="select"
              >
                {statusTypeEnumValues.map(statusTypeEnum => (
                  <option value={statusTypeEnum} key={statusTypeEnum}>
                    {translate('tradeManagementApp.StatusTypeEnum.' + statusTypeEnum)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/exception-message" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ExceptionMessageUpdate;
