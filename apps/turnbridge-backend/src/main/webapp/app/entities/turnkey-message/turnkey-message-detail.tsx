import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './turnkey-message.reducer';

export const TurnkeyMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const turnkeyMessageEntity = useAppSelector(state => state.turnkeyMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="turnkeyMessageDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.detail.title">TurnkeyMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{turnkeyMessageEntity.id}</dd>
          <dt>
            <span id="messageId">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.messageId">Message Id</Translate>
            </span>
            <UncontrolledTooltip target="messageId">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.messageId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{turnkeyMessageEntity.messageId}</dd>
          <dt>
            <span id="messageFamily">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.messageFamily">Message Family</Translate>
            </span>
            <UncontrolledTooltip target="messageFamily">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.messageFamily" />
            </UncontrolledTooltip>
          </dt>
          <dd>{turnkeyMessageEntity.messageFamily}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.type">Type</Translate>
            </span>
            <UncontrolledTooltip target="type">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.type" />
            </UncontrolledTooltip>
          </dt>
          <dd>{turnkeyMessageEntity.type}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.code">Code</Translate>
            </span>
            <UncontrolledTooltip target="code">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.code" />
            </UncontrolledTooltip>
          </dt>
          <dd>{turnkeyMessageEntity.code}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.message">Message</Translate>
            </span>
            <UncontrolledTooltip target="message">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.message" />
            </UncontrolledTooltip>
          </dt>
          <dd>{turnkeyMessageEntity.message}</dd>
          <dt>
            <span id="payloadPath">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.payloadPath">Payload Path</Translate>
            </span>
            <UncontrolledTooltip target="payloadPath">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.payloadPath" />
            </UncontrolledTooltip>
          </dt>
          <dd>{turnkeyMessageEntity.payloadPath}</dd>
          <dt>
            <span id="receivedAt">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.receivedAt">Received At</Translate>
            </span>
            <UncontrolledTooltip target="receivedAt">
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.help.receivedAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {turnkeyMessageEntity.receivedAt ? (
              <TextFormat value={turnkeyMessageEntity.receivedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.invoice">Invoice</Translate>
          </dt>
          <dd>{turnkeyMessageEntity.invoice ? turnkeyMessageEntity.invoice.invoiceNo : ''}</dd>
        </dl>
        <Button tag={Link} to="/turnkey-message" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/turnkey-message/${turnkeyMessageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TurnkeyMessageDetail;
