import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InvoiceAssignNo from './invoice-assign-no';
import InvoiceAssignNoDetail from './invoice-assign-no-detail';
import InvoiceAssignNoUpdate from './invoice-assign-no-update';
import InvoiceAssignNoDeleteDialog from './invoice-assign-no-delete-dialog';

const InvoiceAssignNoRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InvoiceAssignNo />} />
    <Route path="new" element={<InvoiceAssignNoUpdate />} />
    <Route path=":id">
      <Route index element={<InvoiceAssignNoDetail />} />
      <Route path="edit" element={<InvoiceAssignNoUpdate />} />
      <Route path="delete" element={<InvoiceAssignNoDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InvoiceAssignNoRoutes;
