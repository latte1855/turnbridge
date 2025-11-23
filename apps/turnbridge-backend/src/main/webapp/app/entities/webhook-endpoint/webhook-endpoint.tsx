import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { getEntities as getTenants } from 'app/entities/tenant/tenant.reducer';

import { getEntities } from './webhook-endpoint.reducer';

export const WebhookEndpoint = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const [tenantId, setTenantId] = useState(null);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const tenants = useAppSelector(state => state.tenant.entities);

  const webhookEndpointList = useAppSelector(state => state.webhookEndpoint.entities);
  const loading = useAppSelector(state => state.webhookEndpoint.loading);
  const totalItems = useAppSelector(state => state.webhookEndpoint.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
        tenantId,
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
  }, [paginationState.activePage, paginationState.order, paginationState.sort, tenantId]);

  useEffect(() => {
    if (isAdmin) {
      dispatch(getTenants({}));
    }
  }, [isAdmin]);

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
      <h2 id="webhook-endpoint-heading" data-cy="WebhookEndpointHeading">
        <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.home.title">Webhook Endpoints</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/webhook-endpoint/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.home.createLabel">Create new Webhook Endpoint</Translate>
          </Link>
        </div>
      </h2>
      {isAdmin && (
        <div className="mb-3">
          <select
            className="form-control"
            value={tenantId || ''}
            onChange={e => setTenantId(e.target.value ? Number(e.target.value) : null)}
          >
            <option value="">All Tenants</option>
            {tenants.map(tenant => (
              <option key={tenant.id} value={tenant.id}>
                {tenant.name}
              </option>
            ))}
          </select>
        </div>
      )}
      <div className="table-responsive">
        {webhookEndpointList && webhookEndpointList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('targetUrl')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.targetUrl">Target Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('targetUrl')} />
                </th>
                <th className="hand" onClick={sort('secret')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.secret">Secret</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('secret')} />
                </th>
                <th className="hand" onClick={sort('events')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.events">Events</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('events')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.tenant">Tenant</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {webhookEndpointList.map((webhookEndpoint, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/webhook-endpoint/${webhookEndpoint.id}`} color="link" size="sm">
                      {webhookEndpoint.id}
                    </Button>
                  </td>
                  <td>{webhookEndpoint.name}</td>
                  <td>{webhookEndpoint.targetUrl}</td>
                  <td>{webhookEndpoint.secret}</td>
                  <td>{webhookEndpoint.events}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.WebhookStatus.${webhookEndpoint.status}`} />
                  </td>
                  <td>
                    {webhookEndpoint.tenant ? <Link to={`/tenant/${webhookEndpoint.tenant.id}`}>{webhookEndpoint.tenant.name}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/webhook-endpoint/${webhookEndpoint.id}`}
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
                        to={`/webhook-endpoint/${webhookEndpoint.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/webhook-endpoint/${webhookEndpoint.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.webhookEndpoint.home.notFound">No Webhook Endpoints found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={webhookEndpointList && webhookEndpointList.length > 0 ? '' : 'd-none'}>
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

export default WebhookEndpoint;
