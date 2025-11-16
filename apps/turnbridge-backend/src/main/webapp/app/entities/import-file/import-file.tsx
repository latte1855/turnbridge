import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './import-file.reducer';

export const ImportFile = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const importFileList = useAppSelector(state => state.importFile.entities);
  const loading = useAppSelector(state => state.importFile.loading);
  const totalItems = useAppSelector(state => state.importFile.totalItems);

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
      <h2 id="import-file-heading" data-cy="ImportFileHeading">
        <Translate contentKey="turnbridgeBackendApp.importFile.home.title">Import Files</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.importFile.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/import-file/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.importFile.home.createLabel">Create new Import File</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {importFileList && importFileList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('importType')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.importType">Import Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('importType')} />
                </th>
                <th className="hand" onClick={sort('originalFilename')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.originalFilename">Original Filename</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('originalFilename')} />
                </th>
                <th className="hand" onClick={sort('sha256')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.sha256">Sha 256</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sha256')} />
                </th>
                <th className="hand" onClick={sort('totalCount')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.totalCount">Total Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('totalCount')} />
                </th>
                <th className="hand" onClick={sort('successCount')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.successCount">Success Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('successCount')} />
                </th>
                <th className="hand" onClick={sort('errorCount')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.errorCount">Error Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorCount')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('legacyType')}>
                  <Translate contentKey="turnbridgeBackendApp.importFile.legacyType">Legacy Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('legacyType')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.importFile.tenant">Tenant</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {importFileList.map((importFile, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/import-file/${importFile.id}`} color="link" size="sm">
                      {importFile.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.ImportType.${importFile.importType}`} />
                  </td>
                  <td>{importFile.originalFilename}</td>
                  <td>{importFile.sha256}</td>
                  <td>{importFile.totalCount}</td>
                  <td>{importFile.successCount}</td>
                  <td>{importFile.errorCount}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.ImportStatus.${importFile.status}`} />
                  </td>
                  <td>{importFile.legacyType}</td>
                  <td>{importFile.tenant ? <Link to={`/tenant/${importFile.tenant.id}`}>{importFile.tenant.name}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/import-file/${importFile.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/import-file/${importFile.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/import-file/${importFile.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.importFile.home.notFound">No Import Files found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={importFileList && importFileList.length > 0 ? '' : 'd-none'}>
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

export default ImportFile;
