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

import { getEntities } from './import-file-item-error.reducer';

export const ImportFileItemError = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const importFileItemErrorList = useAppSelector(state => state.importFileItemError.entities);
  const loading = useAppSelector(state => state.importFileItemError.loading);
  const totalItems = useAppSelector(state => state.importFileItemError.totalItems);

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
      <h2 id="import-file-item-error-heading" data-cy="ImportFileItemErrorHeading">
        <Translate contentKey="turnbridgeBackendApp.importFileItemError.home.title">Import File Item Errors</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.importFileItemError.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/import-file-item-error/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.importFileItemError.home.createLabel">Create new Import File Item Error</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {importFileItemErrorList && importFileItemErrorList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('columnIndex')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.columnIndex">Column Index</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('columnIndex')} />
                </th>
                <th className="hand" onClick={sort('fieldName')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.fieldName">Field Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fieldName')} />
                </th>
                <th className="hand" onClick={sort('errorCode')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.errorCode">Error Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorCode')} />
                </th>
                <th className="hand" onClick={sort('message')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.message">Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('message')} />
                </th>
                <th className="hand" onClick={sort('severity')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.severity">Severity</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('severity')} />
                </th>
                <th className="hand" onClick={sort('occurredAt')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.occurredAt">Occurred At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('occurredAt')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.importFileItemError.importFileItem">Import File Item</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {importFileItemErrorList.map((importFileItemError, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/import-file-item-error/${importFileItemError.id}`} color="link" size="sm">
                      {importFileItemError.id}
                    </Button>
                  </td>
                  <td>{importFileItemError.columnIndex}</td>
                  <td>{importFileItemError.fieldName}</td>
                  <td>{importFileItemError.errorCode}</td>
                  <td>{importFileItemError.message}</td>
                  <td>{importFileItemError.severity}</td>
                  <td>
                    {importFileItemError.occurredAt ? (
                      <TextFormat type="date" value={importFileItemError.occurredAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {importFileItemError.importFileItem ? (
                      <Link to={`/import-file-item/${importFileItemError.importFileItem.id}`}>
                        {importFileItemError.importFileItem.lineIndex}
                      </Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/import-file-item-error/${importFileItemError.id}`}
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
                        to={`/import-file-item-error/${importFileItemError.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/import-file-item-error/${importFileItemError.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.importFileItemError.home.notFound">No Import File Item Errors found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={importFileItemErrorList && importFileItemErrorList.length > 0 ? '' : 'd-none'}>
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

export default ImportFileItemError;
