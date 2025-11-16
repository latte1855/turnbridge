import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tenant.reducer';

export const TenantDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const tenantEntity = useAppSelector(state => state.tenant.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tenantDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.tenant.detail.title">Tenant</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{tenantEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="turnbridgeBackendApp.tenant.name">Name</Translate>
            </span>
            <UncontrolledTooltip target="name">
              <Translate contentKey="turnbridgeBackendApp.tenant.help.name" />
            </UncontrolledTooltip>
          </dt>
          <dd>{tenantEntity.name}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="turnbridgeBackendApp.tenant.code">Code</Translate>
            </span>
            <UncontrolledTooltip target="code">
              <Translate contentKey="turnbridgeBackendApp.tenant.help.code" />
            </UncontrolledTooltip>
          </dt>
          <dd>{tenantEntity.code}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.tenant.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.tenant.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{tenantEntity.status}</dd>
        </dl>
        <Button tag={Link} to="/tenant" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tenant/${tenantEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TenantDetail;
