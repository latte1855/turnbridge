import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UploadJob from './upload-job';
import UploadJobItem from './upload-job-item';
import StoredObject from './stored-object';
import TrackRange from './track-range';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="upload-job/*" element={<UploadJob />} />
        <Route path="upload-job-item/*" element={<UploadJobItem />} />
        <Route path="stored-object/*" element={<StoredObject />} />
        <Route path="track-range/*" element={<TrackRange />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
