import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';

import { getEntities as getTenants } from 'app/entities/tenant/tenant.reducer';
import { WebhookStatus } from 'app/shared/model/enumerations/webhook-status.model';
import { createEntity, getEntity, reset, updateEntity } from './webhook-endpoint.reducer';

export const WebhookEndpointUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tenants = useAppSelector(state => state.tenant.entities);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const webhookEndpointEntity = useAppSelector(state => state.webhookEndpoint.entity);
  const loading = useAppSelector(state => state.webhookEndpoint.loading);
  const updating = useAppSelector(state => state.webhookEndpoint.updating);
  const updateSuccess = useAppSelector(state => state.webhookEndpoint.updateSuccess);
  const webhookStatusValues = Object.keys(WebhookStatus);

  const handleClose = () => {
    navigate(`/webhook-endpoint${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTenants({}));
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

    const entity = {
      ...webhookEndpointEntity,
      ...values,
      ...(isAdmin && { tenant: tenants.find(it => it.id.toString() === values.tenant?.toString()) }),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...webhookEndpointEntity,
          tenant: webhookEndpointEntity?.tenant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.webhookEndpoint.home.createOrEditLabel" data-cy="WebhookEndpointCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.home.createOrEditLabel">Create or edit a WebhookEndpoint</Translate>
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
                  id="webhook-endpoint-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookEndpoint.name')}
                id="webhook-endpoint-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="nameLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.name" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookEndpoint.targetUrl')}
                id="webhook-endpoint-targetUrl"
                name="targetUrl"
                data-cy="targetUrl"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <UncontrolledTooltip target="targetUrlLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.targetUrl" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookEndpoint.secret')}
                id="webhook-endpoint-secret"
                name="secret"
                data-cy="secret"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="secretLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.secret" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookEndpoint.events')}
                id="webhook-endpoint-events"
                name="events"
                data-cy="events"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <UncontrolledTooltip target="eventsLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.events" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.webhookEndpoint.status')}
                id="webhook-endpoint-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {webhookStatusValues.map(webhookStatus => (
                  <option value={webhookStatus} key={webhookStatus}>
                    {translate(`turnbridgeBackendApp.WebhookStatus.${webhookStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.status" />
              </UncontrolledTooltip>
              {isAdmin && (
                <ValidatedField
                  id="webhook-endpoint-tenant"
                  name="tenant"
                  data-cy="tenant"
                  label={translate('turnbridgeBackendApp.webhookEndpoint.tenant')}
                  type="select"
                >
                  <option value="" key="0" />
                  {tenants
                    ? tenants.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.name}
                        </option>
                      ))
                    : null}
                </ValidatedField>
              )}
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/webhook-endpoint" replace color="info">
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

export default WebhookEndpointUpdate;
