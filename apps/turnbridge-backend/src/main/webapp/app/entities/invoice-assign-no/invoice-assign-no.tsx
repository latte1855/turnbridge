import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './invoice-assign-no.reducer';

export const InvoiceAssignNo = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const invoiceAssignNoList = useAppSelector(state => state.invoiceAssignNo.entities);
  const loading = useAppSelector(state => state.invoiceAssignNo.loading);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        sort: `${sortState.sort},${sortState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="invoice-assign-no-heading" data-cy="InvoiceAssignNoHeading">
        <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.home.title">Invoice Assign Nos</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/invoice-assign-no/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.home.createLabel">Create new Invoice Assign No</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {invoiceAssignNoList && invoiceAssignNoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('track')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.track">Track</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('track')} />
                </th>
                <th className="hand" onClick={sort('period')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.period">Period</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('period')} />
                </th>
                <th className="hand" onClick={sort('fromNo')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.fromNo">From No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fromNo')} />
                </th>
                <th className="hand" onClick={sort('toNo')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.toNo">To No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('toNo')} />
                </th>
                <th className="hand" onClick={sort('usedCount')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.usedCount">Used Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('usedCount')} />
                </th>
                <th className="hand" onClick={sort('rollSize')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.rollSize">Roll Size</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rollSize')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.tenant">Tenant</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {invoiceAssignNoList.map((invoiceAssignNo, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/invoice-assign-no/${invoiceAssignNo.id}`} color="link" size="sm">
                      {invoiceAssignNo.id}
                    </Button>
                  </td>
                  <td>{invoiceAssignNo.track}</td>
                  <td>{invoiceAssignNo.period}</td>
                  <td>{invoiceAssignNo.fromNo}</td>
                  <td>{invoiceAssignNo.toNo}</td>
                  <td>{invoiceAssignNo.usedCount}</td>
                  <td>{invoiceAssignNo.rollSize}</td>
                  <td>{invoiceAssignNo.status}</td>
                  <td>
                    {invoiceAssignNo.tenant ? <Link to={`/tenant/${invoiceAssignNo.tenant.id}`}>{invoiceAssignNo.tenant.name}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/invoice-assign-no/${invoiceAssignNo.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/invoice-assign-no/${invoiceAssignNo.id}/edit`}
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
                        onClick={() => (window.location.href = `/invoice-assign-no/${invoiceAssignNo.id}/delete`)}
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
              <Translate contentKey="turnbridgeBackendApp.invoiceAssignNo.home.notFound">No Invoice Assign Nos found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default InvoiceAssignNo;
