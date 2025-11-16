import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './invoice.reducer';

export const Invoice = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const invoiceList = useAppSelector(state => state.invoice.entities);
  const loading = useAppSelector(state => state.invoice.loading);
  const totalItems = useAppSelector(state => state.invoice.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="invoice-heading" data-cy="InvoiceHeading">
        <Translate contentKey="turnbridgeBackendApp.invoice.home.title">Invoices</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.invoice.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/invoice/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.invoice.home.createLabel">Create new Invoice</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {invoiceList && invoiceList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('invoiceNo')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.invoiceNo">Invoice No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('invoiceNo')} />
                </th>
                <th className="hand" onClick={sort('messageFamily')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.messageFamily">Message Family</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('messageFamily')} />
                </th>
                <th className="hand" onClick={sort('buyerId')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.buyerId">Buyer Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('buyerId')} />
                </th>
                <th className="hand" onClick={sort('buyerName')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.buyerName">Buyer Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('buyerName')} />
                </th>
                <th className="hand" onClick={sort('sellerId')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.sellerId">Seller Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sellerId')} />
                </th>
                <th className="hand" onClick={sort('sellerName')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.sellerName">Seller Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sellerName')} />
                </th>
                <th className="hand" onClick={sort('salesAmount')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.salesAmount">Sales Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('salesAmount')} />
                </th>
                <th className="hand" onClick={sort('taxAmount')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.taxAmount">Tax Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taxAmount')} />
                </th>
                <th className="hand" onClick={sort('totalAmount')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.totalAmount">Total Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalAmount')} />
                </th>
                <th className="hand" onClick={sort('taxType')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.taxType">Tax Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taxType')} />
                </th>
                <th className="hand" onClick={sort('normalizedJson')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.normalizedJson">Normalized Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('normalizedJson')} />
                </th>
                <th className="hand" onClick={sort('originalPayload')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.originalPayload">Original Payload</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('originalPayload')} />
                </th>
                <th className="hand" onClick={sort('invoiceStatus')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.invoiceStatus">Invoice Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('invoiceStatus')} />
                </th>
                <th className="hand" onClick={sort('issuedAt')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.issuedAt">Issued At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('issuedAt')} />
                </th>
                <th className="hand" onClick={sort('legacyType')}>
                  <Translate contentKey="turnbridgeBackendApp.invoice.legacyType">Legacy Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('legacyType')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.invoice.importFile">Import File</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.invoice.tenant">Tenant</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {invoiceList.map((invoice, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/invoice/${invoice.id}`} color="link" size="sm">
                      {invoice.id}
                    </Button>
                  </td>
                  <td>{invoice.invoiceNo}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.MessageFamily.${invoice.messageFamily}`} />
                  </td>
                  <td>{invoice.buyerId}</td>
                  <td>{invoice.buyerName}</td>
                  <td>{invoice.sellerId}</td>
                  <td>{invoice.sellerName}</td>
                  <td>{invoice.salesAmount}</td>
                  <td>{invoice.taxAmount}</td>
                  <td>{invoice.totalAmount}</td>
                  <td>{invoice.taxType}</td>
                  <td>{invoice.normalizedJson}</td>
                  <td>{invoice.originalPayload}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.InvoiceStatus.${invoice.invoiceStatus}`} />
                  </td>
                  <td>{invoice.issuedAt ? <TextFormat type="date" value={invoice.issuedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{invoice.legacyType}</td>
                  <td>
                    {invoice.importFile ? (
                      <Link to={`/import-file/${invoice.importFile.id}`}>{invoice.importFile.originalFilename}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{invoice.tenant ? <Link to={`/tenant/${invoice.tenant.id}`}>{invoice.tenant.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/invoice/${invoice.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/invoice/${invoice.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/invoice/${invoice.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="turnbridgeBackendApp.invoice.home.notFound">No Invoices found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={invoiceList && invoiceList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Invoice;
