import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getImportFiles } from 'app/entities/import-file/import-file.reducer';
import { getEntities as getInvoices } from 'app/entities/invoice/invoice.reducer';
import { ImportItemStatus } from 'app/shared/model/enumerations/import-item-status.model';
import { createEntity, getEntity, reset, updateEntity } from './import-file-item.reducer';

export const ImportFileItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const importFiles = useAppSelector(state => state.importFile.entities);
  const invoices = useAppSelector(state => state.invoice.entities);
  const importFileItemEntity = useAppSelector(state => state.importFileItem.entity);
  const loading = useAppSelector(state => state.importFileItem.loading);
  const updating = useAppSelector(state => state.importFileItem.updating);
  const updateSuccess = useAppSelector(state => state.importFileItem.updateSuccess);
  const importItemStatusValues = Object.keys(ImportItemStatus);

  const handleClose = () => {
    navigate(`/import-file-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getImportFiles({}));
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
    if (values.lineIndex !== undefined && typeof values.lineIndex !== 'number') {
      values.lineIndex = Number(values.lineIndex);
    }

    const entity = {
      ...importFileItemEntity,
      ...values,
      importFile: importFiles.find(it => it.id.toString() === values.importFile?.toString()),
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
      ? {}
      : {
          status: 'PENDING',
          ...importFileItemEntity,
          importFile: importFileItemEntity?.importFile?.id,
          invoice: importFileItemEntity?.invoice?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.importFileItem.home.createOrEditLabel" data-cy="ImportFileItemCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.importFileItem.home.createOrEditLabel">Create or edit a ImportFileItem</Translate>
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
                  id="import-file-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.lineIndex')}
                id="import-file-item-lineIndex"
                name="lineIndex"
                data-cy="lineIndex"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="lineIndexLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.lineIndex" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.rawData')}
                id="import-file-item-rawData"
                name="rawData"
                data-cy="rawData"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <UncontrolledTooltip target="rawDataLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.rawData" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.rawHash')}
                id="import-file-item-rawHash"
                name="rawHash"
                data-cy="rawHash"
                type="text"
                validate={{
                  minLength: { value: 64, message: translate('entity.validation.minlength', { min: 64 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="rawHashLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.rawHash" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.sourceFamily')}
                id="import-file-item-sourceFamily"
                name="sourceFamily"
                data-cy="sourceFamily"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="sourceFamilyLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.sourceFamily" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.normalizedFamily')}
                id="import-file-item-normalizedFamily"
                name="normalizedFamily"
                data-cy="normalizedFamily"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="normalizedFamilyLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.normalizedFamily" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.normalizedJson')}
                id="import-file-item-normalizedJson"
                name="normalizedJson"
                data-cy="normalizedJson"
                type="textarea"
              />
              <UncontrolledTooltip target="normalizedJsonLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.normalizedJson" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.status')}
                id="import-file-item-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {importItemStatusValues.map(importItemStatus => (
                  <option value={importItemStatus} key={importItemStatus}>
                    {translate(`turnbridgeBackendApp.ImportItemStatus.${importItemStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.errorCode')}
                id="import-file-item-errorCode"
                name="errorCode"
                data-cy="errorCode"
                type="text"
                validate={{
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="errorCodeLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.errorCode" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItem.errorMessage')}
                id="import-file-item-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="errorMessageLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItem.help.errorMessage" />
              </UncontrolledTooltip>
              <ValidatedField
                id="import-file-item-importFile"
                name="importFile"
                data-cy="importFile"
                label={translate('turnbridgeBackendApp.importFileItem.importFile')}
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
                id="import-file-item-invoice"
                name="invoice"
                data-cy="invoice"
                label={translate('turnbridgeBackendApp.importFileItem.invoice')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/import-file-item" replace color="info">
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

export default ImportFileItemUpdate;
