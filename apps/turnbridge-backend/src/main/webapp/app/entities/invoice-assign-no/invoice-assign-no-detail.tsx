import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './invoice-assign-no.reducer';

export const InvoiceAssignNoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const invoiceAssignNoEntity = useAppSelector(state => state.invoiceAssignNo.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="invoiceAssignNoDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.detail.title">InvoiceAssignNo</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{invoiceAssignNoEntity.id}</dd>
          <dt>
            <span id="track">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.track">Track</Translate>
            </span>
            <UncontrolledTooltip target="track">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.track" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.track}</dd>
          <dt>
            <span id="period">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.period">Period</Translate>
            </span>
            <UncontrolledTooltip target="period">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.period" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.period}</dd>
          <dt>
            <span id="fromNo">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.fromNo">From No</Translate>
            </span>
            <UncontrolledTooltip target="fromNo">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.fromNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.fromNo}</dd>
          <dt>
            <span id="toNo">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.toNo">To No</Translate>
            </span>
            <UncontrolledTooltip target="toNo">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.toNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.toNo}</dd>
          <dt>
            <span id="usedCount">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.usedCount">Used Count</Translate>
            </span>
            <UncontrolledTooltip target="usedCount">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.usedCount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.usedCount}</dd>
          <dt>
            <span id="rollSize">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.rollSize">Roll Size</Translate>
            </span>
            <UncontrolledTooltip target="rollSize">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.rollSize" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.rollSize}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceAssignNoEntity.status}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.tenant">Tenant</Translate>
          </dt>
          <dd>{invoiceAssignNoEntity.tenant ? invoiceAssignNoEntity.tenant.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/invoice-assign-no" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/invoice-assign-no/${invoiceAssignNoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InvoiceAssignNoDetail;
