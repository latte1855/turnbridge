import React from 'react';
import { translate, Translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';
import MenuItem from './menu-item';

interface OperationsMenuProps {
  isAdmin: boolean;
}

export const OperationsMenu = ({ isAdmin }: OperationsMenuProps) => (
  <NavDropdown
    icon="screwdriver-wrench"
    name={translate('global.menu.operations.main')}
    id="operations-menu"
    data-cy="operations"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <MenuItem icon="clipboard-list" to="/import-monitor" data-cy="operations-import-monitor">
      <Translate contentKey="global.menu.operations.importMonitor" />
    </MenuItem>
    <MenuItem icon="chart-line" to="/dashboard/webhook" data-cy="operations-webhook-dashboard">
      <Translate contentKey="global.menu.operations.webhookDashboard" />
    </MenuItem>
    <MenuItem icon="network-wired" to="/webhook/endpoints" data-cy="operations-webhook-registration">
      <Translate contentKey="global.menu.operations.webhookRegistration" />
    </MenuItem>
    {isAdmin && (
      <MenuItem icon="file-export" to="/turnkey/export" data-cy="operations-turnkey-export">
        <Translate contentKey="global.menu.operations.turnkeyExport" />
      </MenuItem>
    )}
  </NavDropdown>
);
