import React, { Suspense, lazy } from 'react';
import { Route } from 'react-router';

import { useLocation } from 'react-router-dom';

import Login from 'app/modules/login/login';
import Register from 'app/modules/account/register/register';
import Activate from 'app/modules/account/activate/activate';
import PasswordResetInit from 'app/modules/account/password-reset/init/password-reset-init';
import PasswordResetFinish from 'app/modules/account/password-reset/finish/password-reset-finish';
import Logout from 'app/modules/login/logout';
import Home from 'app/modules/home/home';
import EntitiesRoutes from 'app/entities/routes';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';
import { AUTHORITIES } from 'app/config/constants';
import { sendActivity } from 'app/config/websocket-middleware';

const loading = <div>loading ...</div>;

/**
 * 懶加載使用 React.lazy() 與 Suspense，以避免使用舊的 contextTypes API
 */
const Account = lazy(() => import(/* webpackChunkName: "account" */ 'app/modules/account'));

const Admin = lazy(() => import(/* webpackChunkName: "administration" */ 'app/modules/administration'));

const ImportMonitor = lazy(() => import('app/modules/import-monitor'));

const WebhookDashboard = lazy(() => import('app/modules/webhook/dashboard'));

const WebhookRegistration = lazy(() => import('app/modules/webhook/registration'));
const TurnkeyExportControl = lazy(() => import('app/modules/turnkey/export-control'));
const AppRoutes = () => {
  const pageLocation = useLocation();
  React.useEffect(() => {
    sendActivity(pageLocation.pathname);
  }, [pageLocation]);
  return (
    <div className="view-routes">
      <ErrorBoundaryRoutes>
        <Route index element={<Home />} />
        <Route path="login" element={<Login />} />
        <Route path="logout" element={<Logout />} />
        <Route path="account">
          <Route
            path="*"
            element={
              <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN, AUTHORITIES.USER]}>
                <Suspense fallback={loading}>
                  <Account />
                </Suspense>
              </PrivateRoute>
            }
          />
          <Route path="register" element={<Register />} />
          <Route path="activate" element={<Activate />} />
          <Route path="reset">
            <Route path="request" element={<PasswordResetInit />} />
            <Route path="finish" element={<PasswordResetFinish />} />
          </Route>
        </Route>
        <Route
          path="admin/*"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
              <Suspense fallback={loading}>
                <Admin />
              </Suspense>
            </PrivateRoute>
          }
        />
        <Route
          path="import-monitor/*"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
              <Suspense fallback={loading}>
                <ImportMonitor />
              </Suspense>
            </PrivateRoute>
          }
        />
        <Route
          path="dashboard/webhook"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
              <Suspense fallback={loading}>
                <WebhookDashboard />
              </Suspense>
            </PrivateRoute>
          }
        />
        <Route
          path="webhook/endpoints"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
              <Suspense fallback={loading}>
                <WebhookRegistration />
              </Suspense>
            </PrivateRoute>
          }
        />
        <Route
          path="turnkey/export"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
              <Suspense fallback={loading}>
                <TurnkeyExportControl />
              </Suspense>
            </PrivateRoute>
          }
        />
        <Route
          path="*"
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
              <EntitiesRoutes />
            </PrivateRoute>
          }
        />
        <Route path="*" element={<PageNotFound />} />
      </ErrorBoundaryRoutes>
    </div>
  );
};

export default AppRoutes;
