import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getImportFiles } from 'app/entities/import-file/import-file.reducer';
import { createEntity, getEntity, reset, updateEntity } from './import-file-log.reducer';

export const ImportFileLogUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const importFiles = useAppSelector(state => state.importFile.entities);
  const importFileLogEntity = useAppSelector(state => state.importFileLog.entity);
  const loading = useAppSelector(state => state.importFileLog.loading);
  const updating = useAppSelector(state => state.importFileLog.updating);
  const updateSuccess = useAppSelector(state => state.importFileLog.updateSuccess);

  const handleClose = () => {
    navigate(`/import-file-log${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.lineIndex !== undefined && typeof values.lineIndex !== 'number') {
      values.lineIndex = Number(values.lineIndex);
    }

    const entity = {
      ...importFileLogEntity,
      ...values,
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
      ? {}
      : {
          ...importFileLogEntity,
          importFile: importFileLogEntity?.importFile?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.importFileLog.home.createOrEditLabel" data-cy="ImportFileLogCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.importFileLog.home.createOrEditLabel">Create or edit a ImportFileLog</Translate>
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
                  id="import-file-log-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.lineIndex')}
                id="import-file-log-lineIndex"
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
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.lineIndex" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.field')}
                id="import-file-log-field"
                name="field"
                data-cy="field"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="fieldLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.field" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.errorCode')}
                id="import-file-log-errorCode"
                name="errorCode"
                data-cy="errorCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="errorCodeLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.errorCode" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.message')}
                id="import-file-log-message"
                name="message"
                data-cy="message"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="messageLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.message" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.rawLine')}
                id="import-file-log-rawLine"
                name="rawLine"
                data-cy="rawLine"
                type="textarea"
              />
              <UncontrolledTooltip target="rawLineLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.rawLine" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.sourceFamily')}
                id="import-file-log-sourceFamily"
                name="sourceFamily"
                data-cy="sourceFamily"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="sourceFamilyLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.sourceFamily" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.normalizedFamily')}
                id="import-file-log-normalizedFamily"
                name="normalizedFamily"
                data-cy="normalizedFamily"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="normalizedFamilyLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.normalizedFamily" />
              </UncontrolledTooltip>
              <ValidatedField
                id="import-file-log-importFile"
                name="importFile"
                data-cy="importFile"
                label={translate('turnbridgeBackendApp.importFileLog.importFile')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/import-file-log" replace color="info">
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

export default ImportFileLogUpdate;
