import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { Button, Table, Alert, Spinner, Card, CardBody, CardTitle, CardText } from 'reactstrap';
import { Translate, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { triggerDownload, resolveFilename } from './utils';

interface ImportFileDTO {
  id?: number;
  originalFilename?: string;
  importType?: string;
  status?: string;
  totalCount?: number;
  successCount?: number;
  errorCount?: number;
  legacyType?: string;
}

interface ImportFileItemDTO {
  id?: number;
  lineIndex?: number;
  rawData?: string;
  status?: string;
  errorCode?: string;
  errorMessage?: string;
}

interface ImportFileItemErrorDTO {
  id?: number;
  fieldName?: string;
  errorCode?: string;
  message?: string;
  columnIndex?: number;
  importFileItem?: {
    id?: number;
  };
}

interface ImportFileLogDTO {
  id?: number;
  eventCode?: string;
  level?: string;
  message?: string;
  detail?: string;
  occurredAt?: string;
}

interface ImportFileItemView {
  item: ImportFileItemDTO;
  raw: Record<string, unknown>;
  errors: ImportFileItemErrorDTO[];
}

const ImportMonitorDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [importFile, setImportFile] = useState<ImportFileDTO | null>(null);
  const [itemPage, setItemPage] = useState(1);
  const [itemTotal, setItemTotal] = useState(0);
  const itemsPerPage = 20;
  const [items, setItems] = useState<ImportFileItemView[]>([]);
  const [logs, setLogs] = useState<ImportFileLogDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadDetail = async (importFileId: string, targetPage: number = itemPage) => {
    try {
      setLoading(true);
      setError(null);
      const [fileRes, itemsRes, logsRes] = await Promise.all([
        axios.get<ImportFileDTO>(`/api/import-files/${importFileId}`),
        axios.get<ImportFileItemDTO[]>(`/api/import-file-items`, {
          params: { 'importFileId.equals': importFileId, page: targetPage - 1, size: itemsPerPage, sort: 'lineIndex,asc' },
        }),
        axios.get<ImportFileLogDTO[]>(`/api/import-file-logs`, {
          params: { 'importFileId.equals': importFileId, sort: 'occurredAt,asc' },
        }),
      ]);

      const totalItems = parseInt(itemsRes.headers['x-total-count'], 10);
      setItemTotal(Number.isNaN(totalItems) ? 0 : totalItems);
      setItemPage(targetPage);

      const enriched: ImportFileItemView[] = itemsRes.data.map(item => ({
        item,
        raw: safeParseMap(item.rawData),
        errors: [],
      }));

      if (enriched.length > 0) {
        const ids = enriched.map(e => e.item.id).filter((value): value is number => value !== null && value !== undefined);
        if (ids.length > 0) {
          const viewMap = new Map<number, ImportFileItemView>();
          enriched.forEach(view => {
            if (view.item.id) {
              viewMap.set(view.item.id, view);
            }
          });
          const errRes = await axios.get<ImportFileItemErrorDTO[]>(`/api/import-file-item-errors`, {
            params: { 'importFileItemId.in': ids.join(','), size: 1000, sort: 'columnIndex,asc' },
          });
          errRes.data.forEach(err => {
            const targetItemId = err.importFileItem?.id;
            if (targetItemId && viewMap.has(targetItemId)) {
              viewMap.get(targetItemId).errors.push(err);
            }
          });
        }
      }

      setImportFile(fileRes.data);
      setItems(enriched);
      setLogs(logsRes.data);
    } catch (e) {
      setError('importMonitor.messages.loadError');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) {
      loadDetail(id, 1);
    }
  }, [id]);

  const handleDownload = () => {
    if (!id) {
      return;
    }
    axios
      .get(`/api/import-files/${id}/result`, { responseType: 'blob' })
      .then(response => {
        const fallback = `${importFile?.originalFilename || `import_${id}`}_result.csv`;
        const filename = resolveFilename(response.headers['content-disposition'], fallback) ?? fallback;
        triggerDownload(response.data, filename);
      })
      .catch(() => setError('importMonitor.messages.downloadError'));
  };

  const renderFieldErrors = (errors: ImportFileItemErrorDTO[]) =>
    errors
      .map(err => `${err.fieldName ?? 'N/A'}(${err.columnIndex ?? '-'}) - ${err.errorCode}${err.message ? `: ${err.message}` : ''}`)
      .join('; ');

  const renderLogDetail = (log: ImportFileLogDTO): React.ReactNode => {
    if (!log.detail) {
      return '';
    }
    try {
      const parsed = JSON.parse(log.detail);
      if (log.eventCode === 'NORMALIZE_ROW_ERROR') {
        const parts: string[] = [];
        if (parsed.lineIndex) {
          parts.push(`Line ${parsed.lineIndex}`);
        }
        if (parsed.invoiceNo) {
          parts.push(`Invoice ${parsed.invoiceNo}`);
        }
        if (parsed.field) {
          parts.push(`Field ${parsed.field}`);
        }
        if (parsed.errorCode) {
          parts.push(`Code ${parsed.errorCode}`);
        }
        return (
          <>
            <div>{parts.join(' / ')}</div>
            {parsed.rawData && <pre className="small bg-light p-2 mt-1">{JSON.stringify(parsed.rawData, null, 2)}</pre>}
          </>
        );
      }
      return <pre className="small bg-light p-2 mb-0">{JSON.stringify(parsed, null, 2)}</pre>;
    } catch (err) {
      return log.detail;
    }
  };

  return (
    <div>
      <Button color="link" onClick={() => navigate(-1)}>
        <FontAwesomeIcon icon="arrow-left" /> <Translate contentKey="entity.action.back" />
      </Button>
      <h2>
        <Translate contentKey="importMonitor.detail.title" interpolate={{ id }} />
      </h2>
      {error && (
        <Alert color="danger">
          <Translate contentKey={error} />
        </Alert>
      )}
      {loading && (
        <div className="text-center">
          <Spinner />
        </div>
      )}
      {!loading && importFile && (
        <>
          <Card className="mb-3">
            <CardBody>
              <CardTitle tag="h5">
                <Translate contentKey="importMonitor.detail.summary" />
              </CardTitle>
              <CardText>
                <strong>
                  <Translate contentKey="importMonitor.table.filename" />:
                </strong>{' '}
                {importFile.originalFilename}
              </CardText>
              <CardText>
                <strong>
                  <Translate contentKey="importMonitor.table.type" />:
                </strong>{' '}
                {importFile.importType}
              </CardText>
              <CardText>
                <strong>
                  <Translate contentKey="importMonitor.table.status" />:
                </strong>{' '}
                {importFile.status}
              </CardText>
              <CardText>
                <strong>
                  <Translate contentKey="importMonitor.table.success" />:
                </strong>{' '}
                {importFile.successCount} / {importFile.totalCount}
              </CardText>
              <CardText>
                <strong>
                  <Translate contentKey="importMonitor.table.error" />:
                </strong>{' '}
                {importFile.errorCount}
              </CardText>
              <Button color="secondary" onClick={handleDownload}>
                <FontAwesomeIcon icon="download" /> <Translate contentKey="importMonitor.downloadSingle" />
              </Button>
            </CardBody>
          </Card>

          <h4>
            <Translate contentKey="importMonitor.detail.items" />
          </h4>
          {items.length === 0 ? (
            <Alert color="info">
              <Translate contentKey="importMonitor.detail.noItems" />
            </Alert>
          ) : (
            <Table responsive hover>
              <thead>
                <tr>
                  <th>#</th>
                  <th>
                    <Translate contentKey="importMonitor.detail.invoiceNo" />
                  </th>
                  <th>
                    <Translate contentKey="importMonitor.table.status" />
                  </th>
                  <th>
                    <Translate contentKey="importMonitor.table.error" />
                  </th>
                  <th>
                    <Translate contentKey="importMonitor.detail.fieldErrors" />
                  </th>
                </tr>
              </thead>
              <tbody>
                {items.map((view, index) => (
                  <tr key={view.item.id ?? `item-${index}`}>
                    <td>{view.item.lineIndex}</td>
                    <td>{extractInvoiceNo(view.raw)}</td>
                    <td>{view.item.status}</td>
                    <td>
                      {view.item.errorCode}
                      {view.item.errorMessage ? ` - ${view.item.errorMessage}` : ''}
                    </td>
                    <td>{view.errors.length > 0 ? renderFieldErrors(view.errors) : '-'}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
          {itemTotal > 0 && (
            <div className="d-flex justify-content-between align-items-center">
              <JhiItemCount page={itemPage} total={itemTotal} itemsPerPage={itemsPerPage} i18nEnabled />
              <JhiPagination
                activePage={itemPage}
                onSelect={currentPage => id && loadDetail(id, currentPage)}
                itemsPerPage={itemsPerPage}
                totalItems={itemTotal}
                maxButtons={5}
              />
            </div>
          )}

          <h4>
            <Translate contentKey="importMonitor.detail.logs" />
          </h4>
          {logs.length === 0 ? (
            <Alert color="info">
              <Translate contentKey="importMonitor.detail.noLogs" />
            </Alert>
          ) : (
            <Table responsive>
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="importMonitor.detail.logTime" />
                  </th>
                  <th>
                    <Translate contentKey="importMonitor.detail.logEvent" />
                  </th>
                  <th>
                    <Translate contentKey="importMonitor.detail.logLevel" />
                  </th>
                  <th>
                    <Translate contentKey="importMonitor.detail.logMessage" />
                  </th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log, index) => (
                  <tr key={log.id ?? `log-${index}`}>
                    <td>{log.occurredAt ? new Date(log.occurredAt).toLocaleString() : '-'}</td>
                    <td>{log.eventCode}</td>
                    <td>{log.level}</td>
                    <td>
                      {log.message}
                      <div>{renderLogDetail(log)}</div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </>
      )}
    </div>
  );
};

const safeParseMap = (raw?: string): Record<string, unknown> => {
  if (!raw) {
    return {};
  }
  try {
    return JSON.parse(raw);
  } catch (e) {
    return {};
  }
};

const extractInvoiceNo = (raw: Record<string, unknown>) => {
  return (raw.InvoiceNo as string) || (raw.invoiceNo as string) || (raw.AllowanceNo as string) || '-';
};

export default ImportMonitorDetail;
