import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getWebhookEndpoints } from 'app/entities/webhook-endpoint/webhook-endpoint.reducer';
import { DeliveryResult } from 'app/shared/model/enumerations/delivery-result.model';
import { createEntity, getEntity, reset, updateEntity } from './webhook-delivery-log.reducer';

export const WebhookDeliveryLogUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const webhookEndpoints = useAppSelector(state => state.webhookEndpoint.entities);
  const webhookDeliveryLogEntity = useAppSelector(state => state.webhookDeliveryLog.entity);
  const loading = useAppSelector(state => state.webhookDeliveryLog.loading);
  const updating = useAppSelector(state => state.webhookDeliveryLog.updating);
  const updateSuccess = useAppSelector(state => state.webhookDeliveryLog.updateSuccess);
  const deliveryResultValues = Object.keys(DeliveryResult);

  const handleClose = () => {
    navigate(`/webhook-delivery-log${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getWebhookEndpoints({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.httpStatus !== undefined && typeof values.httpStatus !== 'number') {
      values.httpStatus = Number(values.httpStatus);
    }
    if (values.attempts !== undefined && typeof values.attempts !== 'number') {
      values.attempts = Number(values.attempts);
    }
    values.deliveredAt = convertDateTimeToServer(values.deliveredAt);
    values.nextAttemptAt = convertDateTimeToServer(values.nextAttemptAt);
    values.lockedAt = convertDateTimeToServer(values.lockedAt);

    const entity = {
      ...webhookDeliveryLogEntity,
      ...values,
      webhookEndpoint: webhookEndpoints.find(it => it.id.toString() === values.webhookEndpoint?.toString()),
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
          deliveredAt: displayDefaultDateTime(),
          nextAttemptAt: displayDefaultDateTime(),
          lockedAt: displayDefaultDateTime(),
        }
      : {
          status: 'SUCCESS',
          ...webhookDeliveryLogEntity,
          deliveredAt: convertDateTimeFromServer(webhookDeliveryLogEntity.deliveredAt),
          nextAttemptAt: convertDateTimeFromServer(webhookDeliveryLogEntity.nextAttemptAt),
          lockedAt: convertDateTimeFromServer(webhookDeliveryLogEntity.lockedAt),
          webhookEndpoint: webhookDeliveryLogEntity?.webhookEndpoint?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.webhookDeliveryLog.home.createOrEditLabel" data-cy="WebhookDeliveryLogCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.home.createOrEditLabel">
              Create or edit a WebhookDeliveryLog
            </Translate>
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
                  id="webhook-delivery-log-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.deliveryId')}
                id="webhook-delivery-log-deliveryId"
                name="deliveryId"
                data-cy="deliveryId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="deliveryIdLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.deliveryId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.event')}
                id="webhook-delivery-log-event"
                name="event"
                data-cy="event"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="eventLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.event" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.payload')}
                id="webhook-delivery-log-payload"
                name="payload"
                data-cy="payload"
                type="textarea"
              />
              <UncontrolledTooltip target="payloadLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.payload" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.status')}
                id="webhook-delivery-log-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {deliveryResultValues.map(deliveryResult => (
                  <option value={deliveryResult} key={deliveryResult}>
                    {translate(`turnbridgeBackendApp.DeliveryResult.${deliveryResult}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.httpStatus')}
                id="webhook-delivery-log-httpStatus"
                name="httpStatus"
                data-cy="httpStatus"
                type="text"
              />
              <UncontrolledTooltip target="httpStatusLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.httpStatus" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.attempts')}
                id="webhook-delivery-log-attempts"
                name="attempts"
                data-cy="attempts"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="attemptsLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.attempts" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.lastError')}
                id="webhook-delivery-log-lastError"
                name="lastError"
                data-cy="lastError"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="lastErrorLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.lastError" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.deliveredAt')}
                id="webhook-delivery-log-deliveredAt"
                name="deliveredAt"
                data-cy="deliveredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="deliveredAtLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.deliveredAt" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.nextAttemptAt')}
                id="webhook-delivery-log-nextAttemptAt"
                name="nextAttemptAt"
                data-cy="nextAttemptAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="nextAttemptAtLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.nextAttemptAt" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.lockedAt')}
                id="webhook-delivery-log-lockedAt"
                name="lockedAt"
                data-cy="lockedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="lockedAtLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.lockedAt" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.dlqReason')}
                id="webhook-delivery-log-dlqReason"
                name="dlqReason"
                data-cy="dlqReason"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="dlqReasonLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.dlqReason" />
              </UncontrolledTooltip>
              <ValidatedField
                id="webhook-delivery-log-webhookEndpoint"
                name="webhookEndpoint"
                data-cy="webhookEndpoint"
                label={translate('turnbridgeBackendApp.webhookDeliveryLog.webhookEndpoint')}
                type="select"
                required
              >
                <option value="" key="0" />
                {webhookEndpoints
                  ? webhookEndpoints.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/webhook-delivery-log" replace color="info">
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

export default WebhookDeliveryLogUpdate;
