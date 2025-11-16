import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, Translate, getSortState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './manual-action.reducer';

export const ManualAction = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const manualActionList = useAppSelector(state => state.manualAction.entities);
  const loading = useAppSelector(state => state.manualAction.loading);

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
      <h2 id="manual-action-heading" data-cy="ManualActionHeading">
        <Translate contentKey="turnbridgeBackendApp.manualAction.home.title">Manual Actions</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.manualAction.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/manual-action/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.manualAction.home.createLabel">Create new Manual Action</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {manualActionList && manualActionList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('actionType')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.actionType">Action Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('actionType')} />
                </th>
                <th className="hand" onClick={sort('reason')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.reason">Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reason')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('requestedBy')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.requestedBy">Requested By</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedBy')} />
                </th>
                <th className="hand" onClick={sort('requestedAt')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.requestedAt">Requested At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedAt')} />
                </th>
                <th className="hand" onClick={sort('approvedBy')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.approvedBy">Approved By</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('approvedBy')} />
                </th>
                <th className="hand" onClick={sort('approvedAt')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.approvedAt">Approved At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('approvedAt')} />
                </th>
                <th className="hand" onClick={sort('resultMessage')}>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.resultMessage">Result Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('resultMessage')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.tenant">Tenant</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.invoice">Invoice</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.manualAction.importFile">Import File</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {manualActionList.map((manualAction, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/manual-action/${manualAction.id}`} color="link" size="sm">
                      {manualAction.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.ManualActionType.${manualAction.actionType}`} />
                  </td>
                  <td>{manualAction.reason}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.ApprovalStatus.${manualAction.status}`} />
                  </td>
                  <td>{manualAction.requestedBy}</td>
                  <td>
                    {manualAction.requestedAt ? <TextFormat type="date" value={manualAction.requestedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{manualAction.approvedBy}</td>
                  <td>
                    {manualAction.approvedAt ? <TextFormat type="date" value={manualAction.approvedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{manualAction.resultMessage}</td>
                  <td>{manualAction.tenant ? <Link to={`/tenant/${manualAction.tenant.id}`}>{manualAction.tenant.name}</Link> : ''}</td>
                  <td>
                    {manualAction.invoice ? <Link to={`/invoice/${manualAction.invoice.id}`}>{manualAction.invoice.invoiceNo}</Link> : ''}
                  </td>
                  <td>
                    {manualAction.importFile ? (
                      <Link to={`/import-file/${manualAction.importFile.id}`}>{manualAction.importFile.originalFilename}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/manual-action/${manualAction.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/manual-action/${manualAction.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/manual-action/${manualAction.id}/delete`)}
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
              <Translate contentKey="turnbridgeBackendApp.manualAction.home.notFound">No Manual Actions found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default ManualAction;
