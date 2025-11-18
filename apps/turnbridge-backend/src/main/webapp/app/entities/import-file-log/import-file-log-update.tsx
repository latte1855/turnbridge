import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
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
    values.occurredAt = convertDateTimeToServer(values.occurredAt);

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
      ? {
          occurredAt: displayDefaultDateTime(),
        }
      : {
          ...importFileLogEntity,
          occurredAt: convertDateTimeFromServer(importFileLogEntity.occurredAt),
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
                label={translate('turnbridgeBackendApp.importFileLog.eventCode')}
                id="import-file-log-eventCode"
                name="eventCode"
                data-cy="eventCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="eventCodeLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.eventCode" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.level')}
                id="import-file-log-level"
                name="level"
                data-cy="level"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="levelLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.level" />
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
                label={translate('turnbridgeBackendApp.importFileLog.detail')}
                id="import-file-log-detail"
                name="detail"
                data-cy="detail"
                type="textarea"
              />
              <UncontrolledTooltip target="detailLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.detail" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileLog.occurredAt')}
                id="import-file-log-occurredAt"
                name="occurredAt"
                data-cy="occurredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="occurredAtLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileLog.help.occurredAt" />
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
