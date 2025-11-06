import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getStoredObjects } from 'app/entities/stored-object/stored-object.reducer';
import { UploadJobStatus } from 'app/shared/model/enumerations/upload-job-status.model';
import { createEntity, getEntity, reset, updateEntity } from './upload-job.reducer';

export const UploadJobUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const storedObjects = useAppSelector(state => state.storedObject.entities);
  const uploadJobEntity = useAppSelector(state => state.uploadJob.entity);
  const loading = useAppSelector(state => state.uploadJob.loading);
  const updating = useAppSelector(state => state.uploadJob.updating);
  const updateSuccess = useAppSelector(state => state.uploadJob.updateSuccess);
  const uploadJobStatusValues = Object.keys(UploadJobStatus);

  const handleClose = () => {
    navigate(`/upload-job${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStoredObjects({}));
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
    if (values.total !== undefined && typeof values.total !== 'number') {
      values.total = Number(values.total);
    }
    if (values.accepted !== undefined && typeof values.accepted !== 'number') {
      values.accepted = Number(values.accepted);
    }
    if (values.failed !== undefined && typeof values.failed !== 'number') {
      values.failed = Number(values.failed);
    }
    if (values.sent !== undefined && typeof values.sent !== 'number') {
      values.sent = Number(values.sent);
    }

    const entity = {
      ...uploadJobEntity,
      ...values,
      originalFile: storedObjects.find(it => it.id.toString() === values.originalFile?.toString()),
      resultFile: storedObjects.find(it => it.id.toString() === values.resultFile?.toString()),
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
          status: 'RECEIVED',
          ...uploadJobEntity,
          originalFile: uploadJobEntity?.originalFile?.id,
          resultFile: uploadJobEntity?.resultFile?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.uploadJob.home.createOrEditLabel" data-cy="UploadJobCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.uploadJob.home.createOrEditLabel">Create or edit a UploadJob</Translate>
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
                  id="upload-job-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.jobId')}
                id="upload-job-jobId"
                name="jobId"
                data-cy="jobId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 8, message: translate('entity.validation.minlength', { min: 8 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="jobIdLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.jobId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.sellerId')}
                id="upload-job-sellerId"
                name="sellerId"
                data-cy="sellerId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="sellerIdLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sellerId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.sellerName')}
                id="upload-job-sellerName"
                name="sellerName"
                data-cy="sellerName"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="sellerNameLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sellerName" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.period')}
                id="upload-job-period"
                name="period"
                data-cy="period"
                type="text"
                validate={{
                  pattern: { value: /[0-9]{6}/, message: translate('entity.validation.pattern', { pattern: '[0-9]{6}' }) },
                }}
              />
              <UncontrolledTooltip target="periodLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.period" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.profile')}
                id="upload-job-profile"
                name="profile"
                data-cy="profile"
                type="text"
                validate={{
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="profileLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.profile" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.sourceFilename')}
                id="upload-job-sourceFilename"
                name="sourceFilename"
                data-cy="sourceFilename"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <UncontrolledTooltip target="sourceFilenameLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sourceFilename" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.sourceMediaType')}
                id="upload-job-sourceMediaType"
                name="sourceMediaType"
                data-cy="sourceMediaType"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="sourceMediaTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sourceMediaType" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.status')}
                id="upload-job-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {uploadJobStatusValues.map(uploadJobStatus => (
                  <option value={uploadJobStatus} key={uploadJobStatus}>
                    {translate(`turnbridgeBackendApp.UploadJobStatus.${uploadJobStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.total')}
                id="upload-job-total"
                name="total"
                data-cy="total"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="totalLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.total" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.accepted')}
                id="upload-job-accepted"
                name="accepted"
                data-cy="accepted"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="acceptedLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.accepted" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.failed')}
                id="upload-job-failed"
                name="failed"
                data-cy="failed"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="failedLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.failed" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.sent')}
                id="upload-job-sent"
                name="sent"
                data-cy="sent"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="sentLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sent" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJob.remark')}
                id="upload-job-remark"
                name="remark"
                data-cy="remark"
                type="text"
                validate={{
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <UncontrolledTooltip target="remarkLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJob.help.remark" />
              </UncontrolledTooltip>
              <ValidatedField
                id="upload-job-originalFile"
                name="originalFile"
                data-cy="originalFile"
                label={translate('turnbridgeBackendApp.uploadJob.originalFile')}
                type="select"
                required
              >
                <option value="" key="0" />
                {storedObjects
                  ? storedObjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="upload-job-resultFile"
                name="resultFile"
                data-cy="resultFile"
                label={translate('turnbridgeBackendApp.uploadJob.resultFile')}
                type="select"
              >
                <option value="" key="0" />
                {storedObjects
                  ? storedObjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/upload-job" replace color="info">
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

export default UploadJobUpdate;
