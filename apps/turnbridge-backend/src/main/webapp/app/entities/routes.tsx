import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tenant from './tenant';
import ImportFile from './import-file';
import ImportFileLog from './import-file-log';
import Invoice from './invoice';
import InvoiceItem from './invoice-item';
import InvoiceAssignNo from './invoice-assign-no';
import TurnkeyMessage from './turnkey-message';
import WebhookEndpoint from './webhook-endpoint';
import WebhookDeliveryLog from './webhook-delivery-log';
import ManualAction from './manual-action';
import ImportFileItem from './import-file-item';
import ImportFileItemError from './import-file-item-error';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="tenant/*" element={<Tenant />} />
        <Route path="import-file/*" element={<ImportFile />} />
        <Route path="import-file-log/*" element={<ImportFileLog />} />
        <Route path="invoice/*" element={<Invoice />} />
        <Route path="invoice-item/*" element={<InvoiceItem />} />
        <Route path="invoice-assign-no/*" element={<InvoiceAssignNo />} />
        <Route path="turnkey-message/*" element={<TurnkeyMessage />} />
        <Route path="webhook-endpoint/*" element={<WebhookEndpoint />} />
        <Route path="webhook-delivery-log/*" element={<WebhookDeliveryLog />} />
        <Route path="manual-action/*" element={<ManualAction />} />
        <Route path="import-file-item/*" element={<ImportFileItem />} />
        <Route path="import-file-item-error/*" element={<ImportFileItemError />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
