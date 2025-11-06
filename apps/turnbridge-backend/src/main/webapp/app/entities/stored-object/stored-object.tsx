import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './stored-object.reducer';

export const StoredObject = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const storedObjectList = useAppSelector(state => state.storedObject.entities);
  const loading = useAppSelector(state => state.storedObject.loading);
  const totalItems = useAppSelector(state => state.storedObject.totalItems);

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
      <h2 id="stored-object-heading" data-cy="StoredObjectHeading">
        <Translate contentKey="turnbridgeBackendApp.storedObject.home.title">Stored Objects</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.storedObject.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/stored-object/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.storedObject.home.createLabel">Create new Stored Object</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {storedObjectList && storedObjectList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('bucket')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.bucket">Bucket</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bucket')} />
                </th>
                <th className="hand" onClick={sort('objectKey')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.objectKey">Object Key</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('objectKey')} />
                </th>
                <th className="hand" onClick={sort('mediaType')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.mediaType">Media Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mediaType')} />
                </th>
                <th className="hand" onClick={sort('contentLength')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.contentLength">Content Length</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentLength')} />
                </th>
                <th className="hand" onClick={sort('sha256')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.sha256">Sha 256</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sha256')} />
                </th>
                <th className="hand" onClick={sort('purpose')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.purpose">Purpose</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('purpose')} />
                </th>
                <th className="hand" onClick={sort('filename')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.filename">Filename</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('filename')} />
                </th>
                <th className="hand" onClick={sort('storageClass')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.storageClass">Storage Class</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('storageClass')} />
                </th>
                <th className="hand" onClick={sort('encryption')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.encryption">Encryption</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('encryption')} />
                </th>
                <th className="hand" onClick={sort('metadata')}>
                  <Translate contentKey="turnbridgeBackendApp.storedObject.metadata">Metadata</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('metadata')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {storedObjectList.map((storedObject, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/stored-object/${storedObject.id}`} color="link" size="sm">
                      {storedObject.id}
                    </Button>
                  </td>
                  <td>{storedObject.bucket}</td>
                  <td>{storedObject.objectKey}</td>
                  <td>{storedObject.mediaType}</td>
                  <td>{storedObject.contentLength}</td>
                  <td>{storedObject.sha256}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.StoragePurpose.${storedObject.purpose}`} />
                  </td>
                  <td>{storedObject.filename}</td>
                  <td>{storedObject.storageClass}</td>
                  <td>{storedObject.encryption}</td>
                  <td>{storedObject.metadata}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/stored-object/${storedObject.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/stored-object/${storedObject.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/stored-object/${storedObject.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.storedObject.home.notFound">No Stored Objects found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={storedObjectList && storedObjectList.length > 0 ? '' : 'd-none'}>
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

export default StoredObject;
