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

import { getEntities } from './track-range.reducer';

export const TrackRange = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const trackRangeList = useAppSelector(state => state.trackRange.entities);
  const loading = useAppSelector(state => state.trackRange.loading);
  const totalItems = useAppSelector(state => state.trackRange.totalItems);

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
      <h2 id="track-range-heading" data-cy="TrackRangeHeading">
        <Translate contentKey="turnbridgeBackendApp.trackRange.home.title">Track Ranges</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.trackRange.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/track-range/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.trackRange.home.createLabel">Create new Track Range</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {trackRangeList && trackRangeList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('sellerId')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.sellerId">Seller Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sellerId')} />
                </th>
                <th className="hand" onClick={sort('period')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.period">Period</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('period')} />
                </th>
                <th className="hand" onClick={sort('prefix')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.prefix">Prefix</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('prefix')} />
                </th>
                <th className="hand" onClick={sort('startNo')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.startNo">Start No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('startNo')} />
                </th>
                <th className="hand" onClick={sort('endNo')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.endNo">End No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('endNo')} />
                </th>
                <th className="hand" onClick={sort('currentNo')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.currentNo">Current No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('currentNo')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('version')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.version">Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('version')} />
                </th>
                <th className="hand" onClick={sort('lockOwner')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.lockOwner">Lock Owner</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lockOwner')} />
                </th>
                <th className="hand" onClick={sort('lockAt')}>
                  <Translate contentKey="turnbridgeBackendApp.trackRange.lockAt">Lock At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lockAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {trackRangeList.map((trackRange, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/track-range/${trackRange.id}`} color="link" size="sm">
                      {trackRange.id}
                    </Button>
                  </td>
                  <td>{trackRange.sellerId}</td>
                  <td>{trackRange.period}</td>
                  <td>{trackRange.prefix}</td>
                  <td>{trackRange.startNo}</td>
                  <td>{trackRange.endNo}</td>
                  <td>{trackRange.currentNo}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.TrackRangeStatus.${trackRange.status}`} />
                  </td>
                  <td>{trackRange.version}</td>
                  <td>{trackRange.lockOwner}</td>
                  <td>{trackRange.lockAt ? <TextFormat type="date" value={trackRange.lockAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/track-range/${trackRange.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/track-range/${trackRange.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/track-range/${trackRange.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.trackRange.home.notFound">No Track Ranges found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={trackRangeList && trackRangeList.length > 0 ? '' : 'd-none'}>
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

export default TrackRange;
