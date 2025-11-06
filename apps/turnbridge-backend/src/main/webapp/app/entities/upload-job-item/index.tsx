import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UploadJobItem from './upload-job-item';
import UploadJobItemDetail from './upload-job-item-detail';
import UploadJobItemUpdate from './upload-job-item-update';
import UploadJobItemDeleteDialog from './upload-job-item-delete-dialog';

const UploadJobItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UploadJobItem />} />
    <Route path="new" element={<UploadJobItemUpdate />} />
    <Route path=":id">
      <Route index element={<UploadJobItemDetail />} />
      <Route path="edit" element={<UploadJobItemUpdate />} />
      <Route path="delete" element={<UploadJobItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UploadJobItemRoutes;
