import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getUploadJobs } from 'app/entities/upload-job/upload-job.reducer';
import { JobItemStatus } from 'app/shared/model/enumerations/job-item-status.model';
import { TaxType } from 'app/shared/model/enumerations/tax-type.model';
import { createEntity, getEntity, reset, updateEntity } from './upload-job-item.reducer';

export const UploadJobItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const uploadJobs = useAppSelector(state => state.uploadJob.entities);
  const uploadJobItemEntity = useAppSelector(state => state.uploadJobItem.entity);
  const loading = useAppSelector(state => state.uploadJobItem.loading);
  const updating = useAppSelector(state => state.uploadJobItem.updating);
  const updateSuccess = useAppSelector(state => state.uploadJobItem.updateSuccess);
  const jobItemStatusValues = Object.keys(JobItemStatus);
  const taxTypeValues = Object.keys(TaxType);

  const handleClose = () => {
    navigate(`/upload-job-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUploadJobs({}));
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
    if (values.lineNo !== undefined && typeof values.lineNo !== 'number') {
      values.lineNo = Number(values.lineNo);
    }
    if (values.amountExcl !== undefined && typeof values.amountExcl !== 'number') {
      values.amountExcl = Number(values.amountExcl);
    }
    if (values.taxAmount !== undefined && typeof values.taxAmount !== 'number') {
      values.taxAmount = Number(values.taxAmount);
    }
    if (values.amountIncl !== undefined && typeof values.amountIncl !== 'number') {
      values.amountIncl = Number(values.amountIncl);
    }

    const entity = {
      ...uploadJobItemEntity,
      ...values,
      job: uploadJobs.find(it => it.id.toString() === values.job?.toString()),
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
          status: 'QUEUED',
          taxType: 'TAXABLE',
          ...uploadJobItemEntity,
          job: uploadJobItemEntity?.job?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.uploadJobItem.home.createOrEditLabel" data-cy="UploadJobItemCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.uploadJobItem.home.createOrEditLabel">Create or edit a UploadJobItem</Translate>
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
                  id="upload-job-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.lineNo')}
                id="upload-job-item-lineNo"
                name="lineNo"
                data-cy="lineNo"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <UncontrolledTooltip target="lineNoLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.lineNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.traceId')}
                id="upload-job-item-traceId"
                name="traceId"
                data-cy="traceId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 8, message: translate('entity.validation.minlength', { min: 8 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="traceIdLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.traceId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.status')}
                id="upload-job-item-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {jobItemStatusValues.map(jobItemStatus => (
                  <option value={jobItemStatus} key={jobItemStatus}>
                    {translate(`turnbridgeBackendApp.JobItemStatus.${jobItemStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="statusLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.status" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.resultCode')}
                id="upload-job-item-resultCode"
                name="resultCode"
                data-cy="resultCode"
                type="text"
                validate={{
                  minLength: { value: 2, message: translate('entity.validation.minlength', { min: 2 }) },
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="resultCodeLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.resultCode" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.resultMsg')}
                id="upload-job-item-resultMsg"
                name="resultMsg"
                data-cy="resultMsg"
                type="text"
                validate={{
                  maxLength: { value: 2048, message: translate('entity.validation.maxlength', { max: 2048 }) },
                }}
              />
              <UncontrolledTooltip target="resultMsgLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.resultMsg" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.buyerId')}
                id="upload-job-item-buyerId"
                name="buyerId"
                data-cy="buyerId"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="buyerIdLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.buyerId" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.buyerName')}
                id="upload-job-item-buyerName"
                name="buyerName"
                data-cy="buyerName"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <UncontrolledTooltip target="buyerNameLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.buyerName" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.currency')}
                id="upload-job-item-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  maxLength: { value: 3, message: translate('entity.validation.maxlength', { max: 3 }) },
                }}
              />
              <UncontrolledTooltip target="currencyLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.currency" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.amountExcl')}
                id="upload-job-item-amountExcl"
                name="amountExcl"
                data-cy="amountExcl"
                type="text"
              />
              <UncontrolledTooltip target="amountExclLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.amountExcl" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.taxAmount')}
                id="upload-job-item-taxAmount"
                name="taxAmount"
                data-cy="taxAmount"
                type="text"
              />
              <UncontrolledTooltip target="taxAmountLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.taxAmount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.amountIncl')}
                id="upload-job-item-amountIncl"
                name="amountIncl"
                data-cy="amountIncl"
                type="text"
              />
              <UncontrolledTooltip target="amountInclLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.amountIncl" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.taxType')}
                id="upload-job-item-taxType"
                name="taxType"
                data-cy="taxType"
                type="select"
              >
                {taxTypeValues.map(taxType => (
                  <option value={taxType} key={taxType}>
                    {translate(`turnbridgeBackendApp.TaxType.${taxType}`)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="taxTypeLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.taxType" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.invoiceDate')}
                id="upload-job-item-invoiceDate"
                name="invoiceDate"
                data-cy="invoiceDate"
                type="date"
              />
              <UncontrolledTooltip target="invoiceDateLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.invoiceDate" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.invoiceNo')}
                id="upload-job-item-invoiceNo"
                name="invoiceNo"
                data-cy="invoiceNo"
                type="text"
                validate={{
                  maxLength: { value: 16, message: translate('entity.validation.maxlength', { max: 16 }) },
                }}
              />
              <UncontrolledTooltip target="invoiceNoLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.invoiceNo" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.assignedPrefix')}
                id="upload-job-item-assignedPrefix"
                name="assignedPrefix"
                data-cy="assignedPrefix"
                type="text"
                validate={{
                  pattern: { value: /[A-Z]{2}/, message: translate('entity.validation.pattern', { pattern: '[A-Z]{2}' }) },
                }}
              />
              <UncontrolledTooltip target="assignedPrefixLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.assignedPrefix" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.rawPayload')}
                id="upload-job-item-rawPayload"
                name="rawPayload"
                data-cy="rawPayload"
                type="textarea"
              />
              <UncontrolledTooltip target="rawPayloadLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.rawPayload" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.rawHash')}
                id="upload-job-item-rawHash"
                name="rawHash"
                data-cy="rawHash"
                type="text"
                validate={{
                  minLength: { value: 64, message: translate('entity.validation.minlength', { min: 64 }) },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="rawHashLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.rawHash" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.uploadJobItem.profileDetected')}
                id="upload-job-item-profileDetected"
                name="profileDetected"
                data-cy="profileDetected"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <UncontrolledTooltip target="profileDetectedLabel">
                <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.profileDetected" />
              </UncontrolledTooltip>
              <ValidatedField
                id="upload-job-item-job"
                name="job"
                data-cy="job"
                label={translate('turnbridgeBackendApp.uploadJobItem.job')}
                type="select"
                required
              >
                <option value="" key="0" />
                {uploadJobs
                  ? uploadJobs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.jobId}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/upload-job-item" replace color="info">
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

export default UploadJobItemUpdate;
