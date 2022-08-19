import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ExceptionMessage from './exception-message';
import ExceptionMessageDetail from './exception-message-detail';
import ExceptionMessageUpdate from './exception-message-update';
import ExceptionMessageDeleteDialog from './exception-message-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ExceptionMessageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ExceptionMessageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ExceptionMessageDetail} />
      <ErrorBoundaryRoute path={match.url} component={ExceptionMessage} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ExceptionMessageDeleteDialog} />
  </>
);

export default Routes;
