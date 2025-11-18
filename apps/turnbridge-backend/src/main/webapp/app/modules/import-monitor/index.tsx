import React from 'react';
import { Route } from 'react-router-dom';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import ImportMonitorList from './import-monitor-list';
import ImportMonitorDetail from './import-monitor-detail';

const ImportMonitorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ImportMonitorList />} />
    <Route path=":id" element={<ImportMonitorDetail />} />
  </ErrorBoundaryRoutes>
);

export default ImportMonitorRoutes;
