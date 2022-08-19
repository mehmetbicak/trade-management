import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import RawMessage from './raw-message';
import RawMessageDetail from './raw-message-detail';
import RawMessageUpdate from './raw-message-update';
import RawMessageDeleteDialog from './raw-message-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={RawMessageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={RawMessageUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={RawMessageDetail} />
      <ErrorBoundaryRoute path={match.url} component={RawMessage} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={RawMessageDeleteDialog} />
  </>
);

export default Routes;
