import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tenant from './tenant';
import TenantDetail from './tenant-detail';
import TenantUpdate from './tenant-update';
import TenantDeleteDialog from './tenant-delete-dialog';

const TenantRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Tenant />} />
    <Route path="new" element={<TenantUpdate />} />
    <Route path=":id">
      <Route index element={<TenantDetail />} />
      <Route path="edit" element={<TenantUpdate />} />
      <Route path="delete" element={<TenantDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TenantRoutes;
