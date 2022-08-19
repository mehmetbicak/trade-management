import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IRawMessage } from 'app/shared/model/raw-message.model';
import { MessageTypeEnum } from 'app/shared/model/enumerations/message-type-enum.model';
import { ExecutionTypeEnum } from 'app/shared/model/enumerations/execution-type-enum.model';
import { getEntity, updateEntity, createEntity, reset } from './raw-message.reducer';

export const RawMessageUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const rawMessageEntity = useAppSelector(state => state.trademanagement.rawMessage.entity);
  const loading = useAppSelector(state => state.trademanagement.rawMessage.loading);
  const updating = useAppSelector(state => state.trademanagement.rawMessage.updating);
  const updateSuccess = useAppSelector(state => state.trademanagement.rawMessage.updateSuccess);
  const messageTypeEnumValues = Object.keys(MessageTypeEnum);
  const executionTypeEnumValues = Object.keys(ExecutionTypeEnum);
  const handleClose = () => {
    props.history.push('/raw-message');
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

    const entity = {
      ...rawMessageEntity,
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
        }
      : {
          messageType: 'FIX',
          executionType: 'ADD',
          ...rawMessageEntity,
          creationTime: convertDateTimeFromServer(rawMessageEntity.creationTime),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tradeManagementApp.rawMessage.home.createOrEditLabel" data-cy="RawMessageCreateUpdateHeading">
            <Translate contentKey="tradeManagementApp.rawMessage.home.createOrEditLabel">Create or edit a RawMessage</Translate>
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
                  id="raw-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.messageType')}
                id="raw-message-messageType"
                name="messageType"
                data-cy="messageType"
                type="select"
              >
                {messageTypeEnumValues.map(messageTypeEnum => (
                  <option value={messageTypeEnum} key={messageTypeEnum}>
                    {translate('tradeManagementApp.MessageTypeEnum.' + messageTypeEnum)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.executionType')}
                id="raw-message-executionType"
                name="executionType"
                data-cy="executionType"
                type="select"
              >
                {executionTypeEnumValues.map(executionTypeEnum => (
                  <option value={executionTypeEnum} key={executionTypeEnum}>
                    {translate('tradeManagementApp.ExecutionTypeEnum.' + executionTypeEnum)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.kafkaKey')}
                id="raw-message-kafkaKey"
                name="kafkaKey"
                data-cy="kafkaKey"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.companyId')}
                id="raw-message-companyId"
                name="companyId"
                data-cy="companyId"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.executionId')}
                id="raw-message-executionId"
                name="executionId"
                data-cy="executionId"
                type="text"
              />
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.message')}
                id="raw-message-message"
                name="message"
                data-cy="message"
                type="textarea"
              />
              <ValidatedField
                label={translate('tradeManagementApp.rawMessage.creationTime')}
                id="raw-message-creationTime"
                name="creationTime"
                data-cy="creationTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/raw-message" replace color="info">
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

export default RawMessageUpdate;
