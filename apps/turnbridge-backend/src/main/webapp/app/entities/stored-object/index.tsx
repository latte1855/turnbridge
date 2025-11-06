import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StoredObject from './stored-object';
import StoredObjectDetail from './stored-object-detail';
import StoredObjectUpdate from './stored-object-update';
import StoredObjectDeleteDialog from './stored-object-delete-dialog';

const StoredObjectRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StoredObject />} />
    <Route path="new" element={<StoredObjectUpdate />} />
    <Route path=":id">
      <Route index element={<StoredObjectDetail />} />
      <Route path="edit" element={<StoredObjectUpdate />} />
      <Route path="delete" element={<StoredObjectDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StoredObjectRoutes;
