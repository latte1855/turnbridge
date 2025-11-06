import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import TrackRange from './track-range';
import TrackRangeDetail from './track-range-detail';
import TrackRangeUpdate from './track-range-update';
import TrackRangeDeleteDialog from './track-range-delete-dialog';

const TrackRangeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<TrackRange />} />
    <Route path="new" element={<TrackRangeUpdate />} />
    <Route path=":id">
      <Route index element={<TrackRangeDetail />} />
      <Route path="edit" element={<TrackRangeUpdate />} />
      <Route path="delete" element={<TrackRangeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TrackRangeRoutes;
