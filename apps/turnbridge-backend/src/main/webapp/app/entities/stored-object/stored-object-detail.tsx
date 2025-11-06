import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './stored-object.reducer';

export const StoredObjectDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const storedObjectEntity = useAppSelector(state => state.storedObject.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="storedObjectDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.storedObject.detail.title">StoredObject</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{storedObjectEntity.id}</dd>
          <dt>
            <span id="bucket">
              <Translate contentKey="turnbridgeBackendApp.storedObject.bucket">Bucket</Translate>
            </span>
            <UncontrolledTooltip target="bucket">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.bucket" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.bucket}</dd>
          <dt>
            <span id="objectKey">
              <Translate contentKey="turnbridgeBackendApp.storedObject.objectKey">Object Key</Translate>
            </span>
            <UncontrolledTooltip target="objectKey">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.objectKey" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.objectKey}</dd>
          <dt>
            <span id="mediaType">
              <Translate contentKey="turnbridgeBackendApp.storedObject.mediaType">Media Type</Translate>
            </span>
            <UncontrolledTooltip target="mediaType">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.mediaType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.mediaType}</dd>
          <dt>
            <span id="contentLength">
              <Translate contentKey="turnbridgeBackendApp.storedObject.contentLength">Content Length</Translate>
            </span>
            <UncontrolledTooltip target="contentLength">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.contentLength" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.contentLength}</dd>
          <dt>
            <span id="sha256">
              <Translate contentKey="turnbridgeBackendApp.storedObject.sha256">Sha 256</Translate>
            </span>
            <UncontrolledTooltip target="sha256">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.sha256" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.sha256}</dd>
          <dt>
            <span id="purpose">
              <Translate contentKey="turnbridgeBackendApp.storedObject.purpose">Purpose</Translate>
            </span>
            <UncontrolledTooltip target="purpose">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.purpose" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.purpose}</dd>
          <dt>
            <span id="filename">
              <Translate contentKey="turnbridgeBackendApp.storedObject.filename">Filename</Translate>
            </span>
            <UncontrolledTooltip target="filename">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.filename" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.filename}</dd>
          <dt>
            <span id="storageClass">
              <Translate contentKey="turnbridgeBackendApp.storedObject.storageClass">Storage Class</Translate>
            </span>
            <UncontrolledTooltip target="storageClass">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.storageClass" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.storageClass}</dd>
          <dt>
            <span id="encryption">
              <Translate contentKey="turnbridgeBackendApp.storedObject.encryption">Encryption</Translate>
            </span>
            <UncontrolledTooltip target="encryption">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.encryption" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.encryption}</dd>
          <dt>
            <span id="metadata">
              <Translate contentKey="turnbridgeBackendApp.storedObject.metadata">Metadata</Translate>
            </span>
            <UncontrolledTooltip target="metadata">
              <Translate contentKey="turnbridgeBackendApp.storedObject.help.metadata" />
            </UncontrolledTooltip>
          </dt>
          <dd>{storedObjectEntity.metadata}</dd>
        </dl>
        <Button tag={Link} to="/stored-object" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/stored-object/${storedObjectEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StoredObjectDetail;
