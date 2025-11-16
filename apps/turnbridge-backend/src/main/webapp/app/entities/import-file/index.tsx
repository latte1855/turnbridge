import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ImportFile from './import-file';
import ImportFileDetail from './import-file-detail';
import ImportFileUpdate from './import-file-update';
import ImportFileDeleteDialog from './import-file-delete-dialog';

const ImportFileRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ImportFile />} />
    <Route path="new" element={<ImportFileUpdate />} />
    <Route path=":id">
      <Route index element={<ImportFileDetail />} />
      <Route path="edit" element={<ImportFileUpdate />} />
      <Route path="delete" element={<ImportFileDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ImportFileRoutes;
