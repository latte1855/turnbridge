import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ImportFileLog from './import-file-log';
import ImportFileLogDetail from './import-file-log-detail';
import ImportFileLogUpdate from './import-file-log-update';
import ImportFileLogDeleteDialog from './import-file-log-delete-dialog';

const ImportFileLogRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ImportFileLog />} />
    <Route path="new" element={<ImportFileLogUpdate />} />
    <Route path=":id">
      <Route index element={<ImportFileLogDetail />} />
      <Route path="edit" element={<ImportFileLogUpdate />} />
      <Route path="delete" element={<ImportFileLogDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ImportFileLogRoutes;
