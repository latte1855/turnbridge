import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './upload-job.reducer';

export const UploadJobDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const uploadJobEntity = useAppSelector(state => state.uploadJob.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="uploadJobDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.uploadJob.detail.title">UploadJob</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{uploadJobEntity.id}</dd>
          <dt>
            <span id="jobId">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.jobId">Job Id</Translate>
            </span>
            <UncontrolledTooltip target="jobId">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.jobId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.jobId}</dd>
          <dt>
            <span id="sellerId">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.sellerId">Seller Id</Translate>
            </span>
            <UncontrolledTooltip target="sellerId">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sellerId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.sellerId}</dd>
          <dt>
            <span id="sellerName">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.sellerName">Seller Name</Translate>
            </span>
            <UncontrolledTooltip target="sellerName">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sellerName" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.sellerName}</dd>
          <dt>
            <span id="period">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.period">Period</Translate>
            </span>
            <UncontrolledTooltip target="period">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.period" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.period}</dd>
          <dt>
            <span id="profile">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.profile">Profile</Translate>
            </span>
            <UncontrolledTooltip target="profile">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.profile" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.profile}</dd>
          <dt>
            <span id="sourceFilename">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.sourceFilename">Source Filename</Translate>
            </span>
            <UncontrolledTooltip target="sourceFilename">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sourceFilename" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.sourceFilename}</dd>
          <dt>
            <span id="sourceMediaType">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.sourceMediaType">Source Media Type</Translate>
            </span>
            <UncontrolledTooltip target="sourceMediaType">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sourceMediaType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.sourceMediaType}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.status}</dd>
          <dt>
            <span id="total">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.total">Total</Translate>
            </span>
            <UncontrolledTooltip target="total">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.total" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.total}</dd>
          <dt>
            <span id="accepted">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.accepted">Accepted</Translate>
            </span>
            <UncontrolledTooltip target="accepted">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.accepted" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.accepted}</dd>
          <dt>
            <span id="failed">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.failed">Failed</Translate>
            </span>
            <UncontrolledTooltip target="failed">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.failed" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.failed}</dd>
          <dt>
            <span id="sent">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.sent">Sent</Translate>
            </span>
            <UncontrolledTooltip target="sent">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.sent" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.sent}</dd>
          <dt>
            <span id="remark">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.remark">Remark</Translate>
            </span>
            <UncontrolledTooltip target="remark">
              <Translate contentKey="turnbridgeBackendApp.uploadJob.help.remark" />
            </UncontrolledTooltip>
          </dt>
          <dd>{uploadJobEntity.remark}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.uploadJob.originalFile">Original File</Translate>
          </dt>
          <dd>{uploadJobEntity.originalFile ? uploadJobEntity.originalFile.id : ''}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.uploadJob.resultFile">Result File</Translate>
          </dt>
          <dd>{uploadJobEntity.resultFile ? uploadJobEntity.resultFile.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/upload-job" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/upload-job/${uploadJobEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UploadJobDetail;
