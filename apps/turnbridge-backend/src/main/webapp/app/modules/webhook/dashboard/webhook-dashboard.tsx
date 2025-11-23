import React, { useEffect, useState } from 'react';
import { Button, Card, CardBody, CardHeader, Col, Row, Table } from 'reactstrap';
import axios from 'axios';
import { Translate, JhiItemCount, JhiPagination } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faRedo } from '@fortawesome/free-solid-svg-icons';
import { Link } from 'react-router-dom';

interface TbSummary {
  tbCode?: string;
  tbCategory?: string;
  count?: number;
  sampleInvoice?: string;
  sampleImportId?: number;
  lastUpdated?: string;
}

interface DlqItem {
  id?: number;
  deliveryId?: string;
  event?: string;
  status?: string;
  attempts?: number;
  lastError?: string;
  dlqReason?: string;
  webhookEndpointName?: string;
  tbCode?: string;
  tbCategory?: string;
  invoiceNo?: string;
  importId?: number;
}

const SAMPLE_SUMMARY: TbSummary[] = [
  {
    tbCode: 'TB-5003',
    tbCategory: 'PLATFORM.DATA_AMOUNT_MISMATCH',
    count: 5,
    sampleInvoice: 'AB10000001',
    sampleImportId: 1,
    lastUpdated: '2025-11-10T10:00:00Z',
  },
];

const SAMPLE_DLQ: DlqItem[] = [
  {
    id: -1,
    deliveryId: 'sample-delivery',
    event: 'invoice.status.updated',
    status: 'FAILED',
    attempts: 3,
    tbCode: 'TB-5003',
    tbCategory: 'PLATFORM.DATA_AMOUNT_MISMATCH',
    invoiceNo: 'AB10000001',
    importId: 1,
  },
];

const itemsPerPage = 10;
const WebhookDashboard = () => {
  const [summary, setSummary] = useState<TbSummary[]>([]);
  const [dlq, setDlq] = useState<DlqItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [activePage, setActivePage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  const fetchSummary = () => axios.get<TbSummary[]>('/api/dashboard/webhook-tb-summary');
  const fetchDlq = (page: number) =>
    axios.get<DlqItem[]>('/api/dashboard/webhook-dlq', {
      params: { page: page - 1, size: itemsPerPage },
    });

  const loadData = async (page: number = activePage) => {
    setLoading(true);
    try {
      const [sumRes, dlqRes] = await Promise.all([fetchSummary(), fetchDlq(page)]);
      const summaryData = sumRes.data.length > 0 ? sumRes.data : SAMPLE_SUMMARY;
      const dlqData = dlqRes.data.length > 0 ? dlqRes.data : SAMPLE_DLQ;
      setSummary(summaryData);
      setDlq(dlqData);
      const totalCount = parseInt(dlqRes.headers['x-total-count'], 10);
      setTotalItems(Number.isNaN(totalCount) ? dlqData.length : totalCount);
      setActivePage(page);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleResend = (id?: number) => {
    if (!id) {
      return;
    }
    axios.post(`/api/dashboard/webhook-dlq/${id}/resend`).then(() => loadData());
  };

  return (
    <div>
      <Row className="mb-3">
        <Col>
          <h2>
            <Translate contentKey="turnbridgeBackendApp.webhookDashboard.title">Webhook Dashboard</Translate>
          </h2>
        </Col>
        <Col className="text-end">
          <Button color="info" onClick={() => loadData()} disabled={loading}>
            <FontAwesomeIcon icon={faRedo} spin={loading} />{' '}
            <Translate contentKey="turnbridgeBackendApp.webhookDashboard.refresh">Refresh</Translate>
          </Button>
        </Col>
      </Row>
      <Row>
        {summary.map(item => (
          <Col md={4} key={item.tbCode}>
            <Card className="mb-3">
              <CardHeader>
                <strong>
                  {item.tbCode}
                  {item.tbCategory ? ` / ${item.tbCategory}` : ''}
                </strong>
              </CardHeader>
              <CardBody>
                <p>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.count">Count</Translate>: {item.count ?? 0}
                </p>
                {item.sampleInvoice && (
                  <p>
                    <Translate contentKey="turnbridgeBackendApp.webhookDashboard.sampleInvoice">Sample</Translate>: {item.sampleInvoice}
                    {item.sampleImportId ? (
                      <Button tag={Link} to={`/import-monitor/${item.sampleImportId}`} size="sm" color="link">
                        <Translate contentKey="turnbridgeBackendApp.webhookDashboard.viewImport">View Import</Translate>
                      </Button>
                    ) : null}
                  </p>
                )}
              </CardBody>
            </Card>
          </Col>
        ))}
      </Row>
      <Card>
        <CardHeader>
          <strong>
            <Translate contentKey="turnbridgeBackendApp.webhookDashboard.dlqTitle">Webhook DLQ</Translate>
          </strong>
        </CardHeader>
        <CardBody>
          <Table responsive hover>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.deliveryId">Delivery</Translate>
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.event">Event</Translate>
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.tbCode">TB Code</Translate>
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.status">Status</Translate>
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.attempts">Attempts</Translate>
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.import">Import</Translate>
                </th>
                <th>
                  <Translate contentKey="turnbridgeBackendApp.webhookDashboard.action">Action</Translate>
                </th>
              </tr>
            </thead>
            <tbody>
              {dlq.map(item => (
                <tr key={item.id}>
                  <td>{item.deliveryId}</td>
                  <td>{item.event}</td>
                  <td>
                    {item.tbCode}
                    {item.tbCategory ? ` / ${item.tbCategory}` : ''}
                  </td>
                  <td>{item.status}</td>
                  <td>{item.attempts}</td>
                  <td>
                    {item.invoiceNo}
                    {item.importId ? ` / ${item.importId}` : ''}
                  </td>
                  <td>
                    <Button color="info" size="sm" onClick={() => handleResend(item.id)}>
                      <Translate contentKey="turnbridgeBackendApp.webhookDashboard.resend">Resend</Translate>
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
          {totalItems > itemsPerPage && (
            <div className="d-flex justify-content-between align-items-center">
              <JhiItemCount page={activePage} total={totalItems} itemsPerPage={itemsPerPage} i18nEnabled />
              <JhiPagination
                totalItems={totalItems}
                itemsPerPage={itemsPerPage}
                activePage={activePage}
                onSelect={page => loadData(page)}
                maxButtons={5}
              />
            </div>
          )}
        </CardBody>
      </Card>
    </div>
  );
};

export default WebhookDashboard;
