import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './import-file.reducer';

export const ImportFileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const importFileEntity = useAppSelector(state => state.importFile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="importFileDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.importFile.detail.title">ImportFile</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{importFileEntity.id}</dd>
          <dt>
            <span id="importType">
              <Translate contentKey="turnbridgeBackendApp.importFile.importType">Import Type</Translate>
            </span>
            <UncontrolledTooltip target="importType">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.importType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.importType}</dd>
          <dt>
            <span id="originalFilename">
              <Translate contentKey="turnbridgeBackendApp.importFile.originalFilename">Original Filename</Translate>
            </span>
            <UncontrolledTooltip target="originalFilename">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.originalFilename" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.originalFilename}</dd>
          <dt>
            <span id="sha256">
              <Translate contentKey="turnbridgeBackendApp.importFile.sha256">Sha 256</Translate>
            </span>
            <UncontrolledTooltip target="sha256">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.sha256" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.sha256}</dd>
          <dt>
            <span id="totalCount">
              <Translate contentKey="turnbridgeBackendApp.importFile.totalCount">Total Count</Translate>
            </span>
            <UncontrolledTooltip target="totalCount">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.totalCount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.totalCount}</dd>
          <dt>
            <span id="successCount">
              <Translate contentKey="turnbridgeBackendApp.importFile.successCount">Success Count</Translate>
            </span>
            <UncontrolledTooltip target="successCount">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.successCount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.successCount}</dd>
          <dt>
            <span id="errorCount">
              <Translate contentKey="turnbridgeBackendApp.importFile.errorCount">Error Count</Translate>
            </span>
            <UncontrolledTooltip target="errorCount">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.errorCount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.errorCount}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.importFile.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.status}</dd>
          <dt>
            <span id="legacyType">
              <Translate contentKey="turnbridgeBackendApp.importFile.legacyType">Legacy Type</Translate>
            </span>
            <UncontrolledTooltip target="legacyType">
              <Translate contentKey="turnbridgeBackendApp.importFile.help.legacyType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileEntity.legacyType}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.importFile.tenant">Tenant</Translate>
          </dt>
          <dd>{importFileEntity.tenant ? importFileEntity.tenant.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/import-file" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/import-file/${importFileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ImportFileDetail;
