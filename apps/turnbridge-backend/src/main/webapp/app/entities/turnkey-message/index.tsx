import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TurnkeyMessage from './turnkey-message';
import TurnkeyMessageDetail from './turnkey-message-detail';
import TurnkeyMessageUpdate from './turnkey-message-update';
import TurnkeyMessageDeleteDialog from './turnkey-message-delete-dialog';

const TurnkeyMessageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TurnkeyMessage />} />
    <Route path="new" element={<TurnkeyMessageUpdate />} />
    <Route path=":id">
      <Route index element={<TurnkeyMessageDetail />} />
      <Route path="edit" element={<TurnkeyMessageUpdate />} />
      <Route path="delete" element={<TurnkeyMessageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TurnkeyMessageRoutes;
