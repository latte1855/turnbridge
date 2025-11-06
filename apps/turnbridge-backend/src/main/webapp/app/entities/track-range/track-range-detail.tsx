import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './track-range.reducer';

export const TrackRangeDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const trackRangeEntity = useAppSelector(state => state.trackRange.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="trackRangeDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.trackRange.detail.title">TrackRange</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{trackRangeEntity.id}</dd>
          <dt>
            <span id="sellerId">
              <Translate contentKey="turnbridgeBackendApp.trackRange.sellerId">Seller Id</Translate>
            </span>
            <UncontrolledTooltip target="sellerId">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.sellerId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.sellerId}</dd>
          <dt>
            <span id="period">
              <Translate contentKey="turnbridgeBackendApp.trackRange.period">Period</Translate>
            </span>
            <UncontrolledTooltip target="period">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.period" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.period}</dd>
          <dt>
            <span id="prefix">
              <Translate contentKey="turnbridgeBackendApp.trackRange.prefix">Prefix</Translate>
            </span>
            <UncontrolledTooltip target="prefix">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.prefix" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.prefix}</dd>
          <dt>
            <span id="startNo">
              <Translate contentKey="turnbridgeBackendApp.trackRange.startNo">Start No</Translate>
            </span>
            <UncontrolledTooltip target="startNo">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.startNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.startNo}</dd>
          <dt>
            <span id="endNo">
              <Translate contentKey="turnbridgeBackendApp.trackRange.endNo">End No</Translate>
            </span>
            <UncontrolledTooltip target="endNo">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.endNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.endNo}</dd>
          <dt>
            <span id="currentNo">
              <Translate contentKey="turnbridgeBackendApp.trackRange.currentNo">Current No</Translate>
            </span>
            <UncontrolledTooltip target="currentNo">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.currentNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.currentNo}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.trackRange.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.status}</dd>
          <dt>
            <span id="version">
              <Translate contentKey="turnbridgeBackendApp.trackRange.version">Version</Translate>
            </span>
            <UncontrolledTooltip target="version">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.version" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.version}</dd>
          <dt>
            <span id="lockOwner">
              <Translate contentKey="turnbridgeBackendApp.trackRange.lockOwner">Lock Owner</Translate>
            </span>
            <UncontrolledTooltip target="lockOwner">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.lockOwner" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.lockOwner}</dd>
          <dt>
            <span id="lockAt">
              <Translate contentKey="turnbridgeBackendApp.trackRange.lockAt">Lock At</Translate>
            </span>
            <UncontrolledTooltip target="lockAt">
              <Translate contentKey="turnbridgeBackendApp.trackRange.help.lockAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>{trackRangeEntity.lockAt ? <TextFormat value={trackRangeEntity.lockAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/track-range" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/track-range/${trackRangeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TrackRangeDetail;
