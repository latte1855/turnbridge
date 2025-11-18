import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './import-file-item.reducer';

export const ImportFileItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const importFileItemEntity = useAppSelector(state => state.importFileItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="importFileItemDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.importFileItem.detail.title">ImportFileItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{importFileItemEntity.id}</dd>
          <dt>
            <span id="lineIndex">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.lineIndex">Line Index</Translate>
            </span>
            <UncontrolledTooltip target="lineIndex">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.lineIndex" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.lineIndex}</dd>
          <dt>
            <span id="rawData">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.rawData">Raw Data</Translate>
            </span>
            <UncontrolledTooltip target="rawData">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.rawData" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.rawData}</dd>
          <dt>
            <span id="rawHash">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.rawHash">Raw Hash</Translate>
            </span>
            <UncontrolledTooltip target="rawHash">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.rawHash" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.rawHash}</dd>
          <dt>
            <span id="sourceFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.sourceFamily">Source Family</Translate>
            </span>
            <UncontrolledTooltip target="sourceFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.sourceFamily" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.sourceFamily}</dd>
          <dt>
            <span id="normalizedFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.normalizedFamily">Normalized Family</Translate>
            </span>
            <UncontrolledTooltip target="normalizedFamily">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.normalizedFamily" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.normalizedFamily}</dd>
          <dt>
            <span id="normalizedJson">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.normalizedJson">Normalized Json</Translate>
            </span>
            <UncontrolledTooltip target="normalizedJson">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.normalizedJson" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.normalizedJson}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.status">Status</Translate>
            </span>
            <UncontrolledTooltip target="status">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.status" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.status}</dd>
          <dt>
            <span id="errorCode">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.errorCode">Error Code</Translate>
            </span>
            <UncontrolledTooltip target="errorCode">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.errorCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.errorCode}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.errorMessage">Error Message</Translate>
            </span>
            <UncontrolledTooltip target="errorMessage">
              <Translate contentKey="turnbridgeBackendApp.importFileItem.help.errorMessage" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemEntity.errorMessage}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.importFileItem.importFile">Import File</Translate>
          </dt>
          <dd>{importFileItemEntity.importFile ? importFileItemEntity.importFile.originalFilename : ''}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.importFileItem.invoice">Invoice</Translate>
          </dt>
          <dd>{importFileItemEntity.invoice ? importFileItemEntity.invoice.invoiceNo : ''}</dd>
        </dl>
        <Button tag={Link} to="/import-file-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/import-file-item/${importFileItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ImportFileItemDetail;
