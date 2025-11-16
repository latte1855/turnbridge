import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTenants } from 'app/entities/tenant/tenant.reducer';
import { getEntities as getInvoices } from 'app/entities/invoice/invoice.reducer';
import { getEntities as getImportFiles } from 'app/entities/import-file/import-file.reducer';
import { ManualActionType } from 'app/shared/model/enumerations/manual-action-type.model';
import { ApprovalStatus } from 'app/shared/model/enumerations/approval-status.model';
import { createEntity, getEntity, reset, updateEntity } from './manual-action.reducer';

export const ManualActionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tenants = useAppSelector(state => state.tenant.entities);
  const invoices = useAppSelector(state => state.invoice.entities);
  const importFiles = useAppSelector(state => state.importFile.entities);
  const manualActionEntity = useAppSelector(state => state.manualAction.entity);
  const loading = useAppSelector(state => state.manualAction.loading);
  const updating = useAppSelector(state => state.manualAction.updating);
  const updateSuccess = useAppSelector(state => state.manualAction.updateSuccess);
  const manualActionTypeValues = Object.keys(ManualActionType);
  const approvalStatusValues = Object.keys(ApprovalStatus);

  const handleClose = () => {
    navigate('/manual-action');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTenants({}));
    dispatch(getInvoices({}));
    dispatch(getImportFiles({}));
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
    values.requestedAt = convertDateTimeToServer(values.requestedAt);
    values.approvedAt = convertDateTimeToServer(values.approvedAt);

    const entity = {
      ...manualActionEntity,
      ...values,
      tenant: tenants.find(it => it.id.toString() === values.tenant?.toString()),
      invoice: invoices.find(it => it.id.toString() === values.invoice?.toString()),
      importFile: importFiles.find(it => it.id.toString() === values.importFile?.toString()),
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
          requestedAt: displayDefaultDateTime(),
          approvedAt: displayDefaultDateTime(),
        }
      : {
          actionType: 'RESEND_XML',
          status: 'PENDING',
          ...manualActionEntity,
          requestedAt: convertDateTimeFromServer(manualActionEntity.requestedAt),
          approvedAt: convertDateTimeFromServer(manualActionEntity.approvedAt),
          tenant: manualActionEntity?.tenant?.id,
          invoice: manualActionEntity?.invoice?.id,
          importFile: manualActionEntity?.importFile?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.manualAction.home.createOrEditLabel" data-cy="ManualActionCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.manualAction.home.createOrEditLabel">Create or edit a ManualAction</Translate>
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
                  id="manual-action-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.actionType')}
                id="manual-action-actionType"
                name="actionType"
                data-cy="actionType"
                type="select"
              >
                {manualActionTypeValues.map(manualActionType => (
                  <option value={manualActionType} key={manualActionType}>
                    {translate(`turnbridgeBackendApp.ManualActionType.${manualActionType}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="actionTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.actionType" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.reason')}
                id="manual-action-reason"
                name="reason"
                data-cy="reason"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="reasonLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.reason" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.status')}
                id="manual-action-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {approvalStatusValues.map(approvalStatus => (
                  <option value={approvalStatus} key={approvalStatus}>
                    {translate(`turnbridgeBackendApp.ApprovalStatus.${approvalStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.requestedBy')}
                id="manual-action-requestedBy"
                name="requestedBy"
                data-cy="requestedBy"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="requestedByLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.requestedBy" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.requestedAt')}
                id="manual-action-requestedAt"
                name="requestedAt"
                data-cy="requestedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="requestedAtLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.requestedAt" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.approvedBy')}
                id="manual-action-approvedBy"
                name="approvedBy"
                data-cy="approvedBy"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="approvedByLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.approvedBy" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.approvedAt')}
                id="manual-action-approvedAt"
                name="approvedAt"
                data-cy="approvedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="approvedAtLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.approvedAt" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.manualAction.resultMessage')}
                id="manual-action-resultMessage"
                name="resultMessage"
                data-cy="resultMessage"
                type="textarea"
              />
              <UncontrolledTooltip target="resultMessageLabel">
                <Translate contentKey="turnbridgeBackendApp.manualAction.help.resultMessage" />
              </UncontrolledTooltip>
              <ValidatedField
                id="manual-action-tenant"
                name="tenant"
                data-cy="tenant"
                label={translate('turnbridgeBackendApp.manualAction.tenant')}
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
              <ValidatedField
                id="manual-action-invoice"
                name="invoice"
                data-cy="invoice"
                label={translate('turnbridgeBackendApp.manualAction.invoice')}
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
              <ValidatedField
                id="manual-action-importFile"
                name="importFile"
                data-cy="importFile"
                label={translate('turnbridgeBackendApp.manualAction.importFile')}
                type="select"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/manual-action" replace color="info">
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

export default ManualActionUpdate;
