import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getImportFileItems } from 'app/entities/import-file-item/import-file-item.reducer';
import { createEntity, getEntity, reset, updateEntity } from './import-file-item-error.reducer';

export const ImportFileItemErrorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const importFileItems = useAppSelector(state => state.importFileItem.entities);
  const importFileItemErrorEntity = useAppSelector(state => state.importFileItemError.entity);
  const loading = useAppSelector(state => state.importFileItemError.loading);
  const updating = useAppSelector(state => state.importFileItemError.updating);
  const updateSuccess = useAppSelector(state => state.importFileItemError.updateSuccess);

  const handleClose = () => {
    navigate(`/import-file-item-error${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getImportFileItems({}));
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
    if (values.columnIndex !== undefined && typeof values.columnIndex !== 'number') {
      values.columnIndex = Number(values.columnIndex);
    }
    values.occurredAt = convertDateTimeToServer(values.occurredAt);

    const entity = {
      ...importFileItemErrorEntity,
      ...values,
      importFileItem: importFileItems.find(it => it.id.toString() === values.importFileItem?.toString()),
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
          ...importFileItemErrorEntity,
          occurredAt: convertDateTimeFromServer(importFileItemErrorEntity.occurredAt),
          importFileItem: importFileItemErrorEntity?.importFileItem?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.importFileItemError.home.createOrEditLabel" data-cy="ImportFileItemErrorCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.importFileItemError.home.createOrEditLabel">
              Create or edit a ImportFileItemError
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
                  id="import-file-item-error-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItemError.columnIndex')}
                id="import-file-item-error-columnIndex"
                name="columnIndex"
                data-cy="columnIndex"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="columnIndexLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.columnIndex" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItemError.fieldName')}
                id="import-file-item-error-fieldName"
                name="fieldName"
                data-cy="fieldName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="fieldNameLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.fieldName" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItemError.errorCode')}
                id="import-file-item-error-errorCode"
                name="errorCode"
                data-cy="errorCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="errorCodeLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.errorCode" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItemError.message')}
                id="import-file-item-error-message"
                name="message"
                data-cy="message"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="messageLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.message" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItemError.severity')}
                id="import-file-item-error-severity"
                name="severity"
                data-cy="severity"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="severityLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.severity" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.importFileItemError.occurredAt')}
                id="import-file-item-error-occurredAt"
                name="occurredAt"
                data-cy="occurredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="occurredAtLabel">
                <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.occurredAt" />
              </UncontrolledTooltip>
              <ValidatedField
                id="import-file-item-error-importFileItem"
                name="importFileItem"
                data-cy="importFileItem"
                label={translate('turnbridgeBackendApp.importFileItemError.importFileItem')}
                type="select"
                required
              >
                <option value="" key="0" />
                {importFileItems
                  ? importFileItems.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.lineIndex}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/import-file-item-error" replace color="info">
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

export default ImportFileItemErrorUpdate;
