import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Translate, translate, ValidatedField, ValidatedForm, JhiItemCount, JhiPagination, TranslatorContext } from 'react-jhipster';
import {
  Alert,
  Badge,
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
  ModalFooter,
  ModalHeader,
  Row,
  Spinner,
  Table,
} from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlus, faSync, faPen, faTrash, faKey } from '@fortawesome/free-solid-svg-icons';
import { useAppSelector } from 'app/config/store';
import { AUTHORITIES } from 'app/config/constants';
import { IWebhookEndpoint } from 'app/shared/model/webhook-endpoint.model';
import { ITenant } from 'app/shared/model/tenant.model';
import { WebhookStatus } from 'app/shared/model/enumerations/webhook-status.model';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import translationsEn from '../../../../i18n/en/webhookRegistration.json';
import translationsZhTw from '../../../../i18n/zh-tw/webhookRegistration.json';

TranslatorContext.registerTranslations('en', translationsEn);
TranslatorContext.registerTranslations('zh-tw', translationsZhTw);

const TENANT_STORAGE_KEY = 'turnbridge-tenant-code';

interface RotateResponse {
  id?: number;
  secret?: string;
  rotatedAt?: string;
}

const EVENT_OPTIONS = [
  { value: 'upload.completed', key: 'webhookRegistration.events.uploadCompleted' },
  { value: 'invoice.status.updated', key: 'webhookRegistration.events.invoiceStatus' },
  { value: 'turnkey.feedback.daily-summary', key: 'webhookRegistration.events.turnkeySummary' },
];

