import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './webhook-endpoint.reducer';

export const WebhookEndpointDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const webhookEndpointEntity = useAppSelector(state => state.webhookEndpoint.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="webhookEndpointDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.detail.title">WebhookEndpoint</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{webhookEndpointEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.name">Name</Translate>
            </span>
            <UncontrolledTooltip target="name">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.name" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookEndpointEntity.name}</dd>
          <dt>
            <span id="targetUrl">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.targetUrl">Target Url</Translate>
            </span>
            <UncontrolledTooltip target="targetUrl">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.targetUrl" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookEndpointEntity.targetUrl}</dd>
          <dt>
            <span id="secret">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.secret">Secret</Translate>
            </span>
            <UncontrolledTooltip target="secret">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.secret" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookEndpointEntity.secret}</dd>
          <dt>
            <span id="events">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.events">Events</Translate>
            </span>
            <UncontrolledTooltip target="events">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.events" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookEndpointEntity.events}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookEndpointEntity.status}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.tenant">Tenant</Translate>
          </dt>
          <dd>{webhookEndpointEntity.tenant ? webhookEndpointEntity.tenant.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/webhook-endpoint" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/webhook-endpoint/${webhookEndpointEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default WebhookEndpointDetail;
