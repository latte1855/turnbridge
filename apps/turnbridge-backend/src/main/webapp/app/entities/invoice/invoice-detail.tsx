import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, UncontrolledTooltip } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './invoice.reducer';

export const InvoiceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const invoiceEntity = useAppSelector(state => state.invoice.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="invoiceDetailsHeading">
          <Translate contentKey="turnbridgeBackendApp.invoice.detail.title">Invoice</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{invoiceEntity.id}</dd>
          <dt>
            <span id="invoiceNo">
              <Translate contentKey="turnbridgeBackendApp.invoice.invoiceNo">Invoice No</Translate>
            </span>
            <UncontrolledTooltip target="invoiceNo">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.invoiceNo" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.invoiceNo}</dd>
          <dt>
            <span id="messageFamily">
              <Translate contentKey="turnbridgeBackendApp.invoice.messageFamily">Message Family</Translate>
            </span>
            <UncontrolledTooltip target="messageFamily">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.messageFamily" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.messageFamily}</dd>
          <dt>
            <span id="buyerId">
              <Translate contentKey="turnbridgeBackendApp.invoice.buyerId">Buyer Id</Translate>
            </span>
            <UncontrolledTooltip target="buyerId">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.buyerId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.buyerId}</dd>
          <dt>
            <span id="buyerName">
              <Translate contentKey="turnbridgeBackendApp.invoice.buyerName">Buyer Name</Translate>
            </span>
            <UncontrolledTooltip target="buyerName">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.buyerName" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.buyerName}</dd>
          <dt>
            <span id="sellerId">
              <Translate contentKey="turnbridgeBackendApp.invoice.sellerId">Seller Id</Translate>
            </span>
            <UncontrolledTooltip target="sellerId">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.sellerId" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.sellerId}</dd>
          <dt>
            <span id="sellerName">
              <Translate contentKey="turnbridgeBackendApp.invoice.sellerName">Seller Name</Translate>
            </span>
            <UncontrolledTooltip target="sellerName">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.sellerName" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.sellerName}</dd>
          <dt>
            <span id="salesAmount">
              <Translate contentKey="turnbridgeBackendApp.invoice.salesAmount">Sales Amount</Translate>
            </span>
            <UncontrolledTooltip target="salesAmount">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.salesAmount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.salesAmount}</dd>
          <dt>
            <span id="taxAmount">
              <Translate contentKey="turnbridgeBackendApp.invoice.taxAmount">Tax Amount</Translate>
            </span>
            <UncontrolledTooltip target="taxAmount">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.taxAmount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.taxAmount}</dd>
          <dt>
            <span id="totalAmount">
              <Translate contentKey="turnbridgeBackendApp.invoice.totalAmount">Total Amount</Translate>
            </span>
            <UncontrolledTooltip target="totalAmount">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.totalAmount" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.totalAmount}</dd>
          <dt>
            <span id="taxType">
              <Translate contentKey="turnbridgeBackendApp.invoice.taxType">Tax Type</Translate>
            </span>
            <UncontrolledTooltip target="taxType">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.taxType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.taxType}</dd>
          <dt>
            <span id="normalizedJson">
              <Translate contentKey="turnbridgeBackendApp.invoice.normalizedJson">Normalized Json</Translate>
            </span>
            <UncontrolledTooltip target="normalizedJson">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.normalizedJson" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.normalizedJson}</dd>
          <dt>
            <span id="invoiceStatus">
              <Translate contentKey="turnbridgeBackendApp.invoice.invoiceStatus">Invoice Status</Translate>
            </span>
            <UncontrolledTooltip target="invoiceStatus">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.invoiceStatus" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.invoiceStatus}</dd>
          <dt>
            <span id="issuedAt">
              <Translate contentKey="turnbridgeBackendApp.invoice.issuedAt">Issued At</Translate>
            </span>
            <UncontrolledTooltip target="issuedAt">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.issuedAt" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.issuedAt ? <TextFormat value={invoiceEntity.issuedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="legacyType">
              <Translate contentKey="turnbridgeBackendApp.invoice.legacyType">Legacy Type</Translate>
            </span>
            <UncontrolledTooltip target="legacyType">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.legacyType" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.legacyType}</dd>
          <dt>
            <span id="tbCode">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbCode">Tb Code</Translate>
            </span>
            <UncontrolledTooltip target="tbCode">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbCode}</dd>
          <dt>
            <span id="tbCategory">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbCategory">Tb Category</Translate>
            </span>
            <UncontrolledTooltip target="tbCategory">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbCategory" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbCategory}</dd>
          <dt>
            <span id="tbCanAutoRetry">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbCanAutoRetry">Tb Can Auto Retry</Translate>
            </span>
            <UncontrolledTooltip target="tbCanAutoRetry">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbCanAutoRetry" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbCanAutoRetry ? 'true' : 'false'}</dd>
          <dt>
            <span id="tbRecommendedAction">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbRecommendedAction">Tb Recommended Action</Translate>
            </span>
            <UncontrolledTooltip target="tbRecommendedAction">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbRecommendedAction" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbRecommendedAction}</dd>
          <dt>
            <span id="tbSourceCode">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbSourceCode">Tb Source Code</Translate>
            </span>
            <UncontrolledTooltip target="tbSourceCode">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbSourceCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbSourceCode}</dd>
          <dt>
            <span id="tbSourceMessage">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbSourceMessage">Tb Source Message</Translate>
            </span>
            <UncontrolledTooltip target="tbSourceMessage">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbSourceMessage" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbSourceMessage}</dd>
          <dt>
            <span id="tbResultCode">
              <Translate contentKey="turnbridgeBackendApp.invoice.tbResultCode">Tb Result Code</Translate>
            </span>
            <UncontrolledTooltip target="tbResultCode">
              <Translate contentKey="turnbridgeBackendApp.invoice.help.tbResultCode" />
            </UncontrolledTooltip>
          </dt>
          <dd>{invoiceEntity.tbResultCode}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.invoice.importFile">Import File</Translate>
          </dt>
          <dd>{invoiceEntity.importFile ? invoiceEntity.importFile.originalFilename : ''}</dd>
          <dt>
            <Translate contentKey="turnbridgeBackendApp.invoice.tenant">Tenant</Translate>
          </dt>
          <dd>{invoiceEntity.tenant ? invoiceEntity.tenant.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/invoice" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/invoice/${invoiceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InvoiceDetail;
