import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ManualAction from './manual-action';
import ManualActionDetail from './manual-action-detail';
import ManualActionUpdate from './manual-action-update';
import ManualActionDeleteDialog from './manual-action-delete-dialog';

const ManualActionRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ManualAction />} />
    <Route path="new" element={<ManualActionUpdate />} />
    <Route path=":id">
      <Route index element={<ManualActionDetail />} />
      <Route path="edit" element={<ManualActionUpdate />} />
      <Route path="delete" element={<ManualActionDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ManualActionRoutes;
