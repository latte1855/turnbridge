import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { TrackRangeStatus } from 'app/shared/model/enumerations/track-range-status.model';
import { createEntity, getEntity, reset, updateEntity } from './track-range.reducer';

export const TrackRangeUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const trackRangeEntity = useAppSelector(state => state.trackRange.entity);
  const loading = useAppSelector(state => state.trackRange.loading);
  const updating = useAppSelector(state => state.trackRange.updating);
  const updateSuccess = useAppSelector(state => state.trackRange.updateSuccess);
  const trackRangeStatusValues = Object.keys(TrackRangeStatus);

  const handleClose = () => {
    navigate(`/track-range${location.search}`);
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
    if (values.startNo !== undefined && typeof values.startNo !== 'number') {
      values.startNo = Number(values.startNo);
    }
    if (values.endNo !== undefined && typeof values.endNo !== 'number') {
      values.endNo = Number(values.endNo);
    }
    if (values.currentNo !== undefined && typeof values.currentNo !== 'number') {
      values.currentNo = Number(values.currentNo);
    }
    if (values.version !== undefined && typeof values.version !== 'number') {
      values.version = Number(values.version);
    }
    values.lockAt = convertDateTimeToServer(values.lockAt);

    const entity = {
      ...trackRangeEntity,
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
      ? {
          lockAt: displayDefaultDateTime(),
        }
      : {
          status: 'ACTIVE',
          ...trackRangeEntity,
          lockAt: convertDateTimeFromServer(trackRangeEntity.lockAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.trackRange.home.createOrEditLabel" data-cy="TrackRangeCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.trackRange.home.createOrEditLabel">Create or edit a TrackRange</Translate>
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
                  id="track-range-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.sellerId')}
                id="track-range-sellerId"
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
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.sellerId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.period')}
                id="track-range-period"
                name="period"
                data-cy="period"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: { value: /[0-9]{6}/, message: translate('entity.validation.pattern', { pattern: '[0-9]{6}' }) },
                }}
              />
              <UncontrolledTooltip target="periodLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.period" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.prefix')}
                id="track-range-prefix"
                name="prefix"
                data-cy="prefix"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: { value: /[A-Z]{2}/, message: translate('entity.validation.pattern', { pattern: '[A-Z]{2}' }) },
                }}
              />
              <UncontrolledTooltip target="prefixLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.prefix" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.startNo')}
                id="track-range-startNo"
                name="startNo"
                data-cy="startNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="startNoLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.startNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.endNo')}
                id="track-range-endNo"
                name="endNo"
                data-cy="endNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="endNoLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.endNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.currentNo')}
                id="track-range-currentNo"
                name="currentNo"
                data-cy="currentNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="currentNoLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.currentNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.status')}
                id="track-range-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {trackRangeStatusValues.map(trackRangeStatus => (
                  <option value={trackRangeStatus} key={trackRangeStatus}>
                    {translate(`turnbridgeBackendApp.TrackRangeStatus.${trackRangeStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.version')}
                id="track-range-version"
                name="version"
                data-cy="version"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 0, message: translate('entity.validation.min', { min: 0 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="versionLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.version" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.lockOwner')}
                id="track-range-lockOwner"
                name="lockOwner"
                data-cy="lockOwner"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="lockOwnerLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.lockOwner" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.trackRange.lockAt')}
                id="track-range-lockAt"
                name="lockAt"
                data-cy="lockAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <UncontrolledTooltip target="lockAtLabel">
                <Translate contentKey="turnbridgeBackendApp.trackRange.help.lockAt" />
              </UncontrolledTooltip>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/track-range" replace color="info">
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

export default TrackRangeUpdate;
