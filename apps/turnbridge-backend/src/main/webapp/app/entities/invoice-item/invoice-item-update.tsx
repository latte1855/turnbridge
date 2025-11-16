import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row, UncontrolledTooltip } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInvoices } from 'app/entities/invoice/invoice.reducer';
import { createEntity, getEntity, reset, updateEntity } from './invoice-item.reducer';

export const InvoiceItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const invoices = useAppSelector(state => state.invoice.entities);
  const invoiceItemEntity = useAppSelector(state => state.invoiceItem.entity);
  const loading = useAppSelector(state => state.invoiceItem.loading);
  const updating = useAppSelector(state => state.invoiceItem.updating);
  const updateSuccess = useAppSelector(state => state.invoiceItem.updateSuccess);

  const handleClose = () => {
    navigate(`/invoice-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInvoices({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    if (values.unitPrice !== undefined && typeof values.unitPrice !== 'number') {
      values.unitPrice = Number(values.unitPrice);
    }
    if (values.amount !== undefined && typeof values.amount !== 'number') {
      values.amount = Number(values.amount);
    }
    if (values.sequence !== undefined && typeof values.sequence !== 'number') {
      values.sequence = Number(values.sequence);
    }

    const entity = {
      ...invoiceItemEntity,
      ...values,
      invoice: invoices.find(it => it.id.toString() === values.invoice?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...invoiceItemEntity,
          invoice: invoiceItemEntity?.invoice?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="turnbridgeBackendApp.invoiceItem.home.createOrEditLabel" data-cy="InvoiceItemCreateUpdateHeading">
            <Translate contentKey="turnbridgeBackendApp.invoiceItem.home.createOrEditLabel">Create or edit a InvoiceItem</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="invoice-item-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceItem.description')}
                id="invoice-item-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 256, message: translate('entity.validation.maxlength', { max: 256 }) },
                }}
              />
              <UncontrolledTooltip target="descriptionLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.description" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceItem.quantity')}
                id="invoice-item-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
              />
              <UncontrolledTooltip target="quantityLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.quantity" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceItem.unitPrice')}
                id="invoice-item-unitPrice"
                name="unitPrice"
                data-cy="unitPrice"
                type="text"
              />
              <UncontrolledTooltip target="unitPriceLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.unitPrice" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceItem.amount')}
                id="invoice-item-amount"
                name="amount"
                data-cy="amount"
                type="text"
              />
              <UncontrolledTooltip target="amountLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.amount" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('turnbridgeBackendApp.invoiceItem.sequence')}
                id="invoice-item-sequence"
                name="sequence"
                data-cy="sequence"
                type="text"
              />
              <UncontrolledTooltip target="sequenceLabel">
                <Translate contentKey="turnbridgeBackendApp.invoiceItem.help.sequence" />
              </UncontrolledTooltip>
              <ValidatedField
                id="invoice-item-invoice"
                name="invoice"
                data-cy="invoice"
                label={translate('turnbridgeBackendApp.invoiceItem.invoice')}
                type="select"
                required
              >
                <option value="" key="0" />
                {invoices
                  ? invoices.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.invoiceNo}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/invoice-item" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default InvoiceItemUpdate;
