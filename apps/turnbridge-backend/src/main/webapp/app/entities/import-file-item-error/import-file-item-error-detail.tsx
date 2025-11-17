import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './import-file-item-error.reducer';

export const ImportFileItemErrorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const importFileItemErrorEntity = useAppSelector(state => state.importFileItemError.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="importFileItemErrorDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.importFileItemError.detail.title">ImportFileItemError</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{importFileItemErrorEntity.id}</dd>
          <dt>
            <span id="columnIndex">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.columnIndex">Column Index</Translate>
            </span>
            <UncontrolledTooltip target="columnIndex">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.columnIndex" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemErrorEntity.columnIndex}</dd>
          <dt>
            <span id="fieldName">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.fieldName">Field Name</Translate>
            </span>
            <UncontrolledTooltip target="fieldName">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.fieldName" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemErrorEntity.fieldName}</dd>
          <dt>
            <span id="errorCode">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.errorCode">Error Code</Translate>
            </span>
            <UncontrolledTooltip target="errorCode">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.errorCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemErrorEntity.errorCode}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.message">Message</Translate>
            </span>
            <UncontrolledTooltip target="message">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.message" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemErrorEntity.message}</dd>
          <dt>
            <span id="severity">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.severity">Severity</Translate>
            </span>
            <UncontrolledTooltip target="severity">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.severity" />
            </UncontrolledTooltip>
          </dt>
          <dd>{importFileItemErrorEntity.severity}</dd>
          <dt>
            <span id="occurredAt">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.occurredAt">Occurred At</Translate>
            </span>
            <UncontrolledTooltip target="occurredAt">
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.help.occurredAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>
            {importFileItemErrorEntity.occurredAt ? (
              <TextFormat value={importFileItemErrorEntity.occurredAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.importFileItemError.importFileItem">Import File Item</Translate>
          </dt>
          <dd>{importFileItemErrorEntity.importFileItem ? importFileItemErrorEntity.importFileItem.lineIndex : ''}</dd>
        </dl>
        <Button tag={Link} to="/import-file-item-error" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/import-file-item-error/${importFileItemErrorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ImportFileItemErrorDetail;
