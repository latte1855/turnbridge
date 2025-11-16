import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './manual-action.reducer';

export const ManualActionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const manualActionEntity = useAppSelector(state => state.manualAction.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="manualActionDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.manualAction.detail.title">ManualAction</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{manualActionEntity.id}</dd>
          <dt>
            <span id="actionType">
              <Translate contentKey="turnbridgeBackendApp.manualAction.actionType">Action Type</Translate>
            </span>
            <UncontrolledTooltip target="actionType">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.actionType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{manualActionEntity.actionType}</dd>
          <dt>
            <span id="reason">
              <Translate contentKey="turnbridgeBackendApp.manualAction.reason">Reason</Translate>
            </span>
            <UncontrolledTooltip target="reason">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.reason" />
            </UncontrolledTooltip>
          </dt>
          <dd>{manualActionEntity.reason}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.manualAction.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{manualActionEntity.status}</dd>
          <dt>
            <span id="requestedBy">
              <Translate contentKey="turnbridgeBackendApp.manualAction.requestedBy">Requested By</Translate>
            </span>
            <UncontrolledTooltip target="requestedBy">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.requestedBy" />
            </UncontrolledTooltip>
          </dt>
          <dd>{manualActionEntity.requestedBy}</dd>
          <dt>
            <span id="requestedAt">
              <Translate contentKey="turnbridgeBackendApp.manualAction.requestedAt">Requested At</Translate>
            </span>
            <UncontrolledTooltip target="requestedAt">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.requestedAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {manualActionEntity.requestedAt ? (
              <TextFormat value={manualActionEntity.requestedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="approvedBy">
              <Translate contentKey="turnbridgeBackendApp.manualAction.approvedBy">Approved By</Translate>
            </span>
            <UncontrolledTooltip target="approvedBy">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.approvedBy" />
            </UncontrolledTooltip>
          </dt>
          <dd>{manualActionEntity.approvedBy}</dd>
          <dt>
            <span id="approvedAt">
              <Translate contentKey="turnbridgeBackendApp.manualAction.approvedAt">Approved At</Translate>
            </span>
            <UncontrolledTooltip target="approvedAt">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.approvedAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {manualActionEntity.approvedAt ? (
              <TextFormat value={manualActionEntity.approvedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="resultMessage">
              <Translate contentKey="turnbridgeBackendApp.manualAction.resultMessage">Result Message</Translate>
            </span>
            <UncontrolledTooltip target="resultMessage">
              <Translate contentKey="turnbridgeBackendApp.manualAction.help.resultMessage" />
            </UncontrolledTooltip>
          </dt>
          <dd>{manualActionEntity.resultMessage}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.manualAction.tenant">Tenant</Translate>
          </dt>
          <dd>{manualActionEntity.tenant ? manualActionEntity.tenant.name : ''}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.manualAction.invoice">Invoice</Translate>
          </dt>
          <dd>{manualActionEntity.invoice ? manualActionEntity.invoice.invoiceNo : ''}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.manualAction.importFile">Import File</Translate>
          </dt>
          <dd>{manualActionEntity.importFile ? manualActionEntity.importFile.originalFilename : ''}</dd>
        </dl>
        <Button tag={Link} to="/manual-action" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/manual-action/${manualActionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ManualActionDetail;
