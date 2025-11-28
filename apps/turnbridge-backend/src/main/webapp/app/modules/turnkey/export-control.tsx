import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Translate, TranslatorContext, translate } from 'react-jhipster';
import {
  Alert,
  Button,
  Card,
  CardBody,
  CardTitle,
  Col,
  Form,
  FormGroup,
  Input,
  Label,
  Modal,
  ModalBody,
  ModalHeader,
  Row,
  Spinner,
  Table,
} from 'reactstrap';
import translationsEn from '../../../i18n/en/turnkeyExport.json';
import translationsZhTw from '../../../i18n/zh-tw/turnkeyExport.json';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';

TranslatorContext.registerTranslations('en', translationsEn);
TranslatorContext.registerTranslations('zh-tw', translationsZhTw);

interface ExportHistoryEntry {
  timestamp: string;
  batchSize: number;
  processed: number;
}

interface LogEntry {
  eventCode: string;
  message: string;
  occurredAt: string;
  level: string;
  detail?: string | null;
  importFile?: {
    id?: number;
    originalFilename?: string;
  };
}

interface PickupStageStatus {
  stage: string;
  family: string;
  count: number;
}

interface PickupStatus {
  lastScanEpoch?: number;
  srcStuck?: number;
  packStuck?: number;
  uploadPending?: number;
  err?: number;
  alertTriggered?: boolean;
  stages?: PickupStageStatus[];
}

