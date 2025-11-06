import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './upload-job-item.reducer';

export const UploadJobItem = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const uploadJobItemList = useAppSelector(state => state.uploadJobItem.entities);
  const loading = useAppSelector(state => state.uploadJobItem.loading);
  const totalItems = useAppSelector(state => state.uploadJobItem.totalItems);

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
      <h2 id="upload-job-item-heading" data-cy="UploadJobItemHeading">
        <Translate contentKey="turnbridgeBackendApp.uploadJobItem.home.title">Upload Job Items</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.uploadJobItem.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/upload-job-item/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="turnbridgeBackendApp.uploadJobItem.home.createLabel">Create new Upload Job Item</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {uploadJobItemList && uploadJobItemList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('lineNo')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.lineNo">Line No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lineNo')} />
                </th>
                <th className="hand" onClick={sort('traceId')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.traceId">Trace Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('traceId')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('resultCode')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.resultCode">Result Code</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('resultCode')} />
                </th>
                <th className="hand" onClick={sort('resultMsg')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.resultMsg">Result Msg</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('resultMsg')} />
                </th>
                <th className="hand" onClick={sort('buyerId')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.buyerId">Buyer Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('buyerId')} />
                </th>
                <th className="hand" onClick={sort('buyerName')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.buyerName">Buyer Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('buyerName')} />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.currency">Currency</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('currency')} />
                </th>
                <th className="hand" onClick={sort('amountExcl')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.amountExcl">Amount Excl</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('amountExcl')} />
                </th>
                <th className="hand" onClick={sort('taxAmount')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.taxAmount">Tax Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taxAmount')} />
                </th>
                <th className="hand" onClick={sort('amountIncl')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.amountIncl">Amount Incl</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('amountIncl')} />
                </th>
                <th className="hand" onClick={sort('taxType')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.taxType">Tax Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('taxType')} />
                </th>
                <th className="hand" onClick={sort('invoiceDate')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.invoiceDate">Invoice Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('invoiceDate')} />
                </th>
                <th className="hand" onClick={sort('invoiceNo')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.invoiceNo">Invoice No</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('invoiceNo')} />
                </th>
                <th className="hand" onClick={sort('assignedPrefix')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.assignedPrefix">Assigned Prefix</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('assignedPrefix')} />
                </th>
                <th className="hand" onClick={sort('rawPayload')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.rawPayload">Raw Payload</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rawPayload')} />
                </th>
                <th className="hand" onClick={sort('rawHash')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.rawHash">Raw Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rawHash')} />
                </th>
                <th className="hand" onClick={sort('profileDetected')}>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.profileDetected">Profile Detected</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('profileDetected')} />
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.uploadJobItem.job">Job</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {uploadJobItemList.map((uploadJobItem, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/upload-job-item/${uploadJobItem.id}`} color="link" size="sm">
                      {uploadJobItem.id}
                    </Button>
                  </td>
                  <td>{uploadJobItem.lineNo}</td>
                  <td>{uploadJobItem.traceId}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.JobItemStatus.${uploadJobItem.status}`} />
                  </td>
                  <td>{uploadJobItem.resultCode}</td>
                  <td>{uploadJobItem.resultMsg}</td>
                  <td>{uploadJobItem.buyerId}</td>
                  <td>{uploadJobItem.buyerName}</td>
                  <td>{uploadJobItem.currency}</td>
                  <td>{uploadJobItem.amountExcl}</td>
                  <td>{uploadJobItem.taxAmount}</td>
                  <td>{uploadJobItem.amountIncl}</td>
                  <td>
                    <Translate contentKey={`turnbridgeBackendApp.TaxType.${uploadJobItem.taxType}`} />
                  </td>
                  <td>
                    {uploadJobItem.invoiceDate ? (
                      <TextFormat type="date" value={uploadJobItem.invoiceDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{uploadJobItem.invoiceNo}</td>
                  <td>{uploadJobItem.assignedPrefix}</td>
                  <td>{uploadJobItem.rawPayload}</td>
                  <td>{uploadJobItem.rawHash}</td>
                  <td>{uploadJobItem.profileDetected}</td>
                  <td>{uploadJobItem.job ? <Link to={`/upload-job/${uploadJobItem.job.id}`}>{uploadJobItem.job.jobId}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/upload-job-item/${uploadJobItem.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/upload-job-item/${uploadJobItem.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/upload-job-item/${uploadJobItem.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="turnbridgeBackendApp.uploadJobItem.home.notFound">No Upload Job Items found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={uploadJobItemList && uploadJobItemList.length > 0 ? '' : 'd-none'}>
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

export default UploadJobItem;
