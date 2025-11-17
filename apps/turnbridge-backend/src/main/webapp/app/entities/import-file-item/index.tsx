import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ImportFileItem from './import-file-item';
import ImportFileItemDetail from './import-file-item-detail';
import ImportFileItemUpdate from './import-file-item-update';
import ImportFileItemDeleteDialog from './import-file-item-delete-dialog';

const ImportFileItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ImportFileItem />} />
    <Route path="new" element={<ImportFileItemUpdate />} />
    <Route path=":id">
      <Route index element={<ImportFileItemDetail />} />
      <Route path="edit" element={<ImportFileItemUpdate />} />
      <Route path="delete" element={<ImportFileItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ImportFileItemRoutes;
