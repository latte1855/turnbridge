import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ImportFileItemError from './import-file-item-error';
import ImportFileItemErrorDetail from './import-file-item-error-detail';
import ImportFileItemErrorUpdate from './import-file-item-error-update';
import ImportFileItemErrorDeleteDialog from './import-file-item-error-delete-dialog';

const ImportFileItemErrorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ImportFileItemError />} />
    <Route path="new" element={<ImportFileItemErrorUpdate />} />
    <Route path=":id">
      <Route index element={<ImportFileItemErrorDetail />} />
      <Route path="edit" element={<ImportFileItemErrorUpdate />} />
      <Route path="delete" element={<ImportFileItemErrorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ImportFileItemErrorRoutes;
