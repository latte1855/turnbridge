import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTenants } from 'app/entities/tenant/tenant.reducer';
import { createEntity, getEntity, reset, updateEntity } from './invoice-assign-no.reducer';

export const InvoiceAssignNoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tenants = useAppSelector(state => state.tenant.entities);
  const invoiceAssignNoEntity = useAppSelector(state => state.invoiceAssignNo.entity);
  const loading = useAppSelector(state => state.invoiceAssignNo.loading);
  const updating = useAppSelector(state => state.invoiceAssignNo.updating);
  const updateSuccess = useAppSelector(state => state.invoiceAssignNo.updateSuccess);

  const handleClose = () => {
    navigate('/invoice-assign-no');
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
    if (values.usedCount !== undefined && typeof values.usedCount !== 'number') {
      values.usedCount = Number(values.usedCount);
    }
    if (values.rollSize !== undefined && typeof values.rollSize !== 'number') {
      values.rollSize = Number(values.rollSize);
    }

    const entity = {
      ...invoiceAssignNoEntity,
      ...values,
      tenant: tenants.find(it => it.id.toString() === values.tenant?.toString()),
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
          ...invoiceAssignNoEntity,
          tenant: invoiceAssignNoEntity?.tenant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.invoiceAssignNo.home.createOrEditLabel" data-cy="InvoiceAssignNoCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.home.createOrEditLabel">Create or edit a InvoiceAssignNo</Translate>
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
                  id="invoice-assign-no-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.track')}
                id="invoice-assign-no-track"
                name="track"
                data-cy="track"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 2, message: translate('entity.validation.maxlength', { max: 2 }) },
                }}
              />
              <UncontrolledTooltip target="trackLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.track" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.period')}
                id="invoice-assign-no-period"
                name="period"
                data-cy="period"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: { value: /[0-9]{6}/, message: translate('entity.validation.pattern', { pattern: '[0-9]{6}' }) },
                }}
              />
              <UncontrolledTooltip target="periodLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.period" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.fromNo')}
                id="invoice-assign-no-fromNo"
                name="fromNo"
                data-cy="fromNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <UncontrolledTooltip target="fromNoLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.fromNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.toNo')}
                id="invoice-assign-no-toNo"
                name="toNo"
                data-cy="toNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <UncontrolledTooltip target="toNoLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.toNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.usedCount')}
                id="invoice-assign-no-usedCount"
                name="usedCount"
                data-cy="usedCount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="usedCountLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.usedCount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.rollSize')}
                id="invoice-assign-no-rollSize"
                name="rollSize"
                data-cy="rollSize"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="rollSizeLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.rollSize" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceAssignNo.status')}
                id="invoice-assign-no-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                id="invoice-assign-no-tenant"
                name="tenant"
                data-cy="tenant"
                label={translate('turnbridgeBackendApp.invoiceAssignNo.tenant')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/invoice-assign-no" replace color="info">
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

export default InvoiceAssignNoUpdate;
