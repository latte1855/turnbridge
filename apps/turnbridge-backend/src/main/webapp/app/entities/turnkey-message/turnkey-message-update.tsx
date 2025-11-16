import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInvoices } from 'app/entities/invoice/invoice.reducer';
import { MessageFamily } from 'app/shared/model/enumerations/message-family.model';
import { createEntity, getEntity, reset, updateEntity } from './turnkey-message.reducer';

export const TurnkeyMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const invoices = useAppSelector(state => state.invoice.entities);
  const turnkeyMessageEntity = useAppSelector(state => state.turnkeyMessage.entity);
  const loading = useAppSelector(state => state.turnkeyMessage.loading);
  const updating = useAppSelector(state => state.turnkeyMessage.updating);
  const updateSuccess = useAppSelector(state => state.turnkeyMessage.updateSuccess);
  const messageFamilyValues = Object.keys(MessageFamily);

  const handleClose = () => {
    navigate(`/turnkey-message${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInvoices({}));
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
    values.receivedAt = convertDateTimeToServer(values.receivedAt);

    const entity = {
      ...turnkeyMessageEntity,
      ...values,
      invoice: invoices.find(it => it.id.toString() === values.invoice?.toString()),
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
          receivedAt: displayDefaultDateTime(),
        }
      : {
          messageFamily: 'F0401',
          ...turnkeyMessageEntity,
          receivedAt: convertDateTimeFromServer(turnkeyMessageEntity.receivedAt),
          invoice: turnkeyMessageEntity?.invoice?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.turnkeyMessage.home.createOrEditLabel" data-cy="TurnkeyMessageCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.home.createOrEditLabel">Create or edit a TurnkeyMessage</Translate>
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
                  id="turnkey-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.messageId')}
                id="turnkey-message-messageId"
                name="messageId"
                data-cy="messageId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="messageIdLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.messageId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.messageFamily')}
                id="turnkey-message-messageFamily"
                name="messageFamily"
                data-cy="messageFamily"
                type="select"
              >
                {messageFamilyValues.map(messageFamily => (
                  <option value={messageFamily} key={messageFamily}>
                    {translate(`turnbridgeBackendApp.MessageFamily.${messageFamily}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="messageFamilyLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.messageFamily" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.type')}
                id="turnkey-message-type"
                name="type"
                data-cy="type"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="typeLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.type" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.code')}
                id="turnkey-message-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="codeLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.code" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.message')}
                id="turnkey-message-message"
                name="message"
                data-cy="message"
                type="textarea"
              />
              <UncontrolledTooltip target="messageLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.message" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.payloadPath')}
                id="turnkey-message-payloadPath"
                name="payloadPath"
                data-cy="payloadPath"
                type="text"
                validate={{
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <UncontrolledTooltip target="payloadPathLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.payloadPath" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.turnkeyMessage.receivedAt')}
                id="turnkey-message-receivedAt"
                name="receivedAt"
                data-cy="receivedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="receivedAtLabel">
                <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.receivedAt" />
              </UncontrolledTooltip>
              <ValidatedField
                id="turnkey-message-invoice"
                name="invoice"
                data-cy="invoice"
                label={translate('turnbridgeBackendApp.turnkeyMessage.invoice')}
                type="select"
              >
                <option value="" key="0" />
                {invoices
                  ? invoices.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.invoiceNo}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/turnkey-message" replace color="info">
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

export default TurnkeyMessageUpdate;