const TurnkeyExportControl = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = account?.authorities?.includes(AUTHORITIES.ADMIN) ?? false;
  const tenantCodeFromStorage = (localStorage.getItem('turnbridge-tenant-code') || '').trim().toUpperCase();
  const tenantDisplay = tenantCodeFromStorage.length > 0 ? tenantCodeFromStorage : translate('turnkeyExport.adminNotice.tenantAll');
  const [batchSize, setBatchSize] = useState('');
  const [history, setHistory] = useState<ExportHistoryEntry[]>([]);
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [logsLoading, setLogsLoading] = useState(false);
  const [logPage, setLogPage] = useState(0);
  const [logSize, setLogSize] = useState(10);
  const [logTotal, setLogTotal] = useState(0);
  const [logPageInput, setLogPageInput] = useState('1');
  const LOG_EVENT_OPTIONS = ['XML_GENERATED', 'XML_DELIVERED_TO_TURNKEY', 'XML_DELIVERY_FAILURE'];
  const [logEventFilters, setLogEventFilters] = useState<string[]>([...LOG_EVENT_OPTIONS]);
  const [loading, setLoading] = useState(false);
  const [errorKey, setErrorKey] = useState<string | null>(null);
  const [successKey, setSuccessKey] = useState<string | null>(null);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedLog, setSelectedLog] = useState<LogEntry | null>(null);
  const [pickupStatus, setPickupStatus] = useState<PickupStatus | null>(null);
  const [pickupLoading, setPickupLoading] = useState(false);
  const [pickupError, setPickupError] = useState(false);

  const fetchRecentLogs = () => {
    setLogsLoading(true);
    axios
      .get('/api/turnkey/export/logs', {
        params: {
          page: logPage,
          size: logSize,
          event: logEventFilters,
        },
      })
      .then(response => {
        setLogs(response.data ?? []);
        const headerCount = response.headers['x-total-count'] ?? response.headers['x-totalcount'];
        setLogTotal(Number(headerCount ?? 0));
      })
      .catch(() => {
        setLogs([]);
        setLogTotal(0);
      })
      .finally(() => setLogsLoading(false));
  };

  const fetchPickupStatus = () => {
    setPickupLoading(true);
    setPickupError(false);
    axios
      .get('/api/turnkey/pickup-status')
      .then(response => setPickupStatus(response.data ?? null))
      .catch(() => {
        setPickupError(true);
        setPickupStatus(null);
      })
      .finally(() => setPickupLoading(false));
  };

  useEffect(() => {
    fetchRecentLogs();
  }, [logPage, logSize, logEventFilters]);

  useEffect(() => {
    fetchPickupStatus();
  }, []);

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (!isAdmin) {
      setErrorKey('turnkeyExport.messages.forbidden');
      return;
    }
    setLoading(true);
    setErrorKey(null);
    setSuccessKey(null);

    const params: Record<string, string> = {};
    if (batchSize.trim().length > 0) {
      params.batchSize = batchSize.trim();
    }

    axios
      .post('/api/turnkey/export', null, { params })
      .then(response => {
        const entry: ExportHistoryEntry = {
          timestamp: new Date().toISOString(),
          batchSize: response.data?.batchSize ?? (Number(batchSize) || 0),
          processed: response.data?.processed ?? 0,
        };
        setHistory(prev => [entry, ...prev].slice(0, 5));
        fetchRecentLogs();
        setSuccessKey('turnkeyExport.messages.success');
      })
      .catch(() => setErrorKey('turnkeyExport.messages.failure'))
      .finally(() => setLoading(false));
  };

  const openDetailModal = (log: LogEntry) => {
    setSelectedLog(log);
    setDetailModalOpen(true);
  };

  const closeDetailModal = () => {
    setDetailModalOpen(false);
    setSelectedLog(null);
  };

  const parsedDetail = useMemo(() => {
    if (!selectedLog?.detail) {
      return '';
    }
    try {
      return JSON.stringify(JSON.parse(selectedLog.detail), null, 2);
    } catch {
      return selectedLog.detail;
    }
  }, [selectedLog]);

  const formatEpoch = (epoch?: number) => {
    if (!epoch || epoch <= 0) {
      return '-';
    }
    return new Date(epoch * 1000).toLocaleString();
  };

  const totalLogPages = Math.max(1, Math.ceil(logTotal / logSize));
  const canPrevPage = logPage > 0;
  const canNextPage = logPage + 1 < totalLogPages;

  useEffect(() => {
    setLogPageInput((logPage + 1).toString());
  }, [logPage]);

  const handlePageSizeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = Number(event.target.value);
    if (value >= 1) {
      setLogSize(value);
      setLogPage(0);
    }
  };

  const handleGotoPage = () => {
    const target = Number(logPageInput);
    if (Number.isFinite(target)) {
      const clamped = Math.min(Math.max(1, Math.floor(target)), totalLogPages);
      setLogPage(clamped - 1);
      setLogPageInput(clamped.toString());
    }
  };

  const toggleEventFilter = (code: string) => {
    setLogPage(0);
    setLogEventFilters(prev => (prev.includes(code) ? prev.filter(item => item !== code) : [...prev, code]));
  };

  return (
    <div className="container mt-4">
      <Alert color={isAdmin ? 'info' : 'danger'}>
        <div className="fw-semibold">
          <Translate contentKey="turnkeyExport.adminNotice.title" />
        </div>
        <div>
          <Translate
            contentKey={isAdmin ? 'turnkeyExport.adminNotice.body' : 'turnkeyExport.adminNotice.forbidden'}
            interpolate={{ tenant: tenantDisplay }}
          />
        </div>
      </Alert>
      <Row className="mb-3">
        <Col md="8">
          <Card>
            <CardBody>
              <CardTitle tag="h2">
                <Translate contentKey="turnkeyExport.title" />
              </CardTitle>
              <p className="text-muted">
                <Translate contentKey="turnkeyExport.description" />
              </p>
              {successKey && (
                <Alert color="success" toggle={() => setSuccessKey(null)}>
                  <Translate contentKey={successKey} />
                </Alert>
              )}
              {errorKey && (
                <Alert color="danger" toggle={() => setErrorKey(null)}>
                  <Translate contentKey={errorKey} />
                </Alert>
              )}
              <Form onSubmit={handleSubmit}>
                <FormGroup>
                  <Label for="batchSize">
                    <Translate contentKey="turnkeyExport.form.batchSize" />
                  </Label>
                  <Input
                    id="batchSize"
                    type="number"
                    min="1"
                    value={batchSize}
                    onChange={event => setBatchSize(event.target.value)}
                    placeholder="200"
                  />
                  <small className="text-muted">
                    <Translate contentKey="turnkeyExport.form.help" />
                  </small>
                </FormGroup>
                <div className="d-flex gap-2">
                  <Button color="primary" type="submit" disabled={loading || !isAdmin}>
                    <Translate contentKey="turnkeyExport.form.submit" />
                  </Button>
                  <Button color="secondary" type="button" onClick={() => setBatchSize('')} disabled={loading}>
                    <Translate contentKey="entity.action.reset" />
                  </Button>
                </div>
              </Form>
            </CardBody>
          </Card>
        </Col>
        <Col md="4">
          <Card className="mb-3">
            <CardBody>
              <CardTitle tag="h5">
                <Translate contentKey="turnkeyExport.info.scheduleTitle" />
              </CardTitle>
              <ul className="mb-0 small">
                <li>
                  <Translate contentKey="turnkeyExport.info.schedule" />
                </li>
                <li>
                  <Translate contentKey="turnkeyExport.info.permission" />
                </li>
                <li>
                  <Translate contentKey="turnkeyExport.info.log" />
                </li>
              </ul>
            </CardBody>
          </Card>
          <Card>
            <CardBody>
              <CardTitle tag="h5">
                <Translate contentKey="turnkeyExport.info.monitorTitle" />
              </CardTitle>
              <p className="small mb-0">
                <Translate contentKey="turnkeyExport.info.monitorDescription" />
              </p>
            </CardBody>
          </Card>
          <Card>
            <CardBody>
              <CardTitle tag="h5">
                <Translate contentKey="turnkeyExport.helper.title" />
              </CardTitle>
              <div className="d-flex flex-column gap-2">
                <Button tag={Link} color="light" className="text-start" to="/import-monitor">
                  <Translate contentKey="turnkeyExport.helper.importMonitor" />
                </Button>
                <Button tag={Link} color="light" className="text-start" to="/dashboard/webhook">
                  <Translate contentKey="turnkeyExport.helper.webhookDashboard" />
                </Button>
                <Button
                  color="light"
                  className="text-start"
                  href="https://grafana.example.com/d/turnkey"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <Translate contentKey="turnkeyExport.helper.grafana" />
                </Button>
              </div>
            </CardBody>
          </Card>
          <Card className="mt-3">
            <CardBody>
              <div className="d-flex justify-content-between align-items-center mb-2">
                <CardTitle tag="h5" className="mb-0">
                  <Translate contentKey="turnkeyExport.pickup.title" />
                </CardTitle>
                <Button color="light" size="sm" onClick={fetchPickupStatus} disabled={pickupLoading}>
                  <Translate contentKey="turnkeyExport.pickup.refresh" />
                </Button>
              </div>
              {pickupLoading ? (
                <div className="text-center py-3">
                  <Spinner size="sm" color="primary" />
                </div>
              ) : pickupError ? (
                <p className="text-muted small mb-0">
                  <Translate contentKey="turnkeyExport.pickup.error" />
                </p>
              ) : pickupStatus ? (
                <>
                  <p className="small mb-2">
                    <Translate contentKey="turnkeyExport.pickup.lastScan" />: {formatEpoch(pickupStatus.lastScanEpoch)}
                  </p>
                  <div className="d-flex flex-wrap gap-2 mb-3">
                    <span className={`badge ${pickupStatus.srcStuck ? 'bg-danger' : 'bg-secondary'}`}>
                      <Translate contentKey="turnkeyExport.pickup.src" /> {pickupStatus.srcStuck ?? 0}
                    </span>
                    <span className={`badge ${pickupStatus.packStuck ? 'bg-danger' : 'bg-secondary'}`}>
                      <Translate contentKey="turnkeyExport.pickup.pack" /> {pickupStatus.packStuck ?? 0}
                    </span>
                    <span className={`badge ${pickupStatus.uploadPending ? 'bg-warning' : 'bg-secondary'}`}>
                      <Translate contentKey="turnkeyExport.pickup.upload" /> {pickupStatus.uploadPending ?? 0}
                    </span>
                    <span className={`badge ${pickupStatus.err ? 'bg-danger' : 'bg-secondary'}`}>
                      <Translate contentKey="turnkeyExport.pickup.err" /> {pickupStatus.err ?? 0}
                    </span>
                  </div>
                  {(pickupStatus.stages?.filter(stage => stage.count > 0).length ?? 0) > 0 ? (
                    <Table responsive borderless size="sm" className="mb-0">
                      <thead>
                        <tr>
                          <th>
                            <Translate contentKey="turnkeyExport.pickup.stage" />
                          </th>
                          <th>
                            <Translate contentKey="turnkeyExport.pickup.family" />
                          </th>
                          <th className="text-end">
                            <Translate contentKey="turnkeyExport.pickup.count" />
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        {pickupStatus.stages
                          ?.filter(stage => stage.count > 0)
                          .map(stage => (
                            <tr key={`${stage.stage}-${stage.family}`}>
                              <td>{stage.stage}</td>
                              <td>{stage.family}</td>
                              <td className="text-end">{stage.count}</td>
                            </tr>
                          ))}
                      </tbody>
                    </Table>
                  ) : (
                    <p className="text-muted small mb-0">
                      <Translate contentKey="turnkeyExport.pickup.empty" />
                    </p>
                  )}
                </>
              ) : (
                <p className="text-muted small mb-0">
                  <Translate contentKey="turnkeyExport.pickup.empty" />
                </p>
              )}
            </CardBody>
          </Card>
        </Col>
      </Row>
      <Card>
        <CardBody>
          <CardTitle tag="h5">
            <Translate contentKey="turnkeyExport.history.title" />
          </CardTitle>
          {history.length === 0 ? (
            <p className="text-muted mb-0">
              <Translate contentKey="turnkeyExport.history.empty" />
            </p>
          ) : (
            <Table responsive>
              <thead>
                <tr>
                  <th>
                    <Translate contentKey="turnkeyExport.history.timestamp" />
                  </th>
                  <th>
                    <Translate contentKey="turnkeyExport.history.batchSize" />
                  </th>
                  <th>
                    <Translate contentKey="turnkeyExport.history.processed" />
                  </th>
                </tr>
              </thead>
              <tbody>
                {history.map(entry => (
                  <tr key={entry.timestamp}>
                    <td>{new Date(entry.timestamp).toLocaleString()}</td>
                    <td>{entry.batchSize}</td>
                    <td>{entry.processed}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </CardBody>
      </Card>
      <Card className="mt-3">
        <CardBody>
          <div className="d-flex justify-content-between align-items-center mb-2">
            <CardTitle tag="h5" className="mb-0">
              <Translate contentKey="turnkeyExport.logs.title" />
            </CardTitle>
            <Button color="light" size="sm" onClick={fetchRecentLogs} disabled={logsLoading}>
              <Translate contentKey="turnkeyExport.logs.refresh" />
            </Button>
          </div>
          <Form className="mb-3">
            <Row className="align-items-end gy-2">
              <Col md="7">
                <div className="fw-semibold mb-1">
                  <Translate contentKey="turnkeyExport.logs.filterTitle" />
                </div>
                <div className="d-flex flex-wrap gap-2">
                  {LOG_EVENT_OPTIONS.map(code => (
                    <FormGroup check inline key={code}>
                      <Input
                        id={`log-filter-${code}`}
                        type="checkbox"
                        checked={logEventFilters.includes(code)}
                        onChange={() => toggleEventFilter(code)}
                      />
                      <Label check htmlFor={`log-filter-${code}`}>
                        {code}
                      </Label>
                    </FormGroup>
                  ))}
                </div>
              </Col>
              <Col md="5" className="text-md-end">
                <Label className="me-2 fw-semibold">
                  <Translate contentKey="turnkeyExport.logs.pageSize" />
                </Label>
                <Input
                  type="select"
                  value={logSize}
                  onChange={e => {
                    setLogPage(0);
                    setLogSize(Number(e.target.value));
                  }}
                >
                  {[5, 10, 20, 50].map(size => (
                    <option key={size} value={size}>
                      {size}
                    </option>
                  ))}
                </Input>
              </Col>
            </Row>
          </Form>
          {logsLoading ? (
            <div className="text-center py-4">
              <Spinner color="primary" />
            </div>
          ) : logs.length === 0 ? (
            <p className="text-muted mb-0">
              <Translate contentKey="turnkeyExport.logs.empty" />
            </p>
          ) : (
            <>
              <Table responsive>
                <thead>
                  <tr>
                    <th>
                      <Translate contentKey="turnkeyExport.logs.occurredAt" />
                    </th>
                    <th>
                      <Translate contentKey="turnkeyExport.logs.eventCode" />
                    </th>
                    <th>
                      <Translate contentKey="turnkeyExport.logs.message" />
                    </th>
                    <th>
                      <Translate contentKey="turnkeyExport.logs.importFile" />
                    </th>
                    <th>
                      <Translate contentKey="turnkeyExport.logs.action" />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((log, index) => (
                    <tr key={log.occurredAt + index}>
                      <td>{new Date(log.occurredAt).toLocaleString()}</td>
                      <td>
                        <span className={`badge ${log.level === 'ERROR' ? 'bg-danger' : 'bg-info'}`}>{log.eventCode}</span>
                      </td>
                      <td>
                        <div>{log.message}</div>
                        {log.detail && (
                          <div className="text-muted small">
                            <Translate contentKey="turnkeyExport.logs.detail" />: {log.detail}
                          </div>
                        )}
                      </td>
                      <td>
                        {log.importFile?.id ? (
                          <>
                            #{log.importFile.id}
                            {log.importFile.originalFilename ? ` (${log.importFile.originalFilename})` : ''}
                          </>
                        ) : (
                          '-'
                        )}
                      </td>
                      <td>
                        <Button color="link" className="p-0 me-2" onClick={() => openDetailModal(log)}>
                          <Translate contentKey="turnkeyExport.logs.viewDetail" />
                        </Button>
                        {log.importFile?.id ? (
                          <Button color="link" className="p-0" tag={Link} to={`/import-monitor?importFileId=${log.importFile.id}`}>
                            <Translate contentKey="turnkeyExport.logs.viewImport" />
                          </Button>
                        ) : (
                          '-'
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
              <div className="d-flex flex-column gap-2 mb-3">
                <div className="d-flex flex-wrap gap-2 align-items-center">
                  <span className="fw-semibold text-muted">
                    <Translate contentKey="turnkeyExport.logs.filterTitle" />
                  </span>
                  {LOG_EVENT_OPTIONS.map(code => (
                    <Button
                      key={code}
                      size="sm"
                      color={logEventFilters.includes(code) ? 'primary' : 'outline-primary'}
                      onClick={() => toggleEventFilter(code)}
                    >
                      {code}
                    </Button>
                  ))}
                </div>
                <div className="d-flex flex-wrap gap-3 align-items-center">
                  <div className="d-flex align-items-center gap-2">
                    <Label className="mb-0" for="logPageSize">
                      <Translate contentKey="turnkeyExport.logs.pageSize" />
                    </Label>
                    <Input
                      id="logPageSize"
                      type="number"
                      min="1"
                      value={logSize}
                      style={{ width: '5rem' }}
                      onChange={handlePageSizeChange}
                    />
                  </div>
                  <div className="d-flex align-items-center gap-2">
                    <Label className="mb-0" for="logPageJump">
                      <Translate contentKey="turnkeyExport.logs.gotoLabel" />
                    </Label>
                    <Input
                      id="logPageJump"
                      type="number"
                      min="1"
                      value={logPageInput}
                      style={{ width: '5rem' }}
                      onChange={event => setLogPageInput(event.target.value)}
                      onKeyDown={event => {
                        if (event.key === 'Enter') {
                          event.preventDefault();
                          handleGotoPage();
                        }
                      }}
                    />
                    <Button size="sm" color="primary" onClick={handleGotoPage}>
                      <Translate contentKey="turnkeyExport.logs.gotoButton" />
                    </Button>
                  </div>
                </div>
              </div>
              <div className="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-3">
                <div className="text-muted">
                  <Translate
                    contentKey="turnkeyExport.logs.pageInfo"
                    interpolate={{
                      page: logPage + 1,
                      total: totalLogPages,
                    }}
                  />
                </div>
                <div className="d-flex gap-2">
                  <Button color="secondary" size="sm" disabled={!canPrevPage} onClick={() => setLogPage(p => Math.max(0, p - 1))}>
                    <Translate contentKey="turnkeyExport.logs.prev" />
                  </Button>
                  <Button
                    color="secondary"
                    size="sm"
                    disabled={!canNextPage}
                    onClick={() => setLogPage(p => Math.min(p + 1, Math.max(0, totalLogPages - 1)))}
                  >
                    <Translate contentKey="turnkeyExport.logs.next" />
                  </Button>
                </div>
              </div>
            </>
          )}
        </CardBody>
      </Card>
      <Modal isOpen={detailModalOpen} toggle={closeDetailModal} size="lg">
        <ModalHeader toggle={closeDetailModal}>
          <Translate contentKey="turnkeyExport.logs.detailModalTitle" />
        </ModalHeader>
        <ModalBody>
          {selectedLog ? (
            <>
              <p className="text-muted small mb-2">
                <Translate contentKey="turnkeyExport.logs.detailDescription" interpolate={{ eventCode: selectedLog.eventCode }} />
              </p>
              {parsedDetail ? (
                <pre className="bg-light p-3 rounded border small">{parsedDetail}</pre>
              ) : (
                <p className="text-muted mb-0">
                  <Translate contentKey="turnkeyExport.logs.detailEmpty" />
                </p>
              )}
            </>
          ) : (
            <p className="text-muted mb-0">
              <Translate contentKey="turnkeyExport.logs.detailEmpty" />
            </p>
          )}
        </ModalBody>
      </Modal>
    </div>
  );
};

export default TurnkeyExportControl;
