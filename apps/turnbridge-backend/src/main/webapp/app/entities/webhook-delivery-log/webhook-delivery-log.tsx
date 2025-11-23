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

import { getEntities } from './webhook-delivery-log.reducer';

export const WebhookDeliveryLog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const webhookDeliveryLogList = useAppSelector(state => state.webhookDeliveryLog.entities);
  const loading = useAppSelector(state => state.webhookDeliveryLog.loading);
  const totalItems = useAppSelector(state => state.webhookDeliveryLog.totalItems);

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
      <h2 id="webhook-delivery-log-heading" data-cy="WebhookDeliveryLogHeading">
        <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.home.title">Webhook Delivery Logs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/webhook-delivery-log/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.home.createLabel">Create new Webhook Delivery Log</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {webhookDeliveryLogList && webhookDeliveryLogList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('deliveryId')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.deliveryId">Delivery Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('deliveryId')} />
                </th>
                <th className="hand" onClick={sort('event')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.event">Event</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('event')} />
                </th>
                <th className="hand" onClick={sort('payload')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.payload">Payload</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('payload')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('httpStatus')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.httpStatus">Http Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('httpStatus')} />
                </th>
                <th className="hand" onClick={sort('attempts')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.attempts">Attempts</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('attempts')} />
                </th>
                <th className="hand" onClick={sort('lastError')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.lastError">Last Error</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastError')} />
                </th>
                <th className="hand" onClick={sort('deliveredAt')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.deliveredAt">Delivered At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('deliveredAt')} />
                </th>
                <th className="hand" onClick={sort('nextAttemptAt')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.nextAttemptAt">Next Attempt At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('nextAttemptAt')} />
                </th>
                <th className="hand" onClick={sort('lockedAt')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.lockedAt">Locked At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lockedAt')} />
                </th>
                <th className="hand" onClick={sort('dlqReason')}>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.dlqReason">Dlq Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dlqReason')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.webhookEndpoint">Webhook Endpoint</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {webhookDeliveryLogList.map((webhookDeliveryLog, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/webhook-delivery-log/${webhookDeliveryLog.id}`} color="link" size="sm">
                      {webhookDeliveryLog.id}
                    </Button>
                  </td>
                  <td>{webhookDeliveryLog.deliveryId}</td>
                  <td>{webhookDeliveryLog.event}</td>
                  <td>{webhookDeliveryLog.payload}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.DeliveryResult.${webhookDeliveryLog.status}`} />
                  </td>
                  <td>{webhookDeliveryLog.httpStatus}</td>
                  <td>{webhookDeliveryLog.attempts}</td>
                  <td>{webhookDeliveryLog.lastError}</td>
                  <td>
                    {webhookDeliveryLog.deliveredAt ? (
                      <TextFormat type="date" value={webhookDeliveryLog.deliveredAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {webhookDeliveryLog.nextAttemptAt ? (
                      <TextFormat type="date" value={webhookDeliveryLog.nextAttemptAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {webhookDeliveryLog.lockedAt ? (
                      <TextFormat type="date" value={webhookDeliveryLog.lockedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{webhookDeliveryLog.dlqReason}</td>
                  <td>
                    {webhookDeliveryLog.webhookEndpoint ? (
                      <Link to={`/webhook-endpoint/${webhookDeliveryLog.webhookEndpoint.id}`}>
                        {webhookDeliveryLog.webhookEndpoint.name}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/webhook-delivery-log/${webhookDeliveryLog.id}`}
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
                        to={`/webhook-delivery-log/${webhookDeliveryLog.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/webhook-delivery-log/${webhookDeliveryLog.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.webhookDeliveryLog.home.notFound">No Webhook Delivery Logs found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={webhookDeliveryLogList && webhookDeliveryLogList.length > 0 ? '' : 'd-none'}>
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

export default WebhookDeliveryLog;
