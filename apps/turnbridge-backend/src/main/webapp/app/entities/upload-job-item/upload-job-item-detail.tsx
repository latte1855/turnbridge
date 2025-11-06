import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './upload-job-item.reducer';

export const UploadJobItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const uploadJobItemEntity = useAppSelector(state => state.uploadJobItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="uploadJobItemDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.uploadJobItem.detail.title">UploadJobItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{uploadJobItemEntity.id}</dd>
          <dt>
            <span id="lineNo">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.lineNo">Line No</Translate>
            </span>
            <UncontrolledTooltip target="lineNo">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.lineNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.lineNo}</dd>
          <dt>
            <span id="traceId">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.traceId">Trace Id</Translate>
            </span>
            <UncontrolledTooltip target="traceId">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.traceId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.traceId}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.status}</dd>
          <dt>
            <span id="resultCode">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.resultCode">Result Code</Translate>
            </span>
            <UncontrolledTooltip target="resultCode">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.resultCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.resultCode}</dd>
          <dt>
            <span id="resultMsg">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.resultMsg">Result Msg</Translate>
            </span>
            <UncontrolledTooltip target="resultMsg">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.resultMsg" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.resultMsg}</dd>
          <dt>
            <span id="buyerId">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.buyerId">Buyer Id</Translate>
            </span>
            <UncontrolledTooltip target="buyerId">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.buyerId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.buyerId}</dd>
          <dt>
            <span id="buyerName">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.buyerName">Buyer Name</Translate>
            </span>
            <UncontrolledTooltip target="buyerName">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.buyerName" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.buyerName}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.currency">Currency</Translate>
            </span>
            <UncontrolledTooltip target="currency">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.currency" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.currency}</dd>
          <dt>
            <span id="amountExcl">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.amountExcl">Amount Excl</Translate>
            </span>
            <UncontrolledTooltip target="amountExcl">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.amountExcl" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.amountExcl}</dd>
          <dt>
            <span id="taxAmount">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.taxAmount">Tax Amount</Translate>
            </span>
            <UncontrolledTooltip target="taxAmount">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.taxAmount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.taxAmount}</dd>
          <dt>
            <span id="amountIncl">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.amountIncl">Amount Incl</Translate>
            </span>
            <UncontrolledTooltip target="amountIncl">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.amountIncl" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.amountIncl}</dd>
          <dt>
            <span id="taxType">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.taxType">Tax Type</Translate>
            </span>
            <UncontrolledTooltip target="taxType">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.taxType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.taxType}</dd>
          <dt>
            <span id="invoiceDate">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.invoiceDate">Invoice Date</Translate>
            </span>
            <UncontrolledTooltip target="invoiceDate">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.invoiceDate" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {uploadJobItemEntity.invoiceDate ? (
              <TextFormat value={uploadJobItemEntity.invoiceDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="invoiceNo">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.invoiceNo">Invoice No</Translate>
            </span>
            <UncontrolledTooltip target="invoiceNo">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.invoiceNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.invoiceNo}</dd>
          <dt>
            <span id="assignedPrefix">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.assignedPrefix">Assigned Prefix</Translate>
            </span>
            <UncontrolledTooltip target="assignedPrefix">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.assignedPrefix" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.assignedPrefix}</dd>
          <dt>
            <span id="rawPayload">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.rawPayload">Raw Payload</Translate>
            </span>
            <UncontrolledTooltip target="rawPayload">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.rawPayload" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.rawPayload}</dd>
          <dt>
            <span id="rawHash">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.rawHash">Raw Hash</Translate>
            </span>
            <UncontrolledTooltip target="rawHash">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.rawHash" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.rawHash}</dd>
          <dt>
            <span id="profileDetected">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.profileDetected">Profile Detected</Translate>
            </span>
            <UncontrolledTooltip target="profileDetected">
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.help.profileDetected" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobItemEntity.profileDetected}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.uploadJobItem.job">Job</Translate>
          </dt>
          <dd>{uploadJobItemEntity.job ? uploadJobItemEntity.job.jobId : ''}</dd>
        </dl>
        <Button tag={Link} to="/upload-job-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/upload-job-item/${uploadJobItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UploadJobItemDetail;
