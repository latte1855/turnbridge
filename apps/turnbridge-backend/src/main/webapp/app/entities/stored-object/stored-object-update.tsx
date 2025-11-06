import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { StoragePurpose } from 'app/shared/model/enumerations/storage-purpose.model';
import { createEntity, getEntity, reset, updateEntity } from './stored-object.reducer';

export const StoredObjectUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const storedObjectEntity = useAppSelector(state => state.storedObject.entity);
  const loading = useAppSelector(state => state.storedObject.loading);
  const updating = useAppSelector(state => state.storedObject.updating);
  const updateSuccess = useAppSelector(state => state.storedObject.updateSuccess);
  const storagePurposeValues = Object.keys(StoragePurpose);

  const handleClose = () => {
    navigate(`/stored-object${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.contentLength !== undefined && typeof values.contentLength !== 'number') {
      values.contentLength = Number(values.contentLength);
    }

    const entity = {
      ...storedObjectEntity,
      ...values,
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
          purpose: 'UPLOAD_ORIGINAL',
          ...storedObjectEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.storedObject.home.createOrEditLabel" data-cy="StoredObjectCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.storedObject.home.createOrEditLabel">Create or edit a StoredObject</Translate>
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
                  id="stored-object-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.bucket')}
                id="stored-object-bucket"
                name="bucket"
                data-cy="bucket"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="bucketLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.bucket" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.objectKey')}
                id="stored-object-objectKey"
                name="objectKey"
                data-cy="objectKey"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                }}
              />
              <UncontrolledTooltip target="objectKeyLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.objectKey" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.mediaType')}
                id="stored-object-mediaType"
                name="mediaType"
                data-cy="mediaType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="mediaTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.mediaType" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.contentLength')}
                id="stored-object-contentLength"
                name="contentLength"
                data-cy="contentLength"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="contentLengthLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.contentLength" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.sha256')}
                id="stored-object-sha256"
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
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.sha256" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.purpose')}
                id="stored-object-purpose"
                name="purpose"
                data-cy="purpose"
                type="select"
              >
                {storagePurposeValues.map(storagePurpose => (
                  <option value={storagePurpose} key={storagePurpose}>
                    {translate(`turnbridgeBackendApp.StoragePurpose.${storagePurpose}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="purposeLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.purpose" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.filename')}
                id="stored-object-filename"
                name="filename"
                data-cy="filename"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <UncontrolledTooltip target="filenameLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.filename" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.storageClass')}
                id="stored-object-storageClass"
                name="storageClass"
                data-cy="storageClass"
                type="text"
                validate={{
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="storageClassLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.storageClass" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.encryption')}
                id="stored-object-encryption"
                name="encryption"
                data-cy="encryption"
                type="text"
                validate={{
                  maxLength: { value: 32, message: translate('entity.validation.maxlength', { max: 32 }) },
                }}
              />
              <UncontrolledTooltip target="encryptionLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.encryption" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.storedObject.metadata')}
                id="stored-object-metadata"
                name="metadata"
                data-cy="metadata"
                type="textarea"
              />
              <UncontrolledTooltip target="metadataLabel">
                <Translate contentKey="turnbridgeBackendApp.storedObject.help.metadata" />
              </UncontrolledTooltip>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/stored-object" replace color="info">
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

export default StoredObjectUpdate;
