import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

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
            <span id="lineIndex">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.lineIndex">Line Index</Translate>
            </span>
            <UncontrolledTooltip target="lineIndex">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.lineIndex" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.lineIndex}</dd>
          <dt>
            <span id="field">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.field">Field</Translate>
            </span>
            <UncontrolledTooltip target="field">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.field" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.field}</dd>
          <dt>
            <span id="errorCode">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.errorCode">Error Code</Translate>
            </span>
            <UncontrolledTooltip target="errorCode">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.errorCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.errorCode}</dd>
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
            <span id="rawLine">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.rawLine">Raw Line</Translate>
            </span>
            <UncontrolledTooltip target="rawLine">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.rawLine" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.rawLine}</dd>
          <dt>
            <span id="sourceFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.sourceFamily">Source Family</Translate>
            </span>
            <UncontrolledTooltip target="sourceFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.sourceFamily" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.sourceFamily}</dd>
          <dt>
            <span id="normalizedFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.normalizedFamily">Normalized Family</Translate>
            </span>
            <UncontrolledTooltip target="normalizedFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileLog.help.normalizedFamily" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileLogEntity.normalizedFamily}</dd>
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
