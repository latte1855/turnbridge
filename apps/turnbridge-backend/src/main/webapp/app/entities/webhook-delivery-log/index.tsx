import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import WebhookDeliveryLog from './webhook-delivery-log';
import WebhookDeliveryLogDetail from './webhook-delivery-log-detail';
import WebhookDeliveryLogUpdate from './webhook-delivery-log-update';
import WebhookDeliveryLogDeleteDialog from './webhook-delivery-log-delete-dialog';

const WebhookDeliveryLogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<WebhookDeliveryLog />} />
    <Route path="new" element={<WebhookDeliveryLogUpdate />} />
    <Route path=":id">
      <Route index element={<WebhookDeliveryLogDetail />} />
      <Route path="edit" element={<WebhookDeliveryLogUpdate />} />
      <Route path="delete" element={<WebhookDeliveryLogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default WebhookDeliveryLogRoutes;
