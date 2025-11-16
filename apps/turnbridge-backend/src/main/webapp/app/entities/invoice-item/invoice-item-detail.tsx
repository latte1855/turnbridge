import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './invoice-item.reducer';

export const InvoiceItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const invoiceItemEntity = useAppSelector(state => state.invoiceItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="invoiceItemDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.invoiceItem.detail.title">InvoiceItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{invoiceItemEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.description">Description</Translate>
            </span>
            <UncontrolledTooltip target="description">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.description" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceItemEntity.description}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.quantity">Quantity</Translate>
            </span>
            <UncontrolledTooltip target="quantity">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.quantity" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceItemEntity.quantity}</dd>
          <dt>
            <span id="unitPrice">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.unitPrice">Unit Price</Translate>
            </span>
            <UncontrolledTooltip target="unitPrice">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.unitPrice" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceItemEntity.unitPrice}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.amount">Amount</Translate>
            </span>
            <UncontrolledTooltip target="amount">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.amount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceItemEntity.amount}</dd>
          <dt>
            <span id="sequence">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.sequence">Sequence</Translate>
            </span>
            <UncontrolledTooltip target="sequence">
              <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.sequence" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceItemEntity.sequence}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.invoiceItem.invoice">Invoice</Translate>
          </dt>
          <dd>{invoiceItemEntity.invoice ? invoiceItemEntity.invoice.invoiceNo : ''}</dd>
        </dl>
        <Button tag={Link} to="/invoice-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/invoice-item/${invoiceItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InvoiceItemDetail;
