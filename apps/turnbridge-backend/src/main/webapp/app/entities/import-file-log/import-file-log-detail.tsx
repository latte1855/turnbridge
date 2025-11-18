import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './import-file-log.reducer';

export const ImportFileLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const importFileLogEntity = useAppSelector(state => state.importFileLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="importFileLogDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.importFileLog.detail.title">ImportFileLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{importFileLogEntity.id}</dd>
          <dt>
            <span id="eventCode">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.eventCode">Event Code</Translate>
            </span>
            <UncontrolledTooltip target="eventCode">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.eventCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.eventCode}</dd>
          <dt>
            <span id="level">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.level">Level</Translate>
            </span>
            <UncontrolledTooltip target="level">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.level" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.level}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.message">Message</Translate>
            </span>
            <UncontrolledTooltip target="message">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.message" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.message}</dd>
          <dt>
            <span id="detail">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.detail">Detail</Translate>
            </span>
            <UncontrolledTooltip target="detail">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.detail" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.detail}</dd>
          <dt>
            <span id="occurredAt">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.occurredAt">Occurred At</Translate>
            </span>
            <UncontrolledTooltip target="occurredAt">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.occurredAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {importFileLogEntity.occurredAt ? (
              <TextFormat value={importFileLogEntity.occurredAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.importFileLog.importFile">Import File</Translate>
          </dt>
          <dd>{importFileLogEntity.importFile ? importFileLogEntity.importFile.originalFilename : ''}</dd>
        </dl>
        <Button tag={Link} to="/import-file-log" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/import-file-log/${importFileLogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ImportFileLogDetail;
