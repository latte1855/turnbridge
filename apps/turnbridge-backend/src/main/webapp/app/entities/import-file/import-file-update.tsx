import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTenants } from 'app/entities/tenant/tenant.reducer';
import { ImportType } from 'app/shared/model/enumerations/import-type.model';
import { ImportStatus } from 'app/shared/model/enumerations/import-status.model';
import { createEntity, getEntity, reset, updateEntity } from './import-file.reducer';

export const ImportFileUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const tenants = useAppSelector(state => state.tenant.entities);
  const importFileEntity = useAppSelector(state => state.importFile.entity);
  const loading = useAppSelector(state => state.importFile.loading);
  const updating = useAppSelector(state => state.importFile.updating);
  const updateSuccess = useAppSelector(state => state.importFile.updateSuccess);
  const importTypeValues = Object.keys(ImportType);
  const importStatusValues = Object.keys(ImportStatus);

  const handleClose = () => {
    navigate(`/import-file${location.search}`);
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
    if (values.totalCount !== undefined && typeof values.totalCount !== 'number') {
      values.totalCount = Number(values.totalCount);
    }
    if (values.successCount !== undefined && typeof values.successCount !== 'number') {
      values.successCount = Number(values.successCount);
    }
    if (values.errorCount !== undefined && typeof values.errorCount !== 'number') {
      values.errorCount = Number(values.errorCount);
    }

    const entity = {
      ...importFileEntity,
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
          importType: 'INVOICE',
          status: 'RECEIVED',
          ...importFileEntity,
          tenant: importFileEntity?.tenant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.importFile.home.createOrEditLabel" data-cy="ImportFileCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.importFile.home.createOrEditLabel">Create or edit a ImportFile</Translate>
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
                  id="import-file-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.importType')}
                id="import-file-importType"
                name="importType"
                data-cy="importType"
                type="select"
              >
                {importTypeValues.map(importType => (
                  <option value={importType} key={importType}>
                    {translate(`turnbridgeBackendApp.ImportType.${importType}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="importTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.importType" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.originalFilename')}
                id="import-file-originalFilename"
                name="originalFilename"
                data-cy="originalFilename"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <UncontrolledTooltip target="originalFilenameLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.originalFilename" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.sha256')}
                id="import-file-sha256"
                name="sha256"
                data-cy="sha256"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 64, message: translate('entity.validation.minlength', { min: 64 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="sha256Label">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.sha256" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.totalCount')}
                id="import-file-totalCount"
                name="totalCount"
                data-cy="totalCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="totalCountLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.totalCount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.successCount')}
                id="import-file-successCount"
                name="successCount"
                data-cy="successCount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="successCountLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.successCount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.errorCount')}
                id="import-file-errorCount"
                name="errorCount"
                data-cy="errorCount"
                type="text"
                validate={{
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="errorCountLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.errorCount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.status')}
                id="import-file-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {importStatusValues.map(importStatus => (
                  <option value={importStatus} key={importStatus}>
                    {translate(`turnbridgeBackendApp.ImportStatus.${importStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFile.legacyType')}
                id="import-file-legacyType"
                name="legacyType"
                data-cy="legacyType"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="legacyTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.importFile.help.legacyType" />
              </UncontrolledTooltip>
              <ValidatedField
                id="import-file-tenant"
                name="tenant"
                data-cy="tenant"
                label={translate('turnbridgeBackendApp.importFile.tenant')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/import-file" replace color="info">
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

export default ImportFileUpdate;
