import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './webhook-delivery-log.reducer';

export const WebhookDeliveryLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const webhookDeliveryLogEntity = useAppSelector(state => state.webhookDeliveryLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="webhookDeliveryLogDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.detail.title">WebhookDeliveryLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{webhookDeliveryLogEntity.id}</dd>
          <dt>
            <span id="deliveryId">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.deliveryId">Delivery Id</Translate>
            </span>
            <UncontrolledTooltip target="deliveryId">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.deliveryId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.deliveryId}</dd>
          <dt>
            <span id="event">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.event">Event</Translate>
            </span>
            <UncontrolledTooltip target="event">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.event" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.event}</dd>
          <dt>
            <span id="payload">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.payload">Payload</Translate>
            </span>
            <UncontrolledTooltip target="payload">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.payload" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.payload}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.status}</dd>
          <dt>
            <span id="httpStatus">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.httpStatus">Http Status</Translate>
            </span>
            <UncontrolledTooltip target="httpStatus">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.httpStatus" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.httpStatus}</dd>
          <dt>
            <span id="attempts">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.attempts">Attempts</Translate>
            </span>
            <UncontrolledTooltip target="attempts">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.attempts" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.attempts}</dd>
          <dt>
            <span id="lastError">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.lastError">Last Error</Translate>
            </span>
            <UncontrolledTooltip target="lastError">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.lastError" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.lastError}</dd>
          <dt>
            <span id="deliveredAt">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.deliveredAt">Delivered At</Translate>
            </span>
            <UncontrolledTooltip target="deliveredAt">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.deliveredAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {webhookDeliveryLogEntity.deliveredAt ? (
              <TextFormat value={webhookDeliveryLogEntity.deliveredAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="nextAttemptAt">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.nextAttemptAt">Next Attempt At</Translate>
            </span>
            <UncontrolledTooltip target="nextAttemptAt">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.nextAttemptAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {webhookDeliveryLogEntity.nextAttemptAt ? (
              <TextFormat value={webhookDeliveryLogEntity.nextAttemptAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lockedAt">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.lockedAt">Locked At</Translate>
            </span>
            <UncontrolledTooltip target="lockedAt">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.lockedAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {webhookDeliveryLogEntity.lockedAt ? (
              <TextFormat value={webhookDeliveryLogEntity.lockedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="dlqReason">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.dlqReason">Dlq Reason</Translate>
            </span>
            <UncontrolledTooltip target="dlqReason">
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.help.dlqReason" />
            </UncontrolledTooltip>
          </dt>
          <dd>{webhookDeliveryLogEntity.dlqReason}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.webhookEndpoint">Webhook Endpoint</Translate>
          </dt>
          <dd>{webhookDeliveryLogEntity.webhookEndpoint ? webhookDeliveryLogEntity.webhookEndpoint.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/webhook-delivery-log" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/webhook-delivery-log/${webhookDeliveryLogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default WebhookDeliveryLogDetail;
