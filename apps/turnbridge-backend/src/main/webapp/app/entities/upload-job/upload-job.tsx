import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './upload-job.reducer';

export const UploadJob = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const uploadJobList = useAppSelector(state => state.uploadJob.entities);
  const loading = useAppSelector(state => state.uploadJob.loading);
  const totalItems = useAppSelector(state => state.uploadJob.totalItems);

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
      <h2 id="upload-job-heading" data-cy="UploadJobHeading">
        <Translate contentKey="turnbridgeBackendApp.uploadJob.home.title">Upload Jobs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.uploadJob.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/upload-job/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.uploadJob.home.createLabel">Create new Upload Job</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {uploadJobList && uploadJobList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('jobId')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.jobId">Job Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('jobId')} />
                </th>
                <th className="hand" onClick={sort('sellerId')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.sellerId">Seller Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sellerId')} />
                </th>
                <th className="hand" onClick={sort('sellerName')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.sellerName">Seller Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sellerName')} />
                </th>
                <th className="hand" onClick={sort('period')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.period">Period</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('period')} />
                </th>
                <th className="hand" onClick={sort('profile')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.profile">Profile</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('profile')} />
                </th>
                <th className="hand" onClick={sort('sourceFilename')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.sourceFilename">Source Filename</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceFilename')} />
                </th>
                <th className="hand" onClick={sort('sourceMediaType')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.sourceMediaType">Source Media Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sourceMediaType')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('total')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.total">Total</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('total')} />
                </th>
                <th className="hand" onClick={sort('accepted')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.accepted">Accepted</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('accepted')} />
                </th>
                <th className="hand" onClick={sort('failed')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.failed">Failed</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('failed')} />
                </th>
                <th className="hand" onClick={sort('sent')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.sent">Sent</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sent')} />
                </th>
                <th className="hand" onClick={sort('remark')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.remark">Remark</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remark')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.originalFile">Original File</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.uploadJob.resultFile">Result File</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {uploadJobList.map((uploadJob, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/upload-job/${uploadJob.id}`} color="link" size="sm">
                      {uploadJob.id}
                    </Button>
                  </td>
                  <td>{uploadJob.jobId}</td>
                  <td>{uploadJob.sellerId}</td>
                  <td>{uploadJob.sellerName}</td>
                  <td>{uploadJob.period}</td>
                  <td>{uploadJob.profile}</td>
                  <td>{uploadJob.sourceFilename}</td>
                  <td>{uploadJob.sourceMediaType}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.UploadJobStatus.${uploadJob.status}`} />
                  </td>
                  <td>{uploadJob.total}</td>
                  <td>{uploadJob.accepted}</td>
                  <td>{uploadJob.failed}</td>
                  <td>{uploadJob.sent}</td>
                  <td>{uploadJob.remark}</td>
                  <td>
                    {uploadJob.originalFile ? (
                      <Link to={`/stored-object/${uploadJob.originalFile.id}`}>{uploadJob.originalFile.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {uploadJob.resultFile ? <Link to={`/stored-object/${uploadJob.resultFile.id}`}>{uploadJob.resultFile.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/upload-job/${uploadJob.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/upload-job/${uploadJob.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/upload-job/${uploadJob.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.uploadJob.home.notFound">No Upload Jobs found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={uploadJobList && uploadJobList.length > 0 ? '' : 'd-none'}>
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

export default UploadJob;
