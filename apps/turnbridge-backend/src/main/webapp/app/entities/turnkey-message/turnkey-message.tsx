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

import { getEntities } from './turnkey-message.reducer';

export const TurnkeyMessage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const turnkeyMessageList = useAppSelector(state => state.turnkeyMessage.entities);
  const loading = useAppSelector(state => state.turnkeyMessage.loading);
  const totalItems = useAppSelector(state => state.turnkeyMessage.totalItems);

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
      <h2 id="turnkey-message-heading" data-cy="TurnkeyMessageHeading">
        <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.home.title">Turnkey Messages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/turnkey-message/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.home.createLabel">Create new Turnkey Message</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {turnkeyMessageList && turnkeyMessageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('messageId')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.messageId">Message Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('messageId')} />
                </th>
                <th className="hand" onClick={sort('messageFamily')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.messageFamily">Message Family</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('messageFamily')} />
                </th>
                <th className="hand" onClick={sort('type')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.type">Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('type')} />
                </th>
                <th className="hand" onClick={sort('code')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.code">Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                </th>
                <th className="hand" onClick={sort('message')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.message">Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('message')} />
                </th>
                <th className="hand" onClick={sort('payloadPath')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.payloadPath">Payload Path</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('payloadPath')} />
                </th>
                <th className="hand" onClick={sort('receivedAt')}>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.receivedAt">Received At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('receivedAt')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.invoice">Invoice</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {turnkeyMessageList.map((turnkeyMessage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/turnkey-message/${turnkeyMessage.id}`} color="link" size="sm">
                      {turnkeyMessage.id}
                    </Button>
                  </td>
                  <td>{turnkeyMessage.messageId}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.MessageFamily.${turnkeyMessage.messageFamily}`} />
                  </td>
                  <td>{turnkeyMessage.type}</td>
                  <td>{turnkeyMessage.code}</td>
                  <td>{turnkeyMessage.message}</td>
                  <td>{turnkeyMessage.payloadPath}</td>
                  <td>
                    {turnkeyMessage.receivedAt ? (
                      <TextFormat type="date" value={turnkeyMessage.receivedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {turnkeyMessage.invoice ? (
                      <Link to={`/invoice/${turnkeyMessage.invoice.id}`}>{turnkeyMessage.invoice.invoiceNo}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/turnkey-message/${turnkeyMessage.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/turnkey-message/${turnkeyMessage.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/turnkey-message/${turnkeyMessage.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.turnkeyMessage.home.notFound">No Turnkey Messages found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={turnkeyMessageList && turnkeyMessageList.length > 0 ? '' : 'd-none'}>
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

export default TurnkeyMessage;
