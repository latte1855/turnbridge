import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getImportFiles } from 'app/entities/import-file/import-file.reducer';
import { getEntities as getTenants } from 'app/entities/tenant/tenant.reducer';
import { MessageFamily } from 'app/shared/model/enumerations/message-family.model';
import { InvoiceStatus } from 'app/shared/model/enumerations/invoice-status.model';
import { createEntity, getEntity, reset, updateEntity } from './invoice.reducer';

export const InvoiceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const importFiles = useAppSelector(state => state.importFile.entities);
  const tenants = useAppSelector(state => state.tenant.entities);
  const invoiceEntity = useAppSelector(state => state.invoice.entity);
  const loading = useAppSelector(state => state.invoice.loading);
  const updating = useAppSelector(state => state.invoice.updating);
  const updateSuccess = useAppSelector(state => state.invoice.updateSuccess);
  const messageFamilyValues = Object.keys(MessageFamily);
  const invoiceStatusValues = Object.keys(InvoiceStatus);

  const handleClose = () => {
    navigate(`/invoice${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getImportFiles({}));
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
    if (values.salesAmount !== undefined && typeof values.salesAmount !== 'number') {
      values.salesAmount = Number(values.salesAmount);
    }
    if (values.taxAmount !== undefined && typeof values.taxAmount !== 'number') {
      values.taxAmount = Number(values.taxAmount);
    }
    if (values.totalAmount !== undefined && typeof values.totalAmount !== 'number') {
      values.totalAmount = Number(values.totalAmount);
    }
    values.issuedAt = convertDateTimeToServer(values.issuedAt);

    const entity = {
      ...invoiceEntity,
      ...values,
      importFile: importFiles.find(it => it.id.toString() === values.importFile?.toString()),
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
      ? {
          issuedAt: displayDefaultDateTime(),
        }
      : {
          messageFamily: 'F0401',
          invoiceStatus: 'DRAFT',
          ...invoiceEntity,
          issuedAt: convertDateTimeFromServer(invoiceEntity.issuedAt),
          importFile: invoiceEntity?.importFile?.id,
          tenant: invoiceEntity?.tenant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.invoice.home.createOrEditLabel" data-cy="InvoiceCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.invoice.home.createOrEditLabel">Create or edit a Invoice</Translate>
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
                  id="invoice-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.invoiceNo')}
                id="invoice-invoiceNo"
                name="invoiceNo"
                data-cy="invoiceNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <UncontrolledTooltip target="invoiceNoLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.invoiceNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.messageFamily')}
                id="invoice-messageFamily"
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
                <Translate contentKey="turnbridgeBackendApp.invoice.help.messageFamily" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.buyerId')}
                id="invoice-buyerId"
                name="buyerId"
                data-cy="buyerId"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="buyerIdLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.buyerId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.buyerName')}
                id="invoice-buyerName"
                name="buyerName"
                data-cy="buyerName"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="buyerNameLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.buyerName" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.sellerId')}
                id="invoice-sellerId"
                name="sellerId"
                data-cy="sellerId"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="sellerIdLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.sellerId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.sellerName')}
                id="invoice-sellerName"
                name="sellerName"
                data-cy="sellerName"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="sellerNameLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.sellerName" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.salesAmount')}
                id="invoice-salesAmount"
                name="salesAmount"
                data-cy="salesAmount"
                type="text"
              />
              <UncontrolledTooltip target="salesAmountLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.salesAmount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.taxAmount')}
                id="invoice-taxAmount"
                name="taxAmount"
                data-cy="taxAmount"
                type="text"
              />
              <UncontrolledTooltip target="taxAmountLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.taxAmount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.totalAmount')}
                id="invoice-totalAmount"
                name="totalAmount"
                data-cy="totalAmount"
                type="text"
              />
              <UncontrolledTooltip target="totalAmountLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.totalAmount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.taxType')}
                id="invoice-taxType"
                name="taxType"
                data-cy="taxType"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="taxTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.taxType" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.normalizedJson')}
                id="invoice-normalizedJson"
                name="normalizedJson"
                data-cy="normalizedJson"
                type="textarea"
              />
              <UncontrolledTooltip target="normalizedJsonLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.normalizedJson" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.invoiceStatus')}
                id="invoice-invoiceStatus"
                name="invoiceStatus"
                data-cy="invoiceStatus"
                type="select"
              >
                {invoiceStatusValues.map(invoiceStatus => (
                  <option value={invoiceStatus} key={invoiceStatus}>
                    {translate(`turnbridgeBackendApp.InvoiceStatus.${invoiceStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="invoiceStatusLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.invoiceStatus" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.issuedAt')}
                id="invoice-issuedAt"
                name="issuedAt"
                data-cy="issuedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="issuedAtLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.issuedAt" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoice.legacyType')}
                id="invoice-legacyType"
                name="legacyType"
                data-cy="legacyType"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="legacyTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.invoice.help.legacyType" />
              </UncontrolledTooltip>
              <ValidatedField
                id="invoice-importFile"
                name="importFile"
                data-cy="importFile"
                label={translate('turnbridgeBackendApp.invoice.importFile')}
                type="select"
                required
              >
                <option value="" key="0" />
                {importFiles
                  ? importFiles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.originalFilename}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="invoice-tenant"
                name="tenant"
                data-cy="tenant"
                label={translate('turnbridgeBackendApp.invoice.tenant')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/invoice" replace color="info">
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

export default InvoiceUpdate;
