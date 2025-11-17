import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './import-file-item.reducer';

export const ImportFileItem = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const importFileItemList = useAppSelector(state => state.importFileItem.entities);
  const loading = useAppSelector(state => state.importFileItem.loading);
  const totalItems = useAppSelector(state => state.importFileItem.totalItems);

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
      <h2 id="import-file-item-heading" data-cy="ImportFileItemHeading">
        <Translate contentKey="turnbridgeBackendApp.importFileItem.home.title">Import File Items</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.importFileItem.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/import-file-item/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.importFileItem.home.createLabel">Create new Import File Item</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {importFileItemList && importFileItemList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('lineIndex')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.lineIndex">Line Index</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lineIndex')} />
                </th>
                <th className="hand" onClick={sort('rawData')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.rawData">Raw Data</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rawData')} />
                </th>
                <th className="hand" onClick={sort('rawHash')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.rawHash">Raw Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rawHash')} />
                </th>
                <th className="hand" onClick={sort('sourceFamily')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.sourceFamily">Source Family</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceFamily')} />
                </th>
                <th className="hand" onClick={sort('normalizedFamily')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.normalizedFamily">Normalized Family</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('normalizedFamily')} />
                </th>
                <th className="hand" onClick={sort('normalizedJson')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.normalizedJson">Normalized Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('normalizedJson')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('errorCode')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.errorCode">Error Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorCode')} />
                </th>
                <th className="hand" onClick={sort('errorMessage')}>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.errorMessage">Error Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.importFile">Import File</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.importFileItem.invoice">Invoice</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {importFileItemList.map((importFileItem, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/import-file-item/${importFileItem.id}`} color="link" size="sm">
                      {importFileItem.id}
                    </Button>
                  </td>
                  <td>{importFileItem.lineIndex}</td>
                  <td>{importFileItem.rawData}</td>
                  <td>{importFileItem.rawHash}</td>
                  <td>{importFileItem.sourceFamily}</td>
                  <td>{importFileItem.normalizedFamily}</td>
                  <td>{importFileItem.normalizedJson}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.ImportItemStatus.${importFileItem.status}`} />
                  </td>
                  <td>{importFileItem.errorCode}</td>
                  <td>{importFileItem.errorMessage}</td>
                  <td>
                    {importFileItem.importFile ? (
                      <Link to={`/import-file/${importFileItem.importFile.id}`}>{importFileItem.importFile.originalFilename}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {importFileItem.invoice ? (
                      <Link to={`/invoice/${importFileItem.invoice.id}`}>{importFileItem.invoice.invoiceNo}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/import-file-item/${importFileItem.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/import-file-item/${importFileItem.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/import-file-item/${importFileItem.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.importFileItem.home.notFound">No Import File Items found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={importFileItemList && importFileItemList.length > 0 ? '' : 'd-none'}>
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

export default ImportFileItem;
