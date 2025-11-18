import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Button, Table, Spinner, Alert, Input, Row, Col, Card, CardBody, CardTitle } from 'reactstrap';
import { Translate, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { triggerDownload, resolveFilename } from './utils';

interface ImportFileSummary {
  id?: number;
  originalFilename?: string;
  importType?: string;
  status?: string;
  totalCount?: number;
  successCount?: number;
  errorCount?: number;
  legacyType?: string;
}

interface UploadResponse {
  importId: number;
}

const ImportMonitorList = () => {
  const [files, setFiles] = useState<ImportFileSummary[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [page, setPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const itemsPerPage = 20;
  const [filters, setFilters] = useState({
    filename: '',
    importType: '',
    status: '',
  });
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadState, setUploadState] = useState({
    type: 'INVOICE',
    sellerId: '',
    sha256: '',
    legacyType: '',
    profile: '',
    encoding: 'UTF-8',
  });
  const [uploading, setUploading] = useState(false);
  const [uploadSuccessId, setUploadSuccessId] = useState<number | null>(null);
  const [uploadError, setUploadError] = useState<string | null>(null);

  const fetchFiles = (targetPage: number = page) => {
    setLoading(true);
    setError(null);
    axios
      .get<ImportFileSummary[]>('/api/import-files', {
        params: {
          page: targetPage - 1,
          size: itemsPerPage,
          sort: 'id,desc',
          'originalFilename.contains': filters.filename || undefined,
          'importType.equals': filters.importType || undefined,
          'status.equals': filters.status || undefined,
        },
      })
      .then(response => {
        setFiles(response.data);
        setSelectedIds(new Set());
        const total = parseInt(response.headers['x-total-count'], 10);
        setTotalItems(Number.isNaN(total) ? 0 : total);
        setPage(targetPage);
      })
      .catch(() => setError('importMonitor.messages.loadError'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchFiles(1);
  }, []);

  const toggleSelection = (id?: number) => {
    if (!id) {
      return;
    }
    setSelectedIds(prev => {
      const next = new Set(prev);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  };

  const handleSingleDownload = (file: ImportFileSummary) => {
    if (!file.id) {
      return;
    }
    axios
      .get(`/api/import-files/${file.id}/result`, { responseType: 'blob' })
      .then(response => {
        const filename =
          resolveFilename(response.headers['content-disposition'], `${file.originalFilename || `import_${file.id}`}_result.csv`) ??
          `${file.originalFilename || `import_${file.id}`}_result.csv`;
        triggerDownload(response.data, filename);
      })
      .catch(() => setError('importMonitor.messages.downloadError'));
  };

  const handleBatchDownload = () => {
    if (selectedIds.size === 0) {
      return;
    }
    axios
      .post('/api/import-files/results/download', { importFileIds: Array.from(selectedIds) }, { responseType: 'blob' })
      .then(response => {
        const filename = resolveFilename(response.headers['content-disposition'], 'import-results.zip') ?? 'import-results.zip';
        triggerDownload(response.data, filename);
      })
      .catch(() => setError('importMonitor.messages.downloadError'));
  };

  const computeSha256 = async (file: File) => {
    if (!window.crypto?.subtle) {
      return '';
    }
    const buffer = await file.arrayBuffer();
    const hash = await window.crypto.subtle.digest('SHA-256', buffer);
    return Array.from(new Uint8Array(hash))
      .map(b => b.toString(16).padStart(2, '0'))
      .join('');
  };

  const handleFileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0] ?? null;
    setUploadFile(file);
    if (file) {
      try {
        const hash = await computeSha256(file);
        if (hash) {
          setUploadState(prev => ({ ...prev, sha256: hash }));
        }
      } catch (e) {
        // ignore hash errors
      }
    }
  };

  const handleUpload = () => {
    if (!uploadFile) {
      setUploadError('importMonitor.upload.noFile');
      setUploadSuccessId(null);
      return;
    }
    if (!uploadState.sellerId) {
      setUploadError('importMonitor.upload.noSeller');
      setUploadSuccessId(null);
      return;
    }
    if (!uploadState.sha256) {
      setUploadError('importMonitor.upload.noSha');
      setUploadSuccessId(null);
      return;
    }
    const formData = new FormData();
    formData.append('file', uploadFile);
    formData.append('sellerId', uploadState.sellerId);
    formData.append('sha256', uploadState.sha256);
    formData.append('encoding', uploadState.encoding);
    if (uploadState.profile) {
      formData.append('profile', uploadState.profile);
    }
    if (uploadState.legacyType) {
      formData.append('legacyType', uploadState.legacyType);
    }

    setUploading(true);
    setUploadError(null);
    setUploadSuccessId(null);
    const url = uploadState.type === 'E0501' ? '/api/upload/e0501' : '/api/upload/invoice';
    axios
      .post<UploadResponse>(url, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then(response => {
        setUploadSuccessId(response.data.importId);
        setUploadFile(null);
        setUploadState(prev => ({ ...prev, sha256: '' }));
        fetchFiles(1);
      })
      .catch(() => setUploadError('importMonitor.upload.failed'))
      .finally(() => setUploading(false));
  };

  return (
    <div>
      <h2>
        <Translate contentKey="importMonitor.title" />
      </h2>
      <Card className="mb-4">
        <CardBody>
          <CardTitle tag="h5">
            <Translate contentKey="importMonitor.upload.title" />
          </CardTitle>
          <Row>
            <Col md="3">
              <label className="form-label">
                <Translate contentKey="importMonitor.upload.type" />
              </label>
              <Input
                type="select"
                value={uploadState.type}
                onChange={event => setUploadState(prev => ({ ...prev, type: event.target.value }))}
              >
                <option value="INVOICE">INVOICE</option>
                <option value="E0501">E0501</option>
              </Input>
            </Col>
            <Col md="4">
              <label className="form-label">
                <Translate contentKey="importMonitor.upload.file" />
              </label>
              <Input type="file" accept=".csv" onChange={handleFileChange} />
            </Col>
            <Col md="5">
              <label className="form-label">
                <Translate contentKey="importMonitor.upload.seller" />
              </label>
              <Input
                type="text"
                value={uploadState.sellerId}
                onChange={event => setUploadState(prev => ({ ...prev, sellerId: event.target.value }))}
                placeholder="24556677"
              />
            </Col>
          </Row>
          <Row className="mt-2">
            <Col md="4">
              <label className="form-label">
                <Translate contentKey="importMonitor.upload.sha" />
              </label>
              <Input
                type="text"
                value={uploadState.sha256}
                onChange={event => setUploadState(prev => ({ ...prev, sha256: event.target.value }))}
                placeholder="SHA-256"
              />
            </Col>
            <Col md="4">
              <label className="form-label">
                <Translate contentKey="importMonitor.upload.legacyType" />
              </label>
              <Input
                type="text"
                value={uploadState.legacyType}
                onChange={event => setUploadState(prev => ({ ...prev, legacyType: event.target.value }))}
                placeholder="C0401"
              />
            </Col>
            <Col md="4">
              <label className="form-label">
                <Translate contentKey="importMonitor.upload.profile" />
              </label>
              <Input
                type="text"
                value={uploadState.profile}
                onChange={event => setUploadState(prev => ({ ...prev, profile: event.target.value }))}
                placeholder="default"
              />
            </Col>
          </Row>
          <div className="mt-3">
            <Button color="primary" onClick={handleUpload} disabled={uploading}>
              {uploading ? <Spinner size="sm" /> : <FontAwesomeIcon icon="file-import" />}
              &nbsp;
              <Translate contentKey="importMonitor.upload.submit" />
            </Button>
            {uploadError && (
              <Alert color="danger" className="mt-2">
                <Translate contentKey={uploadError} />
              </Alert>
            )}
            {uploadSuccessId && (
              <Alert color="success" className="mt-2">
                <Translate contentKey="importMonitor.upload.successMessage" interpolate={{ importId: uploadSuccessId }} />
              </Alert>
            )}
          </div>
        </CardBody>
      </Card>
      <Row className="mb-3">
        <Col md="3">
          <label className="form-label">
            <Translate contentKey="importMonitor.filter.filename" />
          </label>
          <Input
            type="text"
            value={filters.filename}
            onChange={event => setFilters(prev => ({ ...prev, filename: event.target.value }))}
            placeholder="invoice_2025.csv"
          />
        </Col>
        <Col md="3">
          <label className="form-label">
            <Translate contentKey="importMonitor.filter.type" />
          </label>
          <Input
            type="select"
            value={filters.importType}
            onChange={event => setFilters(prev => ({ ...prev, importType: event.target.value }))}
          >
            <option value="">
              <Translate contentKey="importMonitor.filter.all" />
            </option>
            <option value="INVOICE">INVOICE</option>
            <option value="E0501">E0501</option>
          </Input>
        </Col>
        <Col md="3">
          <label className="form-label">
            <Translate contentKey="importMonitor.filter.status" />
          </label>
          <Input type="select" value={filters.status} onChange={event => setFilters(prev => ({ ...prev, status: event.target.value }))}>
            <option value="">
              <Translate contentKey="importMonitor.filter.all" />
            </option>
            <option value="RECEIVED">RECEIVED</option>
            <option value="UPLOADED">UPLOADED</option>
            <option value="NORMALIZED">NORMALIZED</option>
            <option value="FAILED">FAILED</option>
          </Input>
        </Col>
        <Col md="3" className="d-flex align-items-end">
          <Button color="primary" className="me-2" onClick={() => fetchFiles(1)} disabled={loading}>
            <FontAwesomeIcon icon="search" /> <Translate contentKey="importMonitor.filter.search" />
          </Button>
          <Button
            color="light"
            onClick={() => {
              setFilters({ filename: '', importType: '', status: '' });
              fetchFiles(1);
            }}
          >
            <FontAwesomeIcon icon="sync" /> <Translate contentKey="importMonitor.filter.reset" />
          </Button>
        </Col>
      </Row>
      <div className="d-flex mb-2">
        <Button color="primary" onClick={() => fetchFiles(page)} disabled={loading}>
          {loading ? <Spinner size="sm" /> : <FontAwesomeIcon icon="sync" spin={loading} />}
          &nbsp;
          <Translate contentKey="importMonitor.refresh" />
        </Button>
        <Button color="secondary" className="ms-2" disabled={selectedIds.size === 0} onClick={handleBatchDownload}>
          <FontAwesomeIcon icon="file-archive" />
          &nbsp;
          <Translate contentKey="importMonitor.downloadSelected" />
        </Button>
      </div>
      {error && (
        <Alert color="danger">
          <Translate contentKey={error} />
        </Alert>
      )}
      {!loading && files.length === 0 ? (
        <Alert color="info">
          <Translate contentKey="importMonitor.noData" />
        </Alert>
      ) : (
        <Table responsive hover>
          <thead>
            <tr>
              <th />
              <th>
                <Translate contentKey="importMonitor.table.id" />
              </th>
              <th>
                <Translate contentKey="importMonitor.table.filename" />
              </th>
              <th>
                <Translate contentKey="importMonitor.table.type" />
              </th>
              <th>
                <Translate contentKey="importMonitor.table.status" />
              </th>
              <th>
                <Translate contentKey="importMonitor.table.success" />
              </th>
              <th>
                <Translate contentKey="importMonitor.table.error" />
              </th>
              <th className="text-end">
                <Translate contentKey="importMonitor.table.actions" />
              </th>
            </tr>
          </thead>
          <tbody>
            {files.map((file, index) => (
              <tr key={file.id ?? `file-${index}`}>
                <td>
                  <Input
                    type="checkbox"
                    checked={file.id ? selectedIds.has(file.id) : false}
                    onChange={() => toggleSelection(file.id)}
                    disabled={!file.id}
                  />
                </td>
                <td>{file.id}</td>
                <td>{file.originalFilename}</td>
                <td>{file.importType}</td>
                <td>
                  <span
                    className={`badge ${
                      file.status === 'FAILED' ? 'bg-danger' : file.status === 'NORMALIZED' ? 'bg-success' : 'bg-secondary'
                    }`}
                  >
                    {file.status}
                  </span>
                </td>
                <td>{file.successCount}</td>
                <td>{file.errorCount}</td>
                <td className="text-end">
                  {file.id && (
                    <>
                      <Button color="info" size="sm" className="me-1" tag={Link} to={`/import-monitor/${file.id}`}>
                        <FontAwesomeIcon icon="eye" /> <Translate contentKey="entity.action.view" />
                      </Button>
                      <Button color="secondary" size="sm" onClick={() => handleSingleDownload(file)}>
                        <FontAwesomeIcon icon="download" /> <Translate contentKey="importMonitor.downloadSingle" />
                      </Button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
      {totalItems > 0 && (
        <div className="d-flex justify-content-between align-items-center">
          <JhiItemCount page={page} total={totalItems} itemsPerPage={itemsPerPage} i18nEnabled />
          <JhiPagination
            activePage={page}
            itemsPerPage={itemsPerPage}
            onSelect={currentPage => fetchFiles(currentPage)}
            maxButtons={5}
            totalItems={totalItems}
          />
        </div>
      )}
    </div>
  );
};

export default ImportMonitorList;
