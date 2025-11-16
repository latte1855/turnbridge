import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InvoiceItem from './invoice-item';
import InvoiceItemDetail from './invoice-item-detail';
import InvoiceItemUpdate from './invoice-item-update';
import InvoiceItemDeleteDialog from './invoice-item-delete-dialog';

const InvoiceItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InvoiceItem />} />
    <Route path="new" element={<InvoiceItemUpdate />} />
    <Route path=":id">
      <Route index element={<InvoiceItemDetail />} />
      <Route path="edit" element={<InvoiceItemUpdate />} />
      <Route path="delete" element={<InvoiceItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InvoiceItemRoutes;
