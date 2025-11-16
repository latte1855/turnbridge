import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import WebhookEndpoint from './webhook-endpoint';
import WebhookEndpointDetail from './webhook-endpoint-detail';
import WebhookEndpointUpdate from './webhook-endpoint-update';
import WebhookEndpointDeleteDialog from './webhook-endpoint-delete-dialog';

const WebhookEndpointRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<WebhookEndpoint />} />
    <Route path="new" element={<WebhookEndpointUpdate />} />
    <Route path=":id">
      <Route index element={<WebhookEndpointDetail />} />
      <Route path="edit" element={<WebhookEndpointUpdate />} />
      <Route path="delete" element={<WebhookEndpointDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default WebhookEndpointRoutes;
