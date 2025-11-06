import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UploadJob from './upload-job';
import UploadJobDetail from './upload-job-detail';
import UploadJobUpdate from './upload-job-update';
import UploadJobDeleteDialog from './upload-job-delete-dialog';

const UploadJobRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UploadJob />} />
    <Route path="new" element={<UploadJobUpdate />} />
    <Route path=":id">
      <Route index element={<UploadJobDetail />} />
      <Route path="edit" element={<UploadJobUpdate />} />
      <Route path="delete" element={<UploadJobDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UploadJobRoutes;