const WebhookRegistration = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAdmin = useMemo(() => account?.authorities?.includes(AUTHORITIES.ADMIN) ?? false, [account]);
  const tenantCodeFromStorage = (localStorage.getItem(TENANT_STORAGE_KEY) || '').trim().toUpperCase();
  const shouldRestrictToTenant = isAdmin && tenantCodeFromStorage.length > 0;

  const [endpoints, setEndpoints] = useState<IWebhookEndpoint[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [filters, setFilters] = useState({ name: '', event: '', status: '' });
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<IWebhookEndpoint | null>(null);
  const [events, setEvents] = useState<string[]>(['invoice.status.updated']);
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [secretInfo, setSecretInfo] = useState<{ key: string; secret: string } | null>(null);
  const [tenants, setTenants] = useState<ITenant[]>([]);
  const [tenantError, setTenantError] = useState<string | null>(null);
  const [tenantFilterId, setTenantFilterId] = useState<number | null | undefined>(undefined);
  const restrictedTenant = shouldRestrictToTenant ? (tenants.find(tenant => tenant.id === tenantFilterId) ?? null) : null;
  const restrictedTenantLabel = restrictedTenant?.name
    ? `${restrictedTenant.name} (${restrictedTenant.code})`
    : tenantCodeFromStorage || restrictedTenant?.code || '';
  const buildEmptyFormDefaults = () => ({
    id: undefined,
    name: '',
    targetUrl: '',
    status: 'ACTIVE',
    tenantId: isAdmin && tenantFilterId ? tenantFilterId.toString() : '',
  });
  const [formDefaults, setFormDefaults] = useState<Record<string, any>>(buildEmptyFormDefaults());
  const buildEmptyFormValues = () => ({
    name: '',
    targetUrl: '',
    status: 'ACTIVE',
    tenantId: isAdmin && tenantFilterId ? tenantFilterId.toString() : '',
  });
  const [formValues, setFormValues] = useState(buildEmptyFormValues());

  const syncFormState = (defaults: Record<string, any>) => {
    const mergedDefaults = {
      name: '',
      targetUrl: '',
      status: 'ACTIVE',
      tenantId: '',
      ...defaults,
    };
    setFormDefaults(mergedDefaults);
    setFormValues({
      name: mergedDefaults.name ?? '',
      targetUrl: mergedDefaults.targetUrl ?? '',
      status: mergedDefaults.status ?? 'ACTIVE',
      tenantId: mergedDefaults.tenantId ?? '',
    });
  };

  const handleFieldChange = (field: keyof typeof formValues) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setFormValues(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const loadEndpoints = (targetPage: number = page) => {
    if (shouldRestrictToTenant && tenantFilterId === undefined) {
      return;
    }
    setLoading(true);
    setError(null);
    const tenantFilterParam = shouldRestrictToTenant && tenantFilterId !== undefined ? (tenantFilterId ?? -1) : undefined;
    axios
      .get<IWebhookEndpoint[]>('/api/webhook-endpoints', {
        params: {
          page: targetPage - 1,
          size: ITEMS_PER_PAGE,
          sort: 'id,desc',
          'name.contains': filters.name || undefined,
          'events.contains': filters.event || undefined,
          'status.equals': filters.status || undefined,
          'tenantId.equals': tenantFilterParam,
        },
      })
      .then(response => {
        setEndpoints(response.data ?? []);
        const total = parseInt(response.headers['x-total-count'], 10);
        setTotalItems(Number.isNaN(total) ? 0 : total);
        setPage(targetPage);
      })
      .catch(() => setError('webhookRegistration.messages.loadError'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (shouldRestrictToTenant && tenantFilterId === undefined) {
      return;
    }
    loadEndpoints(1);
  }, [tenantFilterId, shouldRestrictToTenant]);

  useEffect(() => {
    if (!editing) {
      syncFormState(buildEmptyFormDefaults());
    }
  }, [tenantFilterId, editing]);

  useEffect(() => {
    if (!isAdmin) {
      return;
    }
    axios
      .get<ITenant[]>('/api/tenants', { params: { sort: 'name,asc', size: 200 } })
      .then(response => {
        setTenants(response.data ?? []);
        setTenantError(null);
      })
      .catch(() => setTenantError('webhookRegistration.messages.loadTenantError'));
  }, [isAdmin]);

  useEffect(() => {
    if (!shouldRestrictToTenant) {
      setTenantFilterId(null);
      return;
    }
    const normalized = tenantCodeFromStorage;
    if (!normalized) {
      setTenantFilterId(null);
      return;
    }
    const matched = tenants.find(tenant => tenant.code?.toUpperCase() === normalized);
    if (matched?.id) {
      setTenantFilterId(matched.id);
      return;
    }
    axios
      .get<ITenant[]>('/api/tenants', { params: { 'code.equals': tenantCodeFromStorage } })
      .then(response => {
        const tenantId = response.data && response.data.length > 0 ? (response.data[0].id ?? null) : null;
        setTenantFilterId(tenantId);
      })
      .catch(() => setTenantFilterId(null));
  }, [shouldRestrictToTenant, tenantCodeFromStorage, tenants]);

  useEffect(() => {
    if (editing) {
      setEvents(parseEvents(editing.events));
    } else {
      setEvents(['invoice.status.updated']);
    }
  }, [editing]);

  const handleFilterSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    loadEndpoints(1);
  };

  const handleFilterReset = () => {
    setFilters({ name: '', event: '', status: '' });
    loadEndpoints(1);
  };

  const parseEvents = (value?: string | null) =>
    value
      ? value
          .split(',')
          .map(item => item.trim())
          .filter(item => item.length > 0)
      : [];

  const openCreateModal = () => {
    setEditing(null);
    setEvents(['invoice.status.updated']);
    syncFormState(buildEmptyFormDefaults());
    setFormError(null);
    setFormLoading(false);
    setModalOpen(true);
  };

  const openEditModal = (endpoint: IWebhookEndpoint) => {
    if (!endpoint.id) {
      return;
    }
    setFormError(null);
    setFormLoading(true);
    setModalOpen(true);
    axios
      .get<IWebhookEndpoint>(`/api/webhook-endpoints/${endpoint.id}`)
      .then(response => {
        const data = response.data ?? endpoint;
        syncFormState({
          id: data.id,
          name: data.name ?? '',
          targetUrl: data.targetUrl ?? '',
          status: data.status ?? 'ACTIVE',
          tenantId: isAdmin ? (data.tenant?.id?.toString() ?? '') : '',
        });
        setEditing(data);
        setEvents(parseEvents(data.events));
      })
      .catch(() => {
        setError('webhookRegistration.messages.loadDetailError');
        setModalOpen(false);
        setEditing(null);
      })
      .finally(() => setFormLoading(false));
  };

  const closeModal = () => {
    setModalOpen(false);
    setEditing(null);
    setEvents(['invoice.status.updated']);
    syncFormState(buildEmptyFormDefaults());
  };

  const toggleEvent = (value: string) => {
    setEvents(prev => {
      if (prev.includes(value)) {
        return prev.filter(item => item !== value);
      }
      return [...prev, value];
    });
  };

  const handleDelete = (endpoint: IWebhookEndpoint) => {
    if (!endpoint.id) {
      return;
    }
    const confirmed = window.confirm(translate('webhookRegistration.messages.deleteConfirm', { name: endpoint.name ?? endpoint.id }));
    if (!confirmed) {
      return;
    }
    axios
      .delete(`/api/webhook-endpoints/${endpoint.id}`)
      .then(() => loadEndpoints(page))
      .catch(() => setError('webhookRegistration.messages.deleteError'));
  };

  const handleRotate = (endpoint: IWebhookEndpoint) => {
    if (!endpoint.id) {
      return;
    }
    axios
      .post<RotateResponse>(`/api/webhook-endpoints/${endpoint.id}/rotate-secret`)
      .then(response => {
        if (response.data?.secret) {
          setSecretInfo({ key: 'webhookRegistration.messages.secretRotated', secret: response.data.secret });
        }
        loadEndpoints(page);
      })
      .catch(() => setError('webhookRegistration.messages.rotateError'));
  };

  const handleSubmit = () => {
    if (events.length === 0) {
      setFormError('webhookRegistration.messages.noEventSelected');
      return;
    }
    let resolvedTenantId: number | undefined;
    if (isAdmin) {
      const parsed = Number(formValues.tenantId);
      resolvedTenantId = Number.isNaN(parsed) ? undefined : parsed;
    }
    if (isAdmin && !resolvedTenantId) {
      setFormError('webhookRegistration.messages.tenantRequired');
      return;
    }
    setFormError(null);
    const payload: IWebhookEndpoint = {
      id: editing?.id,
      name: formValues.name?.trim(),
      targetUrl: formValues.targetUrl?.trim(),
      status: formValues.status as keyof typeof WebhookStatus,
      events: events.join(','),
      secret: editing?.secret,
    };
    if (resolvedTenantId) {
      payload.tenant = { id: resolvedTenantId };
    } else if (editing?.tenant) {
      payload.tenant = editing.tenant;
    }

    const request = payload.id
      ? axios.put<IWebhookEndpoint>(`/api/webhook-endpoints/${payload.id}`, payload)
      : axios.post<IWebhookEndpoint>('/api/webhook-endpoints', payload);

    request
      .then(response => {
        if (!payload.id && response.data?.secret) {
          setSecretInfo({ key: 'webhookRegistration.messages.secretCreated', secret: response.data.secret });
        }
        closeModal();
        loadEndpoints(payload.id ? page : 1);
      })
      .catch(() => setFormError('webhookRegistration.messages.saveError'));
  };

  const renderStatusBadge = (status?: keyof typeof WebhookStatus) => {
    if (!status) {
      return null;
    }
    const badgeColor = status === 'ACTIVE' ? 'success' : 'secondary';
    return (
      <Badge color={badgeColor} pill>
        {WebhookStatus[status]}
      </Badge>
    );
  };

  const handlePagination = (currentPage: number) => {
    loadEndpoints(currentPage);
  };

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between">
        <div>
          <h2>
            <Translate contentKey="webhookRegistration.title">Webhook Registration</Translate>
          </h2>
          <p className="text-muted">
            <Translate contentKey="webhookRegistration.description" />
          </p>
          <Alert color="info" className="py-2" fade={false}>
            <Translate contentKey="webhookRegistration.actions.rotateHint" />
          </Alert>
          {shouldRestrictToTenant && (
            <Alert color="light" className="border" fade={false}>
              {translate('webhookRegistration.messages.restrictedTenant', { tenant: restrictedTenantLabel })}
            </Alert>
          )}
        </div>
        <div className="d-flex gap-2">
          <Button color="info" onClick={() => loadEndpoints(page)} disabled={loading}>
            <FontAwesomeIcon icon={faSync} spin={loading} /> <Translate contentKey="webhookRegistration.actions.refresh" />
          </Button>
          <Button color="primary" onClick={openCreateModal}>
            <FontAwesomeIcon icon={faPlus} /> <Translate contentKey="webhookRegistration.actions.create" />
          </Button>
        </div>
      </div>

      {error && (
        <Alert color="danger" fade={false}>
          <Translate contentKey={error} />
        </Alert>
      )}
      {secretInfo && (
        <Alert color="warning" toggle={() => setSecretInfo(null)} fade={false}>
          <Translate contentKey={secretInfo.key} />
          <span className="ms-2">
            <code>{secretInfo.secret}</code>
          </span>
        </Alert>
      )}

      <Card className="mb-3">
        <CardBody>
          <Form onSubmit={handleFilterSubmit}>
            <Row className="gy-2">
              <Col md="4">
                <FormGroup>
                  <Label for="filter-name">
                    <Translate contentKey="webhookRegistration.filters.name" />
                  </Label>
                  <Input
                    id="filter-name"
                    value={filters.name}
                    onChange={event => setFilters(prev => ({ ...prev, name: event.target.value }))}
                  />
                </FormGroup>
              </Col>
              <Col md="4">
                <FormGroup>
                  <Label for="filter-event">
                    <Translate contentKey="webhookRegistration.filters.event" />
                  </Label>
                  <Input
                    id="filter-event"
                    value={filters.event}
                    onChange={event => setFilters(prev => ({ ...prev, event: event.target.value }))}
                  />
                </FormGroup>
              </Col>
              <Col md="4">
                <FormGroup>
                  <Label for="filter-status">
                    <Translate contentKey="webhookRegistration.filters.status" />
                  </Label>
                  <Input
                    type="select"
                    id="filter-status"
                    value={filters.status}
                    onChange={event => setFilters(prev => ({ ...prev, status: event.target.value }))}
                  >
                    <option value="">{translate('webhookRegistration.filters.all')}</option>
                    <option value="ACTIVE">{WebhookStatus.ACTIVE}</option>
                    <option value="DISABLED">{WebhookStatus.DISABLED}</option>
                  </Input>
                </FormGroup>
              </Col>
            </Row>
            <div className="d-flex justify-content-end gap-2">
              <Button color="secondary" type="button" onClick={handleFilterReset}>
                <Translate contentKey="entity.action.reset" />
              </Button>
              <Button color="primary" type="submit">
                <Translate contentKey="entity.action.search" />
              </Button>
            </div>
          </Form>
        </CardBody>
      </Card>

      <Card>
        <CardBody>
          <CardTitle tag="h5">
            <Translate contentKey="webhookRegistration.table.title" />
          </CardTitle>
          {loading ? (
            <div className="text-center py-5">
              <Spinner color="primary" />
            </div>
          ) : (
            <>
              <Table responsive hover>
                <thead>
                  <tr>
                    <th>
                      <Translate contentKey="webhookRegistration.table.name" />
                    </th>
                    <th>
                      <Translate contentKey="webhookRegistration.table.url" />
                    </th>
                    <th>
                      <Translate contentKey="webhookRegistration.table.events" />
                    </th>
                    <th>
                      <Translate contentKey="webhookRegistration.table.status" />
                    </th>
                    <th>
                      <Translate contentKey="webhookRegistration.table.tenant" />
                    </th>
                    <th className="text-end">
                      <Translate contentKey="webhookRegistration.table.actions" />
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {endpoints.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="text-center text-muted py-4">
                        <Translate contentKey="webhookRegistration.table.empty" />
                      </td>
                    </tr>
                  ) : (
                    endpoints.map(endpoint => (
                      <tr key={endpoint.id}>
                        <td>{endpoint.name}</td>
                        <td className="text-break">{endpoint.targetUrl}</td>
                        <td>
                          <div className="d-flex flex-wrap gap-2">
                            {parseEvents(endpoint.events).map(item => (
                              <Badge key={item} color="info" className="text-dark fw-semibold px-3 py-2">
                                {item}
                              </Badge>
                            ))}
                          </div>
                        </td>
                        <td>{renderStatusBadge(endpoint.status)}</td>
                        <td>{endpoint.tenant?.name ?? endpoint.tenant?.code ?? '-'}</td>
                        <td className="text-end">
                          <Button color="secondary" size="sm" className="me-1" onClick={() => openEditModal(endpoint)}>
                            <FontAwesomeIcon icon={faPen} /> <Translate contentKey="entity.action.edit" />
                          </Button>
                          <Button color="warning" size="sm" className="me-1" onClick={() => handleRotate(endpoint)}>
                            <FontAwesomeIcon icon={faKey} /> <Translate contentKey="webhookRegistration.actions.rotate" />
                          </Button>
                          <Button color="danger" size="sm" onClick={() => handleDelete(endpoint)}>
                            <FontAwesomeIcon icon={faTrash} /> <Translate contentKey="entity.action.delete" />
                          </Button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </Table>
              <div className="d-flex justify-content-between align-items-center">
                <JhiItemCount page={page} total={totalItems} itemsPerPage={ITEMS_PER_PAGE} />
                <JhiPagination
                  activePage={page}
                  onSelect={handlePagination}
                  itemsPerPage={ITEMS_PER_PAGE}
                  totalItems={totalItems}
                  maxButtons={5}
                />
              </div>
            </>
          )}
        </CardBody>
      </Card>

      <Modal isOpen={modalOpen} toggle={closeModal} backdrop="static" size="lg">
        <ModalHeader toggle={closeModal}>
          <Translate contentKey={editing ? 'webhookRegistration.form.editTitle' : 'webhookRegistration.form.createTitle'} />
        </ModalHeader>
        <ModalBody>
          {formError && (
            <Alert color="danger" fade={false}>
              <Translate contentKey={formError} />
            </Alert>
          )}
          {formLoading ? (
            <div className="text-center py-5">
              <Spinner color="primary" />
            </div>
          ) : (
            <ValidatedForm defaultValues={formDefaults} onSubmit={handleSubmit} key={formDefaults.id ?? 'new'}>
              <Row>
                <Col md="6">
                  <ValidatedField
                    name="name"
                    label={translate('webhookRegistration.form.name')}
                    required
                    data-cy="webhook-name"
                    labelClass="required-label"
                    value={formValues.name}
                    onChange={handleFieldChange('name')}
                    validate={{ required: { value: true, message: translate('entity.validation.required') }, maxLength: 64 }}
                  />
                </Col>
                <Col md="6">
                  <ValidatedField
                    name="targetUrl"
                    label={translate('webhookRegistration.form.url')}
                    labelClass="required-label"
                    required
                    value={formValues.targetUrl}
                    onChange={handleFieldChange('targetUrl')}
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      maxLength: { value: 512, message: translate('entity.validation.maxlength', { max: 512 }) },
                      pattern: {
                        value: /^https?:\/\/[^\s]+$/i,
                        message: translate('webhookRegistration.validation.url'),
                      },
                    }}
                  />
                </Col>
              </Row>
              <Row>
                <Col md="4">
                  <ValidatedField
                    name="status"
                    label={translate('webhookRegistration.form.status')}
                    type="select"
                    required
                    labelClass="required-label"
                    value={formValues.status}
                    onChange={handleFieldChange('status')}
                  >
                    <option value="ACTIVE">{WebhookStatus.ACTIVE}</option>
                    <option value="DISABLED">{WebhookStatus.DISABLED}</option>
                  </ValidatedField>
                </Col>
                {isAdmin && (
                  <Col md="8">
                    {tenantError ? (
                      <Alert color="warning" className="mb-0" fade={false}>
                        <Translate contentKey={tenantError} />
                      </Alert>
                    ) : (
                      <ValidatedField
                        name="tenantId"
                        label={translate('webhookRegistration.form.tenant')}
                        type="select"
                        labelClass="required-label"
                        value={formValues.tenantId}
                        onChange={handleFieldChange('tenantId')}
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                        }}
                      >
                        <option value="">{translate('webhookRegistration.form.tenantPlaceholder')}</option>
                        {tenants.map(tenant => (
                          <option key={tenant.id} value={tenant.id}>
                            {tenant.name} ({tenant.code})
                          </option>
                        ))}
                      </ValidatedField>
                    )}
                  </Col>
                )}
              </Row>
              <FormGroup>
                <Label>
                  <Translate contentKey="webhookRegistration.form.events" />
                </Label>
                <div className="d-flex flex-column">
                  {EVENT_OPTIONS.map(option => (
                    <FormGroup check key={option.value} className="mb-1">
                      <Input
                        type="checkbox"
                        id={`event-${option.value}`}
                        checked={events.includes(option.value)}
                        onChange={() => toggleEvent(option.value)}
                      />
                      <Label check htmlFor={`event-${option.value}`}>
                        <Translate contentKey={option.key} />
                      </Label>
                    </FormGroup>
                  ))}
                </div>
              </FormGroup>
              <div className="text-end">
                <Button color="secondary" onClick={closeModal} className="me-2">
                  <Translate contentKey="entity.action.cancel" />
                </Button>
                <Button color="primary" type="submit">
                  <Translate contentKey="entity.action.save" />
                </Button>
              </div>
            </ValidatedForm>
          )}
        </ModalBody>
        <ModalFooter className="justify-content-start">
          <small className="text-muted">
            <Translate contentKey="webhookRegistration.form.secretHint" />
          </small>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default WebhookRegistration;
