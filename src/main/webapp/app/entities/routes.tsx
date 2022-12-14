import React from 'react';
import { Switch } from 'react-router-dom';
import { ReducersMapObject, combineReducers } from '@reduxjs/toolkit';

import getStore from 'app/config/store';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import entitiesReducers from './reducers';

import ExceptionMessage from './exception-message';

export default ({ match }) => {
  const store = getStore();
  store.injectReducer('trademanagement', combineReducers(entitiesReducers as ReducersMapObject));
  return (
    <div>
      <Switch>
        {/* prettier-ignore */}
        <ErrorBoundaryRoute path={`${match.url}exception-message`} component={ExceptionMessage} />
      </Switch>
    </div>
  );
};
