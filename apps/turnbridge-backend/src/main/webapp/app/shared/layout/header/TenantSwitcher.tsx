import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Translate, translate } from 'react-jhipster';
import { FormGroup, Input, Spinner } from 'reactstrap';

interface TenantDTO {
  id?: number;
  name?: string;
  code?: string;
}

interface TenantSwitcherProps {
  isAdmin: boolean;
}

const STORAGE_KEY = 'turnbridge-tenant-code';
const ALL_TENANTS = 'ALL';

const TenantSwitcher = ({ isAdmin }: TenantSwitcherProps) => {
  const [tenants, setTenants] = useState<TenantDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState(localStorage.getItem(STORAGE_KEY) || ALL_TENANTS);

  useEffect(() => {
    if (!isAdmin) {
      return;
    }
    setLoading(true);
    axios
      .get<TenantDTO[]>('/api/tenants', { params: { size: 200, sort: 'name,asc' } })
      .then(response => setTenants(response.data ?? []))
      .catch(() => setError('global.menu.tenantSelector.loadError'))
      .finally(() => setLoading(false));
  }, [isAdmin]);

  const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const code = event.target.value;
    setSelected(code);
    if (code === ALL_TENANTS) {
      localStorage.removeItem(STORAGE_KEY);
    } else {
      localStorage.setItem(STORAGE_KEY, code);
    }
    window.location.reload();
  };

  if (!isAdmin) {
    return null;
  }

  return (
    <FormGroup className="tenant-switcher mb-0 text-white">
      {loading ? (
        <Spinner size="sm" color="light" />
      ) : error ? (
        <span className="text-warning small">
          <Translate contentKey={error} />
        </span>
      ) : (
        <Input type="select" id="tenant-selector" className="tenant-selector" value={selected} onChange={onChange} bsSize="sm">
          <option value={ALL_TENANTS}>{translate('global.menu.tenantSelector.all')}</option>
          {tenants.map(tenant => (
            <option key={tenant.id ?? tenant.code} value={tenant.code}>
              {tenant.name} ({tenant.code})
            </option>
          ))}
        </Input>
      )}
    </FormGroup>
  );
};

export default TenantSwitcher;
